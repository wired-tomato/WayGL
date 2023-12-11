package net.wiredtomato.waygl.mixin;

import com.mojang.blaze3d.glfw.Window;
import net.minecraft.client.util.WindowIcons;
import net.minecraft.resource.pack.ResourcePack;
import net.wiredtomato.waygl.WayGL;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public abstract class WindowMixin {
    @Inject(method = "setIcon", at = @At("HEAD"), cancellable = true)
    public void setIcon(ResourcePack pack, WindowIcons icon, CallbackInfo ci) {
        if (WayGL.useWayland()) ci.cancel();
    }
}
