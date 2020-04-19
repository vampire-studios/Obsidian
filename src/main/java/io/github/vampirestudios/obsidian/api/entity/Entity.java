package io.github.vampirestudios.obsidian.api.entity;

import net.minecraft.util.Identifier;

public class Entity {

    public Identifier identifier;
    public boolean spawnable;
    public boolean summonable;
    public boolean experimental;

    public int spawnEggColorMain;
    public int spawnEggColorOverlay;

    public EntityComponents components;

}