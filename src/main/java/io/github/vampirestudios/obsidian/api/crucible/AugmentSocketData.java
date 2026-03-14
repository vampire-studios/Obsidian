package io.github.vampirestudios.obsidian.api.crucible;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.*;

public record AugmentSocketData(List<AugmentSlotEntry> slots, Map<String, Integer> maxSlotsPerType) {

    public static final AugmentSocketData EMPTY = new AugmentSocketData(List.of(), Map.of());

    public static final Codec<AugmentSocketData> CODEC = RecordCodecBuilder.create(i -> i.group(
            AugmentSlotEntry.CODEC.listOf().fieldOf("slots").forGetter(AugmentSocketData::slots),
            Codec.unboundedMap(Codec.STRING, Codec.INT)
                    .optionalFieldOf("max_slots", Map.of())
                    .forGetter(AugmentSocketData::maxSlotsPerType)
    ).apply(i, AugmentSocketData::new));

    public static final StreamCodec<FriendlyByteBuf, AugmentSocketData> STREAM_CODEC = StreamCodec.composite(
            AugmentSlotEntry.STREAM_CODEC.apply(ByteBufCodecs.list()),
            AugmentSocketData::slots,
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.VAR_INT),
            AugmentSocketData::maxSlotsPerType,
            AugmentSocketData::new
    );

    public int countFreeSlots(String type) {
        return (int) slots.stream().filter(s -> s.type().equalsIgnoreCase(type) && s.isEmpty()).count();
    }

    public int countFilledSlots(String type) {
        return (int) slots.stream().filter(s -> s.type().equalsIgnoreCase(type) && s.isFilled()).count();
    }

    public int countTotalSlots(String type) {
        return (int) slots.stream().filter(s -> s.type().equalsIgnoreCase(type)).count();
    }

    public int getMaxSlots(String type) {
        return maxSlotsPerType.getOrDefault(type.toUpperCase(Locale.ROOT), Integer.MAX_VALUE);
    }

    /** Returns a new AugmentSocketData with the augment added to the first free slot of the given type. */
    public AugmentSocketData withAugmentAdded(String type, String augmentId) {
        List<AugmentSlotEntry> newSlots = new ArrayList<>(slots);
        for (int i = 0; i < newSlots.size(); i++) {
            AugmentSlotEntry slot = newSlots.get(i);
            if (slot.type().equalsIgnoreCase(type) && slot.isEmpty()) {
                newSlots.set(i, slot.withAugment(augmentId));
                return new AugmentSocketData(List.copyOf(newSlots), maxSlotsPerType);
            }
        }
        return this;
    }

    /** Returns a new AugmentSocketData with the last filled slot of the given type cleared (augment removed, slot kept). */
    public AugmentSocketData withAugmentRemoved(String type) {
        List<AugmentSlotEntry> newSlots = new ArrayList<>(slots);
        for (int i = newSlots.size() - 1; i >= 0; i--) {
            AugmentSlotEntry slot = newSlots.get(i);
            if (slot.type().equalsIgnoreCase(type) && slot.isFilled()) {
                newSlots.set(i, slot.cleared());
                return new AugmentSocketData(List.copyOf(newSlots), maxSlotsPerType);
            }
        }
        return this;
    }

    /** Returns a new AugmentSocketData with the last slot of the given type removed entirely. */
    public AugmentSocketData withSocketDestroyed(String type) {
        List<AugmentSlotEntry> newSlots = new ArrayList<>(slots);
        for (int i = newSlots.size() - 1; i >= 0; i--) {
            if (newSlots.get(i).type().equalsIgnoreCase(type)) {
                newSlots.remove(i);
                return new AugmentSocketData(List.copyOf(newSlots), maxSlotsPerType);
            }
        }
        return this;
    }

    /** Returns a new AugmentSocketData with one additional empty slot of the given type. */
    public AugmentSocketData withSlotAdded(String type) {
        List<AugmentSlotEntry> newSlots = new ArrayList<>(slots);
        newSlots.add(new AugmentSlotEntry(type.toUpperCase(Locale.ROOT), Optional.empty()));
        return new AugmentSocketData(List.copyOf(newSlots), maxSlotsPerType);
    }

    /** Returns the augment ID in the last filled slot of the given type, or empty. */
    public Optional<String> getLastAugmentId(String type) {
        for (int i = slots.size() - 1; i >= 0; i--) {
            AugmentSlotEntry slot = slots.get(i);
            if (slot.type().equalsIgnoreCase(type) && slot.isFilled()) {
                return slot.augmentId();
            }
        }
        return Optional.empty();
    }
}
