package io.github.vampirestudios.obsidian.commands;

import com.google.gson.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class DumpRegistriesCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> source = dispatcher.register(CommandManager.literal("dump_registries").executes(cs -> {
            FabricLoader.getInstance().getAllMods().forEach(modContainer -> {
                String modId = modContainer.getMetadata().getId();
                DumpRegistriesCommand.createBiomeDatapack(modId, cs);
            });
            return 1;
        }));
        dispatcher.register(CommandManager.literal("dump_registries").redirect(source));
    }

    public static void createBiomeDatapack(String modId, CommandContext<ServerCommandSource> commandSource) {
        List<Biome> biomeList = new ArrayList<>();
        boolean stopSpamFlag = false;
        Path dataPackPath = dataPackPath(commandSource.getSource().getWorld().getServer().getSavePath(WorldSavePath.DATAPACKS), modId);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        DynamicRegistryManager manager = commandSource.getSource().getServer().getRegistryManager();
        Registry<Biome> biomeRegistry = manager.get(Registry.BIOME_KEY);
        Registry<PlacedFeature> featuresRegistry = manager.get(Registry.PLACED_FEATURE_KEY);
        Registry<ConfiguredStructureFeature<?, ?>> structuresRegistry = manager.get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY);
        Registry<ConfiguredCarver<?>> carverRegistry = manager.get(Registry.CONFIGURED_CARVER_KEY);
        Registry<StructureProcessorList> structureProcessorRegistry = manager.get(Registry.STRUCTURE_PROCESSOR_LIST_KEY);

        createPlacedFeatureJson(modId, dataPackPath, gson, featuresRegistry);
        createConfiguredCarverJson(modId, dataPackPath, gson, carverRegistry);
        createConfiguredStructureJson(modId, dataPackPath, gson, structuresRegistry);
        createProcessorListJson(modId, dataPackPath, gson, structureProcessorRegistry);
        createBiomeJsonAndPackMcMeta(modId, commandSource, biomeList, stopSpamFlag, dataPackPath, gson, biomeRegistry, featuresRegistry, structuresRegistry, carverRegistry);
    }

    private static void createBiomeJsonAndPackMcMeta(String modId, CommandContext<ServerCommandSource> commandSource, List<Biome> biomeList, boolean stopSpamFlag, Path dataPackPath, Gson gson, Registry<Biome> biomeRegistry, Registry<PlacedFeature> featuresRegistry, Registry<ConfiguredStructureFeature<?, ?>> structuresRegistry, Registry<ConfiguredCarver<?>> carverRegistry) {
        for (Map.Entry<RegistryKey<Biome>, Biome> biome : biomeRegistry.getEntries()) {
            String biomeKey = Objects.requireNonNull(biomeRegistry.getKey(biome.getValue())).get().getValue().toString();
            if (biomeKey.contains(modId)) {
                biomeList.add(biome.getValue());
            }
        }

        if (biomeList.size() > 0) {
            for (Biome biome : biomeList) {
                Identifier key = biomeRegistry.getId(biome);
                if (key != null) {
                    Path biomeJsonPath = biomeJsonPath(dataPackPath, key, modId);
                    Function<Supplier<Biome>, DataResult<JsonElement>> biomeCodec = JsonOps.INSTANCE.withEncoder(Biome.REGISTRY_CODEC);
                    try {
                        if (!Files.exists(biomeJsonPath)) {
                            Files.createDirectories(biomeJsonPath.getParent());
                            Optional<JsonElement> optional = (biomeCodec.apply(() -> biome).result());
                            if (optional.isPresent()) {
                                JsonElement root = optional.get();
                                JsonArray features = new JsonArray();
                                for (List<Supplier<PlacedFeature>> list : biome.getGenerationSettings().getFeatures()) {
                                    JsonArray stage = new JsonArray();
                                    for (Supplier<PlacedFeature> feature : list) {
                                        featuresRegistry.getKey(feature.get()).ifPresent(featureKey -> stage.add(featureKey.getValue().toString()));
                                    }
                                    features.add(stage);
                                }
                                root.getAsJsonObject().add("features", features);

                                JsonObject carvers = new JsonObject();
                                for (GenerationStep.Carver step : GenerationStep.Carver.values()) {
                                    JsonArray stage = new JsonArray();
                                    for (Supplier<ConfiguredCarver<?>> carver : biome.getGenerationSettings().getCarversForStep(step)) {
                                        carverRegistry.getKey(carver.get()).ifPresent(carverKey -> stage.add(carverKey.getValue().toString()));
                                    }
                                    if (stage.size() > 0) {
                                        carvers.add(step.asString(), stage);
                                    }
                                }
                                root.getAsJsonObject().add("carvers", carvers);
                                Files.write(biomeJsonPath, gson.toJson(root).getBytes());
                            }
                        }
                    } catch (IOException e) {
                        if (!stopSpamFlag) {
                            commandSource.getSource().sendFeedback(new TranslatableText("commands.gendata.failed", modId).styled(text -> text.withColor(TextColor.fromFormatting(Formatting.RED))), false);
                            stopSpamFlag = true;
                        }
                    }
                }
            }

            try {
                createPackMCMeta(dataPackPath, modId);
            } catch (IOException e) {
                commandSource.getSource().sendFeedback(new TranslatableText("commands.gendata.mcmeta.failed", modId).styled(text -> text.withColor(TextColor.fromFormatting(Formatting.RED))), false);
            }

            Text filePathText = (new LiteralText(dataPackPath.toString())).formatted(Formatting.UNDERLINE).styled(text -> text.withColor(TextColor.fromFormatting(Formatting.GREEN)).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, dataPackPath.toString())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("commands.gendata.hovertext"))));

            commandSource.getSource().sendFeedback(new TranslatableText("commands.gendata.success", commandSource.getArgument("modid", String.class), filePathText), false);
        } else {
            commandSource.getSource().sendFeedback(new TranslatableText("commands.gendata.listisempty", modId).styled(text -> text.withColor(TextColor.fromFormatting(Formatting.RED))), false);
        }
    }

    private static void createPlacedFeatureJson(String modId, Path dataPackPath, Gson gson, Registry<PlacedFeature> featuresRegistry) {
        for (Map.Entry<RegistryKey<PlacedFeature>, PlacedFeature> feature : featuresRegistry.getEntries()) {
            Function<Supplier<PlacedFeature>, DataResult<JsonElement>> featureCodec = JsonOps.INSTANCE.withEncoder(PlacedFeature.REGISTRY_CODEC);

            PlacedFeature configuredFeature = feature.getValue();
            if (feature.getKey().getValue().toString().contains(modId)) {
                if (configuredFeature != null) {
                    if (Objects.requireNonNull(featuresRegistry.getKey(configuredFeature)).toString().contains(modId)) {
                        Optional<JsonElement> optional = (featureCodec.apply(() -> configuredFeature).result());
                        if (optional.isPresent()) {
                            try {
                                Path cfPath = placedFeatureJsonPath(dataPackPath, Objects.requireNonNull(featuresRegistry.getId(configuredFeature)), modId);
                                Files.createDirectories(cfPath.getParent());

                                Files.write(cfPath, gson.toJson(optional.get()).getBytes());
                            } catch (IOException e) {
                            }
                        }
                    }
                }
            }
        }
    }

    private static void createConfiguredCarverJson(String modId, Path dataPackPath, Gson gson, Registry<ConfiguredCarver<?>> carverRegistry) {
        for (Map.Entry<RegistryKey<ConfiguredCarver<?>>, ConfiguredCarver<?>> carver : carverRegistry.getEntries()) {
            Function<Supplier<ConfiguredCarver<?>>, DataResult<JsonElement>> carverCodec = JsonOps.INSTANCE.withEncoder(ConfiguredCarver.REGISTRY_CODEC);

            ConfiguredCarver<?> configuredCarver = carver.getValue();
            if (carver.getKey().getValue().toString().contains(modId)) {
                if (configuredCarver != null) {
                    if (Objects.requireNonNull(carverRegistry.getKey(configuredCarver)).toString().contains(modId)) {
                        Optional<JsonElement> optional = (carverCodec.apply(() -> configuredCarver).result());
                        if (optional.isPresent()) {
                            try {
                                Path carverPath = configuredCarverJsonPath(dataPackPath, Objects.requireNonNull(carverRegistry.getId(configuredCarver)), modId);
                                Files.createDirectories(carverPath.getParent());
                                Files.write(carverPath, gson.toJson(optional.get()).getBytes());
                            } catch (IOException e) {
                            }
                        }
                    }
                }
            }
        }
    }

    private static void createConfiguredStructureJson(String modId, Path dataPackPath, Gson gson, Registry<ConfiguredStructureFeature<?, ?>> structureRegistry) {
        for (Map.Entry<RegistryKey<ConfiguredStructureFeature<?, ?>>, ConfiguredStructureFeature<?, ?>> structure : structureRegistry.getEntries()) {
            Function<Supplier<ConfiguredStructureFeature<?, ?>>, DataResult<JsonElement>> structureCodec = JsonOps.INSTANCE.withEncoder(ConfiguredStructureFeature.REGISTRY_CODEC);

            ConfiguredStructureFeature<?, ?> configuredStructure = structure.getValue();
            if (structure.getKey().getValue().toString().contains(modId)) {
                if (configuredStructure != null) {
                    if (Objects.requireNonNull(structureRegistry.getKey(configuredStructure)).toString().contains(modId)) {
                        Optional<JsonElement> optional = (structureCodec.apply(() -> configuredStructure).result());
                        if (optional.isPresent()) {
                            try {
                                Path structurePath = configuredStructureFeatureJsonPath(dataPackPath, Objects.requireNonNull(structureRegistry.getId(configuredStructure)), modId);
                                Files.createDirectories(structurePath.getParent());
                                Files.write(structurePath, gson.toJson(optional.get()).getBytes());
                            } catch (IOException e) {
                            }
                        }
                    }
                }
            }
        }
    }

    private static void createProcessorListJson(String modId, Path dataPackPath, Gson gson, Registry<StructureProcessorList> structureProcessorRegistry) {
        for (Map.Entry<RegistryKey<StructureProcessorList>, StructureProcessorList> processor : structureProcessorRegistry.getEntries()) {
            Function<Supplier<StructureProcessorList>, DataResult<JsonElement>> processorCodec = JsonOps.INSTANCE.withEncoder(StructureProcessorType.REGISTRY_CODEC);

            StructureProcessorList processorList = processor.getValue();
            if (processor.getKey().getValue().toString().contains(modId)) {
                if (processorList != null) {
                    if (Objects.requireNonNull(structureProcessorRegistry.getKey(processorList)).toString().contains(modId)) {
                        Optional<JsonElement> optional = (processorCodec.apply(() -> processorList).result());
                        if (optional.isPresent()) {
                            try {
                                Path processorListPath = configuredProceesorListPath(dataPackPath, Objects.requireNonNull(structureProcessorRegistry.getId(processorList)), modId);
                                Files.createDirectories(processorListPath.getParent());
                                Files.write(processorListPath, gson.toJson(optional.get()).getBytes());
                            } catch (IOException e) {
                            }
                        }
                    }
                }
            }
        }
    }

    private static Path placedFeatureJsonPath(Path path, Identifier identifier, String modId) {
        return path.resolve("data/" + modId + "/worldgen/placed_features" + identifier.getPath() + ".json");
    }

    private static Path configuredCarverJsonPath(Path path, Identifier identifier, String modId) {
        return path.resolve("data/" + modId + "/worldgen/configured_carver/" + identifier.getPath() + ".json");
    }

    private static Path configuredStructureFeatureJsonPath(Path path, Identifier identifier, String modId) {
        return path.resolve("data/" + modId + "/worldgen/configured_structure_feature/" + identifier.getPath() + ".json");
    }

    private static Path configuredProceesorListPath(Path path, Identifier identifier, String modId) {
        return path.resolve("data/" + modId + "/worldgen/processor_list/" + identifier.getPath() + ".json");
    }

    private static Path biomeJsonPath(Path path, Identifier identifier, String modId) {
        return path.resolve("data/" + modId + "/worldgen/biome/" + identifier.getPath() + ".json");
    }

    private static Path dataPackPath(Path path, String modId) {
        return path.resolve("gendata/" + modId + "-custom");
    }

    //Generate the pack.mcmeta file required for datapacks.
    private static void createPackMCMeta(Path dataPackPath, String modID) throws IOException {
        String fileString = "{\n" +
                "\t\"pack\":{\n" +
                "\t\t\"pack_format\": 8,\n" +
                "\t\t\"description\": \"Custom biome datapack for " + modID + ".\"\n" +
                "\t}\n" +
                "}\n";

        Files.write(dataPackPath.resolve("pack.mcmeta"), fileString.getBytes());
    }

}
