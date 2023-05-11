package io.github.vampirestudios.obsidian.utils.parse.function;

@FunctionalInterface
public interface FloatFunction<T>
{
    T apply(float value);
}
