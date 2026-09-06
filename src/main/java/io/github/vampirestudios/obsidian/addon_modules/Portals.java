package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.world.Portal;
import io.github.vampirestudios.obsidian.minecraft.obsidian.PortalLogic;
import io.github.vampirestudios.obsidian.minecraft.obsidian.PortalFrame;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.io.File;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Portals implements AddonModule {

	/** Registered once, the first time a portal is loaded, rather than per definition. */
	private static boolean ignitionHooked = false;

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		Portal portal = AddonFormats.read(addon, file, Portal.class);
		if (portal == null) return;

		Identifier portalId = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));
		try {
			portal.id = portalId;

			if (portal.block == null) {
				Obsidian.LOGGER.warn("Portal {} names no block to fill its frame with; skipping it.", portalId);
				return;
			}

			// The block is the pack's own, from block/. Portals load after blocks, so it exists by now.
			Block portalBlock = BuiltInRegistries.BLOCK.getValue(portal.block);
			if (portalBlock == null || portalBlock == Blocks.AIR) {
				Obsidian.LOGGER.warn("Portal {} names unknown block {}; skipping it.", portalId, portal.block);
				return;
			}

			PortalLogic.bind(portalBlock, portal);
			register(ContentRegistries.PORTALS, "portal", portalId, portal);

			hookIgnition();
		} catch (Exception e) {
			failedRegistering("portal", file.getName(), e);
		}
	}

	/**
	 * Lighting a frame. One callback serves every portal a pack declares — it asks the registry which
	 * definition, if any, the item and block in hand match.
	 */
	private static void hookIgnition() {
		if (ignitionHooked) return;
		ignitionHooked = true;

		UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
			if (level.isClientSide()) return InteractionResult.PASS;

			ItemStack held = player.getItemInHand(hand);
			if (held.isEmpty()) return InteractionResult.PASS;

			BlockPos clicked = hit.getBlockPos();
			BlockState state = level.getBlockState(clicked);

			for (Portal portal : ContentRegistries.PORTALS) {
				if (!matchesIgnition(portal, held)) continue;
				if (portal.frame == null || !state.is(BuiltInRegistries.BLOCK.getValue(portal.frame))) continue;

				if (light(level, clicked.relative(hit.getDirection()), portal, player)) {
					return InteractionResult.SUCCESS;
				}
			}
			return InteractionResult.PASS;
		});
	}

	private static boolean matchesIgnition(Portal portal, ItemStack held) {
		if (portal.ignition == null) return false;
		return held.is(BuiltInRegistries.ITEM.getValue(portal.ignition));
	}

	/** Fills the frame around {@code inside}, if there is a valid one. */
	private static boolean light(Level level, BlockPos inside, Portal portal, Player player) {
		PortalFrame.Found found = PortalFrame.find(level, inside, portal);
		if (found == null) return false;

		Block portalBlock = BuiltInRegistries.BLOCK.getValue(portal.block);
		if (portalBlock == null) return false;

		BlockState filled = PortalLogic.fillState(portalBlock, found.axis());
		found.interior().forEach(pos -> level.setBlock(pos, filled, Block.UPDATE_ALL));

		if (portal.sound != null) {
			var sound = BuiltInRegistries.SOUND_EVENT.getValue(portal.sound);
			if (sound != null) {
				level.playSound(null, inside, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
			}
		}

		Obsidian.LOGGER.debug("Lit portal {} at {}", portal.id, inside);
		return true;
	}

	@Override
	public String getType() {
		return "world/portal";
	}
}
