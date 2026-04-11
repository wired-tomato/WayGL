package net.wiredtomato.waygl.mixin;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.input.CharacterEvent;
import net.wiredtomato.waygl.core.Loader;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EditBox.class, MultiLineEditBox.class})
public class EditBoxMixin {
    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void charTyped(CharacterEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (Loader.useWayland() && ((event.modifiers() & GLFW.GLFW_MOD_CONTROL) != 0)) cir.setReturnValue(false);
    }
}
