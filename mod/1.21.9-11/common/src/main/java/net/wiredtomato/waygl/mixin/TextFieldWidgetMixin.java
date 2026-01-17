package net.wiredtomato.waygl.mixin;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.wiredtomato.waygl.core.Loader;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EditBox.class)
public class TextFieldWidgetMixin {
    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void charTyped(CharacterEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (Loader.useWayland() && wayGL$isSpecialChar(event.codepointAsString().charAt(0)) && ((event.modifiers() & GLFW.GLFW_MOD_CONTROL) != 0)) cir.setReturnValue(false);
    }

    @Unique
    private boolean wayGL$isSpecialChar(char chr) {
        return chr == 'a' || chr == 'v' || chr == 'c' || chr == 'x';
    }
}
