package io.github.vampirestudios.obsidian.api.obsidian.block;

import com.electronwill.nightconfig.core.conversion.Path;
import com.google.gson.annotations.SerializedName;
import io.github.vampirestudios.obsidian.api.bedrock.Description;
import io.github.vampirestudios.obsidian.api.obsidian.DisplayInformation;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import io.github.vampirestudios.obsidian.api.obsidian.PaintingTableInformation;
import io.github.vampirestudios.obsidian.api.obsidian.SpecialText;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodInformation;
import io.github.vampirestudios.obsidian.api.obsidian.menu.CustomMenuConfig;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public class Block {
	public Description description;
	public String block_type = "block";
	public BlockInformation information;

	/**
	 * A {@code block/template} entry to inherit from, by id. Anything this block leaves out is taken from
	 * the template; anything it declares wins. Templates are not registered as blocks themselves.
	 */
	public String template;

	/** Event action lists keyed by event name, mirroring the item {@code events} format. */
	public Map<String, List<Map<String, Object>>> events = new HashMap<>();

	/**
	 * The menu an {@code open_crate} action opens for this block, and the loot pool behind it. Same format
	 * as an item's, so a crate block and a crate item are described the same way.
	 */
	@SerializedName("menu_config")
	public CustomMenuConfig menuConfig;

	public List<Map<String, Object>> getEventActions(String event) {
		return events == null ? List.of() : events.getOrDefault(event, List.of());
	}

	public Blocks[] blocks;
	public Behaviour behaviour;

	// Accept both "rendering" (current) and "display" (legacy alias used by older packs)
	@SerializedName(value = "rendering", alternate = {"display"})
	public DisplayInformation rendering;
	@SerializedName("drop_information")
	@Path("drop_information")
	public DropInformation dropInformation;
	public AdditionalBlockInformation additional_information;
	public Functions functions;
	public DataComponentMap components;
	public FoodInformation food_information;
	public CampfireProperties campfire_properties;
	public List<Identifier> can_plant_on = new ArrayList<>();
	public Identifier particle_type;
	public Growable growable;

	/** How a {@code "block_type": "bush"} grows, is picked and scratches. Ignored by every other type. */
	@SerializedName(value = "bush_properties", alternate = {"bush"})
	public BushProperties bushProperties;
	public OxidizableProperties oxidizable_properties;
	public boolean is_multi_block = false;

	/** Declaring this is what makes a block multi-block; {@code is_multi_block} is no longer read. */
	@SerializedName(value = "multi_block_information", alternate = {"multi_block"})
	public MultiBlockInformation multi_block_information;
	public Identifier placeable_feature;

	@SerializedName("painting_table_information")
	public PaintingTableInformation paintingTableInformation;

	public List<SpecialText> lore = new ArrayList<>();

	public void addLore(Consumer<Component> tooltip) {
		if (lore != null && !lore.isEmpty()) {
			for (SpecialText text : lore) {
				tooltip.accept(text.getName());
			}
		}
	}

	public List<net.minecraft.world.level.block.Block> getSupportableBlocks() {
		List<net.minecraft.world.level.block.Block> blocks2 = new ArrayList<>();
		can_plant_on.forEach(identifier -> blocks2.add(BuiltInRegistries.BLOCK.getValue(identifier)));
		return blocks2;
	}

	public BlockType getBlockType() {
		if (block_type == null || block_type.isBlank()) return null;
		try {
			return BlockType.valueOf(block_type.toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Unknown block_type \"" + block_type + "\"", e);
		}
	}

	public enum BlockType {
		PAINTING_TABLE,
		BLOCK,
		HORIZONTAL_DIRECTIONAL,
		DIRECTIONAL,
		CAMPFIRE,
		STAIRS,
		SLAB,
		WALL,
		FENCE,
		FENCE_GATE,
		CAKE,
		BED,
		TRAPDOOR,
		DOOR,
		LOG,
		STEM,
		WOOD,
		OXIDIZING_BLOCK,
		PLANT,
		BUSH,
		ROTATED_PILLAR,
		HORIZONTAL_FACING_PLANT,
		SAPLING,
		TORCH,
		BEEHIVE,
		LEAVES,
		LADDER,
		PATH,
		BUTTON,
		PRESSURE_PLATE,
		DOUBLE_PLANT,
		HORIZONTAL_FACING_DOUBLE_PLANT,
		HANGING_DOUBLE_LEAVES,
		EIGHT_DIRECTIONAL_BLOCK,
		SIXTEEN_DIRECTIONAL_BLOCK,
		CROP,
		CLIMBABLE,
		ROD,
		CANDLE,
		LEVER,
		LANTERN,
		CHAIN,
		PANE,
		DYEABLE,
		LOOM,
		CRAFTING_TABLE,
		SMOKER,
		FURNACE,
		BLAST_FURNACE,
		BARREL,
		CARPET
	}

	public static class OxidizableProperties {
		public List<OxidationStage> stages;
		public List<String> cycle;

		public static class OxidationStage {
			public boolean can_be_waxed = true;
			public List<VariantBlock> blocks;
			public boolean stairs = true;
			public boolean slab = true;

			public static class VariantBlock {
				public NameInformation name;
				public DisplayInformation display;
				public transient net.minecraft.resources.Identifier id;
			}
		}
	}

	public static class CampfireProperties {
		public boolean emits_particles;
		public int fire_damage;
		public int luminance;
	}

	/**
	 * The size of the structure one placement builds, in cells. {@code width} runs along the block's local
	 * X and {@code depth} along its local Z, both rotating with the block's facing; {@code height} runs up.
	 */
	public static class MultiBlockInformation {
		public int width = 1;
		public int height = 1;
		public int depth = 1;

		/**
		 * Whether one model on the origin cell covers the whole structure, leaving the other cells invisible.
		 * Only worth it while the model stays within a block of overhang, which is as far as the model format
		 * reaches; past that the structure has to be modelled cell by cell anyway.
		 */
		@SerializedName("whole_model")
		public boolean wholeModel = false;

		public int width() {
			return Math.max(1, width);
		}

		public int height() {
			return Math.max(1, height);
		}

		public int depth() {
			return Math.max(1, depth);
		}

		public int cellCount() {
			return width() * height() * depth();
		}
	}

	public static class Properties {
		public String facing;
//        public Map<String, PropertiesInfo> properties = new HashMap<>();

		public static class PropertiesInfo {
			public String type;
			public String[] values;
			public int min = 0, max = 1;
		}
	}


	public static class Blocks {
		public Vec3 origin;
		public Vec3 size;
		public Identifier block;
	}

	public static class Behaviour {
		public Repeater repeater;
		public PowerSource power_source;
		public Placement placement;
		public boolean glowing;
		public boolean rotate;
		public boolean rotateSmooth;

		public Container container;
		public Lock lock;
		public List<Seat> seat;
		public List<Showcase> showcase;

		public static class Repeater {
			public int delay = 0;
			public int loss = 0;
		}

		public static class PowerSource {
			public int value = 15;
		}

		public static class Container {
			/**
			 * The title shown at the top of the container screen. Without one the block's own name is used.
			 */
			public NameInformation name;

			/**
			 * How many slots the container holds. Either {@code 5}, for a hopper-shaped screen, or a
			 * multiple of 9 from 9 to 81 — nine rows being the largest screen Obsidian registers.
			 */
			public int size = 27;

			/**
			 * Whether the contents are thrown away once the last viewer closes the screen. Useful for
			 * loot crates and one-shot dispensers; leave off for anything a player stores things in.
			 */
			public boolean purge = false;

			/**
			 * Whether the contents drop on the floor when the block is broken. Off keeps them in the
			 * block item, vanilla shulker-box style, only if the pack writes the component itself —
			 * Obsidian does not copy contents into the item on its own.
			 */
			@SerializedName(value = "drop_contents", alternate = {"dropContents"})
			public boolean dropContents = true;

			/**
			 * The name of the animation to play when the container is opened (if applicable).
			 */
			@SerializedName(value = "open_animation", alternate = {"openAnimation"})
			public String openAnimation;

			/**
			 * The name of the animation to play when the container is closed (if applicable).
			 */
			@SerializedName(value = "close_animation", alternate = {"closeAnimation"})
			public String closeAnimation;
		}

		public static class Placement {
			public boolean floor;
			public boolean wall;
			public boolean ceiling;
		}

		public static class Lock {
			/**
			 * The item that opens the lock, held in the main hand. A lock naming no key can never be
			 * opened by anyone.
			 */
			public Identifier key = null;

			/**
			 * Whether one key is taken from the stack on each successful unlock. Creative-mode players
			 * never lose theirs.
			 */
			@SerializedName(value = "consume_key", alternate = {"consumeKey"})
			public boolean consumeKey = false;

			/**
			 * Whether the lock is gone for good once opened. A discarding lock remembers being opened in
			 * the block's {@code unlocked} state; one that stays asks for the key every time.
			 */
			public boolean discard = false;

			/**
			 * Name of the animation to play upon successful unlocking. Reserved — nothing plays it yet.
			 */
			@SerializedName(value = "unlock_animation", alternate = {"unlockAnimation"})
			public String unlockAnimation = null;

			/**
			 * Command run as the server when the lock is opened, at the block's position and with the
			 * unlocking player as the executing entity.
			 */
			public String command = null;
		}

		public static class Seat {
			/**
			 * The passenger attachment offset in a north-facing block's local frame. A missing offset
			 * uses Obsidian's chair-height default.
			 */
			public Vector3f offset;

			/**
			 * Additional clockwise yaw, in degrees, after rotating with the block.
			 */
			public float direction = 0;

			/** How the passenger is presented. */
			public SeatPose pose = SeatPose.SITTING;

			/** Keeps the passenger facing the seat direction. Lying seats are always rotation-locked. */
			@SerializedName("lock_rotation")
			public boolean lockRotation = false;

			/** Optional preferred dismount point in the same local frame as {@link #offset}. */
			@SerializedName("dismount_offset")
			public Vector3f dismountOffset;

			/** Set false only for intentionally enclosed furniture. */
			@SerializedName("check_space")
			public boolean checkSpace = true;

			/** Whether a lying player contributes to vanilla's night-skip count. */
			@SerializedName("skip_night")
			public boolean skipNight = false;

			/** Whether entering this seat resets the phantom rest timer. */
			@SerializedName("reset_phantoms")
			public boolean resetPhantoms = false;

			public enum SeatPose {
				@SerializedName("sitting")
				SITTING,
				@SerializedName("lying")
				LYING
			}
		}

		public static class Showcase {
			public Vector3f offset;
			public Vector3f scale;
			public Quaternionf rotation;
			public Type type;
			public List<Identifier> filterItems;
			public List<Identifier> filterTags;

			public static enum Type {
				BLOCK,
				ITEM,
				DYNAMIC
			}
		}
	}
}
