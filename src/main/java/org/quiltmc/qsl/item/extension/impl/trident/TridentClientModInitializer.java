package org.quiltmc.qsl.item.extension.impl.trident;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import java.util.ArrayDeque;
import java.util.Deque;

public class TridentClientModInitializer implements ClientModInitializer {
    public static final Deque<ItemStack> TRIDENT_QUEUE = new ArrayDeque<>();

    public static final Identifier TRIDENT_SPAWN_PACKET_ID = new Identifier("quilt_item_extensions", "trident_spawn_stack");

    @Override
    public void onInitializeClient(ModContainer mod) {
        ClientPlayNetworking.registerGlobalReceiver(TRIDENT_SPAWN_PACKET_ID, (client, handler, buf, responseSender) ->
                TRIDENT_QUEUE.add(buf.readItemStack())
        );
    }
}