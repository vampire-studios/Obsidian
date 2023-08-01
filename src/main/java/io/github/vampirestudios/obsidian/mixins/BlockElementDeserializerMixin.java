package io.github.vampirestudios.obsidian.mixins;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.util.GsonHelper;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockElement.Deserializer.class)
public abstract class BlockElementDeserializerMixin {

    @Shadow
    protected abstract Vector3f getVector3f(JsonObject json, String memberName);

    /**
     * @author a
     * @reason a
     */
    @Overwrite
    private float getAngle(JsonObject json) {
        return GsonHelper.getAsFloat(json, "angle");
    }

    /**
     * @author a
     * @reason a
     */
    @Overwrite
    private Vector3f getTo(JsonObject json) {
        Vector3f vector3f = this.getVector3f(json, "to");
        if (!(vector3f.x() < -512.0F)
                && !(vector3f.y() < -512.0F)
                && !(vector3f.z() < -512.0F)
                && !(vector3f.x() > 512.0F)
                && !(vector3f.y() > 512.0F)
                && !(vector3f.z() > 512.0F)) {
            return vector3f;
        } else {
            throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + vector3f);
        }
    }

    /**
     * @author a
     * @reason a
     */
    @Overwrite
    private Vector3f getFrom(JsonObject json) {
        Vector3f vector3f = this.getVector3f(json, "from");
        if (!(vector3f.x() < -512.0F)
                && !(vector3f.y() < -512.0F)
                && !(vector3f.z() < -512.0F)
                && !(vector3f.x() > 512.0F)
                && !(vector3f.y() > 512.0F)
                && !(vector3f.z() > 512.0F)) {
            return vector3f;
        } else {
            throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + vector3f);
        }
    }
}
