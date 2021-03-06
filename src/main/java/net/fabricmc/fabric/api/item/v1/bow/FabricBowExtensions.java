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

package net.fabricmc.fabric.api.item.v1.bow;

import net.fabricmc.fabric.api.item.v1.ShotProjectileEvents;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;

/**
 * An interface to implement for all custom bows in fabric. <br>
 * This is meant to be used on a {@link BowItem} class. Unless similar functionality is implemented on your custom item, most functionality will not work.
 *
 * @see net.fabricmc.fabric.api.item.v1.bow.FabricBowItem
 */
public interface FabricBowExtensions extends ShotProjectileEvents.ModifyProjectileFromBow {
	/**
	 * Returns the pull progress of the bow between 0 and 1.
	 *
	 * @param useTicks the number of ticks the bow has been pulled.
	 * @param bowStack the ItemStack for the bow
	 * @return the progress of the pull from {@code 0.0f} to {@code 1.0f}.
	 */
	default float getCustomPullProgress(int useTicks, ItemStack bowStack) {
		return BowItem.getPullProgress(useTicks);
	}
}