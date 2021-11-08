package io.github.vampirestudios.obsidian.api.obsidian.item;

import net.minecraft.util.Identifier;

public class FoodInformation {

    public int fullness = 0;
    public Identifier returnItem;
    public Identifier foodComponent;
    public boolean drinkable;
    public Identifier drinkSound = new Identifier("entity.generic.drink");
    public Identifier eatSound = new Identifier("entity.generic.eat");
    public int use_time;

}