package io.github.vampirestudios.obsidian;


import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.Optional;

public class CosmeticsData {
	public static void setHeadCosmetics(IEntityDataSaver player, ItemStack is) {
//        String cosmetics;
//        CompoundTag nbt = player.getPersistentData();
//        if(is != ItemStack.EMPTY) {
//            cosmetics = is.getItem() + "," + Objects.requireNonNull(is.getTag()).getInt("CustomModelData");
//        } else {
//            cosmetics = is.getItem().toString();
//        }
//        nbt.putString("head_cosmetics", cosmetics);
	}

	public static ItemStack getHeadCosmetics(IEntityDataSaver player) {
		CompoundTag nbt = player.getPersistentData();
		Optional<String> headCosmeticsString = nbt.getString("head_cosmetics");

		if (headCosmeticsString.isEmpty()) return ItemStack.EMPTY;

		if (Objects.equals(headCosmeticsString.get(), "air")) {
			return ItemStack.EMPTY;
		}

		String[] parts = headCosmeticsString.get().split(",", 2);

		String itemId = parts[0].toLowerCase();

		ItemStack itemStack = new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace(itemId)));
		if (parts.length > 1) {
//            int itemCMD = Integer.parseInt(parts[1]);
//            CompoundTag itemStackNbtData = new CompoundTag();
//            itemStackNbtData.putInt("CustomModelData", itemCMD);
//            itemStack.setTag(itemStackNbtData);
		}

		return itemStack;
	}
}