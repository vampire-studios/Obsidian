package io.github.vampirestudios.obsidian.api.obsidian.biomeLayouts;

import net.minecraft.world.biome.source.util.MultiNoiseUtil;

public class MultiNoise {

    public MultiNoiseValue temperatureNoise;
    public MultiNoiseValue humidityNoise;
    public MultiNoiseValue continentalnessNoise;
    public MultiNoiseValue erosionNoise;
    public MultiNoiseValue depthNoise;
    public MultiNoiseValue weirdnessNoise;
    public MultiNoiseValue regionSideNoise;

    public float temperatureValue;
    public float humidityValue;
    public float continentalnessValue;
    public float erosionValue;
    public float depthValue;
    public float weirdnessValue;
    public float regionSideValue;

    public float offset = 0.0F;
    public boolean floatValues = false;

    public static class MultiNoiseValue {
        public MultiNoiseValueType type;
        public float minimum;
        public float maximum;
        public float value;

        public enum MultiNoiseValueType {
            RANGE,
            POINT
        }

        public MultiNoiseUtil.ParameterRange getValue() {
            return switch(type) {
                case POINT -> MultiNoiseUtil.ParameterRange.of(value);
                case RANGE -> MultiNoiseUtil.ParameterRange.of(minimum, maximum);
            };
        }
    }

}
