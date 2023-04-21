package org.quiltmc.qsl.item.extension.impl.trident;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayDeque;
import java.util.Deque;

public class TridentClientModInitializer implements ClientModInitializer {
    public static final Deque<ItemStack> TRIDENT_QUEUE = new ArrayDeque<>();

    public static final ResourceLocation TRIDENT_SPAWN_PACKET_ID = new ResourceLocation("quilt_item_extensions", "trident_spawn_stack");

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(TRIDENT_SPAWN_PACKET_ID, (client, handler, buf, responseSender) ->
                TRIDENT_QUEUE.add(buf.readItem())
        );
    }
}