package org.quiltmc.qsl.item.extension.impl.trident;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.ArrayDeque;
import java.util.Deque;

public class TridentClientModInitializer implements ClientModInitializer {
    public static final Deque<ItemStack> TRIDENT_QUEUE = new ArrayDeque<>();

    public static final Identifier TRIDENT_SPAWN_PACKET_ID = new Identifier("quilt_item_extensions", "trident_spawn_stack");

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(TRIDENT_SPAWN_PACKET_ID, (client, handler, buf, responseSender) ->
                TRIDENT_QUEUE.add(buf.readItemStack())
        );
    }
}