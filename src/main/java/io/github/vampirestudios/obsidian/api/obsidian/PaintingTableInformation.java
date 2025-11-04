package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.utils.ColorUtil;
import io.github.vampirestudios.obsidian.utils.CustomList;
import io.github.vampirestudios.obsidian.utils.IntArray;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PaintingTableInformation {
	public String title;
	public Buttons buttons;
	public Colors colors;

	public static class Buttons {
		public int inputSlot;
		public int outputSlot;
		public BaseColorGrid baseColorGrid = new BaseColorGrid();
		public SubColorGrid subColorGrid = new SubColorGrid();
		public int[] subColorRow;
		public int effectButton;
	}

	public static class ColorCategory {
		public Color baseColor;
		public List<Color> subColors;
	}

	public static enum Type {
		NORMAL,
		SCROLLING
	}

	public static class Color {
		public String name;
		public Object color;

		public int getColor() {
			if (color instanceof int[] ints) {
				return ColorUtil.color(ints[0],ints[1],ints[2]);
			} else if(color instanceof String s) {
				return ColorUtil.color(s.replaceAll("#", ""));
			} else {
				return ColorUtil.color("ffffff");
			}
		}
	}

	public static class Effect {
		public String name;
		public Color color;
	}

	public static class Colors {
		public Map<String, Color> baseColor;
		public Set<Map<String, Color>> subColors;
	}

	public static class BaseColorGrid {
		public Type type = Type.NORMAL;
		public Normal normalGrid = new Normal();
		public static class Normal {
			public IntArray first = new IntArray(12, 14);
			public IntArray second = new IntArray(21, 23);
			public IntArray third = new IntArray(30, 32);
			public List<IntArray> rows = List.of(first, second, third);
		}
//		public int[][] normalGrid = {
//				{12, 13, 14},
//				{21, 22, 23},
//				{30, 31, 32}
//		};
		public Scrolling scrollingGrid = new Scrolling();
		public Item baseColorItem = null;

		public static class Scrolling {
			public IntArray row = new IntArray(19, 25);
			public int backwardSlot = row.getMin() - 1;
			public int forwardSlot = row.getMax() + 1;
			public Item backwardItem = new Item(Items.ARROW, "<!italic>Scroll base-colors backwards", List.of());
			public Item forwardItem = new Item(Items.ARROW, "<!italic>Scroll base-colors forward", List.of());
		}
	}

	public static class SubColorGrid {
		public Type type = Type.NORMAL;
		public boolean autoFillColorGradient = true;
		public Normal normalGrid = new Normal();
		public Scrolling scrollingGrid = new Scrolling();
		public Item subColorItem = null;

		public static class Normal {
			public CustomList<IntArray> rows = CustomList.of(new IntArray(37, 43), new IntArray(46, 52));
		}

		public static class Scrolling {
			public IntArray row = new IntArray(46, 52);
			public int backwardsSlot = 36;
			public int forwardsSlot = 45;
			public Item backwardItem = new Item(Items.ARROW, "<!italic>Scroll sub-colors backwards", List.of());
			public Item forwardItem = new Item(Items.ARROW, "<!italic>Scroll sub-colors forward", List.of());
		}
	}

	public static class Item {
		public ResourceLocation item;
		public String name;
		public List<String> lore;

		public Item(net.minecraft.world.item.Item item, String name, List<String> lore) {
			this.item = BuiltInRegistries.ITEM.getKey(item);
			this.name = name;
			this.lore = lore;
		}
	}
}
