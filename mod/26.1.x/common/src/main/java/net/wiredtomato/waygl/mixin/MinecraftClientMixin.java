package net.wiredtomato.waygl.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.BackendOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.util.TimeSource;
import net.wiredtomato.waygl.core.Loader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;initBackendSystem(Lcom/mojang/blaze3d/platform/BackendOptions;)Lnet/minecraft/util/TimeSource$NanoTimeSource;"))
    private TimeSource.NanoTimeSource wrapGLFWInit(BackendOptions options, Operation<TimeSource.NanoTimeSource> original) {
        //New GLFW version should default to Wayland
        //Set init hint anyways, just in case sorta thing
        Loader.tryUseWayland();
        return original.call(options);
    }
}
