package io.github.vampirestudios.obsidian.client.bbmodel;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector4d;

import java.util.Map;

public class BBModelElement {
	public String name;
	public boolean boxUV;
	public boolean rescale;
	public boolean locked;
	public int lightEmission;
	public String renderOrder;
	public boolean allowMirrorModelling;
	public Vec3 from;
	public Vec3 to;
	public int autoUV;
	public int color;
	public Vec3 origin;
	public Map<String, Face> faces;
	public String type;
	public String uuid;

	public static class Face {
		public Vector4d uv;
		public int texture;
	}
}
