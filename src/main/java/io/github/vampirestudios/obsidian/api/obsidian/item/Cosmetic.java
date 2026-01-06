package io.github.vampirestudios.obsidian.api.obsidian.item;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import org.joml.Vector3f;

public class Cosmetic extends Item {
	public EquipmentSlot slot;
	public Identifier model;
	public String autoplay;
	public Vector3f scale = new Vector3f(1);
	public Vector3f translation = new Vector3f();
}
