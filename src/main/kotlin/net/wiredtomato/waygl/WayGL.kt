package net.wiredtomato.waygl

import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW
import org.slf4j.LoggerFactory

@Suppress("unused")
object WayGL {
    const val MODID = "waygl"

    @JvmField
    val LOGGER = LoggerFactory.getLogger(WayGL::class.java)

    @JvmStatic
    val osString: String by lazy { System.getProperty("os.name") }
    @JvmStatic
    val osLinuxBased: Boolean by lazy { osString == "Linux" }

    @JvmStatic
    val displayServerType: String  by lazy {  System.getenv("XDG_SESSION_TYPE") }
    @JvmStatic
    val isWayland: Boolean by lazy { displayServerType == "wayland" }

    fun clientInit() {
        if (!osLinuxBased) {
            LOGGER.info("Current OS is $osString, it is not linux based, no action will be taken.")
            return
        }

        LOGGER.info(
            "Current display server is $displayServerType, ${
                if (isWayland) "forcing glfw to use wayland!" 
                else "no action will be taken."
            }"
        )
    }

    @JvmStatic
    fun tryUseWayland() {
        if (useWayland()) {
            GLFW.glfwInitHint(GLFW.GLFW_PLATFORM, GLFW.GLFW_PLATFORM_WAYLAND)
        }
    }

    @JvmStatic
    fun useWayland(): Boolean {
        return osLinuxBased && isWayland
    }

    fun id(path: String) = Identifier(MODID, path)
}
