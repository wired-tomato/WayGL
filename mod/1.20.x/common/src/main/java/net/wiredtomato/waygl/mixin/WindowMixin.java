package net.wiredtomato.waygl.mixin;

import com.mojang.blaze3d.platform.*;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import net.wiredtomato.waygl.WayGL;
import net.wiredtomato.waygl.core.IconInjector;
import net.wiredtomato.waygl.core.Loader;
import net.wiredtomato.waygl.core.service.PlatformService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

@Mixin(Window.class)
public abstract class WindowMixin {
	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwDefaultWindowHints()V", shift = At.Shift.AFTER, remap = false))
	private void addWindowHints(WindowEventHandler windowEventHandler, ScreenManager monitorTracker, DisplayData windowSettings, String string, String string2, CallbackInfo ci) {
		if (Loader.isWayland()) {
			glfwWindowHint(GLFW_FOCUS_ON_SHOW, GLFW_FALSE);
			IconInjector.inject(PlatformService.IMPL.getMinecraftVersionString());
			glfwWindowHintString(GLFW_WAYLAND_APP_ID, IconInjector.APP_ID);
		}
	}

	@Inject(method = "setIcon", at = @At("HEAD"), cancellable = true)
	private void setIcon(PackResources resourcePack, IconSet icons, CallbackInfo ci) {
		if (Loader.isWayland()) {
			List<IoSupplier<InputStream>> iconStreamSuppliers = List.of();

            try {
                iconStreamSuppliers = icons.getStandardIcons(resourcePack);
            } catch (IOException e) {
                WayGL.LOGGER.error("Failed to load icons!", e);
            }

			var iconStreams = iconStreamSuppliers.stream().map((it) -> {
				try {
					return it.get();
				} catch (IOException e) {
					WayGL.LOGGER.error("Failed to load icon!", e);
					return null;
				}
			}).toList();

            IconInjector.setIcon(iconStreams);
			ci.cancel();
		}
	}
}
