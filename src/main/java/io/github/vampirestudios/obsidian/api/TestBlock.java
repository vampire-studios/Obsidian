package io.github.vampirestudios.obsidian.api;

public class TestBlock {

    private float resistance;
    private float hardness;
    private boolean tool_required;
    private boolean random_ticks;
    private float slipperiness;
    private float velocity_multiplier;
    private float jump_velocity_multiplier;
    private String name;

    public TestBlock(float resistance, float hardness, boolean tool_required, boolean random_ticks, float slipperiness, float velocity_multiplier, float jump_velocity_multiplier, String name) {
        this.resistance = resistance;
        this.hardness = hardness;
        this.tool_required = tool_required;
        this.random_ticks = random_ticks;
        this.slipperiness = slipperiness;
        this.velocity_multiplier = velocity_multiplier;
        this.jump_velocity_multiplier = jump_velocity_multiplier;
        this.name = name;
    }

    public float getResistance() {
        return resistance;
    }

    public float getHardness() {
        return hardness;
    }

    public boolean isToolRequired() {
        return tool_required;
    }

    public boolean isRandomTicks() {
        return random_ticks;
    }

    public float getSlipperiness() {
        return slipperiness;
    }

    public float getVelocityMultiplier() {
        return velocity_multiplier;
    }

    public float getJumpVelocityMultiplier() {
        return jump_velocity_multiplier;
    }

    public String getName() {
        return name;
    }
}