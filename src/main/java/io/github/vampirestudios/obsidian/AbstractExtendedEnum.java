package io.github.vampirestudios.obsidian;

import io.github.vampirestudios.obsidian.api.ExtendedEnum;

public abstract class AbstractExtendedEnum<T extends Enum<T>> implements ExtendedEnum<T> {

    protected final T vanilla;

    public AbstractExtendedEnum(T vanilla) {
        this.vanilla = vanilla;
    }

    @Override
    public T getVanilla() {
        return vanilla;
    }
}