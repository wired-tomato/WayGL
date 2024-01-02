package net.wiredtomato.waygl.mixin;

import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.util.*;
import net.minecraft.resource.ResourcePack;
import net.wiredtomato.waygl.IconInjector;
import net.wiredtomato.waygl.WayGL;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.glfw.GLFW.*;

@Mixin(Window.class)
public abstract class WindowMixin {
    @Shadow @Final private long handle;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwCreateWindow(IILjava/lang/CharSequence;JJ)J"))
    public void waygl$addWindowHints(WindowEventHandler windowEventHandler, MonitorTracker monitorTracker, WindowSettings windowSettings, String string, String string2, CallbackInfo ci) {
        if (WayGL.useWayland()) {
            glfwWindowHint(GLFW_FOCUS_ON_SHOW, GLFW_FALSE);
            IconInjector.INSTANCE.inject();
            glfwWindowHintString(GLFW_WAYLAND_APP_ID, IconInjector.INSTANCE.getAPP_ID());
        }
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwSetCursorEnterCallback(JLorg/lwjgl/glfw/GLFWCursorEnterCallbackI;)Lorg/lwjgl/glfw/GLFWCursorEnterCallback;"))
    public void waygl$setIcon(WindowEventHandler windowEventHandler, MonitorTracker monitorTracker, WindowSettings windowSettings, String string, String string2, CallbackInfo ci) {
        if (WayGL.useWayland()) {
            glfwShowWindow(handle);
            IconInjector.INSTANCE.setIcon(MinecraftClient.getInstance().getDefaultResourcePack(), SharedConstants.getGameVersion().isStable() ? Icons.RELEASE : Icons.SNAPSHOT);
        }
    }

    @Inject(method = "setIcon", at = @At("HEAD"), cancellable = true)
    public void waygl$cancelIcon(ResourcePack resourcePack, Icons icons, CallbackInfo ci) {
        if (WayGL.useWayland()) {
            ci.cancel();
        }
    }
}
