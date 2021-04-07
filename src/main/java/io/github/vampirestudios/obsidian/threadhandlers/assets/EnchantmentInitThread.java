package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.google.common.collect.ImmutableMap;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment;
import io.github.vampirestudios.obsidian.client.ClientInit;

public class EnchantmentInitThread implements Runnable {

    private final Enchantment enchantment;

    public EnchantmentInitThread(Enchantment enchantmentIn) {
        enchantment = enchantmentIn;
    }

    @Override
    public void run() {
        if (enchantment.name.translated != null) {
            enchantment.name.translated.forEach((languageId, name) -> ClientInit.translationMap.put(
                    enchantment.name.id.getNamespace(),
                    ImmutableMap.of(
                            languageId,
                            ImmutableMap.of(
                                    String.format("enchantment.%s.%s", enchantment.name.id.getNamespace(), enchantment.name.id.getPath()),
                                    name
                            )
                    )
            ));
        }
    }
}
