package io.github.vampirestudios.obsidian;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface DeletableObjectInternal extends DeletableObject {
    void obsidian$setDeleted(boolean value);
}