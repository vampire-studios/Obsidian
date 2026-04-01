package io.github.vampirestudios.obsidian.mixins;

import com.google.gson.JsonObject;
import net.minecraft.client.resources.model.cuboid.CuboidModelElement;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CuboidModelElement.Deserializer.class)
public abstract class BlockElementDeserializerMixin {

	@Shadow
	private static Vector3f getVector3f(JsonObject object, String key) {
		return null;
	}

	/**
	 * @author Obsidian
	 * @reason Remove the hardcoded [-16, 32] coordinate boundary so that block
	 *         models can use element positions outside vanilla's limits (e.g.
	 *         large furniture, multi-block display models, etc.).
	 *         Vanilla throws a JsonParseException when any x/y/z component of
	 *         "from" or "to" falls outside that range; we simply skip the check
	 *         and return the parsed vector directly.
	 */
	@Overwrite
	private static Vector3f getPosition(JsonObject object, String key) {
		return getVector3f(object, key);
	}
}
