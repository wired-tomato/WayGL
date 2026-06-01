package net.wiredtomato.waygl.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
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
public final class IconInjector {
    private static final Logger LOGGER = LoggerFactory.getLogger("WayGL/IconInjector");

    private static final IconInjector INSTANCE = new IconInjector();

    public static final String APP_ID = "com.mojang.minecraft";

    private static final String ICON_FILE_NAME = "minecraft.png";
    private static final String DESKTOP_FILE_NAME = APP_ID + ".desktop";
    private static final String DESKTOP_FILE_RESOURCE = "/assets/waygl/" + DESKTOP_FILE_NAME;

    private final List<Path> injected = new ArrayList<>();

    private IconInjector() {}

    public void inject(String minecraftVersion) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            uninjectFiles();

            updateIcons();
        }));

        try (var stream = IconInjector.class.getResourceAsStream(DESKTOP_FILE_RESOURCE)) {
            if (stream == null) {
                LOGGER.error("Icon resource not found: " + DESKTOP_FILE_RESOURCE);
                return;
            }

            var location = getDesktopFileLocation();

            var data = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            var formatted = String.format(data, minecraftVersion, "minecraft");

            injectFile(location, formatted.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOGGER.error("Could not write icon file", e);
        }
    }

    public void setIcon(Collection<InputStream> icons) {
        try {
            for (var icon : icons) {
                if (icon == null) continue;

                var iconData = icon.readAllBytes();

                var image = ImageIO.read(new ByteArrayInputStream(iconData));
                var target = getIconFileLocation(image.getWidth(), image.getHeight());

                injectFile(target, iconData);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to set icon: ", e);
            return;
        }

        updateIcons();
    }

    private void injectFile(Path target, byte[] data) {
        try {
            Files.createDirectories(target.getParent());
            Files.write(target, data);
            injected.add(target);
        } catch (IOException e) {
            LOGGER.error("Failed to create file: {}", target);
            LOGGER.error(e.toString());
        }
    }

    private void uninjectFiles() {
        for (var path : injected) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                LOGGER.error("Failed to delete file: {}", path, e);
            }
        }
    }

    public static IconInjector getInstance() {
        return INSTANCE;
    }

    private static Path getIconFileLocation(int width, int height) {
        return XDGUtils.getUserDataLocation()
                .resolve("icons/hicolor")
                .resolve(width + "x" + height)
                .resolve("apps")
                .resolve(ICON_FILE_NAME);
    }

    private static Path getDesktopFileLocation() {
        return XDGUtils.getUserDataLocation()
                .resolve("applications")
                .resolve(DESKTOP_FILE_NAME);
    }

    private static void updateIcons() {

        try {
            String[] xdgIconResourceCmdLine = Loader.isFlatpak() ?
                    new String[] { "flatpak-spawn", "--host", "xdg-icon-resource", "forceupdate" } : new String[] { "xdg-icon-resource", "forceupdate" };

            var xdgUpdateTask = new ProcessBuilder(xdgIconResourceCmdLine);
            xdgUpdateTask.start();
        } catch (IOException e) {
            LOGGER.warn("Failed to update icon theme", e);
        }
    }
}
