package net.wiredtomato.waygl.neoforge.mixin;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.VirtualScreen;
import net.wiredtomato.waygl.NeoForgeWayGL;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VirtualScreen.class)
public class VirtualScreenMixin {
    @Shadow @Final private Minecraft minecraft;

    @Shadow @Final private ScreenManager screenManager;

    @Inject(method = "newWindow", at = @At("HEAD"), cancellable = true)
    private void newWindow(DisplayData screenSize, String videoModeName, String title, CallbackInfoReturnable<Window> cir) {
        cir.setReturnValue(NeoForgeWayGL.INSTANCE.createWindow(minecraft, screenManager, screenSize, videoModeName, title));
    }
}
