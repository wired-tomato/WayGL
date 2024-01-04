package net.wiredtomato.waygl

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resource.InputSupplier
import java.awt.image.BufferedImage
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.imageio.ImageIO


object IconInjector {
    const val APP_ID = "com.mojang.minecraft"
    private const val ICON_NAME = "minecraft.png"
    private const val FILE_NAME = "$APP_ID.desktop"
    private const val LOCATION = "/assets/${WayGL.MODID}/$FILE_NAME"
    private val injects = mutableListOf<Path>()

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

    fun setIcon(suppliers: Array<InputSupplier<InputStream>>) {
        val result = {
            suppliers.forEach {
                val image: BufferedImage = ImageIO.read(it.get())
                val target: Path = getIconFileLoc(
                    image.width,
                    image.height
                )
                injectFile(target, it.get().readAllBytes())
            }
        }.runCatching {
            this()
        }

        if (result.isFailure) {
            throw result.exceptionOrNull()!!
        }
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