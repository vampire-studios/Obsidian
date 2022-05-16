package io.github.vampirestudios.obsidian;

import io.github.vampirestudios.obsidian.api.ExtendedIdList;
import net.minecraft.util.collection.IdList;

public final class IdListUtils {
    private IdListUtils() {

    }

    @SuppressWarnings("unchecked")
    public static <T> void remove(IdList<T> idList, T item) {
        ((ExtendedIdList<T>) idList).obsidian$remove(item);
    }
}