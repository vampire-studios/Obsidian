/*
package io.github.vampirestudios.obsidian.api;

import java.util.List;
import java.util.Map;

public class EventConfig {
    private List<Event> events;

    // Getters and Setters

    public static class Event {
        private String id;
        private String type;
        private Condition condition;
        private List<Action> actions;

        // Getters and Setters
    }

    public interface Condition {
        boolean evaluate(EventContext context);
    }

    public static class BlockBreakCondition implements Condition {
        private String block;

        @Override
        public boolean evaluate(EventContext context) {
            // Implement evaluation logic based on context
            return context.getBlock().equals(block);
        }

        // Getters and Setters
    }

    public interface Action {
        void execute(EventContext context);
    }

    public static class MessageAction implements Action {
        private String content;

        @Override
        public void execute(EventContext context) {
            // Implement action logic, like sending a message
        }

        // Getters and Setters
    }

    public static class SpawnEntityAction implements Action {
        private String entity;
        private String location; // Consider changing to a more specific type

        @Override
        public void execute(EventContext context) {
            // Implement action logic, like spawning an entity
        }

        // Getters and Setters
    }

    public static class EventContext {
        // Add properties that provide context for events, like the player, world, block, etc.
        private String block;
        private String player;
        // Other relevant context fields

        // Getters and Setters
    }
}*/
