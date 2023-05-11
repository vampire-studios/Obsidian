package io.github.vampirestudios.obsidian.utils.parse.function;

@FunctionalInterface
public interface IntObjBiConsumer<T>
{
    void accept(int index, T obj);
}