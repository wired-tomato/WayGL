package net.wiredtomato.waygl.mixin;

import net.minecraft.SharedConstants;
import net.wiredtomato.waygl.core.Loader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SharedConstants.class)
public class SharedConstantsMixin {
    @Inject(method = "debugFlag", at = @At("HEAD"), cancellable = true)
    private static void preferWayland(String name, CallbackInfoReturnable<Boolean> cir) {
        if (Loader.useWayland() && name.equals("PREFER_WAYLAND")) cir.setReturnValue(true);
    }
}
