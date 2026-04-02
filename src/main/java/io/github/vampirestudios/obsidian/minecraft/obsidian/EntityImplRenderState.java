package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.AnimationState;

import java.util.HashMap;
import java.util.Map;

public class EntityImplRenderState extends LivingEntityRenderState {
    public Map<AnimationState, Identifier> animationStates = new HashMap<>();
}
