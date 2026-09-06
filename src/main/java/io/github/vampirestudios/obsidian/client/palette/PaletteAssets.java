package io.github.vampirestudios.obsidian.client.palette;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.item.Item;
import io.github.vampirestudios.obsidian.api.obsidian.palette.Palette;
import io.github.vampirestudios.obsidian.api.obsidian.palette.PaletteChannel;
import io.github.vampirestudios.obsidian.api.obsidian.palette.PaletteResolver;
import net.minecraft.resources.Identifier;
import net.vampirestudios.packwright.assets.item.ItemModel;
import net.vampirestudios.packwright.assets.item.RangeEntry;
import net.vampirestudios.packwright.assets.item.SelectCase;
import net.vampirestudios.packwright.assets.item.models.ModelBasic;
import net.vampirestudios.packwright.assets.item.models.ModelComposite;
import net.vampirestudios.packwright.assets.item.models.ModelCondition;
import net.vampirestudios.packwright.assets.item.models.ModelRangeDispatch;
import net.vampirestudios.packwright.assets.item.models.ModelSelect;
import net.vampirestudios.packwright.assets.item.tints.Tint;

import java.util.ArrayList;
import java.util.List;

/** Generation-side glue between an asset's channels and the item models Obsidian writes out. */
public final class PaletteAssets {

	private PaletteAssets() {
	}

	/**
	 * Writes one tint entry per channel, in tint-layer order, so the model's {@code tintindex}
	 * regions read their colours from whatever palette the stack carries. Layers left empty get a
	 * neutral tint so the indices still line up.
	 */
	public static void applyTints(Item item, ItemModel model) {
		if (item == null || item.rendering == null || item.information == null) return;
		applyTints(item.rendering.resolveChannels(item.information.id), item.information.id, model);
	}

	public static void applyTints(Identifier channelsId, Identifier ownerId, ItemModel model) {
		if (channelsId == null) return;

		Palette channels = PaletteResolver.palette(channelsId);
		if (channels == null) {
			Obsidian.LOGGER.warn("{} references unknown palette {}", ownerId, channelsId);
			return;
		}

		List<Tint> tints = new ArrayList<>();
		for (PaletteChannel channel : channels.byTintIndex()) {
			tints.add(channel == null
					? Tint.constant(0xFFFFFF)
					: new PaletteTint(channel.name, channels.id, channel.fallbackColor() & 0xFFFFFF));
		}

		for (Tint tint : tints) tintEveryModel(model, tint);
	}

	/**
	 * Adds a tint to every plain model in the tree.
	 *
	 * <p>Only {@code minecraft:model} carries tints — {@code condition}, {@code select},
	 * {@code range_dispatch} and {@code composite} have no such field, and the game ignores one
	 * written there. A sword is a bare model so tinting the root works, but a bow or crossbow is a
	 * condition wrapping a dispatch wrapping the real models, and tinting only the root would
	 * silently colour nothing.</p>
	 */
	public static void tintEveryModel(ItemModel model, Tint tint) {
		if (model == null || tint == null) return;

		switch (model) {
			case ModelBasic basic -> basic.tint(tint);
			case ModelCondition condition -> {
				tintEveryModel(condition.getOnTrue(), tint);
				condition.codecGetOnFalse().ifPresent(onFalse -> tintEveryModel(onFalse, tint));
			}
			case ModelSelect select -> {
				if (select.getCases() != null) {
					for (SelectCase selectCase : select.getCases()) tintEveryModel(selectCase.getModel(), tint);
				}
			}
			case ModelRangeDispatch dispatch -> {
				if (dispatch.getEntries() != null) {
					for (RangeEntry entry : dispatch.getEntries()) tintEveryModel(entry.getModel(), tint);
				}
			}
			case ModelComposite composite -> {
				if (composite.getParts() != null) {
					for (ItemModel part : composite.getParts()) tintEveryModel(part, tint);
				}
			}
			default -> {
			}
		}

		// Every model type can carry a fallback, and it renders like any other branch.
		tintEveryModel(model.getFallback(), tint);
	}
}
