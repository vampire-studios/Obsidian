package io.github.vampirestudios.obsidian.api.obsidian.entity;

public class DamageSensor {

    public Trigger triggers;

    public static class Trigger {

        public String cause;
        public boolean deals_damage;

    }

}