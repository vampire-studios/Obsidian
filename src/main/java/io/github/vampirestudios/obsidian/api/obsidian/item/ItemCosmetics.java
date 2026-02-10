package io.github.vampirestudios.obsidian.api.obsidian.item;

import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ItemCosmetics {
	public List<CosmeticLayer> layers = new ArrayList<>();
	public boolean hide_base_equipment;
	public boolean allow_multiple_layers = true;
	public List<String> tags = new ArrayList<>();

	public static class CosmeticLayer {
		public String id;
		public String type;
		public String slot;
		public String anchor;
		public Identifier model;
		public Identifier texture;
		public Transform transform;
		public ParticleTrail particle_trail;
		public Aura aura;
		public Glow glow;
		public List<Animation> animations;
	}

	public static class Transform {
		public Vector3 translation = new Vector3();
		public Vector3 rotation = new Vector3();
		public Vector3 scale = new Vector3(1f, 1f, 1f);
	}

	public static class Vector3 {
		public float x;
		public float y;
		public float z;

		public Vector3() {
		}

		public Vector3(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	public static class ParticleTrail {
		public Identifier particle;
		public int count = 1;
		public float speed = 0.0f;
		public int interval = 2;
		public Vector3 offset = new Vector3();
		public Vector3 spread = new Vector3(0.1f, 0.1f, 0.1f);
		public boolean follow_player = true;
	}

	public static class Aura {
		public Identifier particle;
		public float radius = 0.6f;
		public int count = 6;
		public Vector3 offset = new Vector3();
		public boolean follow_player = true;
	}

	public static class Glow {
		public String color = "#ffffff";
		public float intensity = 1.0f;
		public float pulse_speed = 0.0f;
	}

	public static class Animation {
		public String type;
		public Identifier file;
		public float speed = 1.0f;
		public boolean loop = true;
		public String axis;
	}
}