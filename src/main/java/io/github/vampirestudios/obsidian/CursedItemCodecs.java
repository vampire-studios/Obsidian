/*
package io.github.vampirestudios.obsidian;

import io.github.boogiemonster1o1.opencodecs.builder.FoodComponentBuilder;
import io.github.boogiemonster1o1.opencodecs.builder.ItemBuilder;
import io.github.boogiemonster1o1.opencodecs.builder.ItemSettingsBuilder;
import io.github.boogiemonster1o1.opencodecs.builder.StatusEffectInstanceBuilder;
import io.github.boogiemonster1o1.opencodecs.util.ItemSettingsUtil;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

@SuppressWarnings({"Convert2MethodRef", "CodeBlock2Expr"})
public interface CursedItemCodecs {
    Codec<StatusEffectInstance> STATUS_EFFECT_INSTANCE_CODEC_V0 = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.STRING.fieldOf("id").forGetter((effect) -> {
            return Objects.requireNonNull(Registry.STATUS_EFFECT.getId(effect.getEffectType())).toString();
        }), Codec.INT.fieldOf("duration").forGetter((effect) -> {
            return effect.getDuration();
        }), Codec.intRange(0, 255).fieldOf("amplifier").forGetter((effect) -> {
            return effect.getAmplifier();
        })).apply(instance, StatusEffectInstanceBuilder::createV0);
    });

    Codec<Pair<StatusEffectInstance, Float>> STATUS_EFFECT_ENTRY_CODEC_V0 = RecordCodecBuilder.create((instance) -> {
        return instance.group(STATUS_EFFECT_INSTANCE_CODEC_V0.fieldOf("effect").forGetter((pair) -> {
            return pair.getFirst();
        }), Codec.FLOAT.fieldOf("chance").forGetter((pair) -> {
            return pair.getSecond();
        })).apply(instance, Pair::new);
    });

    Codec<FoodComponent> FOOD_COMPONENT_CODEC_V0 = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("hunger").forGetter((food) -> {
            return food.getHunger();
        }), Codec.DOUBLE.fieldOf("saturationModifier").forGetter((food) -> {
            return Double.valueOf(food.getSaturationModifier());
        }), Codec.BOOL.fieldOf("alwaysEdible").forGetter((food) -> {
            return food.isAlwaysEdible();
        }), Codec.BOOL.fieldOf("meat").forGetter((food) -> {
            return food.isMeat();
        }), Codec.BOOL.fieldOf("snack").forGetter((food) -> {
            return food.isSnack();
        }), Codec.list(STATUS_EFFECT_ENTRY_CODEC_V0).optionalFieldOf("effects", null).forGetter((food) -> {
            return food.getStatusEffects();
        })).apply(instance, FoodComponentBuilder::createV0);
    });

    Codec<Item.Settings> ITEM_SETTINGS_CODEC_V0 = RecordCodecBuilder.create((instance) -> {
       return instance.group(Codec.INT.fieldOf("maxCount").forGetter((settings) -> {
           return ItemSettingsUtil.getMaxCount(settings);
       }), Codec.INT.fieldOf("maxDamage").forGetter((settings) -> {
           return ItemSettingsUtil.getMaxDamage(settings);
       }), Codec.STRING.optionalFieldOf("recipeRemainder", null).forGetter((settings) -> {
           return Registry.ITEM.getId(ItemSettingsUtil.getRecipeRemainder(settings)).toString();
       }), Codec.STRING.optionalFieldOf("itemGroupName", null).forGetter((settings) -> {
           return ItemSettingsUtil.getItemGroup(settings).getName();
       }), Codec.STRING.optionalFieldOf("rarity", Rarity.COMMON.toString()).forGetter((settings) -> {
           return ItemSettingsUtil.getRarity(settings).toString();
       }), FOOD_COMPONENT_CODEC_V0.optionalFieldOf("foodComponent", null).forGetter((settings) -> {
           return ItemSettingsUtil.getFoodComponent(settings);
       }), Codec.BOOL.optionalFieldOf("fireproof", false).forGetter((settings) -> {
           return ItemSettingsUtil.isFireproof(settings);
       })).apply(instance, ItemSettingsBuilder::createV0);
    });

    Codec<Item> ITEM_CODEC_V0 = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.STRING.fieldOf("name").forGetter((item) -> {
            return Registry.ITEM.getId(item).getPath();
        }), ITEM_SETTINGS_CODEC_V0.fieldOf("settings").forGetter((item) -> {
            return Shared.ITEM_SETTINGS_MAP.get(item);
        }), Codec.INT.fieldOf("enchantability").forGetter((item) -> {
            return item.getEnchantability();
        }), Codec.list(Codec.STRING).fieldOf("tooltips").forGetter((item) -> {
            List<Text> tooltip = Lists.newArrayList();
            item.appendTooltip(null, null, tooltip, null);
            return tooltip.stream().map((text) -> {
                return ((TranslatableText) text).getKey();
            }).collect(Collectors.toList());
        }), Codec.BOOL.fieldOf("enchantable").forGetter((item) -> {
            return item.isEnchantable(null);
        }), Codec.BOOL.fieldOf("glint").forGetter((item) -> {
            return item.hasGlint(null);
        }), Codec.STRING.optionalFieldOf("drinkSound", Objects.requireNonNull(Registry.SOUND_EVENT.getId(SoundEvents.ENTITY_GENERIC_DRINK)).toString()).forGetter((item) -> {
            return Objects.requireNonNull(Registry.SOUND_EVENT.getId(item.getDrinkSound())).toString();
        }), Codec.STRING.optionalFieldOf("eatSound", Objects.requireNonNull(Registry.SOUND_EVENT.getId(SoundEvents.ENTITY_GENERIC_EAT)).toString()).forGetter((item) -> {
            return Objects.requireNonNull(Registry.SOUND_EVENT.getId(item.getEatSound())).toString();
        })).apply(instance, ItemBuilder::createV0);
    });
}*/
