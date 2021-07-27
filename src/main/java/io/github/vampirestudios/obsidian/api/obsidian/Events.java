package io.github.vampirestudios.obsidian.api.obsidian;

import java.util.HashMap;
import java.util.Map;

public class Events {

    public Map<String, Event> events = new HashMap<>();

    public static class Event {

        public Map<String, EventVariables> eventVariables = new HashMap<>();

        public static class EventVariables {

        }

    }

}
