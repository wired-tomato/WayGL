package net.wiredtomato.waygl

import org.apache.commons.io.IOUtils
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


/**
 * Port of [moehreag/wayland_fixes](https://github.com/moehreag/wayland-fixes) XDGPathResolver
 */
object XDG {
    private fun getHome(): Path {
        val home = System.getenv().getOrDefault("HOME", System.getProperty("user.home"))
        check(!(home == null || home.isEmpty())) { "could not resolve user home" }
        return Paths.get(home)
    }

    fun getUserDataLocation(): Path {
        val xdgDataHome = System.getenv("XDG_DATA_HOME")
        if (xdgDataHome == null || xdgDataHome.isEmpty()) {
            return getHome().resolve(".local/share/")
        }
        return Paths.get(xdgDataHome)
    }

    fun getIconThemeLocations(): List<Path> {
        val userShare = getUserDataLocation().resolve("icons")
        val homeIcons = getHome().resolve(".icons")
        val systemIcons = Paths.get("/usr/share/icons")
        return listOf(userShare, homeIcons, systemIcons)
    }

    fun getIconTheme(icon: String): Path? {
        var themeName: String

        val builder = ProcessBuilder("gsettings", "get", "org.gnome.desktop.interface", "cursor-theme")

        try {
            val p = builder.start()
            themeName = IOUtils.toString(p.inputStream, StandardCharsets.UTF_8).split("'")[1]
            p.waitFor()
        } catch (e: IOException) {
            themeName = "default"
        } catch (e: InterruptedException) {
            themeName = "default"
        }

        return findInThemes(themeName, icon)
    }

    private fun findInThemes(themeName: String, icon: String): Path? {
        val themePath = getThemePath(themeName)
        val iconPath = themePath.resolve(icon)
        if (Files.exists(iconPath)) {
            return iconPath
        }

        val themeIndex = themePath.resolve("index.theme")
        if (Files.exists(themeIndex)) {
            try {
                val lines: List<String> = Files.readAllLines(themeIndex, StandardCharsets.UTF_8)

                var iconThemeFound = false
                for (s in lines) {
                    if ("[Icon Theme]" == s) {
                        iconThemeFound = true
                    }

                    if (iconThemeFound && !s.startsWith("#")) {
                        val parts = s.split("=".toRegex(), limit = 2).toTypedArray()
                        if ("Inherits" == parts[0]) {
                            return findInThemes(parts[1], icon)
                        }
                    }
                }
            } catch (ignored: IOException) {
            }
        }
        return null
    }

    private fun getThemePath(name: String): Path {
        for (p in getIconThemeLocations()) {
            val theme = p.resolve(name)
            if (Files.exists(theme)) {
                return theme
            }
        }
        if ("default" == name) {
            return Paths.get("usr", "share", "icons", "default")
        }
        return getThemePath("default")
    }

    fun getCursorSize(): Int {
        var size: Int
        val builder = ProcessBuilder("gsettings", "get", "org.gnome.desktop.interface", "cursor-size")

        try {
            val p = builder.start()
            size = IOUtils.toString(p.inputStream, StandardCharsets.UTF_8).split("\n")[0].toInt()
            p.waitFor()
        } catch (e: IOException) {
            size = 24
        } catch (e: InterruptedException) {
            size = 24
        }

        return size
    }
}