package net.wiredtomato.waygl.mixin;

import net.minecraft.client.util.Window;
import net.wiredtomato.waygl.WayGL;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;

@Mixin(Window.class)
public abstract class WindowMixin {
    @Inject(method = "setIcon", at = @At("HEAD"), cancellable = true)
    public void setIcon(InputStream inputStream, InputStream inputStream2, CallbackInfo ci) {
        if (WayGL.useWayland()) ci.cancel();
    }
}
