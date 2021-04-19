package io.github.vampirestudios.obsidian.api;

/**
 * Factory type that gets or creates an extended enum from a vanilla enum.
 *
 * @param <Vanilla> the vanilla enum type
 * @param <Custom> the extended enum type
 * @author leocth
 * @since 0.2.0
 */
@FunctionalInterface
public interface VanillaEnumFactory<Vanilla extends Enum<Vanilla>, Custom extends ExtendedEnum<Vanilla>> {
    Custom get(Vanilla vanilla);
}