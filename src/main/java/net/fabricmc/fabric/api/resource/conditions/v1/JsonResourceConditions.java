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

package net.fabricmc.fabric.api.resource.conditions.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.impl.resource.conditions.FabricJsonConditionsImpl;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Registration and access to JSON resource conditions.
 * A JSON resource condition is a named {@code Predicate<JsonElement>} that can get tested when loading JSON resources.
 *
 * <p>TODO: fabric defines the following resource conditions: ...
 *
 * <p>TODO: fabric supports resource conditions in the following places: ...
 */
public final class JsonResourceConditions {
	private static final Map<String, Predicate<JsonElement>> REGISTERED_CONDITIONS = new ConcurrentHashMap<>();
	/**
	 * The key fabric uses to identify resource conditions in a JSON object.
	 */
	public static final String CONDITIONS_KEY = "fabric:conditions";

	/**
	 * Register a new resource condition.
	 * Mods should prefix the name of their conditions with {@code modid:} to make sure there are no clashes.
	 * The only exceptions should be very common conditions, provided by fabric itself.
	 *
	 * @throws IllegalArgumentException If a resource condition is already registered with the same name.
	 */
	public static void register(String name, Predicate<JsonElement> condition) {
		Objects.requireNonNull(condition, "Condition may not be null.");
		name = name.toLowerCase(Locale.ROOT);

		if (REGISTERED_CONDITIONS.put(name, condition) != null) {
			throw new IllegalArgumentException("Duplicate JSON condition registration for name " + name);
		}
	}

	/**
	 * Get the resource condition with the passed name, or {@code null} if none is registered (yet).
	 */
	@Nullable
	public static Predicate<JsonElement> get(String name) {
		return REGISTERED_CONDITIONS.get(name.toLowerCase(Locale.ROOT));
	}

	/**
	 * Check if the passed JSON object either has no {@code fabric:conditions} tag, or all of its conditions match.
	 * This should be called for objects that may contain a conditions entry.
	 */
	public static boolean objectMatchesConditions(JsonObject object) {
		JsonElement conditions = object.get(CONDITIONS_KEY);

		if (conditions == null) {
			return true; // no conditions
		} else {
			return conditionsMatch(conditions);
		}
	}

	/**
	 * Check if the passed JSON conditions match.
	 * This should be called for the conditions entry itself.
	 */
	public static boolean conditionsMatch(JsonElement element) {
		if (element.isJsonObject()) {
			JsonObject object = element.getAsJsonObject();

			for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
				Predicate<JsonElement> condition = get(entry.getKey());

				if (condition == null) {
					throw new IllegalArgumentException("Encountered unknown recipe condition " + entry.getKey());
				} else if (!condition.test(entry.getValue())) {
					// All conditions must match.
					return false;
				}
			}

			return true;
		} else {
			throw new IllegalArgumentException("The conditions must be a JSON object.");
		}
	}

	private JsonResourceConditions() {}

	static {
		// Load Fabric-provided conditions.
		FabricJsonConditionsImpl.register();
	}
}