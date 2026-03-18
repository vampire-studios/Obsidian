package io.github.vampirestudios.obsidian.api.obsidian.block;

import blue.endless.jankson.annotation.SerializedName;
import com.electronwill.nightconfig.core.conversion.Path;
import io.github.vampirestudios.obsidian.api.bedrock.Description;
import io.github.vampirestudios.obsidian.api.obsidian.DisplayInformation;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import io.github.vampirestudios.obsidian.api.obsidian.PaintingTableInformation;
import io.github.vampirestudios.obsidian.api.obsidian.SpecialText;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodInformation;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class Block {
    public Description description;
    public String block_type = "block";
    public BlockInformation information;
    public Blocks[] blocks;
    public Behaviour behaviour;

    public DisplayInformation rendering;
    @SerializedName("drop_information")
    @com.google.gson.annotations.SerializedName("drop_information")
    @Path("drop_information")
    public DropInformation dropInformation;
    public AdditionalBlockInformation additional_information;
    public Functions functions;
    public DataComponentPatch components;
    public OreInformation ore_information;
    public FoodInformation food_information;
    public CampfireProperties campfire_properties;
    public List<Identifier> can_plant_on = new ArrayList<>();
    public Identifier particle_type;
    public Growable growable;
    public OxidizableProperties oxidizable_properties;
    public boolean is_multi_block = false;
    public MultiBlockInformation multi_block_information;
    public Identifier placeable_feature;

    @SerializedName("painting_table_information")
    @com.google.gson.annotations.SerializedName("painting_table_information")
    public PaintingTableInformation paintingTableInformation;

    public List<SpecialText> lore = new ArrayList<>();

    public void addLore(Consumer<Component> tooltip) {
        if (lore != null && !lore.isEmpty()) {
            for (SpecialText text : lore) {
                tooltip.accept(text.getName());
            }
        }
    }

    public List<net.minecraft.world.level.block.Block> getSupportableBlocks() {
        List<net.minecraft.world.level.block.Block> blocks2 = new ArrayList<>();
        can_plant_on.forEach(identifier -> blocks2.add(BuiltInRegistries.BLOCK.getValue(identifier)));
        return blocks2;
    }

    public BlockType getBlockType() {
        return BlockType.valueOf(block_type.toUpperCase(Locale.ROOT));
    }

    public enum BlockType {
        PAINTING_TABLE,
        BLOCK,
        HORIZONTAL_DIRECTIONAL,
        DIRECTIONAL,
        CAMPFIRE,
        STAIRS,
        SLAB,
        WALL,
        FENCE,
        FENCE_GATE,
        CAKE,
        BED,
        TRAPDOOR,
        DOOR,
        LOG,
        STEM,
        WOOD,
        OXIDIZING_BLOCK,
        PLANT,
        ROTATED_PILLAR,
        HORIZONTAL_FACING_PLANT,
        SAPLING,
        TORCH,
        BEEHIVE,
        LEAVES,
        LADDER,
        PATH,
        BUTTON,
        PRESSURE_PLATE,
        DOUBLE_PLANT,
        HORIZONTAL_FACING_DOUBLE_PLANT,
        HANGING_DOUBLE_LEAVES,
        EIGHT_DIRECTIONAL_BLOCK,
        LANTERN,
        CHAIN,
        PANE,
        DYEABLE,
        LOOM,
        GRINDSTONE,
        CRAFTING_TABLE,
        PISTON,
        NOTEBLOCK,
        JUKEBOX,
        SMOKER,
        FURNACE,
        BLAST_FURNACE,
        LECTERN,
        FLETCHING_TABLE,
        BARREL,
        COMPOSTER,
        RAILS,
        CARTOGRAPHY_TABLE,
        CARPET
    }

    public static class OxidizableProperties {
        public List<OxidationStage> stages;
        public List<String> cycle;

        public static class OxidationStage {
            public boolean can_be_waxed = true;
            public List<VariantBlock> blocks;
            public boolean stairs = true;
            public boolean slab = true;

            public static class VariantBlock {
                public NameInformation name;
                public DisplayInformation display;
                public transient net.minecraft.resources.Identifier id;
            }
        }
    }

    public static class CampfireProperties {
        public boolean emits_particles;
        public int fire_damage;
        public int luminance;
    }

    public static class MultiBlockInformation {
        public int width;
        public int height;
    }

    public static class Properties {
        public String facing;
//        public Map<String, PropertiesInfo> properties = new HashMap<>();

        public static class PropertiesInfo {
            public String type;
            public String[] values;
            public int min = 0, max = 1;
        }
    }



    public static class Blocks {
        public Vec3 origin;
        public Vec3 size;
        public Identifier block;
    }

    public static class Behaviour {
        public Repeater repeater;
        public PowerSource power_source;
        public Placement placement;
        public boolean glowing;
        public boolean rotate;
        public boolean rotateSmooth;

        public Container container;
        public Lock lock;
        public List<Seat> seat;
        public List<Showcase> showcase;

        public static class Repeater {
            public int delay = 0;
            public int loss = 0;
        }

        public static class PowerSource {
            public int value = 15;
        }

        public static class Container {
            /**
             * The name displayed in the container UI
             */
            public Component name;

            /**
             * The size of the container, has to be 5 slots or a multiple of 9, up to 6 rows of 9 slots.
             */
            public int size = 9;

            /**
             * Indicates whether the container's contents should be cleared when no player is viewing the inventory.
             */
            public boolean purge = false;

            /**
             * The name of the animation to play when the container is opened (if applicable).
             */
            public String openAnimation;

            /**
             * The name of the animation to play when the container is closed (if applicable).
             */
            public String closeAnimation;
        }

        public static class Placement {
            public boolean floor;
            public boolean wall;
            public boolean ceiling;
        }

        public static class Lock {
            /**
             * The identifier of the key required to unlock.
             */
            public Identifier key = null;

            /**
             * Determines whether the key should be consumed upon unlocking.
             */
            public boolean consumeKey = false;

            /**
             * Specifies whether the lock util should be discarded after unlocking.
             */
            public boolean discard = false;

            /**
             * Name of the animation to play upon successful unlocking (if applicable).
             */
            public String unlockAnimation = null;

            /**
             * Command to execute when the lock is successfully unlocked (if specified).
             * The command can be overwritten using NBT, the path for the command is Lock.Command in the block entities' NBT
             * `formats modify @e[entitySpecifier] Lock.Command set value "say hello"`
             */
            public String command = null;
        }

        public static class Seat {
            /**
             * The player seating offset
             */
            public Vector3f offset = new Vector3f();

            /**
             * The rotation direction of the seat
             */
            public float direction = 0;
        }

        public static class Showcase {
            public Vector3f offset;
            public Vector3f scale;
            public Quaternionf rotation;
            public Type type;
            public List<Identifier> filterItems;
            public List<Identifier> filterTags;

            public static enum Type {
                BLOCK,
                ITEM,
                DYNAMIC
            }
        }
    }
}