package io.github.vampirestudios.obsidian.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

/**
 * Modifies trades for a merchant type.
 */
public final class ModifyTradesEvents {

    public static final Event<ModifyVillager> VILLAGER = EventFactory.createArrayBacked(ModifyVillager.class, listeners ->
            context -> {
                for (ModifyVillager event : listeners) {
                    event.modifyTrades(context);
                }
            });
    public static final Event<ModifyWanderer> WANDERER = EventFactory.createArrayBacked(ModifyWanderer.class, listeners ->
            context -> {
                for (ModifyWanderer event : listeners) {
                    event.modifyTrades(context);
                }
            });

    /**
     * Registers new trades into all villager types trader.
     */
    @FunctionalInterface
    public interface ModifyVillager {

        /**
         * Modifies the trades for all normal villagers.
         *
         * @param context The context for the event
         */
        void modifyTrades(Context context);

        /**
         * Context for registering villager trades.
         *
         * @since 1.0.0
         */
        interface Context {

            /**
             * @return The profession of the villager to add trades to
             */
            VillagerProfession getProfession();

            /**
             * Retrieves the registry of trades for the specified tier.
             *
             * @param tier A number between 1 and 5 to retrieve the tier of trades
             * @return The registry for that tier
             */
            TradeRegistry getTrades(int tier);
        }
    }

    /**
     * Registers new trades into the wandering trader.
     */
    @FunctionalInterface
    public interface ModifyWanderer {

        /**
         * Modifies the trades for the wandering trader.
         *
         * @param context The context for the event
         */
        void modifyTrades(Context context);

        /**
         * Context for registering wanderer trades.
         *
         * @since 1.0.0
         */
        interface Context {

            /**
             * @return The common trades registry
             */
            TradeRegistry getGeneric();

            /**
             * @return The rarer trades registry
             */
            TradeRegistry getRare();
        }
    }

    /**
     * Registers trades into a villager trade list.
     */
    public static class TradeRegistry implements List<TradeOffers.Factory> {

        private final List<TradeOffers.Factory> trades;

        public TradeRegistry() {
            this.trades = DefaultedList.of();
        }

        @Override
        public int size() {
            return this.trades.size();
        }

        @Override
        public boolean isEmpty() {
            return this.trades.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return this.trades.contains(o);
        }

        @NotNull
        @Override
        public Iterator<TradeOffers.Factory> iterator() {
            return this.trades.iterator();
        }

        @NotNull
        @Override
        public Object[] toArray() {
            return this.trades.toArray();
        }

        @NotNull
        @Override
        public <T> T[] toArray(@NotNull T[] a) {
            return this.trades.toArray(a);
        }

        @Override
        public boolean add(TradeOffers.Factory listing) {
            return this.trades.add(listing);
        }

        @Override
        public boolean remove(Object o) {
            return this.trades.remove(o);
        }

        @Override
        public boolean containsAll(@NotNull Collection<?> c) {
            return this.trades.containsAll(c);
        }

        @Override
        public boolean addAll(@NotNull Collection<? extends TradeOffers.Factory> c) {
            return this.trades.addAll(c);
        }

        @Override
        public boolean addAll(int index, @NotNull Collection<? extends TradeOffers.Factory> c) {
            return this.trades.addAll(index, c);
        }

        @Override
        public boolean removeAll(@NotNull Collection<?> c) {
            return this.trades.removeAll(c);
        }

        @Override
        public boolean retainAll(@NotNull Collection<?> c) {
            return this.trades.retainAll(c);
        }

        @Override
        public void clear() {
            this.trades.clear();
        }

        @Override
        public TradeOffers.Factory get(int index) {
            return this.trades.get(index);
        }

        @Override
        public TradeOffers.Factory set(int index, TradeOffers.Factory element) {
            return this.trades.set(index, element);
        }

        @Override
        public void add(int index, TradeOffers.Factory element) {
            this.trades.add(index, element);
        }

        @Override
        public TradeOffers.Factory remove(int index) {
            return this.trades.remove(index);
        }

        @Override
        public int indexOf(Object o) {
            return this.trades.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return this.trades.lastIndexOf(o);
        }

