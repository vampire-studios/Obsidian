package io.github.vampirestudios.obsidian.minecraft;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class CustomMenuComponent implements TooltipComponent {
	public static final Codec<CustomMenuComponent> CODEC = ItemStack.CODEC
			.listOf()
			.flatXmap(CustomMenuComponent::checkAndCreate, component -> DataResult.success(component.items));
	public static final StreamCodec<RegistryFriendlyByteBuf, CustomMenuComponent> STREAM_CODEC = ItemStack.STREAM_CODEC
			.apply(ByteBufCodecs.list())
			.map(CustomMenuComponent::new, component -> component.items);

	final List<ItemStack> items;

	public CustomMenuComponent(List<ItemStack> items) {
		this.items = items;
	}

	private static DataResult<CustomMenuComponent> checkAndCreate(List<ItemStack> items) {
		return DataResult.success(new CustomMenuComponent(items));
	}

	public static boolean canItemBeInBundle(ItemStack itemStack) {
		return !itemStack.isEmpty() && itemStack.getItem().canFitInsideContainerItems();
	}

	public ItemStack getItemUnsafe(int i) {
		return (ItemStack) this.items.get(i);
	}

	public Stream<ItemStack> itemCopyStream() {
		return this.items.stream().map(ItemStack::copy);
	}

	public Iterable<ItemStack> items() {
		return this.items;
	}

	public Iterable<ItemStack> itemsCopy() {
		return Lists.<ItemStack, ItemStack>transform(this.items, ItemStack::copy);
	}

	public int size() {
		return this.items.size();
	}

	public boolean isEmpty() {
		return this.items.isEmpty();
	}

	public static class Mutable {
		private final List<ItemStack> items;

		public Mutable(CustomMenuComponent bundleContents) {
			this.items = new ArrayList<>(bundleContents.items);
		}

		public CustomMenuComponent.Mutable clearItems() {
			this.items.clear();
			return this;
		}

		private int findStackIndex(ItemStack itemStack) {
			if (itemStack.isStackable()) {
				for (int i = 0; i < this.items.size(); i++) {
					if (ItemStack.isSameItemSameComponents(this.items.get(i), itemStack)) {
						return i;
					}
				}

			}
			return -1;
		}

		public int tryInsert(ItemStack itemStack) {
			if (!CustomMenuComponent.canItemBeInBundle(itemStack)) {
				return 0;
			} else {
				int i = Math.min(itemStack.getCount(), 1);
				if (i == 0) {
					return 0;
				} else {
					int j = this.findStackIndex(itemStack);
					if (j != -1) {
						ItemStack itemStack2 = (ItemStack) this.items.remove(j);
						ItemStack itemStack3 = itemStack2.copyWithCount(itemStack2.getCount() + i);
						itemStack.shrink(i);
						this.items.addFirst(itemStack3);
					} else {
						this.items.addFirst(itemStack.split(i));
					}

					return i;
				}
			}
		}

		public int tryTransfer(Slot slot, Player player) {
			ItemStack itemStack = slot.getItem();
			return BundleContents.canItemBeInBundle(itemStack) ? this.tryInsert(slot.safeTake(itemStack.getCount(), 1, player)) : 0;
		}

		@Nullable
		public ItemStack removeOne() {
			if (this.items.isEmpty()) {
				return null;
			} else {
				return this.items.removeFirst().copy();
			}
		}

		public CustomMenuComponent toImmutable() {
			return new CustomMenuComponent(List.copyOf(this.items));
		}
	}
}
