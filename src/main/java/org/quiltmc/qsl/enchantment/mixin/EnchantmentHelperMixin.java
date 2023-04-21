package org.quiltmc.qsl.enchantment.mixin;

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
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.random.Weight;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

@Mixin(value = EnchantmentHelper.class)
public class EnchantmentHelperMixin {
	// This mixin prevents the whole "I can't get your mixin target" thingy
	@Redirect(method = "getPossibleEntries(ILnet/minecraft/item/ItemStack;Z)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/Registry;iterator()Ljava/util/Iterator;"))
	private static Iterator<Enchantment> removeCustomEnchants(Registry<Enchantment> registry) {
		return registry.stream().filter((enchantment) -> !(enchantment instanceof QuiltEnchantment)).iterator();
	}

	@Inject(method = "getPossibleEntries", at = @At("RETURN"), cancellable = true)
	private static void handleCustomEnchants(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentInstance>> callback) {
		List<EnchantmentInstance> extraEntries = callback.getReturnValue();
		BuiltInRegistries.ENCHANTMENT.stream().filter((enchantment) -> enchantment instanceof QuiltEnchantment).forEach((enchantment) -> {
			for (int level = enchantment.getMinLevel(); level <= enchantment.getMaxLevel(); level++) {
				EnchantmentContext context = EnchantmentGodClass.context.get().withLevel(level).withPower(power);
				int probability = ((QuiltEnchantment) enchantment).weightFromEnchantmentContext(context);
				if (probability > 0) {
					EnchantmentInstance entry = new EnchantmentInstance(enchantment, level);
					((MutableWeight) entry).setWeight(Weight.of(probability));
					extraEntries.add(entry);
				}
			}
		});
		callback.setReturnValue(extraEntries);
	}
}