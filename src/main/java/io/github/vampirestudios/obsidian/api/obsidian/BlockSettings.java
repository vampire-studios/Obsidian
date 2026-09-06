package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.api.MapColors;
import io.github.vampirestudios.obsidian.api.VanillaSoundEvents;
import io.github.vampirestudios.obsidian.api.obsidian.block.CustomSoundGroup;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.Locale;

public class BlockSettings {
	@com.google.gson.annotations.SerializedName("parent")
	public Object baseBlockSettings;

	@com.google.gson.annotations.SerializedName("sound_group")
	public Object soundGroup = Identifier.withDefaultNamespace("stone");

	public boolean collidable = true;
	public float hardness = 3.0F;
	public float resistance = 3.0F;
	public boolean randomTicks = false;
	public boolean instant_break = false;
	public float slipperiness = 0.6F;
	public Identifier drop = Identifier.withDefaultNamespace("stone");
	public float velocity_modifier = 1.0F;
	public float jump_velocity_modifier = 1.0F;
	public int luminance = 0;

	@com.google.gson.annotations.SerializedName("powered_luminance")
	public int poweredLuminance = -1;

	public boolean is_emissive = false;
	/** Whether the block lets light and faces through. Solid blocks should say {@code false}. */
	public boolean translucent = true;
	public boolean dynamic_boundaries = false;
	public String push_reaction = "NORMAL";
	public String map_color = "STONE";

	/**
	 * Only drops when broken with the right tool, the way ores and stone do.
	 *
	 * <p>What counts as the right tool is decided entirely by the block's mining tags, so this needs
	 * {@link #mineable} alongside it. On its own it means <em>no</em> tool is ever correct and the block
	 * drops nothing at all, which is warned about rather than generated.
	 */
	@com.google.gson.annotations.SerializedName("requires_tool")
	public boolean requiresTool = false;

	/**
	 * The tool that mines the block: {@code pickaxe}, {@code axe}, {@code shovel} or {@code hoe}. It puts
	 * the block in that tool's {@code minecraft:mineable/} tag, which is what makes the tool break it
	 * quickly — and, with {@link #requiresTool}, what makes it drop at all.
	 */
	public String mineable;

	/**
	 * The lowest tool tier that may mine the block: {@code stone}, {@code iron} or {@code diamond}. Only
	 * meaningful with {@link #requiresTool}, since without it every tier already drops the block.
	 */
	@com.google.gson.annotations.SerializedName("tool_tier")
	public String toolTier;

	/** Catches fire from lava next to it. */
	@com.google.gson.annotations.SerializedName("ignited_by_lava")
	public boolean ignitedByLava = false;

	/** Placing a block into this one replaces it, the way grass and snow layers work. */
	public boolean replaceable = false;

	/** Treated as a fluid for movement and rendering. */
	public boolean liquid = false;

	/** No block-breaking particles, and none from walking on it. */
	@com.google.gson.annotations.SerializedName("no_terrain_particles")
	public boolean noTerrainParticles = false;

	/** How much of a bounce the block gives, as slime does. 0 is no bounce. */
	@com.google.gson.annotations.SerializedName("bounce_restitution")
	public Float bounceRestitution;

	/** How much fall damage the block takes off, as hay bales do. */
	@com.google.gson.annotations.SerializedName("fall_damage_reduction")
	public Float fallDamageReduction;

	/** Forces the game's idea of whether the block is solid, instead of working it out from the shape. */
	@com.google.gson.annotations.SerializedName("force_solid")
	public Boolean forceSolid;

	/** Whether redstone runs through it. Left out, the block's own shape decides. */
	@com.google.gson.annotations.SerializedName("redstone_conductor")
	public Boolean redstoneConductor;

	/** Whether standing in it suffocates. */
	public Boolean suffocating;

	/** Whether it blocks the view of a player inside it. */
	@com.google.gson.annotations.SerializedName("view_blocking")
	public Boolean viewBlocking;

	/** Set {@code false} to stop mobs spawning on the block. */
	public Boolean spawnable;

	/** How the block is nudged in place: {@code none}, {@code xz} — as flowers are — or {@code xyz}. */
	@com.google.gson.annotations.SerializedName("offset_type")
	public String offsetType;

	/** The sound a note block above it plays, e.g. {@code bass}, {@code bell}, {@code harp}. */
	public String instrument;

	public MapColor getMapColor() {
		return MapColors.get(map_color);
	}

	public PushReaction getPushReaction() {
		return switch (push_reaction.toUpperCase(Locale.ROOT)) {
			case "NORMAL" -> PushReaction.PUSH_PULL;
			case "DESTROY" -> PushReaction.POPPED;
			case "BLOCK" -> PushReaction.IMMOVEABLE;
			case "IGNORE" -> PushReaction.IGNORE_ENTITY;
			case "PUSH_ONLY" -> PushReaction.PUSH;
			default -> throw new IllegalStateException("Unexpected value: " + push_reaction);
		};
	}

