package io.github.vampirestudios.obsidian.api.obsidian.block;

import com.google.gson.annotations.SerializedName;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * A berry bush: a plant that grows through a few stages, can be picked by hand once it is ripe without
 * being broken, and scratches whatever walks through it.
 *
 * <p>Growth, bone meal and per-stage shapes come from {@link GrowthSettings}; what is here is picking
 * and scratching. Every field has a default that reproduces vanilla's sweet berry bush, so an empty
 * {@code "bush_properties": {}} is already a working bush.
 */
public class BushProperties extends GrowthSettings {

	{
		// A crop takes several stages from one bone meal; a berry bush takes one. Same setting, different
		// starting point, so a pack that says nothing gets the vanilla behaviour for the block it declared.
		bonemealMin = 1;
		bonemealMax = 1;
	}

	/** The last growth stage. Ages above 3 need eight states, which is as far as the game's age goes. */
	@SerializedName("max_age")
	public int maxAge = 3;

	/** The first stage that can be picked. Below it the bush is still bare. */
	@SerializedName("ripe_age")
	public int ripeAge = 2;

	/** What picking gives. Without one, the bush gives its own item. */
	public Identifier berry;

	@SerializedName("min_berries")
	public int minBerries = 1;

	@SerializedName("max_berries")
	public int maxBerries = 2;

	/** Berries on top of that, once the bush is fully grown rather than merely ripe. */
	@SerializedName("bonus_when_ripe")
	public int bonusWhenRipe = 1;

	/**
	 * The stage a picked bush drops back to. Without one it drops to the stage below {@code ripe_age},
	 * so it is picked over and over rather than once.
	 */
	@SerializedName("picked_age")
	public Integer pickedAge;

	/** Damage to something moving through a bush that has started to grow. {@code 0} turns it off. */
	public float damage = 1.0F;

	/** The kind of damage that is. Anything in the game's damage type registry, yours included. */
	@SerializedName("damage_type")
	public Identifier damageType = Identifier.withDefaultNamespace("sweet_berry_bush");

	/**
	 * The entities a bush never hurts and never slows. Vanilla's berry bushes are home to foxes and bees,
	 * which is why those two are the default; an empty list makes the bush hurt everything.
	 */
	@SerializedName("immune_entities")
	public List<Identifier> immuneEntities;

	/**
	 * How much a bush slows something pushing through it, per axis, as a fraction of normal movement.
	 * A single number applies to all three. {@code 1} is no slowing at all.
	 */
	public Object slowdown;

	/** The sound picking makes. */
	@SerializedName("pick_sound")
	public Identifier pickSound = Identifier.withDefaultNamespace("block.sweet_berry_bush.pick_berries");

	/** The declaration's bush settings, or vanilla's defaults when it left them out. */
	public static BushProperties of(Block block) {
		return block != null && block.bushProperties != null ? block.bushProperties : new BushProperties();
	}

	/** How far the bush grows, held to what the age property it will use can express. */
	public int resolvedMaxAge() {
		return Mth.clamp(maxAge, 1, 7);
	}

	/**
	 * The first age that can be picked, which is never past the last one — a bush that could never be
	 * picked would only look broken.
	 */
	public int resolvedRipeAge() {
		return Mth.clamp(ripeAge, 1, resolvedMaxAge());
	}

	/** The age a picked bush drops back to, which is by default the last one that shows no berries. */
	public int resolvedPickedAge() {
		int fallback = resolvedRipeAge() - 1;
		return pickedAge == null ? fallback : Mth.clamp(pickedAge, 0, resolvedMaxAge());
	}

	/** How many berries one picking gives at {@code age}. */
	public int berriesAt(int age, net.minecraft.util.RandomSource random) {
		int min = Math.max(0, minBerries);
		int max = Math.max(min, maxBerries);
		int count = min + random.nextInt(max - min + 1);
		return age >= resolvedMaxAge() ? count + Math.max(0, bonusWhenRipe) : count;
	}

	/**
	 * The state property holding the bush's age. Four stages fit vanilla's {@code age_3}, the one a berry
	 * bush uses; anything taller needs {@code age_7}.
	 */
	public IntegerProperty ageProperty() {
		return resolvedMaxAge() <= 3 ? BlockStateProperties.AGE_3 : BlockStateProperties.AGE_7;
	}

	/**
	 * How many ages the state property actually has, which is not the same as how far the bush grows — a
	 * bush that stops at 2 still has an {@code age_3} property, and the blockstate has to name every one
	 * of its values or the game finds no model for the ones left out.
	 */
	public int ageStateCount() {
		return resolvedMaxAge() <= 3 ? 4 : 8;
	}

	public SoundEvent getPickSound() {
		return pickSound == null ? null : BuiltInRegistries.SOUND_EVENT.getValue(pickSound);
	}

	/** How much a bush slows movement, as vanilla's own {@code 0.8, 0.75, 0.8} unless the pack said otherwise. */
	public Vec3 getSlowdown() {
		if (slowdown instanceof Number number) {
			double factor = number.doubleValue();
			return new Vec3(factor, factor, factor);
		}
		if (slowdown instanceof List<?> list && list.size() >= 3) {
			return new Vec3(asDouble(list.get(0), 0.8), asDouble(list.get(1), 0.75), asDouble(list.get(2), 0.8));
		}
		return new Vec3(0.8, 0.75, 0.8);
	}

	/** Whether the bush leaves this entity alone entirely — no damage, no slowing. */
	public boolean isImmune(EntityType<?> type) {
		for (Identifier id : resolvedImmuneEntities()) {
			if (BuiltInRegistries.ENTITY_TYPE.getValue(id) == type) return true;
		}
		return false;
	}

	private List<Identifier> resolvedImmuneEntities() {
		if (immuneEntities != null) return immuneEntities;
		List<Identifier> vanilla = new ArrayList<>(2);
		vanilla.add(Identifier.withDefaultNamespace("fox"));
		vanilla.add(Identifier.withDefaultNamespace("bee"));
		return vanilla;
	}

	private static double asDouble(Object value, double fallback) {
		return value instanceof Number number ? number.doubleValue() : fallback;
	}
}
