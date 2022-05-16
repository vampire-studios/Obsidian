package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.*;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.entity.models.AnimationFile;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class EntityAnimations implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        AnimationFile animationFile = Obsidian.GSON.fromJson(new FileReader(file), AnimationFile.class);
        try {
            if (animationFile == null) return;
            animationFile.animations.forEach((animationName, animationInformation) -> {
                AnimationDefinition.Builder animationDefinition = AnimationDefinition.Builder.withLength(animationInformation.animationLength);
                if (animationInformation.loop) animationDefinition.looping();
                animationInformation.bones.forEach((s1, bone) -> {
                    List<Keyframe> keyframes = new ArrayList<>();
                    bone.transformations.forEach((s2, boneTransformation) -> {
                        boneTransformation.animationFrame.forEach((s3, boneInformation) -> {
                            float[] post = boneInformation.post;
                            Vec3f vector = switch(s2) {
                                case "rotation" -> KeyframeAnimations.degreeVec(post[0], post[1], post[2]);
                                case "position" -> KeyframeAnimations.posVec(post[0], post[1], post[2]);
                                case "scale" -> KeyframeAnimations.scaleVec(post[0], post[1], post[2]);
                                default -> throw new InvalidIdentifierException("");
                            };
                            keyframes.add(new Keyframe(s3, vector, Obsidian.ANIMATION_CHANNEL_INTERPOLATIONS.get(boneInformation.lerpMode)));
                        });
                        AnimationChannel.Target target = switch(s2) {
                            case "rotation" -> AnimationChannel.Targets.ROTATION;
                            case "position" -> AnimationChannel.Targets.POSITION;
                            case "scale" -> AnimationChannel.Targets.SCALE;
                            default -> throw new InvalidIdentifierException("");
                        };
                        animationDefinition.addAnimation(s1, new AnimationChannel(target, keyframes));
                    });
                });

                animationName = animationName.replace("animation.", "").replace(".", "_");
                Registry.register(Obsidian.ANIMATION_DEFINITIONS, animationName, animationDefinition.build());
            });
            register(ENTITY_ANIMATIONS, "entity_animation", new Identifier(id.modId, file.getName().replace(".json", "")), animationFile);
        } catch (Exception e) {
            failedRegistering("entity_animation", file.getName().replace(".json", ""), e);
        }
    }

    @Override
    public String getType() {
        return "entities/animations";
    }
}
