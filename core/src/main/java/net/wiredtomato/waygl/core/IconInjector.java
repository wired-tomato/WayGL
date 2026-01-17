package net.wiredtomato.waygl.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * From [moehreag/wayland_fixes](https://github.com/moehreag/wayland-fixes) DesktopFileInjector
 */
public class IconInjector {
    public static final String APP_ID = "com.mojang.minecraft";
    private static final String ICON_NAME = "minecraft.png";
    private static final String FILE_NAME = APP_ID + ".desktop";
    private static final String LOCATION = "/assets/waygl/" + FILE_NAME;
    private static final List<Path> injects = new ArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger("WayGL/IconInjector");

    public static void inject(String minecraftVersion) {
        Runtime.getRuntime().addShutdownHook(new Thread(IconInjector::uninject));

        try (var stream = IconInjector.class.getResourceAsStream(LOCATION)) {
            if (stream == null) {
                LOGGER.error("Icon resource not found: " + LOCATION);
                return;
            }

            var location = getDesktopFileLocation();

            byte[] bytes = stream.readAllBytes();
            var data = new String(bytes, StandardCharsets.UTF_8);
            var formatted = String.format(data, minecraftVersion, "minecraft");

            injectFile(location, formatted.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOGGER.error("Could not write icon file", e);
        }
    }

    public static void setIcon(Collection<InputStream> icons) {
        try {
            for (var icon : icons) {
                if (icon == null) continue;

                var image = ImageIO.read(icon);
                var target = getIconFileLocation(image.getWidth(), image.getHeight());
                injectFile(target, icon.readAllBytes());
            }
        } catch (IOException e) {
            LOGGER.error("Failed to set icon: ", e);
        }
    }

    private static void injectFile(Path target, byte[] data) {
        try {
            Files.createDirectories(target.getParent());
            Files.write(target, data);
        } catch (IOException e) {
            LOGGER.error("Failed to create file: {}", target);
            LOGGER.error(e.toString());
        }
    }

    private static void uninject() {
        injects.forEach((path) -> {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                LOGGER.error("Failed to delete file: {}", path, e);
            }
        });
    }

    private static Path getIconFileLocation(int width, int height) {
        return XDG.getUserDataLocation()
                .resolve("icons/hicolor")
                .resolve("${width}x$height")
                .resolve("apps")
                .resolve(ICON_NAME);
    }

    private static Path getDesktopFileLocation() {
        return XDG.getUserDataLocation()
                .resolve("applications")
                .resolve(FILE_NAME);
    }

    private static void updateIcons() {
        var proc = new ProcessBuilder("xdg-icon-resource", "forceupdate");

        try {
            proc.start();
        } catch (IOException e) {
            LOGGER.error("Failed to update icons with xdg-icon-resource", e);
        }
    }
}
