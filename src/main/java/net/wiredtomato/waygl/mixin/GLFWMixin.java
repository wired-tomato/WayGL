package net.wiredtomato.waygl.mixin;

import net.wiredtomato.waygl.WayGL;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.NativeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static org.lwjgl.system.Checks.remainingSafe;
import static org.lwjgl.system.MemoryUtil.memAddressSafe;

@Mixin(GLFW.class)
public abstract class GLFWMixin {
    @Shadow(remap = false)
    public static void nglfwSetWindowIcon(long window, int count, long images) {}

    /**
     * @author wired-tomato
     * @reason wayland does not support icon setting
     */
    @Overwrite(remap = false)
    public static void glfwSetWindowIcon(@NativeType("GLFWwindow *") long window, @NativeType("GLFWimage const *") GLFWImage.Buffer images) {
        if (!WayGL.useWayland()) nglfwSetWindowIcon(window, remainingSafe(images), memAddressSafe(images));
    }
}
