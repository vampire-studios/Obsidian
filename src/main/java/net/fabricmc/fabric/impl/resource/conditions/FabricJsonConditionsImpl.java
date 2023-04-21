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

package net.fabricmc.fabric.impl.resource.conditions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.conditions.v1.JsonResourceConditions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.function.Predicate;

/**
 * Default fabric-provided resource conditions.
 */
public class FabricJsonConditionsImpl {
	public static final Logger LOGGER = LogManager.getLogger("Fabric Json Conditions");

	private static final Predicate<JsonElement> MODS_LOADED = element -> {
		if (element.isJsonArray()) {
			JsonArray array = element.getAsJsonArray();

			for (int i = 0; i < array.size(); ++i) {
				if (!isModEntryLoaded(array.get(i))) {
					return false;
				}
			}

			return true;
		} else if (GsonHelper.isStringValue(element)) {
			return isModEntryLoaded(element);
		} else {
			throw new IllegalArgumentException("Expected mods_loaded entry to be a string or an array, but it is: " + element);
		}
	};

	private static boolean isModEntryLoaded(JsonElement mod) {
		if (GsonHelper.isStringValue(mod)) {
			return FabricLoader.getInstance().isModLoaded(mod.getAsString());
		} else {
			throw new IllegalArgumentException("Expected mods_loaded mod entry to be a string, but it is: " + mod);
		}
	}

	private static final Predicate<JsonElement> OR = element -> {
		if (element.isJsonObject()) {
			JsonObject object = element.getAsJsonObject();

			for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
				Predicate<JsonElement> condition = JsonResourceConditions.get(entry.getKey());

				if (condition == null) {
					throw new IllegalArgumentException("Encountered unknown recipe condition " + entry.getKey());
				} else if (condition.test(entry.getValue())) {
					return true;
				}
			}

			return false;
		} else {
			throw new IllegalArgumentException("The parameter of an OR resource condition must be a JSON object.");
		}
	};

	private static final Predicate<JsonElement> NOT = element -> !JsonResourceConditions.conditionsMatch(element);

	public static void register() {
		JsonResourceConditions.register("never", element -> false);
		JsonResourceConditions.register("always", element -> true);
		JsonResourceConditions.register("mods_loaded", MODS_LOADED);
		JsonResourceConditions.register("or", OR);
		JsonResourceConditions.register("not", NOT);
		JsonResourceConditions.register("boolean", JsonElement::getAsBoolean);
	}
}