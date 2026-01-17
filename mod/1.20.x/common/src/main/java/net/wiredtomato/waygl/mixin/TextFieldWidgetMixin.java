package net.wiredtomato.waygl.mixin;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.wiredtomato.waygl.core.Loader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EditBox.class)
public class TextFieldWidgetMixin {
    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void charTyped(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (Loader.isWayland() && isSpecialChar(chr) && Screen.hasControlDown()) cir.setReturnValue(false);
    }

    @Unique
    private boolean isSpecialChar(char chr) {
        return chr == 'a' || chr == 'v' || chr == 'c' || chr == 'x';
    }
}
