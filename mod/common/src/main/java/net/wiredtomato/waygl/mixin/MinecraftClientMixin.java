package net.wiredtomato.waygl.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.wiredtomato.waygl.WayGL;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;initBackendSystem()Lnet/minecraft/util/TimeSource$NanoTimeSource;", unsafe = true))
    private void preGLFWInit(GameConfig gameConfig, CallbackInfo ci) {
        WayGL.tryUseWayland();
    }
}
