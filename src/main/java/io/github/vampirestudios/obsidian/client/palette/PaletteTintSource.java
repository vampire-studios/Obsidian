package io.github.vampirestudios.obsidian.client.palette;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.palette.PaletteResolver;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Vanilla tint source backing one channel of a colourable item.
 *
 * <p>Generated item models get one of these per tint layer, so recolouring never touches the model
 * or the texture: {@code example:longsword} keeps a single model and a single texture, and the
 * blade, grip and gem regions read their colours from whatever palette the stack carries.</p>
 *
 * <pre>{@code
 * { "type": "obsidian:palette", "channel": "blade", "channels": "example:sword", "default": "#B0C4DE" }
 * }</pre>
 */
@Environment(EnvType.CLIENT)
public record PaletteTintSource(String channel, Optional<Identifier> channels, int fallback) implements ItemTintSource {

	public static final Identifier ID = Obsidian.id("palette");

	public static final MapCodec<PaletteTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			com.mojang.serialization.Codec.STRING.fieldOf("channel").forGetter(PaletteTintSource::channel),
			Identifier.CODEC.optionalFieldOf("channels").forGetter(PaletteTintSource::channels),
			ExtraCodecs.RGB_COLOR_CODEC.optionalFieldOf("default", 0xFFFFFF).forGetter(PaletteTintSource::fallback)
	).apply(instance, PaletteTintSource::new));

	/**
	 * Teaches the vanilla item model parser about {@code "type": "obsidian:palette"}.
	 * Touching {@link ItemTintSources} first runs its own bootstrap, so vanilla types stay intact.
	 */
	public static void register() {
		ItemTintSources.ID_MAPPER.put(ID, MAP_CODEC);
	}

	@Override
	public int calculate(ItemStack stack, ClientLevel level, LivingEntity entity) {
		int fallbackArgb = 0xFF000000 | this.fallback;
		return PaletteResolver.resolveForStack(stack, this.channels.orElse(null), this.channel, fallbackArgb);
	}

	@Override
	public MapCodec<? extends ItemTintSource> type() {
		return MAP_CODEC;
	}
}
