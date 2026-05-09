package net.wiredtomato.waygl.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
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

    @Inject(method = { "grabMouse", "releaseMouse" }, at = @At(value = "FIELD", target = "Lnet/minecraft/client/MouseHandler;mouseGrabbed:Z", opcode = 181 /* PUTFIELD  */, shift = At.Shift.AFTER), cancellable = true)
    private void onLockCursor(CallbackInfo ci) {
        if (!Loader.useWayland()) return;

        var windowHandle = minecraft.getWindow().handle();
        if (isMouseGrabbed()) {
            GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        } else {
            GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        }

        ci.cancel();
    }
}
