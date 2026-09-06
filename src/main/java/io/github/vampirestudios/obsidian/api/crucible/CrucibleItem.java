package io.github.vampirestudios.obsidian.api.crucible;

import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;

public class CrucibleItem {
	public Identifier id;

	public String Id;
	public String Display;
	public String Type;
	public int Data;
	public int Amount;
	public int Model;
	public List<String> Lore;
	//    public NBT nbt;
//    public List<String> hide;
	public List<String> Skills;
	public List<Skill> internalSkills;
	public List<Enchantment> Enchantments;
	public Firework Firework;
	public Options Options;  // Add this line to hold the options
	public Furniture Furniture;

	// Item Set
	public String Set;

	// Augmentation slots on this item (can receive augments)
	public List<AugmentSlotDef> AugmentSlots;

	// If this item is an augment
	public AugmentationDef Augmentation;

	// If this item removes augments from other items
	public AugmentationRemoverDef AugmentationRemover;

	// If this item unlocks extra augment slots on other items
	public AugmentationSocketDef AugmentationSocket;

	public CrucibleItem() {
	}

	public static class AugmentSlotDef {
		public String Type;
		/** Fixed integer or "XtoY" range string. Rolled once at item registration. */
		public String Amount = "1";
		/** Chance (0.0–1.0) that this slot group is present at all. */
		public double Chance = 1.0;
		/** Maximum total slots of this type this item can ever have (limits slot unlockers). */
		public int MaxAmount = Integer.MAX_VALUE;
	}

	public static class AugmentationDef {
		public String Type;
		public String Tooltip;
		public String Icon;
		public List<String> Conditions;
		public Map<String, Double> Attributes;
		public List<String> Skills;
		public List<Skill> internalSkills;
	}

	public static class AugmentationRemoverDef {
		public String Type;
		/** If true, the slot itself is destroyed after removing the augment. */
		public boolean DestroySocket = false;
		/** If true, the removed augment item is returned to the player's inventory. */
		public boolean ReturnAugment = true;
	}

	public static class AugmentationSocketDef {
		public String Type;
		/** Maximum slots of this type the target item can have after unlocking. */
		public int MaxSockets = Integer.MAX_VALUE;
	}

	// Inner class for Options
	public static class Options {
		public boolean Repairable;
		public boolean Unbreakable;
		public boolean PreventStacking;
		public boolean CancelDamage;
		public boolean Destroy;
		public boolean DestroyOnDrop;
		public boolean KeepOnDeath;
		public boolean PreventDropping;
		public boolean Placeable;
		public boolean PreventAnvil;
		public boolean PreventSmithing;
		public boolean PreventCrafting;
		public boolean PreventEnchanting;
		public int RepairCost;
		public int HideFlags;
		//        public int Color;
		public String Player;
		public String SkinTexture;
		public String Color;
		public String Permission;
	}

	public static class Furniture {
		public String Material;
		public int Model;
		public String Type;
		public String Health;
		public Hitbox Hitbox;
		public String Color;
		public boolean CanRotate;
		public boolean GlowingItem;
		public boolean DropSelf;
		public boolean Diagonalable;
		public boolean Colorable;
		public Placement Placement;

		public enum Placement {
			FLOOR,
			HANGING,
			CEILING,
			WALL,
			ANY
		}

		public static class Hitbox {
			public int Height = 1;
			public int Width = 1;
		}
	}

}