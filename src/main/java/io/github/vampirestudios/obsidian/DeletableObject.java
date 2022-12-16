package io.github.vampirestudios.obsidian;

public interface DeletableObject {
    default boolean wasDeleted() {
        throw new UnsupportedOperationException("Method wasn't implemented!");
    }
}