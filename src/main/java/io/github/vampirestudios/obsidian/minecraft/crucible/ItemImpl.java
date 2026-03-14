package io.github.vampirestudios.obsidian.minecraft.crucible;

import eu.pb4.placeholders.api.parsers.TagParser;
import io.github.vampirestudios.obsidian.api.crucible.*;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.OItemComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

public class ItemImpl extends Item {
    public final CrucibleItem item;

    public ItemImpl(CrucibleItem item, Properties settings) {
        super(settings);
        this.item = item;
    }

    @Override
    public Component getName(ItemStack stack) {
        return TagParser.QUICK_TEXT_WITH_STF.parseNode(item.Display).toText();
    }

    @Override
    public void appendHoverText(ItemStack stack,
                                TooltipContext tooltipContext,
                                TooltipDisplay tooltipDisplay,
                                Consumer<Component> tooltip,
                                TooltipFlag context) {
        if (item.Lore != null) {
            for (String lore : item.Lore) {
                tooltip.accept(TagParser.QUICK_TEXT_WITH_STF.parseNode(lore).toText());
            }
        }

        AugmentSocketData sockets = stack.get(OItemComponents.AUGMENT_SOCKETS);
        if (sockets != null) {
            for (AugmentSlotEntry slot : sockets.slots()) {
                CrucibleAugmentType augType = findAugmentType(slot.type());
                String line;
                if (slot.isEmpty()) {
                    line = augType != null ? augType.resolveEmptyLine()
                            : "§8○ Empty " + slot.type() + " Slot";
                } else {
                    String augIdStr = slot.augmentId().get();
                    Identifier augId = Identifier.tryParse(augIdStr);
                    CrucibleAugment augment = augId != null ? ContentRegistries.AUGMENTS.getValue(augId) : null;
                    String augTooltip = augment != null ? augment.getEffectiveTooltip() : augIdStr;
                    String icon = augment != null ? augment.Icon : null;
                    line = augType != null ? augType.resolveFilledLine(augTooltip, icon)
                            : "§8● " + slot.type() + ": " + augTooltip;
                }
                tooltip.accept(TagParser.QUICK_TEXT_WITH_STF.parseNode(line).toText());
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) return super.use(level, player, hand);

        ItemStack mainStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        AugmentSocketData sockets = mainStack.get(OItemComponents.AUGMENT_SOCKETS);
        if (sockets == null) return super.use(level, player, hand);
        if (level.isClientSide()) return InteractionResultHolder.success(mainStack);

        ItemStack offStack = player.getItemInHand(InteractionHand.OFF_HAND);
        if (offStack.isEmpty()) return super.use(level, player, hand);

        Identifier offItemId = BuiltInRegistries.ITEM.getKey(offStack.getItem());
        CrucibleItem offCrucible = offItemId != null ? ContentRegistries.CRUCIBLE_ITEMS.getValue(offItemId) : null;
        if (offCrucible == null) return super.use(level, player, hand);

        if (offCrucible.Augmentation != null) {
            return handleSocket(player, mainStack, sockets, offStack, offCrucible, offCrucible.Augmentation);
        } else if (offCrucible.AugmentationRemover != null) {
            return handleRemove(player, mainStack, sockets, offStack, offCrucible.AugmentationRemover);
        } else if (offCrucible.AugmentationSocket != null) {
            return handleUnlock(player, mainStack, sockets, offStack, offCrucible.AugmentationSocket);
        }

        return super.use(level, player, hand);
    }

    private InteractionResultHolder<ItemStack> handleSocket(Player player, ItemStack mainStack,
            AugmentSocketData sockets, ItemStack offStack, CrucibleItem offCrucible,
            CrucibleItem.AugmentationDef augDef) {
        if (offCrucible.id == null) return InteractionResultHolder.fail(mainStack);
        if (sockets.countFreeSlots(augDef.Type) == 0) return InteractionResultHolder.fail(mainStack);

        AugmentSocketData updated = sockets.withAugmentAdded(augDef.Type, offCrucible.id.toString());
        mainStack.set(OItemComponents.AUGMENT_SOCKETS, updated);

        if (!player.isCreative()) offStack.shrink(1);

        if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
            AugmentManager.getInstance().onEquipmentChange(sp);
        }
        return InteractionResultHolder.success(mainStack);
    }

    private InteractionResultHolder<ItemStack> handleRemove(Player player, ItemStack mainStack,
            AugmentSocketData sockets, ItemStack offStack,
            CrucibleItem.AugmentationRemoverDef removerDef) {
        Optional<String> removedAugId = sockets.getLastAugmentId(removerDef.Type);
        if (removedAugId.isEmpty()) return InteractionResultHolder.fail(mainStack);

        AugmentSocketData updated = removerDef.DestroySocket
                ? sockets.withSocketDestroyed(removerDef.Type)
                : sockets.withAugmentRemoved(removerDef.Type);
        mainStack.set(OItemComponents.AUGMENT_SOCKETS, updated);

        if (!player.isCreative()) offStack.shrink(1);

        if (removerDef.ReturnAugment) {
            Identifier augId = Identifier.tryParse(removedAugId.get());
            if (augId != null) {
                var itemHolder = BuiltInRegistries.ITEM.getHolder(
                        net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, augId));
                itemHolder.ifPresent(h -> {
                    ItemStack returnStack = new ItemStack(h.value());
                    if (!player.getInventory().add(returnStack)) {
                        player.drop(returnStack, false);
                    }
                });
            }
        }

        if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
            AugmentManager.getInstance().onEquipmentChange(sp);
        }
        return InteractionResultHolder.success(mainStack);
    }

    private InteractionResultHolder<ItemStack> handleUnlock(Player player, ItemStack mainStack,
            AugmentSocketData sockets, ItemStack offStack,
            CrucibleItem.AugmentationSocketDef socketDef) {
        int existing = sockets.countTotalSlots(socketDef.Type);

        // Check the item definition's own MaxAmount cap
        if (item.AugmentSlots != null) {
            for (CrucibleItem.AugmentSlotDef def : item.AugmentSlots) {
                if (def.Type != null && def.Type.equalsIgnoreCase(socketDef.Type)) {
                    if (existing >= def.MaxAmount) return InteractionResultHolder.fail(mainStack);
                    break;
                }
            }
        }
        // Check the component's stored max (set at item creation)
        if (existing >= sockets.getMaxSlots(socketDef.Type)) return InteractionResultHolder.fail(mainStack);
        // Check the unlocker's own cap
        if (existing >= socketDef.MaxSockets) return InteractionResultHolder.fail(mainStack);

        AugmentSocketData updated = sockets.withSlotAdded(socketDef.Type);
        mainStack.set(OItemComponents.AUGMENT_SOCKETS, updated);

        if (!player.isCreative()) offStack.shrink(1);

        return InteractionResultHolder.success(mainStack);
    }

    /** Looks up an augment type by raw type string (namespaced or path-only). */
    private static CrucibleAugmentType findAugmentType(String rawType) {
        Identifier direct = Identifier.tryParse(rawType.toLowerCase(Locale.ROOT));
        if (direct != null) {
            CrucibleAugmentType t = ContentRegistries.AUGMENT_TYPES.getValue(direct);
            if (t != null) return t;
        }
        String lower = rawType.toLowerCase(Locale.ROOT);
        for (var entry : ContentRegistries.AUGMENT_TYPES.entrySet()) {
            if (entry.getKey().location().getPath().equals(lower)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
