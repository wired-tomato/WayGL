package net.wiredtomato.waygl.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * From [moehreag/wayland_fixes](https://github.com/moehreag/wayland-fixes) XDGPathResolver
 */
public final class XDGUtils {
    private XDGUtils() {}

    private static Path getHome() {
        var home = System.getenv().getOrDefault("HOME", System.getProperty("user.home"));
        if (home == null || home.isEmpty()) {
            throw new IllegalStateException("Could not resolve user home");
        }

        return Paths.get(home);
    }

    public static Path getUserDataLocation() {
        var xdgDataHome = System.getenv("XDG_DATA_HOME");
        if (xdgDataHome == null || xdgDataHome.isEmpty()) {
            return getHome().resolve(".local/share");
        }

        return Paths.get(xdgDataHome);
    }

    public static List<Path> getIconThemeLocations() {
        var userShare = getUserDataLocation().resolve("icons");
        var homeIcons = getHome().resolve(".icons");
        var systemIcons = Paths.get("/usr/share/icons");

        return List.of(userShare, homeIcons, systemIcons);
    }
}
