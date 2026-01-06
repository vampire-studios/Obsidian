package io.github.vampirestudios.obsidian.api;

/*
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public final class TooltipAPI {
    // Wire per-stack data via Data Components (1.21+)
    public static DataComponentType<TooltipPayload> TOOLTIP_PAYLOAD;

    // JSON element model (data-driven)
    public sealed interface Element permits TextEl, BadgeEl, BarEl, IconEl, GridEl, KvEl, ConditionalEl {
        Codec<Element> CODEC = Codec.<String, Codec<Element>>dispatchedMap(
            Map.of(
                "text", TextEl.CODEC,
                "badge", BadgeEl.CODEC,
                "bar", BarEl.CODEC,
                "icon", IconEl.CODEC,
                "grid", GridEl.CODEC,
                "kv", KvEl.CODEC,
                "if", ConditionalEl.CODEC
            ),
            e -> e.kind()
        ).xmap(m -> m.values().iterator().next(), e -> Map.of(e.kind(), e));
        String kind();
    }

    public record TextEl(String text, String style) implements Element {
        static final Codec<TextEl> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("text").forGetter(TextEl::text),
            Codec.STRING.optionalFieldOf("style", "").forGetter(TextEl::style)
        ).apply(i, TextEl::new));
        public String kind() { return "text"; }
    }

    public record BadgeEl(String label, String tone) implements Element {
        static final Codec<BadgeEl> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("label").forGetter(BadgeEl::label),
            Codec.STRING.optionalFieldOf("tone", "primary").forGetter(BadgeEl::tone)
        ).apply(i, BadgeEl::new));
        public String kind() { return "badge"; }
    }

    public record BarEl(float value, float max, String label) implements Element {
        static final Codec<BarEl> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.FLOAT.fieldOf("value").forGetter(BarEl::value),
            Codec.FLOAT.fieldOf("max").forGetter(BarEl::max),
            Codec.STRING.optionalFieldOf("label", "").forGetter(BarEl::label)
        ).apply(i, BarEl::new));
        public String kind() { return "bar"; }
    }

    public record IconEl(Identifier sprite, String text) implements Element {
        static final Codec<IconEl> CODEC = RecordCodecBuilder.create(i -> i.group(
            Identifier.CODEC.fieldOf("sprite").forGetter(IconEl::sprite),
            Codec.STRING.optionalFieldOf("text", "").forGetter(IconEl::text)
        ).apply(i, IconEl::new));
        public String kind() { return "icon"; }
    }

    public record GridEl(List<String> items, int columns) implements Element {
        static final Codec<GridEl> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.listOf().fieldOf("items").forGetter(GridEl::items),
            Codec.INT.optionalFieldOf("columns", 3).forGetter(GridEl::columns)
        ).apply(i, GridEl::new));
        public String kind() { return "grid"; }
    }

    public record KvEl(List<Pair> pairs) implements Element {
        public record Pair(String k, String v) {
            static final Codec<Pair> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.fieldOf("k").forGetter(Pair::k),
                Codec.STRING.fieldOf("v").forGetter(Pair::v)
            ).apply(i, Pair::new));
        }
        static final Codec<KvEl> CODEC = RecordCodecBuilder.create(i -> i.group(
            Pair.CODEC.listOf().fieldOf("pairs").forGetter(KvEl::pairs)
        ).apply(i, KvEl::new));
        public String kind() { return "kv"; }
    }

    public record ConditionalEl(String test, Element thenBranch, Optional<Element> elseBranch) implements Element {
        static final Codec<ConditionalEl> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("test").forGetter(ConditionalEl::test),
            Element.CODEC.fieldOf("then").forGetter(ConditionalEl::thenBranch),
            Element.CODEC.optionalFieldOf("else").forGetter(ConditionalEl::elseBranch)
        ).apply(i, ConditionalEl::new));
        public String kind() { return "if"; }
    }

    public record TooltipPayload(List<Element> elements) {}

    public static Component placeholder(String s, TooltipContext ctx) {
        // Very small placeholder engine: %player%, %nbt:ItemName%, %dim%, etc.
        // Extend as needed
        return Component.literal(
            s.replace("%player%", ctx.playerName())
             .replace("%dim%", ctx.dimension())
             // add NBT/attributes accessors…
        );
    }

    public record TooltipContext(String playerName, String dimension, ItemStack stack, boolean advanced) {}

    // Client-side renderer hook for complex elements
    public interface ElementRenderer<T extends Element> {
        boolean supports(Element e);
        void emitLines(T e, TooltipContext ctx, List<Component> lines, List<TooltipComponent> custom);
    }
}
*/
