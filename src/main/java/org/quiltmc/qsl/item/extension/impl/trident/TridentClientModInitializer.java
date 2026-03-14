package org.quiltmc.qsl.item.extension.impl.trident;

import io.github.vampirestudios.obsidian.api.obsidian.TridentStackPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayDeque;
import java.util.Deque;

public class TridentClientModInitializer implements ClientModInitializer {
    public static final Deque<ItemStack> TRIDENT_QUEUE = new ArrayDeque<>();
    public static final Identifier TRIDENT_SPAWN_PACKET_ID =
            Identifier.fromNamespaceAndPath("quilt_item_extensions", "trident_spawn_stack");

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(
                TridentStackPayload.TYPE,
                (payload, context) -> TRIDENT_QUEUE.add(payload.stack())
        );
    }
}
