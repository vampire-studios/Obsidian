package io.github.vampirestudios.obsidian.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.raphydaphy.breakoutapi.BreakoutAPI;
import com.raphydaphy.breakoutapi.network.ModPackets;
import io.github.vampirestudios.obsidian.Obsidian;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.literal;

public class GeneratorCommand {

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("generator").then(literal("block").executes(context -> {
			BreakoutAPI.LOGGER.info("GUI Breakout test requested on server");

			ServerPlayerEntity player = context.getSource().getPlayer();
			ServerPlayNetworking.send(player, Obsidian.GENERATOR_BREAKOUT_PACKET, PacketByteBufs.create().writeString("block"));

			return 0;
		})).then(literal("item").executes(context -> {
			BreakoutAPI.LOGGER.info("Integrated Breakout test requested on server");

			ServerPlayerEntity player = context.getSource().getPlayer();
			ServerPlayNetworking.send(player, ModPackets.BREAKOUT_TEST_PACKET, PacketByteBufs.create().writeString("item"));

			return 0;
		})));
	}

}
