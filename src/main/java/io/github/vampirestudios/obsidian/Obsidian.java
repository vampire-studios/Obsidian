package io.github.vampirestudios.obsidian;

import com.mojang.serialization.Lifecycle;
import io.github.vampirestudios.obsidian.api.TestBlock;
import io.github.vampirestudios.obsidian.configPack.ConfigHelper;
import io.github.vampirestudios.vampirelib.utils.registry.RegistryHelper;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;

public class Obsidian implements ModInitializer {

    public static String MOD_ID = "obsidian";
    public static String NAME = "Obsidian";
    public static final Logger LOGGER = LogManager.getLogger("[" + NAME + "]");
    public static String VERSION = "0.2.0";

//    public static final DataPackModdingManager MODDING_MANAGER = new DataPackModdingManager();
    public static final RegistryHelper REGISTRY_HELPER = RegistryHelper.createRegistryHelper(MOD_ID);

    private static final Identifier TATER_ID = new Identifier(MOD_ID, "test_blocks");
    public static final RegistryKey<? extends Registry<TestBlock>> TATER_KEY = RegistryKey.ofRegistry(TATER_ID);

    public static final Identifier DEFAULT_TATER_ID = new Identifier(MOD_ID, "test_block");
    public static final TestBlock DEFAULT_TATER = new TestBlock(1.0F, 1.0F, false, false,0.0F, 0.0F, 0.0F, "dirt");

    public static SimpleRegistry<TestBlock> TATER_REGISTRY = new SimpleRegistry<>(TATER_KEY, Lifecycle.stable());

    @Override
    public void onInitialize() {
        LOGGER.info(String.format("You're now running %s v%s for %s", NAME, VERSION, "20w28a"));

        /*Registry.register(TATER_REGISTRY, DEFAULT_TATER_ID, DEFAULT_TATER);
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
                dispatcher.register(CommandManager.literal("fabric_dynamic_registries_test").executes(this::executeTestCommand))
        );*/

//        MODDING_MANAGER.registerReloadListener();
        ConfigHelper.loadDefault();
        CompletableFuture.runAsync(ConfigHelper::loadConfig, ConfigHelper.EXECUTOR_SERVICE);
    }

    /*private int executeTestCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Registry<TestBlock> registry = context.getSource().getMinecraftServer().getRegistryManager().get(TATER_KEY);

        Text header = new LiteralText("Found " + registry.getEntries().size() + " taters:").formatted(Formatting.GRAY);
        context.getSource().sendFeedback(header, false);
        for (Map.Entry<RegistryKey<TestBlock>, TestBlock> entry : registry.getEntries()) {
            Identifier id = entry.getKey().getValue();
            TestBlock tater = entry.getValue();
            AbstractBlock.Settings settings = AbstractBlock.Settings.of(Material.STONE).strength(tater.getHardness(), tater.getResistance())
                    .slipperiness(tater.getSlipperiness()).jumpVelocityMultiplier(tater.getJumpVelocityMultiplier())
                    .velocityMultiplier(tater.getVelocityMultiplier());
            if (tater.isRandomTicks())
                settings.ticksRandomly();
            if (tater.isToolRequired())
                settings.requiresTool();
            if (!Registry.BLOCK.containsId(id)) {
                REGISTRY_HELPER.registerBlock(new Block(settings), tater.getName());
            }

            context.getSource().sendFeedback(new LiteralText("- ID:   " + id), false);
            context.getSource().sendFeedback(new LiteralText("  Name: " + tater.getName()), false);
        }

        return 1;
    }*/

}