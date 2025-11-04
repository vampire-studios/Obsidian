/*
package org.quiltmc.qsl.enchantment.mixin;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.random.Weight;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.quiltmc.qsl.enchantment.api.QuiltEnchantment;
import org.quiltmc.qsl.enchantment.impl.EnchantmentContext;
import org.quiltmc.qsl.enchantment.impl.EnchantmentGodClass;
import org.quiltmc.qsl.enchantment.mixinterface.MutableWeight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

@Mixin(value = EnchantmentHelper.class)
public class EnchantmentHelperMixin {
	// This mixin prevents the whole "I can't get your mixin target" thingy
	@Redirect(method = "getAvailableEnchantmentResults", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;iterator()Ljava/util/Iterator;"))
	private static Iterator<Enchantment> removeCustomEnchants(Registry<Enchantment> registry) {
		return registry.stream().filter((enchantment) -> !(enchantment instanceof QuiltEnchantment)).iterator();
	}

	@Inject(method = "getAvailableEnchantmentResults", at = @At("RETURN"), cancellable = true)
	private static void handleCustomEnchants(FeatureFlagSet enabledFeatures, int level, ItemStack stack, boolean allowTreasure, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
		List<EnchantmentInstance> extraEntries = cir.getReturnValue();
		BuiltInRegistries.ENCHANTMENT.stream().filter((enchantment) -> enchantment instanceof QuiltEnchantment).forEach((enchantment) -> {
			for (int level1 = enchantment.getMinLevel(); level1 <= enchantment.getMaxLevel(); level1++) {
				EnchantmentContext context = EnchantmentGodClass.context.get().withLevel(level1).withPower(level);
				int probability = ((QuiltEnchantment) enchantment).weightFromEnchantmentContext(context);
				if (probability > 0) {
					EnchantmentInstance entry = new EnchantmentInstance(enchantment, level1);
					((MutableWeight) entry).setWeight(Weight.of(probability));
					extraEntries.add(entry);
				}
			}
		});
		cir.setReturnValue(extraEntries);
	}
}*/
