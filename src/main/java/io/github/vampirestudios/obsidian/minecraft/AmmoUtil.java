package io.github.vampirestudios.obsidian.minecraft;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class AmmoUtil {
	private AmmoUtil() {
	}

	public record Found(int slot, ItemStack stack) {
	}

	public static Found findAmmo(Player player, AmmoSpec spec) {
		if (spec == null) return null;

		// Check offhand first, then main inventory
//        player.getProjectile()
		ItemStack offhand = player.getOffhandItem();
		if (matches(offhand, spec)) return new Found(-1, offhand); // -1 means offhand

		var inv = player.getInventory();
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack s = inv.getItem(i);
			if (matches(s, spec)) return new Found(i, s);
		}
		return null;
	}

	public static boolean matches(ItemStack stack, AmmoSpec spec) {
		if (stack == null || stack.isEmpty()) return false;

        /*return switch (spec.type()) {
            case ITEM -> {
                Item item = BuiltInRegistries.ITEM.getValue(spec.id());
                yield stack.is(item);
            }
            case TAG -> {
                TagKey<Item> tag = TagKey.create(Registries.ITEM, spec.id());
                yield stack.is(tag);
            }
        };*/
		Item item = BuiltInRegistries.ITEM.getValue(spec.id());
		return stack.is(item);
	}

	public static void consume(Player player, Found found, int count) {
		if (found == null || count <= 0) return;

		if (found.slot() == -1) {
			found.stack().shrink(count);
			return;
		}
		found.stack().shrink(count);
	}
}
