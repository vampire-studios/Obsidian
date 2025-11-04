package io.github.vampirestudios.obsidian.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomList<T> extends ArrayList<T> {

    // If T is itself a List, flatten these Lists into a single List.
    // Note: This method assumes T is a List.
    public List<?> flatten() {
        return this.stream()
                   .flatMap(element -> ((List<?>) element).stream())
                   .collect(Collectors.toList());
    }

    @SafeVarargs
    public static <T> CustomList<T> of(T... elements) {
        CustomList<T> list = new CustomList<>();
        list.addAll(Arrays.asList(elements));
        return list;
    }
}