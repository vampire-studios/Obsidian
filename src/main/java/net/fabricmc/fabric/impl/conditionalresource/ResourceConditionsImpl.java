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

package net.fabricmc.fabric.impl.conditionalresource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import net.fabricmc.fabric.api.conditionalresource.v1.ResourceCondition;
import net.fabricmc.fabric.api.conditionalresource.v1.ResourceConditions;

@ApiStatus.Internal
public final class ResourceConditionsImpl {
	private ResourceConditionsImpl() {
	}

	public static boolean evaluate(Identifier fabricMetaId, JsonElement element) {
		if (!element.isJsonObject()) throw new IllegalArgumentException("Condition element is not an object!");
		JsonObject object = element.getAsJsonObject();

		Identifier type = new Identifier(JsonHelper.getString(object, "type"));
		ResourceCondition condition = ResourceConditions.RESOURCE_CONDITION_REGISTRY.get(fabricMetaId);
		if (condition == null) throw new NullPointerException("Condition '" + type + "' does not exist!");

		return condition.process(fabricMetaId, object.has("condition") ? object.get("condition") : null);
	}
}