	public SoundType getBlockSoundGroup() {
		switch (soundGroup) {
			case Identifier Identifier -> {
				if (!Identifier.getNamespace().equals("minecraft")) {
					CustomSoundGroup customSoundGroup = ContentRegistries.BLOCK_SOUND_GROUPS.getValue(Identifier);
					assert customSoundGroup != null;
					return createSoundType(customSoundGroup);
				} else {
					return VanillaSoundEvents.get(Identifier);
				}
			}
			case String s -> {
				Identifier location = Identifier.tryParse(s);
				assert location != null;
				if (!location.getNamespace().equals("minecraft")) {
					CustomSoundGroup customSoundGroup = ContentRegistries.BLOCK_SOUND_GROUPS.getValue(location);
					assert customSoundGroup != null;
					return createSoundType(customSoundGroup);
				} else {
					return VanillaSoundEvents.get(location);
				}
			}
			case CustomSoundGroup customSoundGroup -> {
				return createSoundType(customSoundGroup);
			}
			case null, default -> {
				System.out.println(soundGroup.toString());
				return SoundType.STONE;
			}
		}
	}

	private SoundType createSoundType(CustomSoundGroup customSoundGroup) {
		SoundEvent breakSound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.getValue(customSoundGroup.break_sound);
		SoundEvent stepSound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.getValue(customSoundGroup.step_sound);
		SoundEvent placeSound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.getValue(customSoundGroup.place_sound);
		SoundEvent hitSound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.getValue(customSoundGroup.hit_sound);
		SoundEvent fallSound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.getValue(customSoundGroup.fall_sound);
		return new SoundType(1.0F, 1.0F, breakSound, stepSound, placeSound, hitSound, fallSound);
	}

	/**
	 * The settings this one starts from, if it names a {@code parent}. Resolved settings already have
	 * their parent's fields folded in, so this is only for reading the parent on its own.
	 */
	public BlockSettings getParentSettings() {
		BlockSettings parent = SettingsResolver.parent(baseBlockSettings, ContentRegistries.BLOCK_SETTINGS, BlockSettings.class);
		// Resolved settings already carry their parent's fields, so they are their own best answer here —
		// and callers reading this as a fallback never have to check for null.
		return parent == null ? this : parent;
	}

	/** Reads a declaration — settings written out, or the id of registered ones — into finished settings. */
	public static BlockSettings resolve(Object declaration) {
		return SettingsResolver.resolve(declaration, ContentRegistries.BLOCK_SETTINGS, BlockSettings.class,
				new BlockSettings(), LegacyProperties::block);
	}

	/**
	 * Puts these settings onto a block's properties. Every block Obsidian registers goes through here,
	 * so a setting added to this class reaches all of them at once.
	 */
	public BlockBehaviour.Properties applyTo(BlockBehaviour.Properties props) {
		props.destroyTime(hardness)
				.explosionResistance(resistance)
				.mapColor(getMapColor())
				.pushReaction(getPushReaction())
				.sound(getBlockSoundGroup())
				.friction(slipperiness)
				.emissiveRendering(state -> is_emissive)
				// A powered block lights differently when the pack asked it to — which is the whole point of
				// a lamp. Blocks without the state, or without a powered_luminance, keep the one light level.
				.lightLevel(state -> poweredLuminance >= 0
						&& state.hasProperty(BlockStateProperties.POWERED)
						&& state.getValue(BlockStateProperties.POWERED)
						? poweredLuminance
						: luminance)
				.speedFactor(velocity_modifier)
				.jumpFactor(jump_velocity_modifier);

		if (translucent) props.noOcclusion();
		if (randomTicks) props.randomTicks();
		if (instant_break) props.instabreak();
		if (!collidable) props.noCollision();
		if (dynamic_boundaries) props.dynamicShape();

		if (requiresTool) props.requiresCorrectToolForDrops();
		if (ignitedByLava) props.ignitedByLava();
		if (replaceable) props.replaceable();
		if (liquid) props.liquid();
		if (noTerrainParticles) props.noTerrainParticles();

		if (bounceRestitution != null) props.bounceRestitution(bounceRestitution);
		if (fallDamageReduction != null) props.fallDistanceReduction(fallDamageReduction);
		if (Boolean.TRUE.equals(forceSolid)) props.forceSolidOn();
		if (Boolean.FALSE.equals(forceSolid)) props.forceSolidOff();

		if (redstoneConductor != null) props.isRedstoneConductor((state, level, pos) -> redstoneConductor);
		if (suffocating != null) props.isSuffocating((state, level, pos) -> suffocating);
		if (viewBlocking != null) props.isViewBlocking((state, level, pos, box) -> viewBlocking);
		if (Boolean.FALSE.equals(spawnable)) props.isValidSpawn((state, level, pos, entityType) -> false);

		if (offsetType != null) props.offsetType(getOffsetType());
		if (instrument != null) props.instrument(getInstrument());

		return props;
	}

	public BlockBehaviour.OffsetType getOffsetType() {
		return switch (offsetType.toUpperCase(Locale.ROOT)) {
			case "XZ" -> BlockBehaviour.OffsetType.XZ;
			case "XYZ" -> BlockBehaviour.OffsetType.XYZ;
			case "NONE" -> BlockBehaviour.OffsetType.NONE;
			default -> throw new IllegalArgumentException("Unknown offset_type \"" + offsetType
					+ "\"; expected none, xz or xyz");
		};
	}

	public NoteBlockInstrument getInstrument() {
		for (NoteBlockInstrument value : NoteBlockInstrument.values()) {
			if (value.getSerializedName().equalsIgnoreCase(instrument)) return value;
		}
		throw new IllegalArgumentException("Unknown instrument \"" + instrument + "\"");
	}
}