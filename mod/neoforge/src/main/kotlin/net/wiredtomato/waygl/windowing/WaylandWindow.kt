package net.wiredtomato.waygl.windowing

import com.mojang.blaze3d.platform.DisplayData
import com.mojang.blaze3d.platform.ScreenManager
import com.mojang.blaze3d.platform.Window
import com.mojang.blaze3d.platform.WindowEventHandler
import net.neoforged.fml.loading.FMLConfig
import net.wiredtomato.waygl.GLVersion
import net.wiredtomato.waygl.NeoForgeWayGL.handleLastGLFWError
import net.wiredtomato.waygl.WayGL
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL

class WaylandWindow(
    eventHandler: WindowEventHandler,
    screenManager: ScreenManager,
    displayData: DisplayData,
    preferredFullscreenVideoMode: String?,
    title: String
) : Window(eventHandler, screenManager, displayData, preferredFullscreenVideoMode, title) {
    private val GL_VERSIONS = listOf(
        GLVersion(4, 6), GLVersion(4, 5), GLVersion(4, 4), GLVersion(4, 3),
        GLVersion(4, 2), GLVersion(4, 1), GLVersion(4, 0), GLVersion(3, 3),
        GLVersion(3, 2)
    )

    init {
        WayGL.LOGGER.warn("NeoForge wayland compatability requires the reinitialization of GLFW and recreation of the window.")

        destroyOldWindow()
        reinitializeGLFW()
        createNewWindow()
    }

    private fun destroyOldWindow() {
        glfwSetWindowShouldClose(window, true)
        WayGL.LOGGER.info("Closing window $window")
    }

    private fun reinitializeGLFW() {
        WayGL.LOGGER.info("Reinitializing GLFW")
        glfwTerminate()
        glfwInitHint(GLFW_PLATFORM, GLFW_PLATFORM_WAYLAND)
        glfwInit()
    }

    private fun createNewWindow() {
        WayGL.LOGGER.info("Creating new window with wayland backend")
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API)
        glfwWindowHint(GLFW_CONTEXT_CREATION_API, GLFW_NATIVE_CONTEXT_API)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        glfwWindowHint(GLFW_FOCUS_ON_SHOW, GLFW_FALSE)

        val toSkip = FMLConfig.getListConfigValue<String>(FMLConfig.ConfigValue.EARLY_WINDOW_SKIP_GL_VERSIONS)
        val windowWidth = FMLConfig.getIntConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_WIDTH)
        val windowHeight = FMLConfig.getIntConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_HEIGHT)

        val validGLVersions = GL_VERSIONS.iterator()

        var newWindowHandle = 0L
        while (validGLVersions.hasNext() && newWindowHandle == 0L) {
            val version = validGLVersions.next()
            val strVersion = version.toString()
            if (toSkip.contains(strVersion)) {
                WayGL.LOGGER.info("Skipping GL Version $strVersion")
            }

            WayGL.LOGGER.info("Attempting to use GL Version $strVersion")
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, version.major)
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, version.minor)
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)
            newWindowHandle = glfwCreateWindow(windowWidth, windowHeight, "Minecraft (WayGL)", 0L, 0L)
            handleLastGLFWError { errId, desc ->
                WayGL.LOGGER.trace("Error while creating the GLFW window with GL Version: $strVersion: $errId. $desc")
            }
        }

        WayGL.LOGGER.info("Successfully created wayland window")
        glfwMakeContextCurrent(newWindowHandle)
        WayGL.LOGGER.info("Creating GL capabilities")
        GL.createCapabilities()

        glfwShowWindow(newWindowHandle)

        while (!glfwWindowShouldClose(newWindowHandle)) {

        }

        WayGL.LOGGER.info("Setting callbacks")
        glfwSetFramebufferSizeCallback(newWindowHandle, ::onFramebufferResize)
        glfwSetWindowPosCallback(newWindowHandle, ::onMove)
        glfwSetWindowSizeCallback(newWindowHandle, ::onResize)
        glfwSetWindowFocusCallback(newWindowHandle, ::onFocus)
        glfwSetCursorEnterCallback(newWindowHandle, ::onEnter)
        glfwSetWindowIconifyCallback(newWindowHandle, ::onIconify)
        WayGL.LOGGER.info("Finished setting callbacks")

        window = newWindowHandle
        WayGL.LOGGER.info("Passed window to MC")
    }
}