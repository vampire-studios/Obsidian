package io.github.vampirestudios.obsidian.threadhandlers.data;

import com.google.gson.JsonObject;
import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class BlockInitThread implements Runnable {

    private final Block block;
    public BlockInitThread(Block blockIn) {
        block = blockIn;
    }

    @Override
    public void run() {
        Artifice.registerDataPack(String.format("%s:%s_data", block.information.name.id.getNamespace(), block.information.name.id.getPath()), serverResourcePackBuilder -> {
            serverResourcePackBuilder.addLootTable(block.information.name.id, lootTableBuilder -> {
                lootTableBuilder.type(new Identifier("block"));
                lootTableBuilder.pool(pool -> {
                    pool.rolls(1);
                    pool.entry(entry -> {
                        entry.type(new Identifier("item"));
                        entry.name(block.information.name.id);
                    });
                    pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> {

                    });
                });
            });
            try {
                serverResourcePackBuilder.dumpResources("testing", "data");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        if (block.additional_information != null) {
            if (block.additional_information.slab) {
                Artifice.registerDataPack(String.format("%s:%s_slab_data", block.information.name.id.getPath(), block.information.name.id.getPath()), serverResourcePackBuilder -> {
                    serverResourcePackBuilder.addLootTable(Utils.appendToPath(block.information.name.id, "_slab"), lootTableBuilder -> {
                        lootTableBuilder.type(new Identifier("block"));
                        lootTableBuilder.pool(pool -> {
                            pool.rolls(1);
                            pool.entry(entry -> {
                                entry.type(new Identifier("item"));
                                entry.function(new Identifier("set_count"), function ->
                                        function.condition(new Identifier("block_state_property"), jsonObjectBuilder -> {
                                            jsonObjectBuilder.add("block", Utils.appendToPath(block.information.name.id, "_slab").toString());
                                            JsonObject property = new JsonObject();
                                            property.addProperty("type", "double");
                                            jsonObjectBuilder.add("property", property);
                                        })
                                );
                                entry.weight(2);
                                entry.function(new Identifier("explosion_decay"), function -> { });
                                entry.name(Utils.appendToPath(block.information.name.id, "_slab"));
                            });
                            pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> { });
                        });
                    });
                    serverResourcePackBuilder.addShapedRecipe(Utils.appendToPath(block.information.name.id, "_slab"), shapedRecipeBuilder -> {
                        shapedRecipeBuilder.group(new Identifier(block.information.name.id.getNamespace(), "slabs"));
                        shapedRecipeBuilder.pattern(
                                "###"
                        );
                        shapedRecipeBuilder.ingredientItem('#', block.information.name.id);
                        shapedRecipeBuilder.result(Utils.appendToPath(block.information.name.id, "_slab"), 6);
                    });
                    try {
                        serverResourcePackBuilder.dumpResources("testing", "data");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            if (block.additional_information.stairs) {
                Artifice.registerDataPack(String.format("%s:%s_stairs_data", block.information.name.id.getPath(), block.information.name.id.getPath()), serverResourcePackBuilder -> {
                    serverResourcePackBuilder.addLootTable(Utils.appendToPath(block.information.name.id, "_stairs"), lootTableBuilder -> {
                        lootTableBuilder.type(new Identifier("block"));
                        lootTableBuilder.pool(pool -> {
                            pool.rolls(1);
                            pool.entry(entry -> {
                                entry.type(new Identifier("item"));
                                entry.name(Utils.appendToPath(block.information.name.id, "_stairs"));
                            });
                            pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> {

                            });
                        });
                    });
                    serverResourcePackBuilder.addShapedRecipe(Utils.appendToPath(block.information.name.id, "_stairs"), shapedRecipeBuilder -> {
                        shapedRecipeBuilder.group(new Identifier(block.information.name.id.getNamespace(), "stairs"));
                        shapedRecipeBuilder.pattern(
                                "#  ",
                                "## ",
                                "###"
                        );
                        shapedRecipeBuilder.ingredientItem('#', block.information.name.id);
                        shapedRecipeBuilder.result(Utils.appendToPath(block.information.name.id, "_stairs"), 4);
                    });
                    try {
                        serverResourcePackBuilder.dumpResources("testing", "data");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            if (block.additional_information.fence) {
                Artifice.registerDataPack(String.format("%s:%s_fence_data", block.information.name.id.getPath(), block.information.name.id.getPath()), serverResourcePackBuilder -> {
                    serverResourcePackBuilder.addLootTable(Utils.appendToPath(block.information.name.id, "_fence"), lootTableBuilder -> {
                        lootTableBuilder.type(new Identifier("block"));
                        lootTableBuilder.pool(pool -> {
                            pool.rolls(1);
                            pool.entry(entry -> {
                                entry.type(new Identifier("item"));
                                entry.name(Utils.appendToPath(block.information.name.id, "_fence"));
                            });
                            pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> {

                            });
                        });
                    });
                    serverResourcePackBuilder.addShapedRecipe(Utils.appendToPath(block.information.name.id, "_fence"), shapedRecipeBuilder -> {
                        shapedRecipeBuilder.group(new Identifier(block.information.name.id.getNamespace(), "fences"));
                        shapedRecipeBuilder.pattern(
                                "W#W",
                                "W#W"
                        );
                        shapedRecipeBuilder.ingredientItem('W', block.information.name.id);
                        shapedRecipeBuilder.ingredientItem('#', new Identifier("stick"));
                        shapedRecipeBuilder.result(Utils.appendToPath(block.information.name.id, "_fence"), 3);
                    });
                    try {
                        serverResourcePackBuilder.dumpResources("testing", "data");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            if (block.additional_information.fenceGate) {
                Artifice.registerDataPack(String.format("%s:%s_fence_gate_data", block.information.name.id.getPath(), block.information.name.id.getPath()), serverResourcePackBuilder -> {
                    serverResourcePackBuilder.addLootTable(Utils.appendToPath(block.information.name.id, "_fence_gate"), lootTableBuilder -> {
                        lootTableBuilder.type(new Identifier("block"));
                        lootTableBuilder.pool(pool -> {
                            pool.rolls(1);
                            pool.entry(entry -> {
                                entry.type(new Identifier("item"));
                                entry.name(new Identifier(block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_fence_gate"));
                            });
                            pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> { });
                        });
                    });
                    serverResourcePackBuilder.addShapedRecipe(Utils.appendToPath(block.information.name.id, "_fence_gate"), shapedRecipeBuilder -> {
                        shapedRecipeBuilder.group(new Identifier(block.information.name.id.getNamespace(), "fence_gates"));
                        shapedRecipeBuilder.pattern(
                                "#W#",
                                "#W#"
                        );
                        shapedRecipeBuilder.ingredientItem('W', block.information.name.id);
                        shapedRecipeBuilder.ingredientItem('#', new Identifier("stick"));
                        shapedRecipeBuilder.result(Utils.appendToPath(block.information.name.id, "_fence_gate"), 3);
                    });
                    try {
                        serverResourcePackBuilder.dumpResources("testing", "data");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            if (block.additional_information.walls) {
                Artifice.registerDataPack(String.format("%s:%s_wall_data", block.information.name.id.getPath(), block.information.name.id.getPath()), serverResourcePackBuilder -> {
                    serverResourcePackBuilder.addLootTable(Utils.appendToPath(block.information.name.id, "_wall"), lootTableBuilder -> {
                        lootTableBuilder.type(new Identifier("block"));
                        lootTableBuilder.pool(pool -> {
                            pool.rolls(1);
                            pool.entry(entry -> {
                                entry.type(new Identifier("item"));
                                entry.name(Utils.appendToPath(block.information.name.id, "_wall"));
                            });
                            pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> {
                            });
                        });
                    });
                    try {
                        serverResourcePackBuilder.dumpResources("testing", "data");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}