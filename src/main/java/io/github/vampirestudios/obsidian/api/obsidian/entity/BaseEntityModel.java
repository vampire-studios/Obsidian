package io.github.vampirestudios.obsidian.api.obsidian.entity;

import it.unimi.dsi.fastutil.objects.ObjectList;

import java.util.List;

public class BaseEntityModel {

	public int textureWidth;
	public int textureHeight;
	public List<Cuboid> boxes;
	public ObjectList<Cuboid.TextureOffSet> offsets;
	public ObjectList<Cuboid.RotationPoint> Rpoints;

	public BaseEntityModel(int textureWidth, int textureHeight, List<Cuboid> boxes, ObjectList<Cuboid.TextureOffSet> textureoffSetlist, ObjectList<Cuboid.RotationPoint> Rpoint) {
		this.boxes = boxes;
		this.offsets = textureoffSetlist;
		this.Rpoints = Rpoint;
	}

	public static class Cuboid {
		public String cuboidName;
		public float offsetX;
		public float offsetY;
		public float offsetZ;
		public int width;
		public int height;
		public int depth;
		public float scaleFactor;

		public Cuboid(String cuboidName, float offsetX, float offsetY, float offsetZ, int width, int height, int depth, float scaleFactor) {
			this.cuboidName = cuboidName;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
			this.offsetZ = offsetZ;
			this.height = height;
			this.width = width;
			this.depth = depth;
			this.scaleFactor = scaleFactor;
		}

		public static class TextureOffSet {
			public float offsetX;
			public float offsetY;

			public TextureOffSet(float offsetX, float offsetY) {
				this.offsetX = offsetX;
				this.offsetY = offsetY;
			}
		}

		public static class RotationPoint {
			public float RotationX;
			public float RotationY;
			public float RotationZ;

			public RotationPoint(float RotationX, float RotationY, float RotationZ) {
				this.RotationX = RotationX;
				this.RotationY = RotationY;
				this.RotationZ = RotationZ;
			}

		}


	}


}
