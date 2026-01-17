package net.wiredtomato.waygl.core;

import net.wiredtomato.waygl.core.os.OS;
import net.wiredtomato.waygl.core.service.PlatformService;
import net.wiredtomato.waygl.core.util.GraphicsAdapterProbe;
import net.wiredtomato.waygl.core.util.GraphicsAdapterProbe.GraphicsAdapterVendor;
import net.wiredtomato.waygl.core.workaround.nvidia.NvidiaWorkaround;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Loader {
    public static final Logger LOGGER = LoggerFactory.getLogger("WayGL/Loader");

    private static Boolean useWayland;

    public static void load(String nativeGLFWPath) {
        if (nativeGLFWPath != null) {
            Configuration.GLFW_LIBRARY_NAME.set(nativeGLFWPath);
        }

        if (applyNvidiaWorkaround()) {
            NvidiaWorkaround.apply();
        }
    }

    public static void tryUseWayland() {
        if (useWayland()) {
            GLFW.glfwInitHint(GLFW.GLFW_PLATFORM, GLFW.GLFW_PLATFORM_WAYLAND);
        }
    }

    private static boolean applyNvidiaWorkaround() {
        return OS.current() == OS.LINUX && useWayland() &&
                GraphicsAdapterProbe.findLinuxAdapters().stream().anyMatch((adapter) -> adapter.vendor() == GraphicsAdapterVendor.NVIDIA) &&
                !(PlatformService.IMPL.isModLoaded("sodium") || PlatformService.IMPL.isModLoaded("embeddium"));
    }

    public static Boolean useWayland() {
        if (useWayland == null) {
            var sessionType = System.getenv("XDG_SESSION_TYPE");
            if (sessionType == null) sessionType = "";

            useWayland = GLFW.glfwPlatformSupported(GLFW.GLFW_PLATFORM_WAYLAND) && sessionType.toLowerCase().startsWith("wayland");
        }


        return useWayland;
    }

    public static Boolean isWayland() {
        return platform() == GLFW.GLFW_PLATFORM_WAYLAND;
    }

    public static int platform() {
        return GLFW.glfwGetPlatform();
    }
}
