package io.github.vampirestudios.obsidian.threadhandlers.assets_temp;

import io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment;
import io.github.vampirestudios.obsidian.client.ClientInit;

public class EnchantmentInitThread implements Runnable {

    private final Enchantment enchantment;

    public EnchantmentInitThread(Enchantment enchantmentIn) {
        enchantment = enchantmentIn;
    }

    @Override
    public void run() {
        if (enchantment.name.translations != null) {
            enchantment.name.translations.forEach((languageId, name) -> ClientInit.addTranslation(
                    enchantment.name.id.getNamespace(), languageId,
                    "enchantment." + enchantment.name.id.getNamespace() + "." + enchantment.name.id.getPath(), name
            ));
        }
    }
}
