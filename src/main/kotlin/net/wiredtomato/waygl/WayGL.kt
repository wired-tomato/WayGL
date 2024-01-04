package net.wiredtomato.waygl

import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("unused")
object WayGL {
    const val MODID = "waygl"

    @JvmField
    val LOGGER: Logger = LoggerFactory.getLogger(WayGL::class.java)

    @JvmStatic
    val platform : Int by lazy { GLFW.glfwGetPlatform() }

    @JvmStatic
    val isWayland : Boolean by lazy { platform == GLFW.GLFW_PLATFORM_WAYLAND }

    fun clientInit() {

    }

    @JvmStatic
    fun tryUseWayland() {
        // The init hint only allows the wayland backend to be selected.
        // GLFW chooses the platform by itself.
        GLFW.glfwInitHint(GLFW.GLFW_PLATFORM, GLFW.GLFW_PLATFORM_WAYLAND)
    }

    @JvmStatic
    fun useWayland(): Boolean {
        // If GLFW chose wayland as the platform we can safely assume that we run on wayland.
        // Note that this function may only be called *after* glfwInit has been called.
        return isWayland
    }

    fun id(path: String) = Identifier(MODID, path)
}
