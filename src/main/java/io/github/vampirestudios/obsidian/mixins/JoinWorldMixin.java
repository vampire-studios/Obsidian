package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.Obsidian;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameJoinS2CPacket.class)
//TODO: make the server send the dp blocks+items to the client for the client to register
public class JoinWorldMixin {
	@Shadow private DynamicRegistryManager.Impl registryManager;
	
	@Inject(at = @At("TAIL"),method = "read(Lnet/minecraft/network/PacketByteBuf;)V")
	public void doRegistries(PacketByteBuf buf, CallbackInfo ci) {
		MutableRegistry<Item> items = registryManager.get(Registry.ITEM.getKey());
		items.forEach((item) -> {
			if (!Registry.ITEM.containsId(items.getId(item))) {
				Obsidian.LOGGER.info("Registering an item called {}", items.getId(item));
				Registry.register(Registry.ITEM, items.getId(item), item);
			}
		});
		MutableRegistry<Block> blocks = registryManager.get(Registry.BLOCK.getKey());
		blocks.forEach((block) -> {
			if (!Registry.BLOCK.containsId(blocks.getId(block))) {
				Obsidian.LOGGER.info("Registering a block called {}", blocks.getId(block));
				Registry.register(Registry.BLOCK, blocks.getId(block), block);
			}
		});
		/*MutableRegistry<ItemGroup> creativeTabs = registryManager.get(Obsidian.ITEM_GROUP_REGISTRY.getKey());
		creativeTabs.forEach((itemGroup) -> {
			if (!Obsidian.ITEM_GROUP_REGISTRY.containsId(creativeTabs.getId(itemGroup))) {
				Obsidian.LOGGER.info("Registering an item group called {}", creativeTabs.getId(itemGroup));
				Registry.register(Obsidian.ITEM_GROUP_REGISTRY, creativeTabs.getId(itemGroup), itemGroup);
			}
		});
		MutableRegistry<Enchantment> enchantments = registryManager.get(Registry.ENCHANTMENT.getKey());
		enchantments.forEach((enchantment) -> {
			if (!Registry.ENCHANTMENT.containsId(enchantments.getId(enchantment))) {
				Obsidian.LOGGER.info("Registering an enchantment called {}", enchantments.getId(enchantment));
				Registry.register(Registry.ENCHANTMENT, enchantments.getId(enchantment), enchantment);
			}
		});
		MutableRegistry<Potion> potions = registryManager.get(Registry.POTION.getKey());
		potions.forEach((potion) -> {
			if (!Registry.POTION.containsId(potions.getId(potion))) {
				Obsidian.LOGGER.info("Registering a potion called {}", potions.getId(potion));
				Registry.register(Registry.POTION, potions.getId(potion), potion);
			}
		});
		MutableRegistry<StatusEffect> statusEffects = registryManager.get(Registry.STATUS_EFFECT.getKey());
		statusEffects.forEach((statusEffect) -> {
			if (!Registry.STATUS_EFFECT.containsId(statusEffects.getId(statusEffect))) {
				Obsidian.LOGGER.info("Registering a status effect called {}", statusEffects.getId(statusEffect));
				Registry.register(Registry.STATUS_EFFECT, statusEffects.getId(statusEffect), statusEffect);
			}
		});*/
	}
}