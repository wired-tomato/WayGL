package net.wiredtomato.waygl

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.Identifier
import net.wiredtomato.waygl.config.Config
import net.wiredtomato.waygl.os.OS
import net.wiredtomato.waygl.os.OSUtils
import net.wiredtomato.waygl.workaround.NvidiaWorkaround
import net.wiredtomato.waygl.workaround.env.GraphicsAdapterProbe
import net.wiredtomato.waygl.workaround.env.GraphicsAdapterVendor
import net.wiredtomato.waygl.workaround.env.LinuxAdapterInfo
import org.lwjgl.glfw.GLFW
import org.lwjgl.system.Configuration
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("unused")
object WayGL {
    const val MODID = "waygl"

    @JvmField
    val LOGGER: Logger = LoggerFactory.getLogger(WayGL::class.java)

    @JvmStatic
    val platform: Int by lazy { GLFW.glfwGetPlatform() }

    @JvmStatic
    val useWayland: Boolean by lazy { GLFW.glfwPlatformSupported(GLFW.GLFW_PLATFORM_WAYLAND) }

    @JvmStatic
    val isWayland: Boolean by lazy { platform == GLFW.GLFW_PLATFORM_WAYLAND }

    fun clientInit() {
        Config.HANDLER.load()

        if (Config.useNativeGlfw) {
            Configuration.GLFW_LIBRARY_NAME.set(Config.nativeGlfwPath)
        }

        if (applyNvidiaWorkaround(OSUtils.os(), GraphicsAdapterProbe.findLinuxAdapters())) {
            NvidiaWorkaround.apply()
        }
    }

    private fun applyNvidiaWorkaround(os: OS, adapters: List<LinuxAdapterInfo>): Boolean {
        return os == OS.LINUX && adapters.any { it.vendor == GraphicsAdapterVendor.NVIDIA } && !FabricLoader.getInstance().isModLoaded("sodium")
    }

    @JvmStatic
    fun tryUseWayland() {
        // The init hint only allows the wayland backend to be selected.
        // GLFW chooses the platform by itself.
        if (useWayland)
            GLFW.glfwInitHint(GLFW.GLFW_PLATFORM, GLFW.GLFW_PLATFORM_WAYLAND)
    }

    @JvmStatic
    fun useWayland(): Boolean {
        // If GLFW chose wayland as the platform we can safely assume that we run on wayland.
        // Note that this function may only be called *after* glfwInit has been called.
        return isWayland
    }

    fun id(path: String) = Identifier.of(MODID, path)
}
