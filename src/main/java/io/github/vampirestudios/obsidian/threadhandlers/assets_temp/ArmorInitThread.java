package io.github.vampirestudios.obsidian.threadhandlers.assets_temp;

import io.github.vampirestudios.obsidian.api.obsidian.ItemDisplayInformation;
import io.github.vampirestudios.obsidian.api.obsidian.RenderModeModel;
import io.github.vampirestudios.obsidian.api.obsidian.SpecialText;
import io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem;
import io.github.vampirestudios.obsidian.client.ARRPGenerationHelper;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.vampirestudios.packwright.api.RuntimeResourcePack;
import net.vampirestudios.packwright.assets.item.ItemModel;
import io.github.vampirestudios.obsidian.client.palette.PaletteAssets;
import net.vampirestudios.packwright.assets.item.ItemModelDefinition;
import net.vampirestudios.packwright.assets.item.SelectCase;
import net.vampirestudios.packwright.assets.item.models.ModelBasic;
import net.vampirestudios.packwright.assets.item.models.ModelSelect;
import net.vampirestudios.packwright.assets.item.properties.PropertyDisplayContext;
import net.vampirestudios.packwright.assets.item.tints.TintDye;

import java.util.Map;

public class ArmorInitThread implements Runnable {
	private final ArmorItem armor;
	private final RuntimeResourcePack resourcePack;

	public ArmorInitThread(RuntimeResourcePack resourcePack, ArmorItem item) {
		this.armor = item;
		this.resourcePack = resourcePack;
	}

	@Override
	public void run() {
		if (armor.information.name.translations != null)
			armor.information.name.translations.forEach((languageId, name) -> ClientInit.addTranslation(
					armor.information.id.getNamespace(), languageId,
					"item." + armor.information.id.getNamespace() + "." + armor.information.id.getPath(),
					name
			));

		ItemModelDefinition itemInfo = new ItemModelDefinition();

		var itemId = armor.information.id;

		Identifier defModelId = armor.rendering != null
				? armor.rendering.resolveItemDefinitionModelId(itemId)
				: Utils.prependToPath(itemId, "item/");

		ModelBasic fallbackModel = ModelBasic.model(defModelId);
		ItemModel model = fallbackModel;
		if (armor.information != null && armor.information.getItemSettings() != null &&
				armor.information.getItemSettings().renderModeModels != null &&
				armor.information.getItemSettings().customRenderMode) {
			ModelSelect select = new ModelSelect().property(PropertyDisplayContext.displayContext());
			for (RenderModeModel renderModeModel : armor.information.getItemSettings().renderModeModels) {
				SelectCase caseX = SelectCase.of(
						renderModeModel.modes,
						ItemModel.model(renderModeModel.model)
				);
				select.addCase(caseX);
			}
			select.fallback(fallbackModel);
			model = select;
		}
		if (armor.information.getItemSettings().dyeable) {
			PaletteAssets.tintEveryModel(model, new TintDye(armor.information.getItemSettings().defaultColor));
		}

		PaletteAssets.applyTints(armor, model);
		if (armor.rendering != null) {
			boolean hasOutItemModel =
					resourcePack.getResource(PackType.CLIENT_RESOURCES, Utils.prependToPath(itemId, "item/")) != null;

			if (!hasOutItemModel && armor.rendering.hasItemModelObject()) {
				var info = armor.rendering.getItemModel();
				ARRPGenerationHelper.generateItemModel(resourcePack, itemId, info.parent, info.textures);
			}
		}
		if (armor.lore != null) {
			for (SpecialText lore : armor.getLore()) {
				if (lore.textType != null && lore.textType.equals("translatable")) {
					lore.translations.forEach((languageId, name) -> ClientInit.addTranslation(
							armor.information.id.getNamespace(), languageId, lore.text, name
					));
				}
			}
		}
		// Always emit items/<id>.json — see ItemInitThread for explanation.
		itemInfo.model(model);
		resourcePack.addItemModelInfo(itemInfo, itemId);

		if (armor.hasCustomMaterial()) {
			Map<String, ItemDisplayInformation.EquipmentLayer> layers = armor.rendering != null
					? armor.rendering.resolveEquipment()
					: Map.of();
			// The asset id is the item id — see the custom ArmorMaterial in the Armor module.
			ARRPGenerationHelper.generateEquipmentModel(resourcePack, itemId, layers);
		}
	}
}
