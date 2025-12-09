package io.github.vampirestudios.obsidian.api.obsidian.item;

import net.minecraft.resources.Identifier;

public class FoodInformation {

    public int fullness = 0;
    public Identifier returnItem;
    public Identifier foodComponent;
    public boolean drinkable;
    public Identifier drinkSound = Identifier.withDefaultNamespace("entity.generic.drink");
    public Identifier eatSound = Identifier.withDefaultNamespace("entity.generic.eat");
    public int use_time;

}