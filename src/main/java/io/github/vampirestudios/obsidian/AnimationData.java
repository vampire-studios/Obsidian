package io.github.vampirestudios.obsidian;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AnimationData {

    private final Animation animation;

    private Keyframe lastKeyframe;
    private Keyframe nextKeyframe;

    private int tickCounter;

    private final boolean shouldLoop, freeze;

    public AnimationData(Animation a, int ticks, boolean shouldLoop, boolean freeze) {
        this.animation = a;
        this.tickCounter = ticks;
        this.shouldLoop = shouldLoop;
        this.freeze = freeze;

        reset();
    }

    public void processAnimation(float delta) {
        while (tickCounter >= nextKeyframe.getTimeStamp() && nextKeyframe != animation.getKeyframes().get(0)) {
            lastKeyframe = nextKeyframe;
            nextKeyframe = getNextKeyframe();
        }
        animation.applyFrameData(lastKeyframe, tickCounter, delta);
    }

    public Keyframe getNextKeyframe() {
        int indexNext = animation.getKeyframes().indexOf(lastKeyframe) + 1;
        return animation.getKeyframes().get(indexNext == animation.getKeyframeCount() ? 0 : indexNext);
    }

    public void reset() {
        this.lastKeyframe = animation.getKeyframes().get(0);

        for(Keyframe f : animation.getKeyframes())
            if(f.getTimeStamp() <= tickCounter)
                this.lastKeyframe = f;

        this.nextKeyframe = getNextKeyframe();
    }

    public boolean tick() {
        if(!freeze) {
            if(tickCounter++ >= animation.getLength()) {
                if(!shouldLoop)
                    return false;
                tickCounter = 0;
            }
        }
        return true;
    }
}
