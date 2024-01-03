package net.wiredtomato.waygl.mixin;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static org.lwjgl.system.MemoryStack.stackGet;

@Mixin(GLFW.class)
public abstract class GLFWMixin {
    @Shadow(remap = false)
    public static long nglfwCreateWindow(int width, int height, long title, long monitor, long share) {
        return 0;
    }

    /**
     * @author WiredTomato
     * @reason Fix Nvidia EGLDisplay issues.
     */
    @Overwrite(remap = false)
    @NativeType("GLFWwindow *")
    public static long glfwCreateWindow(int width, int height, @NativeType("char const *") CharSequence title, @NativeType("GLFWmonitor *") long monitor, @NativeType("GLFWwindow *") long share) {
        MemoryStack stack = stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(title, true);
            long titleEncoded = stack.getPointerAddress();
            return nglfwCreateWindow(width, height, titleEncoded, MemoryUtil.NULL, share);
        } finally {
            stack.setPointer(stackPointer);
        }
    }
}
