package net.wiredtomato.waygl

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.gui.screen.ingame.BookEditScreen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.util.Identifier
import net.wiredtomato.waygl.WayGL.LOGGER
import net.wiredtomato.waygl.config.Config
import org.apache.commons.io.IOUtils
import org.lwjgl.glfw.GLFW
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.math.abs

/**
 * Port of [moehreag/wayland_fixes](https://github.com/moehreag/wayland-fixes) VirtualCursor
 */
object VirtualCursor {
    private val chunks = mutableListOf<XCursor.ImageChunk>()
    private var virtOffsetX = 0.0
    private var virtOffsetY = 0.0
    private var current = 0
    private var images = mutableListOf<Identifier>()
    private var animationTime = 0L
    private var virtual = false
    private var windowHandle = 0L
    private var lastX = 0.0
    private var lastY = 0.0
    private val cursorSize by lazy { XDG.getCursorSize() }

    private fun mayVirtualize() = (Config.useVirtualCursor && MinecraftClient.getInstance().world != null)
    private fun isValidScreen(): Boolean {
        if (!Config.useVirtualCursor) return false

        val s = MinecraftClient.getInstance().currentScreen
        return s is HandledScreen<*> || s is ChatScreen || s is BookEditScreen
    }

    fun setup(window: Long) {
        this.windowHandle = window
        virtOffsetX = 0.0
        virtOffsetY = 0.0
        loadCursor()
    }

    fun destroy() {
        images.forEach {
            MinecraftClient.getInstance().textureManager.destroyTexture(it)
        }
    }

    /**
     * Set virtual cursor pos
     */
    fun setCursorPosition(x: Double, y: Double) {
        virtOffsetY = y - lastY
        virtOffsetX = x - lastX
    }

