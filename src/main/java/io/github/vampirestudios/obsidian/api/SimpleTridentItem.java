package io.github.vampirestudios.obsidian.api;

import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.TridentItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * This is the default implementation for FabricTrident, allowing for the easy creation of new tridents with no new modded functionality.
 */
public class SimpleTridentItem extends TridentItem implements TridentInterface {
	private static final Identifier DEFAULT_TEXTURE = TridentEntityModel.TEXTURE;

	private final Identifier tridentEntityIdentifier;

	public SimpleTridentItem(Settings settings) {
		this(settings, DEFAULT_TEXTURE);
	}

	public SimpleTridentItem(Settings settings, Identifier tridentEntityTexture) {
		super(settings);
		this.tridentEntityIdentifier = tridentEntityTexture;
	}

	@Override
	public ModelIdentifier getInventoryModelIdentifier() {
		// super hacky, probably a better way but it works
		return new ModelIdentifier(Registry.ITEM.getId(this) + "#inventory");
	}

	@Override
	public Identifier getEntityTexture() {
		return this.tridentEntityIdentifier;
	}

	@Override
	public TridentEntity modifyTridentEntity(TridentEntity trident) {
		return new SimpleTridentItemEntity(trident);
	}
}