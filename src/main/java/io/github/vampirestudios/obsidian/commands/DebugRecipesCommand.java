package io.github.vampirestudios.obsidian.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.World;

public class DebugRecipesCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("debug_recipes").requires((serverCommandSource_1) ->
                execute(serverCommandSource_1, serverCommandSource_1.getWorld())));
    }

    private static boolean execute(ServerCommandSource source, World world) {
        System.out.println("Validating recipes");
        source.getWorld().getRecipeManager().keys().forEach(identifier -> {
            try {
                Recipe recipe =  world.getRecipeManager().get(identifier).get();
                RecipeSerializer recipeSerializer = recipe.getSerializer();
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                recipeSerializer.write(buf, recipe);

                Recipe readback = recipeSerializer.read(identifier, buf);
            } catch (Exception e){
                throw new RuntimeException("Failed to read " + identifier, e);
            }
        });
        System.out.println("Done");

        return true;
    }

}
