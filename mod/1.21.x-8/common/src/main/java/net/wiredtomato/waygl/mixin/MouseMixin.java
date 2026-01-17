package net.wiredtomato.waygl.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.wiredtomato.waygl.WayGL;
import net.wiredtomato.waygl.core.Loader;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseMixin {

    @Shadow public abstract boolean isMouseGrabbed();

    @Shadow @Final private Minecraft minecraft;

    @Inject(method = { "grabMouse", "releaseMouse" }, at = @At(value = "FIELD", target = "Lnet/minecraft/client/MouseHandler;mouseGrabbed:Z", ordinal = 1, shift = At.Shift.AFTER))
    private void onLockCursor(CallbackInfo ci) {
        if (Loader.useWayland()) {
            var windowHandle = minecraft.getWindow().getWindow();
            if (isMouseGrabbed()) {
                GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
            } else {
                GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
            }
        }
    }

    @WrapOperation(method = { "grabMouse", "releaseMouse" }, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/InputConstants;grabOrReleaseMouse(JIDD)V"))
    private void cancelCursorSetCursorPosition(long handler, int inputModeValue, double x, double y, Operation<Void> original) {
        if (!Loader.useWayland()) {
            original.call(handler, inputModeValue, x, y);
        }
    }
}
