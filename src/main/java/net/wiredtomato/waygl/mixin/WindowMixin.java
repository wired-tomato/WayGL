package net.wiredtomato.waygl.mixin;

import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.util.MonitorTracker;
import net.minecraft.client.util.Window;
import net.minecraft.resource.InputSupplier;
import net.wiredtomato.waygl.IconInjector;
import net.wiredtomato.waygl.WayGL;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;

import static org.lwjgl.glfw.GLFW.*;

@Mixin(Window.class)
public abstract class WindowMixin {
	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwCreateWindow(IILjava/lang/CharSequence;JJ)J"))
	private void waygl$addWindowHints(WindowEventHandler windowEventHandler, MonitorTracker monitorTracker, WindowSettings windowSettings, String string, String string2, CallbackInfo ci) {
		if (WayGL.useWayland()) {
			glfwWindowHint(GLFW_FOCUS_ON_SHOW, GLFW_FALSE);
			IconInjector.INSTANCE.inject();
			glfwWindowHintString(GLFW_WAYLAND_APP_ID, IconInjector.APP_ID);
		}
	}

	@SuppressWarnings("unchecked")
	@Inject(method = "setIcon", at = @At("HEAD"), cancellable = true)
	private void waygl$setIcon(InputSupplier<InputStream> inputSupplier, InputSupplier<InputStream> inputSupplier2, CallbackInfo ci) {
		if (WayGL.useWayland()) {
			IconInjector.INSTANCE.setIcon(new InputSupplier[] { inputSupplier, inputSupplier2 });
			ci.cancel();
		}
	}
}
