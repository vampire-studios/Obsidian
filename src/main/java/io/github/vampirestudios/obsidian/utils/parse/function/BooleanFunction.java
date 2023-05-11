package io.github.vampirestudios.obsidian.utils.parse.function;

@FunctionalInterface
public
interface BooleanFunction<T>
{
    T apply(boolean b);
}