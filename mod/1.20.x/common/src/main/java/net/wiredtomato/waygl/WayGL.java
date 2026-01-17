package net.wiredtomato.waygl;

import net.wiredtomato.waygl.config.ConfigManager;
import net.wiredtomato.waygl.core.Loader;
import net.wiredtomato.waygl.core.os.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WayGL {
    public static final Logger LOGGER = LoggerFactory.getLogger("WayGL");
    public static final String MOD_ID = "waygl";

    public static void init() {
        if (OS.current() != OS.LINUX) return;

        ConfigManager.load();

        String nativeGLFWPath = null;
        if (ConfigManager.CONFIG.useNativeGlfw) {
            nativeGLFWPath = ConfigManager.CONFIG.nativeGlfwPath;
        }

        Loader.load(nativeGLFWPath);
    }
}
