package net.wiredtomato.waygl.mixin;

import com.mojang.blaze3d.platform.GLX;
import net.wiredtomato.waygl.WayGL;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.LongSupplier;

@Mixin(GLX.class)
public abstract class GLXMixin {
    @Inject(method = "_initGlfw", at = @At("HEAD"), remap = false)
    private static void init(CallbackInfoReturnable<LongSupplier> cir) {
        WayGL.tryUseWayland();
    }
}
