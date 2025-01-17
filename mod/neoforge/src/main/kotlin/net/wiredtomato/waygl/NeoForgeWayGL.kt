package net.wiredtomato.waygl

import com.mojang.blaze3d.platform.DisplayData
import com.mojang.blaze3d.platform.ScreenManager
import com.mojang.blaze3d.platform.Window
import net.minecraft.client.Minecraft
import net.neoforged.fml.common.Mod
import net.neoforged.fml.loading.FMLConfig
import net.wiredtomato.waygl.windowing.WaylandWindow
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Mod(WayGL.MODID)
object NeoForgeWayGL {
    fun createWindow(
        minecraft: Minecraft,
        manager: ScreenManager,
        displayData: DisplayData,
        videoModeName: String?,
        title: String,
    ): Window {
        return if (glfwPlatformSupported(GLFW_PLATFORM_WAYLAND)) {
            WaylandWindow(minecraft, manager, displayData, videoModeName, title)
        } else Window(minecraft, manager, displayData, videoModeName, title)
    }

    @OptIn(ExperimentalContracts::class)
    internal fun handleLastGLFWError(handler: (Int, String) -> Unit) {
        contract {
            callsInPlace(handler, InvocationKind.AT_MOST_ONCE)
        }

        MemoryStack.stackPush().use { stack ->
            val buffer = stack.mallocPointer(1)
            val error = glfwGetError(buffer)
            if (error != GLFW_NO_ERROR) {
                val pDescription = buffer.get()
                val description = if (pDescription == 0L) "" else MemoryUtil.memUTF8(pDescription)
                handler(error, description)
            }
        }
    }
}