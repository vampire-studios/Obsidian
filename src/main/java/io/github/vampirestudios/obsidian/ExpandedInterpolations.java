package io.github.vampirestudios.obsidian;

import io.github.vampirestudios.obsidian.AnimationEasing.EasingCategories;
import io.github.vampirestudios.obsidian.AnimationEasing.Type;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.animation.Transformation;

@Environment(EnvType.CLIENT)
public class ExpandedInterpolations {
	public static final Transformation.Interpolation EASE_IN_SINE = AnimationEasing.interpolation(EasingCategories.SINE, Type.IN);
	public static final Transformation.Interpolation EASE_OUT_SINE = AnimationEasing.interpolation(EasingCategories.SINE, Type.OUT);
	public static final Transformation.Interpolation EASE_IN_OUT_SINE = AnimationEasing.interpolation(EasingCategories.SINE, Type.IN_OUT);
	public static final Transformation.Interpolation EASE_IN_QUAD = AnimationEasing.interpolation(EasingCategories.QUAD, Type.IN);
	public static final Transformation.Interpolation EASE_OUT_QUAD = AnimationEasing.interpolation(EasingCategories.QUAD, Type.OUT);
	public static final Transformation.Interpolation EASE_IN_OUT_QUAD = AnimationEasing.interpolation(EasingCategories.QUAD, Type.IN_OUT);
	public static final Transformation.Interpolation EASE_IN_CUBIC = AnimationEasing.interpolation(EasingCategories.CUBIC, Type.IN);
	public static final Transformation.Interpolation EASE_OUT_CUBIC = AnimationEasing.interpolation(EasingCategories.CUBIC, Type.OUT);
	public static final Transformation.Interpolation EASE_IN_OUT_CUBIC = AnimationEasing.interpolation(EasingCategories.CUBIC, Type.IN_OUT);
	public static final Transformation.Interpolation EASE_IN_QUART = AnimationEasing.interpolation(EasingCategories.QUART, Type.IN);
	public static final Transformation.Interpolation EASE_OUT_QUART = AnimationEasing.interpolation(EasingCategories.QUART, Type.OUT);
	public static final Transformation.Interpolation EASE_IN_OUT_QUART = AnimationEasing.interpolation(EasingCategories.QUART, Type.IN_OUT);
	public static final Transformation.Interpolation EASE_IN_QUINT = AnimationEasing.interpolation(EasingCategories.QUINT, Type.IN);
	public static final Transformation.Interpolation EASE_OUT_QUINT = AnimationEasing.interpolation(EasingCategories.QUINT, Type.OUT);
	public static final Transformation.Interpolation EASE_IN_OUT_QUINT = AnimationEasing.interpolation(EasingCategories.QUINT, Type.IN_OUT);
	public static final Transformation.Interpolation EASE_IN_EXPO = AnimationEasing.interpolation(EasingCategories.EXPO, Type.IN);
	public static final Transformation.Interpolation EASE_OUT_EXPO = AnimationEasing.interpolation(EasingCategories.EXPO, Type.OUT);
	public static final Transformation.Interpolation EASE_IN_OUT_EXPO = AnimationEasing.interpolation(EasingCategories.EXPO, Type.IN_OUT);
	public static final Transformation.Interpolation EASE_IN_CIRC = AnimationEasing.interpolation(EasingCategories.CIRC, Type.IN);
	public static final Transformation.Interpolation EASE_OUT_CIRC = AnimationEasing.interpolation(EasingCategories.CIRC, Type.OUT);
	public static final Transformation.Interpolation EASE_IN_OUT_CIRC = AnimationEasing.interpolation(EasingCategories.CIRC, Type.IN_OUT);
	public static final Transformation.Interpolation EASE_IN_BACK = AnimationEasing.interpolation(EasingCategories.BACK, Type.IN);
	public static final Transformation.Interpolation EASE_OUT_BACK = AnimationEasing.interpolation(EasingCategories.BACK, Type.OUT);
	public static final Transformation.Interpolation EASE_IN_OUT_BACK = AnimationEasing.interpolation(EasingCategories.BACK, Type.IN_OUT);
	public static final Transformation.Interpolation EASE_IN_ELASTIC = AnimationEasing.interpolation(EasingCategories.ELASTIC, Type.IN);
	public static final Transformation.Interpolation EASE_OUT_ELASTIC = AnimationEasing.interpolation(EasingCategories.ELASTIC, Type.OUT);
	public static final Transformation.Interpolation EASE_IN_OUT_ELASTIC = AnimationEasing.interpolation(EasingCategories.ELASTIC, Type.IN_OUT);
	public static final Transformation.Interpolation EASE_IN_BOUNCE = AnimationEasing.interpolation(EasingCategories.BOUNCE, Type.IN);
	public static final Transformation.Interpolation EASE_OUT_BOUNCE = AnimationEasing.interpolation(EasingCategories.BOUNCE, Type.OUT);
	public static final Transformation.Interpolation EASE_IN_OUT_BOUNCE = AnimationEasing.interpolation(EasingCategories.BOUNCE, Type.IN_OUT);
}