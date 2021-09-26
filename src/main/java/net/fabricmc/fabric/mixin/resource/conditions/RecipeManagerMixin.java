/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.mixin.resource.conditions;

import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.resource.conditions.v1.JsonResourceConditions;
import net.fabricmc.fabric.impl.resource.conditions.FabricJsonConditionsImpl;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
	@Inject(
			at = @At("HEAD"),
			method = "apply"
	)
	public void checkRecipeConditions(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
		profiler.push("Fabric: check recipe conditions");
		int skippedRecipes = 0;

		Iterator<Map.Entry<Identifier, JsonElement>> it = map.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<Identifier, JsonElement> entry = it.next();
			JsonElement recipeData = entry.getValue();

			try {
				if (recipeData.isJsonObject() && !JsonResourceConditions.objectMatchesConditions(recipeData.getAsJsonObject())) {
					it.remove();
					skippedRecipes++;
				}
			} catch (RuntimeException exception) {
				String message = String.format(
						"Failed to parse recipe conditions for recipe with identifier \"%s\". Loading it anyway.",
						entry.getKey()
				);
				FabricJsonConditionsImpl.LOGGER.error(message, exception);
			}
		}

		FabricJsonConditionsImpl.LOGGER.info(String.format("Fabric JSON recipe conditions: skipping %d recipes.", skippedRecipes));
		profiler.pop();
	}
}