package io.github.vampirestudios.obsidian.api;

import com.google.common.collect.ImmutableMap;
import io.github.vampirestudios.obsidian.utils.KeyNotFoundException;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class Utils
{
    public static TagKey<Item> itemTag(String pName)
    {
        return TagKey.create(Registries.ITEM, Identifier.tryParse(pName));
    }

    public static TagKey<Block> blockTag(String pName)
    {
        return TagKey.create(Registries.BLOCK, Identifier.tryParse(pName));
    }

    public static <T extends Comparable<T>> T getPropertyValue(Property<T> prop, String value)
    {
        Optional<T> propValue = prop.getValue(value);
        return propValue.orElseThrow(() -> new KeyNotFoundException("Value " + value + " for property " + prop.getName() + " not found in the allowed values."));
    }

    @NonNull
    public static <T> T orElse(@Nullable T val, T def)
    {
        return val != null ? val : def;
    }

    public static <T> T orElseGet(@Nullable T val, Supplier<T> def)
    {
        return val != null ? val : def.get();
    }

    public static Item getItemOrCrash(Identifier which)
    {
        return getOrCrash(BuiltInRegistries.ITEM, which);
    }

    public static Block getBlockOrCrash(Identifier which)
    {
        return getOrCrash(BuiltInRegistries.BLOCK, which);
    }

    public static <T> T getOrCrash(Registry<T> registry, Identifier name)
    {
        T t = (T) registry.get(name);
        if (t == null)
            throw new KeyNotFoundException("No object with name " + name + " found in the registry " + registry);
        return t;
    }

    public static <T> T getOrElse(Registry<T> registry, Identifier name, T fallback)
    {
        if (!registry.containsKey(name))
            return fallback;
        return (T) Objects.requireNonNull(registry.get(name));
    }


    private static final Map<String, ArmorType> BACKWARD_COMPAT = ImmutableMap.<String, ArmorType>builder()
            .put("head", ArmorType.HELMET)
            .put("chest", ArmorType.CHESTPLATE)
            .put("legs", ArmorType.LEGGINGS)
            .put("feet", ArmorType.BOOTS)
        .build();

    public static ArmorType armorTypeByEquipmentSlotName(String name) {
        ArmorType backwardCompat = BACKWARD_COMPAT.get(name);

        if (backwardCompat != null)
            return backwardCompat;

        throw new IllegalArgumentException("Invalid armor type '" + name + "'");
    }

    public static ArmorType armorTypeByName(String name) {

        for(ArmorType equipmentslot : ArmorType.values()) {
            if (equipmentslot.getName().equals(name)) {
                return equipmentslot;
            }
        }

        throw new IllegalArgumentException("Invalid armor type '" + name + "'");
    }

    public MutableComponent withFont(MutableComponent component, FontDescription font)
    {
        return component.withStyle(style -> style.withFont(font));
    }
}