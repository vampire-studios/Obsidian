package io.github.vampirestudios.obsidian.utils;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.lang.reflect.Type;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

//Based from ee3's code
public class ItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    private static final String NAME = "item";
    private static final String STACK_SIZE = "count";
    private static final String TAG_COMPOUND = "tag";

    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();

            String name = null;
            int stackSize = 1;
            CompoundTag tagCompound = null;

            if (jsonObject.has(NAME) && jsonObject.get(NAME).isJsonPrimitive()) {
                name = jsonObject.getAsJsonPrimitive(NAME).getAsString();
            }

            if (jsonObject.has(STACK_SIZE) && jsonObject.get(STACK_SIZE).isJsonPrimitive()) {
                stackSize = jsonObject.getAsJsonPrimitive(STACK_SIZE).getAsInt();
            }

            if (jsonObject.has(TAG_COMPOUND) && jsonObject.get(TAG_COMPOUND).isJsonPrimitive()) {
                try {
                    tagCompound = TagParser.parseTag(jsonObject.getAsJsonPrimitive(TAG_COMPOUND).getAsString());
                } catch (CommandSyntaxException ignored) {

                }
            }

            if (name != null && BuiltInRegistries.ITEM.get(new ResourceLocation(name)) != null) {
                ItemStack itemStack = new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(name)), stackSize);
                itemStack.setTag(tagCompound);
                return itemStack;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {

        if (src != null && src.getItem() != null) {
            JsonObject jsonObject = new JsonObject();

            if (BuiltInRegistries.ITEM.getKey(src.getItem()) != null) {
                jsonObject.addProperty(NAME, BuiltInRegistries.ITEM.getKey(src.getItem()).toString());
            } else {
                return JsonNull.INSTANCE;
            }

            jsonObject.addProperty(STACK_SIZE, src.getCount());

            if (src.getTag() != null) {
                jsonObject.addProperty(TAG_COMPOUND, src.getTag().toString());
            }

            return jsonObject;
        }

        return JsonNull.INSTANCE;
    }
}