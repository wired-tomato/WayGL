package net.wiredtomato.waygl

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resource.InputSupplier
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.imageio.ImageIO
import kotlin.math.sqrt


object IconInjector {
    val APP_ID = "com.mojang.minecraft"
    val ICON_NAME = "minecraft.png"
    val FILE_NAME = "$APP_ID.desktop"
    val LOCATION = "/assets/${WayGL.MODID}/$FILE_NAME"
    val injects = mutableListOf<Path>()

    fun inject() {
        Runtime.getRuntime().addShutdownHook(Thread(IconInjector::uninject))

        val stream = IconInjector::class.java.getResourceAsStream(LOCATION)
        val location = getDesktopFileLoc()

        val version = FabricLoader.getInstance().getModContainer("minecraft").orElseThrow().metadata.version.friendlyString

        injectFile(
            location,
            String.format(stream?.readAllBytes()?.toString(Charsets.UTF_8) ?: "null",
                version,
                ICON_NAME.substring(0, ICON_NAME.lastIndexOf("."))
            ).toByteArray(StandardCharsets.UTF_8)
        )
    }

    private fun uninject() {
        injects.forEach {
            { Files.deleteIfExists(it) }.runCatching {
                this()
            }
        }

        updateIconSys()
    }

    fun setIcon(icons: Array<InputSupplier<InputStream>>) {
        val result = {
            val rawIcons = icons.map {
                ByteBuffer.wrap(it.get().readAllBytes())
            }.toTypedArray()

            setIcon(rawIcons)
        }.runCatching {
            this()
        }

        if (result.isFailure) {
            throw result.exceptionOrNull()!!
        }
    }

    private fun setIcon(icons: Array<ByteBuffer>): Int {
        icons.forEach { icon ->
            val result = {
                val pixels = mutableListOf<Int>()
                for (i in 0 until icon.remaining() / 4) {
                    pixels.add(Integer.rotateRight(icon.getInt(), 8))
                }
                val size = sqrt(pixels.size.toDouble()).toInt()
                val image = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
                image.setRGB(0, 0, size, size, pixels.toIntArray(), 0, size)
                val target: Path = getIconFileLoc(image.width, image.height)
                val outputStream = ByteArrayOutputStream()
                ImageIO.write(image, "png", outputStream)

                injectFile(target, outputStream.toByteArray())
            }.runCatching {
                this()
            }

            if (result.isFailure) {
                return 1
            }
        }

        updateIconSys()

        return 0
    }

    private fun injectFile(target: Path, data: ByteArray) {
        val result = {
            Files.createDirectories(target.parent)
            Files.write(target, data)
            injects.add(target)
        }.runCatching {
            this()
        }

        if (result.isFailure) {
            WayGL.LOGGER.error("Failed to inject file: $target")
            WayGL.LOGGER.error(result.exceptionOrNull().toString())
        }
    }

    private fun getIconFileLoc(width: Int, height: Int) =
        XDG.getUserDataLocation()
            .resolve("icons/hicolor")
            .resolve("${width}x$height")
            .resolve("apps")
            .resolve(ICON_NAME)

    private fun getDesktopFileLoc() =
        XDG.getUserDataLocation()
            .resolve("applications")
            .resolve(FILE_NAME)

    private fun updateIconSys() {
        ProcessBuilder("xdg-icon-resource", "forceupdate").runCatching {
            start()
        }
    }

    private object XDG {
        private fun getHome(): Path {
            val home = System.getenv().getOrDefault("\$HOME", System.getProperty("user.home"))
            check(!(home == null || home.isEmpty())) { "could not resolve user home" }
            return Paths.get(home)
        }

        fun getUserDataLocation(): Path {
            val xdgDataHome = System.getenv("\$XDG_DATA_HOME")
            if (xdgDataHome == null || xdgDataHome.isEmpty()) {
                return getHome().resolve(".local/share/")
            }
            return Paths.get(xdgDataHome)
        }
    }
}