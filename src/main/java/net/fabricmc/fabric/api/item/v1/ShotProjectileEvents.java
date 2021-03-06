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

package net.fabricmc.fabric.api.item.v1;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class ShotProjectileEvents {
	private ShotProjectileEvents() {
	}

	/**
	 * This event modifies the projectile entity shot from a crossbow.
	 */
	public static final Event<ModifyProjectileFromCrossbow> CROSSBOW_MODIFY_SHOT_PROJECTILE = EventFactory.createArrayBacked(ModifyProjectileFromCrossbow.class, callbacks -> (bowStack, projectileStack, user, persistentProjectileEntity) -> {
		for (ModifyProjectileFromCrossbow callback : callbacks) {
			callback.modifyProjectileShot(bowStack, projectileStack, user, persistentProjectileEntity);
		}
	});

	/**
	 * This event replaces the projectile entity shot from a crossbow. Any modifications done in this step without returning a new entity can be erased.
	 * Do not use this event to only modify the arrow entity, as {@link ShotProjectileEvents#CROSSBOW_MODIFY_SHOT_PROJECTILE} is the proper event.
	 */
	public static final Event<ReplaceProjectileFromCrossbow> CROSSBOW_REPLACE_SHOT_PROJECTILE = EventFactory.createArrayBacked(ReplaceProjectileFromCrossbow.class, callbacks -> (bowStack, projectileStack, user, persistentProjectileEntity) -> {
		for (ReplaceProjectileFromCrossbow callback : callbacks) {
			PersistentProjectileEntity replacedEntity = callback.replaceProjectileShot(bowStack, projectileStack, user, persistentProjectileEntity);

			if (replacedEntity != null) {
				return replacedEntity;
			}
		}

		return persistentProjectileEntity;
	});

	/**
	 * This event modifies the projectile entity. Returning a new entity will not change the entity that is spawned in.
	 */
	public static final Event<ModifyProjectileFromBow> BOW_MODIFY_SHOT_PROJECTILE = EventFactory.createArrayBacked(ModifyProjectileFromBow.class, callbacks -> (bowStack, arrowStack, user, pullProgress, persistentProjectileEntity) -> {
		for (ModifyProjectileFromBow callback : callbacks) {
			callback.modifyProjectileShot(bowStack, arrowStack, user, pullProgress, persistentProjectileEntity);
		}
	});

	/**
	 * This event replaces the projectile entity. Any modifications done in this step without returning a new entity can be erased.
	 * Do not use this event to only modify the arrow entity, as {@link ShotProjectileEvents#BOW_MODIFY_SHOT_PROJECTILE} is the proper event.
	 */
	public static final Event<ReplaceProjectileFromBow> BOW_REPLACE_SHOT_PROJECTILE = EventFactory.createArrayBacked(ReplaceProjectileFromBow.class, callbacks -> (bowStack, arrowStack, user, pullProgress, persistentProjectileEntity) -> {
		for (ReplaceProjectileFromBow callback : callbacks) {
			PersistentProjectileEntity replacedEntity = callback.replaceProjectileShot(bowStack, arrowStack, user, pullProgress, persistentProjectileEntity);

			if (replacedEntity != null) {
				return replacedEntity;
			}
		}

		return persistentProjectileEntity;
	});

	public interface ReplaceProjectileFromCrossbow {
		/**
		 * In this method you can replace the arrow shot from your custom crossbow. Applies all of the vanilla arrow modifiers first.
		 *
		 * @param crossbowStack              the ItemStack for the Crossbow Item
		 * @param projectileStack            the ItemStack for the projectile currently being shot
		 * @param user                       the user of the crossbow
		 * @param persistentProjectileEntity the arrow entity to be spawned
		 * @return the new projectile entity. Return {@code null} if you do not change the entity.
		 */
		PersistentProjectileEntity replaceProjectileShot(ItemStack crossbowStack, ItemStack projectileStack, LivingEntity user, @NotNull PersistentProjectileEntity persistentProjectileEntity);
	}

	public interface ModifyProjectileFromCrossbow {
		/**
		 * In this method you can modify the behavior of arrows shot from your custom crossbow. Applies all of the vanilla arrow modifiers first.
		 *
		 * @param crossbowStack              the ItemStack for the Crossbow Item
		 * @param projectileStack            the ItemStack for the projectile currently being shot
		 * @param user                       the user of the crossbow
		 * @param persistentProjectileEntity the arrow entity to be spawned
		 */
		void modifyProjectileShot(ItemStack crossbowStack, ItemStack projectileStack, LivingEntity user, @NotNull PersistentProjectileEntity persistentProjectileEntity);
	}

	public interface ReplaceProjectileFromBow {
		/**
		 * In this method you can replace the arrow shot from your custom bow. Applies all of the vanilla arrow modifiers first.
		 *
		 * @param bowStack                   the ItemStack for the Bow Item
		 * @param arrowStack                 the ItemStack for the arrows
		 * @param user                       the user of the bow
		 * @param pullProgress               the pull progress of the bow from 0.0 to 1.0
		 * @param persistentProjectileEntity the arrow entity to be spawned
		 * @return the arrow entity, either new or {@code null} to signify no changes occurred
		 */
		PersistentProjectileEntity replaceProjectileShot(ItemStack bowStack, ItemStack arrowStack, LivingEntity user, float pullProgress, @NotNull PersistentProjectileEntity persistentProjectileEntity);
	}

	public interface ModifyProjectileFromBow {
		/**
		 * In this method you can modify the behavior of arrows shot from your custom bow. Applies all of the vanilla arrow modifiers first.
		 *
		 * @param bowStack                   the ItemStack for the Bow Item
		 * @param arrowStack                 the ItemStack for the arrows
		 * @param user                       the user of the bow
		 * @param pullProgress               the pull progress of the bow from 0.0 to 1.0
		 * @param persistentProjectileEntity the arrow entity to be spawned
		 */
		void modifyProjectileShot(ItemStack bowStack, ItemStack arrowStack, LivingEntity user, float pullProgress, @NotNull PersistentProjectileEntity persistentProjectileEntity);
	}
}