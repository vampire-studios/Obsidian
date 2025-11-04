package io.github.vampirestudios.obsidian.api.crucible;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class CrucibleItem {
    public ResourceLocation id;

    public String Id;
    public String Display;
    public String Type;
    public int Data;
    public int Amount;
    public int Model;
    public List<String> Lore;
//    public NBT nbt;
//    public List<String> hide;
    public List<String> Skills;
    public List<Skill> internalSkills;
    public List<Enchantment> Enchantments;
    public Firework Firework;
    public Options Options;  // Add this line to hold the options
    public Furniture Furniture;

    public CrucibleItem() {
    }

    // Inner class for Options
    public static class Options {
        public boolean Repairable;
        public boolean Unbreakable;
        public boolean PreventStacking;
        public boolean CancelDamage;
        public boolean Destroy;
        public boolean DestroyOnDrop;
        public boolean KeepOnDeath;
        public boolean PreventDropping;
        public boolean Placeable;
        public boolean PreventAnvil;
        public boolean PreventSmithing;
        public boolean PreventCrafting;
        public boolean PreventEnchanting;
        public int RepairCost;
        public int HideFlags;
//        public int Color;
        public String Player;
        public String SkinTexture;
        public String Color;
        public String Permission;
    }

    public static class Furniture {
        public String Material;
        public int Model;
        public String Type;
        public String Health;
        public Hitbox Hitbox;
        public String Color;
        public boolean CanRotate;
        public boolean GlowingItem;
        public boolean DropSelf;
        public boolean Diagonalable;
        public boolean Colorable;
        public Placement Placement;

        public enum Placement {
            FLOOR,
            HANGING,
            CEILING,
            WALL,
            ANY
        }

        public static class Hitbox {
            public int Height = 1;
            public int Width = 1;
        }
    }

}