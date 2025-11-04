package io.github.vampirestudios.obsidian.api.scripting;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandExecutor {
    public boolean execute(String line, Player player, Level world, BlockPos pos,
                        ScriptManager.FlowController flow) {
        String[] parts = line.split("\\s+");
        switch(parts[0]) {
            case "send" -> {
                // send "<msg>"
                String msg = stripQuotes(line.substring(4).trim())
                        .replace("%player%", player.getName().getString());
                player.displayClientMessage(Component.literal(msg), false);
            }
            case "give", "drop" -> {
                // Regex (case‐insensitive) with groups:
                // 1=count, 2=itemId, 3=name, 4=singleLore, 5=listLore, 6=cmdl
                Pattern p = Pattern.compile(
                        "^(?i)(?:drop|give)\\s+"             // command
                        + "(\\d+)\\s+"                         // (1) count
                        + "(?:of\\s+)?([\\w:]+)"               // (2) itemId
                        + "(?:\\s+named\\s+\"([^\"]+)\")?"     // (3) name
                        + "(?:\\s+with\\s+lore\\s+"            // lore clause
                        + "(?:\"([^\"]+)\"|\\[([^]]+)])" // (4) single or (5) comma-list
                        + ")?"
                        + "(?:\\s+with\\s+custom\\s+model\\s+data\\s+(\\d+))?" // (6) cmdl
                );
                Matcher m = p.matcher(line);
                if (!m.find()) {
                    player.displayClientMessage(Component.literal(STR."[Obs] Invalid syntax: \{line}"), false);
                    break;
                }

                // extract
                int    count     = Integer.parseInt(m.group(1));
                String itemId    = m.group(2);
                String name      = m.group(3);    // may be null
                String singleLore= m.group(4);    // may be null
                String listLore  = m.group(5);    // may be null
                String cmdlStr   = m.group(6);    // may be null
                Integer cmdl     = cmdlStr!=null ? Integer.parseInt(cmdlStr) : null;

                // build lore list
                List<String> lore = new ArrayList<>();
                if (singleLore != null) {
                    lore.add(singleLore);
                } else if (listLore != null) {
                    for (String s : listLore.split("\\s*,\\s*")) {
                        lore.add(stripQuotes(s.trim()));
                    }
                }

                // create the ItemStack
                ResourceLocation rl = ResourceLocation.tryParse(itemId);
                ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.getValue(rl), count);

                // apply name
                if (name != null) {
                    stack.set(DataComponents.ITEM_NAME, Component.literal(name));
                }

                // apply lore & customModelData
                if (!lore.isEmpty() || cmdl != null) {
                    if (!lore.isEmpty()) {
                        stack.set(DataComponents.LORE, new ItemLore(lore.stream()
                                .map(s -> (Component) Component.literal(s))
                                .toList()
                        ));
                    }
                    if (cmdl != null) {
                        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(Float.valueOf(cmdl)), List.of(), List.of(), List.of()));
                    }
                }

                // give or drop
                if (parts[0].equalsIgnoreCase("give")) {
                    if (!player.getInventory().add(stack)) player.drop(stack, false);
                } else {
                    player.drop(stack, false);
                }
            }
            case "broadcast" -> {
                // broadcast "<msg>"
                String msg1 = stripQuotes(line.substring(9)).trim();
                player.getServer().getPlayerList().broadcastSystemMessage(Component.literal(msg1), false);
            }
            case "console" -> {
                // console "<cmd>"
                String cmd = stripQuotes(line.substring(7));
                var css = world.getServer().createCommandSourceStack();
                try {
                    css.dispatcher().execute(cmd, css);
                } catch (CommandSyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
            case "wait" -> {
                // wait <seconds>
                int secs = Integer.parseInt(parts[1]);
                flow.delay(secs * 20);  // schedule remainder
                return false;           // halt current loop
            }
            default -> System.err.println(STR."Unknown command: \{line}");
        }
        return true;
    }

    public String stripQuotes(String s) {
        if ((s.startsWith("\"") && s.endsWith("\"")) ||
                (s.startsWith("'") && s.endsWith("'"))) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }
}
