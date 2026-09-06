package io.github.vampirestudios.obsidian.api.obsidian.block;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.BlockSettings;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import io.github.vampirestudios.obsidian.api.obsidian.TextureAndModelInformation;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The companion blocks a block asks for — its stairs, slab, wall and the rest.
 *
 * <p>Two spellings reach the same place. The short one is a flag per variant, which takes every
 * default:
 *
 * <pre>{@code "additional_information": { "stairs": true, "slab": true } }</pre>
 *
 * <p>The long one names each variant in {@code variants} and can say what makes it different — its id,
 * its display name, the creative tab it sits in, the textures its models are built from, its loot
 * table:
 *
 * <pre>{@code
 * "additional_information": {
 *   "variants": {
 *     "stairs": true,
 *     "slab": { "id": "polished_tuff_slab", "textures": { "all": "tutorial:block/polished_tuff" } }
 *   }
 * }
 * }</pre>
 *
 * <p>Everything that acts on companions — registration, model generation, translations, loot — reads
 * them through {@link #declared(Block)}, so a variant is described in one place and not three.
 */
public final class CompanionBlocks {

	private CompanionBlocks() {
	}

	public enum Type {
		SLAB("slab", "_slab", " Slab", CreativeModeTabs.BUILDING_BLOCKS),
		STAIRS("stairs", "_stairs", " Stairs", CreativeModeTabs.BUILDING_BLOCKS),
		WALL("wall", "_wall", " Wall", CreativeModeTabs.BUILDING_BLOCKS),
		FENCE("fence", "_fence", " Fence", CreativeModeTabs.BUILDING_BLOCKS),
		FENCE_GATE("fence_gate", "_fence_gate", " Fence Gate", CreativeModeTabs.REDSTONE_BLOCKS),
		BUTTON("button", "_button", " Button", CreativeModeTabs.REDSTONE_BLOCKS),
		PRESSURE_PLATE("pressure_plate", "_pressure_plate", " Pressure Plate", CreativeModeTabs.REDSTONE_BLOCKS),
		DOOR("door", "_door", " Door", CreativeModeTabs.REDSTONE_BLOCKS),
		TRAPDOOR("trapdoor", "_trapdoor", " Trapdoor", CreativeModeTabs.REDSTONE_BLOCKS);

		public final String key;
		public final String suffix;
		/** Appended to the base block's English name when the variant does not name itself. */
		public final String englishSuffix;
		public final ResourceKey<CreativeModeTab> defaultTab;

		Type(String key, String suffix, String englishSuffix, ResourceKey<CreativeModeTab> defaultTab) {
			this.key = key;
			this.suffix = suffix;
			this.englishSuffix = englishSuffix;
			this.defaultTab = defaultTab;
		}

		/** Matches the spelling a pack used: {@code walls}, {@code fenceGate}, {@code fence_gate}. */
		public static Type byKey(String key) {
			String wanted = normalize(key);
			for (Type type : values()) {
				if (normalize(type.key).equals(wanted)) return type;
			}
			return null;
		}
	}

	/** Folds the spellings of one variant together: {@code walls}, {@code fenceGate}, {@code fence_gate}. */
	public static String normalize(String key) {
		String folded = key.toLowerCase(Locale.ROOT).replace("_", "").replace(" ", "");
		return folded.equals("walls") ? "wall" : folded;
	}

	/** What one variant asks to have done differently. Every field is optional. */
	public static class Options {

		/** Set {@code false} to drop a variant an earlier flag turned on. */
		public boolean enabled = true;

		/** The variant's whole id path, in the block's namespace. Replaces the base id and the suffix. */
		public String id;

		/** What is appended to the base id instead of the variant's usual suffix. */
		public String suffix;

		/** Display name. Without one, the base block's name plus the variant's own word is used. */
		public NameInformation name;

		@SerializedName("item_group")
		public Identifier itemGroup;

		/**
		 * Settings for this variant alone, in the forms {@code information.block_properties} takes: the
		 * settings written out, or the id of an entry in {@code block/property}. Without one the variant
		 * is built from the block's own settings.
		 */
		@SerializedName("block_properties")
		public JsonElement blockProperties;

		/**
		 * Models are built from these instead of the base block's textures. Takes the same forms as
		 * {@code rendering.block_model}: a model id, or an object with a parent and textures.
		 */
		@SerializedName("block_model")
		public JsonElement blockModel;

		/** Shorthand for a {@code block_model} that is only textures. */
		public Map<String, Identifier> textures;

		/** Use this loot table instead of one generated for the variant. */
		@SerializedName("loot_table")
		public Identifier lootTable;

		/** Set {@code false} for a variant that should drop nothing. */
		@SerializedName("generate_loot_table")
		public Boolean generateLootTable;

		/** This variant's own settings, or null when it should follow the block's. */
		public BlockSettings getBlockSettings() {
			if (blockProperties == null || blockProperties.isJsonNull()) return null;
			// A string is the id of registered settings, an object is settings written out, and either may
			// name a parent — the same declaration a block's own block_properties takes.
			return BlockSettings.resolve(blockProperties.isJsonPrimitive()
					? blockProperties.getAsString()
					: blockProperties);
		}

		public TextureAndModelInformation getBlockModel() {
			if (blockModel != null && !blockModel.isJsonNull()) {
				return io.github.vampirestudios.obsidian.api.obsidian.DisplayInformation
						.parseModelDeclaration(blockModel);
			}
			if (textures != null && !textures.isEmpty()) {
				TextureAndModelInformation information = new TextureAndModelInformation();
				information.textures = textures;
				return information;
			}
			return null;
		}
	}

	/**
	 * One companion block a pack asked for, with the id it is registered under.
	 *
	 * @param options never null — a variant turned on by a plain flag gets the defaults
	 */
	public record Declared(Type type, Identifier id, Options options) {

		public ResourceKey<CreativeModeTab> tab() {
			return options.itemGroup == null
					? type.defaultTab
					: ResourceKey.create(Registries.CREATIVE_MODE_TAB, options.itemGroup);
		}

		public boolean generatesLootTable() {
			return options.lootTable == null
					&& (options.generateLootTable == null || options.generateLootTable);
		}
	}

	/** The base id the variants hang off: {@code extra_blocks_name} when the pack renamed the family. */
	public static Identifier baseId(Block block) {
		Identifier blockId = block.information.id;
		AdditionalBlockInformation info = block.additional_information;
		if (info == null || info.extraBlocksName == null || info.extraBlocksName.isBlank()) return blockId;
		return Identifier.fromNamespaceAndPath(blockId.getNamespace(), info.extraBlocksName);
	}

	/** Every companion this block declares, in a fixed order, however the pack spelled them. */
	public static List<Declared> declared(Block block) {
		AdditionalBlockInformation info = block.additional_information;
		if (info == null) return List.of();

		Map<Type, Options> found = new LinkedHashMap<>();

		// The flags, which are the short spelling of a variant with every default.
		if (info.slab) found.put(Type.SLAB, new Options());
		if (info.stairs) found.put(Type.STAIRS, new Options());
		if (info.walls) found.put(Type.WALL, new Options());
		if (info.fence) found.put(Type.FENCE, new Options());
		if (info.fenceGate) found.put(Type.FENCE_GATE, new Options());
		if (info.button) found.put(Type.BUTTON, new Options());
		if (info.pressurePlate) found.put(Type.PRESSURE_PLATE, new Options());
		if (info.door) found.put(Type.DOOR, new Options());
		if (info.trapdoor) found.put(Type.TRAPDOOR, new Options());

		if (info.variants != null) {
			info.variants.forEach((key, declaration) -> {
				Type type = Type.byKey(key);
				if (type == null) {
					Obsidian.LOGGER.warn("[Obsidian] {} declares a variant called '{}', which is not one "
							+ "Obsidian knows; skipping it", block.information.id, key);
					return;
				}

				Options options = parse(block, key, declaration);
				if (options == null || !options.enabled) {
					found.remove(type);
				} else {
					found.put(type, options);
				}
			});
		}

		Identifier baseId = baseId(block);
		List<Declared> declared = new ArrayList<>(found.size());
		// Iterating the enum keeps the order the same whatever order the pack wrote them in.
		for (Type type : Type.values()) {
			Options options = found.get(type);
			if (options != null) declared.add(new Declared(type, id(baseId, type, options), options));
		}
		return declared;
	}

	/** The one variant of {@code type} this block declares, or null. */
	public static Declared declared(Block block, Type type) {
		for (Declared declared : declared(block)) {
			if (declared.type() == type) return declared;
		}
		return null;
	}

	private static Identifier id(Identifier baseId, Type type, Options options) {
		if (options.id != null && !options.id.isBlank()) {
			return Identifier.fromNamespaceAndPath(baseId.getNamespace(), options.id);
		}
		return Utils.appendToPath(baseId, options.suffix == null || options.suffix.isBlank()
				? type.suffix
				: options.suffix);
	}

	/** A variant is either a plain on/off flag or an object saying how it differs. */
	private static Options parse(Block block, String key, JsonElement declaration) {
		if (declaration == null || declaration.isJsonNull()) return null;

		if (declaration.isJsonPrimitive() && declaration.getAsJsonPrimitive().isBoolean()) {
			Options options = new Options();
			options.enabled = declaration.getAsBoolean();
			return options;
		}

		if (!declaration.isJsonObject()) {
			Obsidian.LOGGER.warn("[Obsidian] {} declares the variant '{}' as neither true/false nor an "
					+ "object; skipping it", block.information.id, key);
			return null;
		}

		return BaseGson.GSON.fromJson(declaration, Options.class);
	}

}
