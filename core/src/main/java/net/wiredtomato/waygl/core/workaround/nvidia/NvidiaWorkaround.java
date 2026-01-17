package net.wiredtomato.waygl.core.workaround.nvidia;

import net.wiredtomato.waygl.core.os.Libc;
import net.wiredtomato.waygl.core.os.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NvidiaWorkaround {
    private static final Logger LOGGER = LoggerFactory.getLogger("WayGL/NvidiaWorkaround");

    public static void apply() {
        LOGGER.warn("Applying workaround: Prevent NVIDIA OpenGL driver from using broken optimization (NVIDIA_THREADED_OPTIMIZATIONS");

        try {
            if (OS.current() == OS.LINUX) {
                Libc.setenv("__GL_THREADED_OPTIMIZATIONS", "0");
            }
        } catch (Throwable t) {
            LOGGER.error("Failed to apply NVIDIA workaround", t);
            LOGGER.error("READ ME! The workaround for the NVIDIA Graphics Driver did not apply correctly!");
            LOGGER.error("READ ME! Your game is highly likely to crash at startup");
            LOGGER.error("READ ME! For more information see: https://github.com/wired-tomato/WayGL/issues/1");
        }
    }
}
