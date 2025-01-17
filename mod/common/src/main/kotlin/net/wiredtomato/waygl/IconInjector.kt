package net.wiredtomato.waygl

import com.mojang.blaze3d.platform.IconSet
import net.minecraft.server.packs.PackResources
import net.wiredtomato.waygl.service.PlatformServiceImpl
import java.awt.image.BufferedImage
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO

/**
 * Port of [moehreag/wayland_fixes](https://github.com/moehreag/wayland-fixes) DesktopFileInjector
 */
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

        val version = PlatformServiceImpl.getMinecraftVersion()

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

    fun setIcon(resourcePack: PackResources, icons: IconSet) {
        val result = {
            val suppliers = icons.getStandardIcons(resourcePack)

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
}
