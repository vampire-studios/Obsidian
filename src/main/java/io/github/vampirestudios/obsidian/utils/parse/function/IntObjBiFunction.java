package io.github.vampirestudios.obsidian.utils.parse.function;

@FunctionalInterface
public interface IntObjBiFunction<T, R>
{
    R apply(int index, T obj);
}