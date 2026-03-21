package io.github.vampirestudios.obsidian;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import io.github.vampirestudios.obsidian.addon_modules.DataComponentPatchDeserializer;
import io.github.vampirestudios.obsidian.api.obsidian.*;
import io.github.vampirestudios.obsidian.registry.components.actionPos.ActionPosition;
import io.github.vampirestudios.obsidian.utils.IntArray;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Brightness;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Type;
import java.util.List;

public class BaseGson {
    private static final HolderLookup.Provider GLOBAL_REGISTRIES = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

    public static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping().setPrettyPrinting().setLenient()
            .enableComplexMapKeySerialization()
            .registerTypeAdapter(Identifier.class, new IdentifierTypeAdapter())
            .registerTypeAdapter(NameInformation.class, new NameInformationDeserializer())
            .registerTypeAdapter(TriState.class, new TriStateAdapter())
            .registerTypeAdapter(IntArray.class, new IntArrayTypeAdapter())
            .registerTypeAdapter(DataComponentPatch.class, new DataComponentPatchDeserializer())
            .registerTypeAdapter(Item.class, new RegistrySerializer<>(BuiltInRegistries.ITEM))
            .registerTypeAdapter(Block.class, new RegistrySerializer<>(BuiltInRegistries.BLOCK))
            .registerTypeAdapter(SoundEvent.class, new RegistrySerializer<>(BuiltInRegistries.SOUND_EVENT))
            .registerTypeAdapter(MobEffect.class, new RegistrySerializer<>(BuiltInRegistries.MOB_EFFECT))
            .registerTypeAdapter(EntityType.class, new RegistrySerializer<>(BuiltInRegistries.ENTITY_TYPE))
            .registerTypeAdapter(BlockEntityType.class, new RegistrySerializer<>(BuiltInRegistries.BLOCK_ENTITY_TYPE))
            .registerTypeAdapter(HeightProviderType.class, new RegistrySerializer<>(BuiltInRegistries.HEIGHT_PROVIDER_TYPE))
            .registerTypeAdapter(BlockPredicateType.class, new RegistrySerializer<>(BuiltInRegistries.BLOCK_PREDICATE_TYPE))
            .registerTypeAdapter(CreativeModeTab.class, new RegistrySerializer<>(BuiltInRegistries.CREATIVE_MODE_TAB))
//            .registerTypeAdapter(Component.class, new Component.Serializer())
            .registerTypeAdapter(ItemStack.class, new ItemStackSerializer())
            .registerTypeAdapter(CompoundTag.class, new CodecSerializer<>(CompoundTag.CODEC))
            .registerTypeAdapter(BlockPos.class, new CodecSerializer<>(BlockPos.CODEC))
            .registerTypeAdapter(Vec3.class, new CodecSerializer<>(Vec3.CODEC))
            .registerTypeAdapter(Vec2.class, new CodecSerializer<>(Codec.list(Codec.DOUBLE).xmap(x -> new Vec2(x.get(0).floatValue(), x.get(1).floatValue()), x -> List.of((double) x.x, (double) x.y))))
            .registerTypeAdapter(EntityDimensions.class, new CodecSerializer<>(Codec.list(Codec.DOUBLE).xmap(x -> EntityDimensions.fixed(x.get(0).floatValue(), x.get(1).floatValue()), x -> List.of((double) x.width(), (double) x.height()))))
            .registerTypeAdapter(BlockState.class, new CodecSerializer<>(BlockState.CODEC))
            .registerTypeAdapter(Transformation.class, new CodecSerializer<>(Transformation.CODEC))
            .registerTypeAdapter(Display.BillboardConstraints.class, new CodecSerializer<>(Display.BillboardConstraints.CODEC))
            .registerTypeAdapter(ParticleOptions.class, new CodecSerializer<>(ParticleTypes.CODEC))
            .registerTypeAdapter(Display.TextDisplay.Align.class, new CodecSerializer<>(Display.TextDisplay.Align.CODEC))
            .registerTypeAdapter(Brightness.class, new CodecSerializer<>(Brightness.CODEC))
            .registerTypeAdapter(ActionPosition.class, new CodecSerializer<>(ActionPosition.CODEC))
            .create();

    private record ItemStackSerializer() implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
        @Override
        public ItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement.isJsonObject()) {
                return ItemStack.CODEC.decode(RegistryOps.create(JsonOps.INSTANCE, GLOBAL_REGISTRIES), jsonElement).result().orElse(Pair.of(ItemStack.EMPTY, null)).getFirst();
            } else {
                return BuiltInRegistries.ITEM.getValue(Identifier.tryParse(jsonElement.getAsString())).getDefaultInstance();
            }
        }

        @Override
        public JsonElement serialize(ItemStack stack, Type type, JsonSerializationContext jsonSerializationContext) {
            if (stack.getCount() == 1) {
                return new JsonPrimitive(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
            }

            return ItemStack.CODEC.encodeStart(RegistryOps.create(JsonOps.INSTANCE, GLOBAL_REGISTRIES), stack).result().orElse(null);
        }
    }

    private record RegistrySerializer<T>(Registry<T> registry) implements JsonSerializer<T>, JsonDeserializer<T> {
        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return this.registry.getValue(Identifier.tryParse(json.getAsString()));
            }
            return null;
        }

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive("" + this.registry.getId(src));
        }
    }

    private record CodecSerializer<T>(Codec<T> codec) implements JsonSerializer<T>, JsonDeserializer<T> {
        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return this.codec.decode(JsonOps.INSTANCE, json).getOrThrow().getFirst();
            } catch (Throwable e) {
                return null;
            }
        }

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            try {
                return src != null ? this.codec.encodeStart(JsonOps.INSTANCE, src).getOrThrow() : JsonNull.INSTANCE;
            } catch (Throwable e) {
                return JsonNull.INSTANCE;
            }
        }
    }
}