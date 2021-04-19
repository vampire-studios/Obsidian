package io.github.vampirestudios.obsidian.api;

/**
 * Interface for "extended" enums.
 *
 * @param <T> the original enum type
 * @author leocth
 * @since 0.2.0
 */
public interface ExtendedEnum<T extends Enum<T>> {
    /**
     * Returns the vanilla representation of the extended value.
     * <em>MIGHT return null if the value does not have a vanilla equivalent!</em>
     * @return the vanilla equivalent
     */
    T getVanilla();

    /**
     * @return whether the extended enum is custom (i.e. does not have a vanilla counterpart).
     */
    default boolean isCustom() {
        return getVanilla() == null;
    }
}