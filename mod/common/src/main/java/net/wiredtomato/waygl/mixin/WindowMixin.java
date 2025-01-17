package net.wiredtomato.waygl.mixin;

import com.mojang.blaze3d.platform.*;
import net.minecraft.server.packs.PackResources;
import net.wiredtomato.waygl.IconInjector;
import net.wiredtomato.waygl.WayGL;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.glfw.GLFW.*;

@Mixin(Window.class)
public abstract class WindowMixin {
	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwDefaultWindowHints()V", shift = At.Shift.AFTER, remap = false, unsafe = true))
	private void addWindowHints(WindowEventHandler eventHandler, ScreenManager screenManager, DisplayData displayData, String preferredFullscreenVideoMode, String title, CallbackInfo ci) {
		if (WayGL.useWayland()) {
			glfwWindowHint(GLFW_FOCUS_ON_SHOW, GLFW_FALSE);
			IconInjector.INSTANCE.inject();
			glfwWindowHintString(GLFW_WAYLAND_APP_ID, IconInjector.APP_ID);
		}
	}

	@Inject(method = "setIcon", at = @At("HEAD"), cancellable = true)
	private void setIcon(PackResources packResources, IconSet iconSet, CallbackInfo ci) {
		if (WayGL.useWayland()) {
			IconInjector.INSTANCE.setIcon(packResources, iconSet);
			ci.cancel();
		}
	}
}
