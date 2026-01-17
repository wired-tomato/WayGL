package net.wiredtomato.waygl.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.wiredtomato.waygl.core.Loader;
import org.lwjgl.glfw.GLFW;
import org.objectweb.asm.Opcodes;
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

    @Inject(method = { "grabMouse", "releaseMouse" }, at = @At(value = "FIELD", target = "Lnet/minecraft/client/MouseHandler;mouseGrabbed:Z", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void onLockCursor(CallbackInfo ci) {
        if (Loader.useWayland()) {
            var windowHandle = minecraft.getWindow().handle();
            if (isMouseGrabbed()) {
                GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
            } else {
                GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
            }
        }
    }

    @WrapOperation(method = "grabMouse", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/InputConstants;grabOrReleaseMouse(Lcom/mojang/blaze3d/platform/Window;IDD)V"))
    private void cancelCursorSetCursorPosition(Window window, int inputMode, double mouseX, double mouseY, Operation<Void> original) {
        if (!Loader.useWayland()) {
            original.call(window, inputMode, mouseX, mouseY);
        }
    }
}
