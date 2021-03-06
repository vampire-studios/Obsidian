package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.FileResourcePackProvider;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.Objects;

@Mixin(OptionsScreen.class)
public class OptionsScreenMixin extends Screen {
    protected OptionsScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    protected void init(CallbackInfo ci) {
        this.addDrawable(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 140, 200, 20, new LiteralText("Obsidian Packs"), (buttonWidget) -> {
            this.client.openScreen(new PackScreen(this, new ResourcePackManager(ResourceType.CLIENT_RESOURCES, new FileResourcePackProvider(ObsidianAddonLoader.OBSIDIAN_ADDON_DIRECTORY, text -> new LiteralText("Addon made by Obsidian"))), this::idk, ObsidianAddonLoader.OBSIDIAN_ADDON_DIRECTORY, new TranslatableText("addonPacks.title")));
        }));
    }

    private void idk(ResourcePackManager resourcePackManager) {
        for (File file : Objects.requireNonNull(ObsidianAddonLoader.OBSIDIAN_ADDON_DIRECTORY.listFiles())) {
            ObsidianAddonLoader.register(file, "addon.info.pack");
        }
    }

}