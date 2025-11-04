package io.github.vampirestudios.obsidian.api.nexo;

import java.util.List;
import java.util.Random;

public class RandomEffectSelector {
    private final List<Runnable> possibleEffects;
    private static final Random random = new Random();

    public RandomEffectSelector(List<Runnable> possibleEffects) {
        this.possibleEffects = possibleEffects;
    }

    public void applyRandomEffect() {
        if (!possibleEffects.isEmpty()) {
            int index = random.nextInt(possibleEffects.size());
            Runnable effect = possibleEffects.get(index);
            effect.run();
            System.out.println(STR."Applied random effect: \{effect.getClass().getSimpleName()}");
        }
    }
}