package net.wiredtomato.waygl.mixin;

import net.minecraft.client.util.Icons;
import net.minecraft.client.util.Window;
import net.minecraft.resource.ResourcePack;
import net.wiredtomato.waygl.WayGL;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public abstract class WindowMixin {
    @Inject(method = "setIcon", at = @At("HEAD"), cancellable = true)
    public void setIcon(ResourcePack resourcePack, Icons icons, CallbackInfo ci) {
        if (WayGL.useWayland()) ci.cancel();
    }
}
