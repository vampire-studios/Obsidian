package io.github.vampirestudios.obsidian.addonapi.network;

import io.github.vampirestudios.obsidian.Const;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class AddonApiNetworking {

	public static final Identifier OPEN_JSON_SCREEN =
			Identifier.fromNamespaceAndPath(Const.MOD_ID, "open_json_screen");

	public static void registerServerReceivers() {
		// currently we only need server → client, so nothing here yet
	}

	public static void registerClientReceivers() {
//        ClientPlayNetworking.registerGlobalReceiver(OPEN_JSON_SCREEN,
//                (idk, context) -> {
//                    String idStr = buf.readUtf();
//                    Identifier id = Identifier.parse(idStr);
//
//                    MenuDefinition def = AddonMenuRegistry.get(id);
//                    if (!(def instanceof ScreenMenuDefinition screenDef)) return;
//
//                    context.client().execute(() -> {
//                        Minecraft mc = Minecraft.getInstance();
//                        mc.setScreen(new JsonUiScreen(screenDef, mc.screen));
//                    });
//                });
	}

	public static void sendOpenScreen(ServerPlayer player, String menuId) {
		FriendlyByteBuf buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
		buf.writeUtf(menuId);
//        ServerPlayNetworking.send(player, CustomPacketPayload.createType(OPEN_JSON_SCREEN, buf).id());
	}
}
