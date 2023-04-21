package io.github.vampirestudios.obsidian.api.obsidian.item;

import net.minecraft.resources.ResourceLocation;

public class FoodInformation {

    public int fullness = 0;
    public ResourceLocation returnItem;
    public ResourceLocation foodComponent;
    public boolean drinkable;
    public ResourceLocation drinkSound = new ResourceLocation("entity.generic.drink");
    public ResourceLocation eatSound = new ResourceLocation("entity.generic.eat");
    public int use_time;

}