    fun grabMouse(grab: Boolean) {
        if (grab) {
            virtual = false
            GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED)
        } else {
            if (isValidScreen() && mayVirtualize()) {
                GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED)
                virtual = true
            } else {
                GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL)
            }
        }
    }

    fun grabMouseNoVirtualize(grab: Boolean, windowHandle: Long) {
        if (grab) {
            GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED)
        } else {
            GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL)
        }
    }

    fun render(context: DrawContext) {
        draw(context)
    }

    /*
	 * Draw the virtual cursor
	 */
    private fun draw(context: DrawContext) {
        //LOGGER.info("$virtual | ${images.getOrNull(0)}")
        if (virtual && images.getOrNull(0) != null) {
            val image = images[current]
            RenderSystem.enableBlend()
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
            RenderSystem.setShaderTexture(0, image)

            val window = MinecraftClient.getInstance().window
            val scale = window.scaleFactor

            val monitor = window.monitor
            var monitorScale = 1f
            if (monitor != null) {
                val monitorScaleX = FloatArray(1)
                val monitorScaleY = FloatArray(1)
                GLFW.glfwGetMonitorContentScale(monitor.handle, monitorScaleX, monitorScaleY)
                monitorScale = monitorScaleX[0]
            }

            val x = getX().toFloat()
            val y = getY().toFloat()

            context.matrices.push()
            context.matrices.translate(0f, 0f, 1000f)
            context.matrices.scale((monitorScale / scale).toFloat(), (monitorScale / scale).toFloat(), 0f)
            context.drawTexture(
                image,
                (x - (getCurrent().xhot / scale)).toInt(),
                (y - (getCurrent().yhot / scale)).toInt(), 0f, 0f,
                getCurrent().width.toInt(),
                getCurrent().height.toInt(), getCurrent().width.toInt(), getCurrent().height.toInt()
            )

            context.matrices.pop()
            advanceAnimation()
        }
    }

    private fun advanceAnimation() {
        if (images.size > 1) {
            if (animationTime == 0L || System.currentTimeMillis() - animationTime > getCurrent().delay) {
                animationTime = System.currentTimeMillis()
                current++
                if (current >= images.size) {
                    current = 0
                }
            }
        }
    }

    private fun getCurrent(): XCursor.ImageChunk {
        return chunks[current]
    }

    /*
	 * Load the virtual cursor
	 */
    private fun loadCursor() {
        val cursor = SystemCursor.load()

        if (java.lang.Boolean.getBoolean("virtual_mouse.export")) {
            cursor.export()
        }

        val iChunks = mutableListOf<XCursor.ImageChunk>()

        chunks.clear()
        for (chunk in cursor.chunks) {
            if (chunk is XCursor.ImageChunk) {
                iChunks += chunk
                if (chunk.subType.toInt() == cursorSize) {
                    chunks.add(chunk)
                }
            }
        }

        if (chunks.isEmpty()) {
            val closest = iChunks.reduce { acc, chunk ->
                if (abs(acc.subType - cursorSize) < abs(chunk.subType - cursorSize)) acc else chunk
            }

            chunks += closest
        }

        current = 0
        images = MutableList(chunks.size) { i ->
            val c = chunks[i]
            val image = NativeImage(c.width.toInt(), c.height.toInt(), true)
            for (x in 0 until c.width) {
                for (y in 0 until c.height) {
                    image.setColor(x.toInt(), y.toInt(), c.pixels[(x + (c.height.toInt() * y)).toInt()].toInt())
                }
            }

            val img = MinecraftClient.getInstance().textureManager.registerDynamicTexture(
                "virtual_cursor",
                NativeImageBackedTexture(image)
            )

            img
        }

        virtual = false
    }

    fun handleMovementX(x: Double): Double {
        lastX = x
        if (virtual) {
            /*
             * Stop the virtual cursor from leaving the screen entirely
             */

            while (getX() < 0) { // TODO get rid if the loops. loops are bad here.
                virtOffsetX++
            }
            while (getX() >= MinecraftClient.getInstance().window.width - 2) {
                virtOffsetX--
            }
        }
        return getX()
    }

    fun handleMovementY(y: Double): Double {
        lastY = y
        if (virtual) {
            /*
             * Stop the virtual cursor from leaving the screen entirely
             */

            while (getY() < 0) {
                virtOffsetY++
            }
            while (getY() >= MinecraftClient.getInstance().window.height - 2) {
                virtOffsetY--
            }
        }
        return getY()
    }

    fun getX(): Double {
        return if (virtual) lastX + virtOffsetX else lastX
    }

    fun getY(): Double {
        return if (virtual) lastY + virtOffsetY else lastY
    }

    private object SystemCursor {
        fun load(): XCursor {
            val res = runCatching {
                val data = IOUtils.toByteArray(getArrowCursor())
                val buf = ByteBuffer.wrap(data)
                XCursor.parse(buf)
            }

            if (res.isSuccess) {
                return res.getOrNull()!!
            } else {
                throw IllegalStateException("Unable to load cursor texture!", res.exceptionOrNull()!!)
            }
        }

        @Throws(IOException::class)
        private fun getArrowCursor(): InputStream {
            val theme = XDG.getIconTheme("cursors/left_ptr") // load the arrow pointer cursor from the selected theme
            if (theme != null) {
                LOGGER.info("Loading system cursor: $theme")
                return Files.newInputStream(theme)
            }

            LOGGER.info("Falling back to packaged cursor")
            return MinecraftClient.getInstance().resourceManager
                .getResource(Identifier.of("virtual_cursor", "default"))
                .map { r ->
                    runCatching { return@map r.inputStream }
                    null
                }.orElse(this.javaClass.getResourceAsStream("/assets/virtual_cursor/default"))!!
        }
    }

    private data class XCursor(
        val magic: String,
        val headerLength: Long,
        val fileVersion: Long,
        val toCEntryCount: Long,
        val toC: List<TableOfContents>,
        val chunks: List<Chunk>,
    ) {
        companion object {
            fun parse(buf: ByteBuffer): XCursor {
                val magic = buf.string(4)
                if ("Xcur" != magic) {
                    throw IllegalArgumentException("Not an Xcursor file! Magic: $magic")
                }

                val headerLength = buf.int()
                val version = buf.int()
                val ntoc = buf.int()
                val toc = mutableListOf<TableOfContents>()
                val chunks = mutableListOf<Chunk>()

                for (i in 0 until ntoc) {
                    val table = TableOfContents(buf.int(), buf.int(), buf.int())
                    toc.add(table)
                    chunks.add(parseChunk(buf, table))
                }

                return XCursor(magic, headerLength, version, ntoc, toc, chunks)
            }

            fun parseChunk(buf: ByteBuffer, table: TableOfContents): Chunk {
                val pos = buf.position()
                buf.position(table.pos.toInt())
                val chunk = when (table.type) {
                    0xfffe0001 -> parseComment(buf, table)
                    0xfffd0002 -> parseImage(buf, table)
                    else -> throw IllegalArgumentException("Unrecognized type: ${table.type}, Valid types are { ${0xfffe0001} , ${0xfffd0002} }")
                }
                buf.position(pos)
                return chunk
            }

            fun parseImage(buf: ByteBuffer, table: TableOfContents): ImageChunk {
                val size: Long = buf.int()
                require(size == 36L) { "not an image chunk! size != 36: $size" }

                val type: Long = buf.int()
                require(type == 0xfffd0002L && type == table.type) { "not an image chunk! type != image: $type" }

                val subtype: Long = buf.int()
                require(subtype == table.subtype) { "not an image chunk! subtype != table.subtype: $subtype" }
                val version: Long = buf.int()

                val width: Long = buf.int()

                require(width <= 0x7ff) { "image too large! width > 0x7ff: $width" }

                val height: Long = buf.int()
                require(height <= 0x7ff) { "image too large! height > 0x7ff: $height" }
                val xhot: Long = buf.int()
                require(xhot <= width) { "xhot outside image!: $xhot" }
                val yhot: Long = buf.int()
                require(yhot <= height) { "yhot outside image!: $yhot" }
                val delay: Long = buf.int()

                val pixels = LongArray((width * height).toInt())

                for (i in pixels.indices) {
                    pixels[i] = buf.int()
                }

                return ImageChunk(size, type, subtype, version, width, height, xhot, yhot, delay, pixels.toList())
            }

            fun parseComment(buf: ByteBuffer, table: TableOfContents): CommentChunk {
                val size = buf.int()
                require(size == 20L) { "not a comment chunk! size != 20: $size" }

                val type = buf.int()
                require(type == 0xfffe0001 && type == table.type) { "not a comment chunk! type != comment: $type" }

                val subtype = buf.int()
                require(subtype == table.subtype) { "not a comment chunk! subtype != table.subtype: $subtype" }

                val version = buf.int()
                val commentLength = buf.int()
                val comment = buf.string(commentLength.toInt())

                return CommentChunk(size, type, subtype, version, commentLength, comment)
            }
        }

        /*
		 * export the cursor files
		 */
        fun export() {
            val dir = Paths.get("cursors")
            dir.createDirectories()
            try {
                Files.walkFileTree(dir,
                    object : SimpleFileVisitor<Path>() {
                        @Throws(IOException::class)
                        override fun postVisitDirectory(
                            dir: Path, exc: IOException,
                        ): FileVisitResult {
                            Files.delete(dir)
                            return FileVisitResult.CONTINUE
                        }

                        @Throws(IOException::class)
                        override fun visitFile(
                            file: Path, attrs: BasicFileAttributes,
                        ): FileVisitResult {
                            Files.delete(file)
                            return FileVisitResult.CONTINUE
                        }
                    })
                Files.createDirectory(dir)
            } catch (e: IOException) {
                LOGGER.warn("Failed to create clean export directory, export will likely fail!", e)
            }

            LOGGER.info("Exporting chunks..")
            for (c in chunks) {
                c.export()
            }
        }

        data class TableOfContents(
            val type: Long,
            val subtype: Long,
            val pos: Long,
        )

        abstract class Chunk {
            abstract val length: Long
            abstract val type: Long
            abstract val subType: Long
            abstract val version: Long

            abstract fun export()
        }

        /**
		 * Comment header size: 20 bytes
		 *
		 * Type: 0xfffe0001
		 * subtype: 1 (COPYRIGHT), 2 (LICENSE), 3 (OTHER)
		 * version: 1
		 */
        data class CommentChunk(
            override val length: Long,
            override val type: Long,
            override val subType: Long,
            override val version: Long,
            val commentLength: Long,
            val comment: String,
        ): Chunk() {
            override fun export() {
                val name = when (subType) {
                    1L -> "COPYRIGHT"
                    2L -> "LICENSE"
                    else -> "COMMENT"
                }

                runCatching {
                    Files.write(Paths.get("cursors", name), comment.toByteArray(StandardCharsets.UTF_8), StandardOpenOption.CREATE)
                }.onFailure { ex ->
                    LOGGER.warn("Image export failed!", ex)
                }
            }
        }

        /**
		 * Image header size: 36 bytes
		 *
		 * Type: 0xfffd002
		 * subtype: image size
		 * version: 1
		 *
		 */
        data class ImageChunk(
            override val length: Long,
            override val type: Long,
            override val subType: Long,
            override val version: Long,
            val width: Long,
            val height: Long,
            val xhot: Long,
            val yhot: Long,
            val delay: Long,
            val pixels: List<Long>,
        ): Chunk() {
            fun image(): List<Int> {
                return pixels.map { it.toInt() }
            }

            override fun export() {
                val image = BufferedImage(width.toInt(), height.toInt(), BufferedImage.TYPE_INT_ARGB)
                image().forEachIndexed { i, data ->
                    image.setRGB((i % width).toInt(), (i / height).toInt(), data)
                }

                val imageName = "${subType}x$subType"
                val lines = arrayListOf(
                    "[Sizes]",
                    "Cursor: $imageName",
                    "Image: ${width}x$height",
                    "[Hotspots]",
                    "X: $xhot",
                    "Y: $yhot",
                    "[Delay]",
                    "$delay"
                )

                var name = imageName
                if (delay != 0L) {
                    var i = 0
                    name = "${imageName}_$i"
                    while (File("cursors", "$name.png").exists()) {
                        i++
                        name = "${imageName}_$i"
                    }
                }

                var cursor = "$subType $xhot $yhot $name.png"
                val cursorFile = Path("cursors", "cursor.cursor")
                if (delay != 0L) {
                    cursor += " $delay"
                    if (Files.exists(cursorFile)) {
                        cursor = "\n$cursor"
                    }
                }

                runCatching {
                    val file = File("cursors", "$name.png")
                    file.parentFile.mkdirs()
                    file.createNewFile()
                    ImageIO.write(image, "png", File("cursors", "$name.png"))
                    Files.write(Path("cursors", "$name.txt"), lines, StandardOpenOption.CREATE)
                    Files.write(cursorFile, cursor.toByteArray(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND)
                }.onFailure { ex ->
                    LOGGER.warn("Image export failed!", ex)
                }
            }
        }
    }
}