        @NotNull
        @Override
        public ListIterator<TradeOffers.Factory> listIterator() {
            return this.trades.listIterator();
        }

        @NotNull
        @Override
        public ListIterator<TradeOffers.Factory> listIterator(int index) {
            return this.trades.listIterator(index);
        }

        @NotNull
        @Override
        public List<TradeOffers.Factory> subList(int fromIndex, int toIndex) {
            return this.trades.subList(fromIndex, toIndex);
        }

        /**
         * Adds a simple trade for items or emeralds.
         *
         * @param item           The item to trade for
         * @param emeralds       The amount of emeralds to trade
         * @param itemCount      The amount of the item to trade
         * @param maxUses        The maximum amount of times this trade can be used before needing to reset
         * @param xpGain         The amount of experience gained by this exchange
         * @param sellToVillager Whether the villager is buying or selling the item for emeralds
         */
        public void add(ItemConvertible item, int emeralds, int itemCount, int maxUses, int xpGain, boolean sellToVillager) {
            this.add(new ItemTrade(() -> item, emeralds, itemCount, maxUses, xpGain, 0.05F, sellToVillager));
        }

        /**
         * Adds a simple trade for items or emeralds.
         *
         * @param item            The item to trade for
         * @param emeralds        The amount of emeralds to trade
         * @param itemCount       The amount of the item to trade
         * @param maxUses         The maximum amount of times this trade can be used before needing to reset
         * @param xpGain          The amount of experience gained by this exchange
         * @param priceMultiplier The multiplier for how much the price deviates
         * @param sellToVillager  Whether the villager is buying or selling the item for emeralds
         */
        public void add(ItemConvertible item, int emeralds, int itemCount, int maxUses, int xpGain, float priceMultiplier, boolean sellToVillager) {
            this.add(new ItemTrade(() -> item, emeralds, itemCount, maxUses, xpGain, priceMultiplier, sellToVillager));
        }

        /**
         * Adds a simple trade for items or emeralds.
         *
         * @param item           The item to trade for as a supplier
         * @param emeralds       The amount of emeralds to trade
         * @param itemCount      The amount of the item to trade
         * @param maxUses        The maximum amount of times this trade can be used before needing to reset
         * @param xpGain         The amount of experience gained by this exchange
         * @param sellToVillager Whether the villager is buying or selling the item for emeralds
         */
        public void add(Supplier<? extends ItemConvertible> item, int emeralds, int itemCount, int maxUses, int xpGain, boolean sellToVillager) {
            this.add(new ItemTrade(item, emeralds, itemCount, maxUses, xpGain, 0.05F, sellToVillager));
        }

        /**
         * Adds a simple trade for items or emeralds.
         *
         * @param item            The item to trade for as a supplier
         * @param emeralds        The amount of emeralds to trade
         * @param itemCount       The amount of the item to trade
         * @param maxUses         The maximum amount of times this trade can be used before needing to reset
         * @param xpGain          The amount of experience gained by this exchange
         * @param priceMultiplier The multiplier for how much the price deviates
         * @param sellToVillager  Whether the villager is buying or selling the item for emeralds
         */
        public void add(Supplier<? extends ItemConvertible> item, int emeralds, int itemCount, int maxUses, int xpGain, float priceMultiplier, boolean sellToVillager) {
            this.add(new ItemTrade(item, emeralds, itemCount, maxUses, xpGain, priceMultiplier, sellToVillager));
        }
    }

    private record ItemTrade(Supplier<? extends ItemConvertible> item, int emeralds,
                             int itemCount, int maxUses, int xpGain, float priceMultiplier,
                             boolean sellToVillager) implements TradeOffers.Factory {
        @Override
        public TradeOffer create(Entity entity, RandomGenerator random) {
            ItemStack emeralds = new ItemStack(Items.EMERALD, this.emeralds);
            ItemStack item = new ItemStack(this.item.get(), this.itemCount);

            return new TradeOffer(this.sellToVillager ? item : emeralds, this.sellToVillager ? emeralds : item, this.maxUses, this.xpGain, this.priceMultiplier);
        }
    }
}