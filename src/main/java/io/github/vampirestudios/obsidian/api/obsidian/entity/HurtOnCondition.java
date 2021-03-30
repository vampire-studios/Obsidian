package io.github.vampirestudios.obsidian.api.obsidian.entity;

public class HurtOnCondition {

    public DamageCondition damage_conditions;

    public static class DamageCondition {

        public Filters filters;
        public String cause;
        public int damage_per_tick;

        public static class Filters {

            public String test;
            public String subject;
            public String operator;
            public String value;

        }

    }

}