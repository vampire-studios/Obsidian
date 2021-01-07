package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.client.ShaderPackScreenButtonOption;
import net.minecraft.client.gui.screen.VideoOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.options.Option;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(VideoOptionsScreen.class)
public class MixinVideoOptionsScreen {

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonListWidget;addAll([Lnet/minecraft/client/options/Option;)V"))
    public void addShaderPackOptionsButton(ButtonListWidget inst, Option[] old) {
        AccessorScreen screen = (AccessorScreen)this;
        Option[] options = new Option[old.length + 1];
        System.arraycopy(old, 0, options, 0, old.length);
        options[options.length - 1] = new ShaderPackScreenButtonOption((VideoOptionsScreen)(Object)this, screen.getClient());
        inst.addAll(options);
    }
}