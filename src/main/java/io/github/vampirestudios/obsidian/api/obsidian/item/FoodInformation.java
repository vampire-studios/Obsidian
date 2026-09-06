package io.github.vampirestudios.obsidian.api.obsidian.item;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.Identifier;

public class FoodInformation {

	public int fullness = 0;

	@SerializedName("return_item")
	public Identifier returnItem;

	@SerializedName("food_component")
	public Identifier foodComponent;

	public boolean drinkable;

	@SerializedName("drink_sound")
	public Identifier drinkSound = Identifier.withDefaultNamespace("entity.generic.drink");

	@SerializedName("eat_sound")
	public Identifier eatSound = Identifier.withDefaultNamespace("entity.generic.eat");

	@SerializedName("use_time")
	public int useTime;

}
