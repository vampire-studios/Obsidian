package io.github.vampirestudios.obsidian.api.crucible.targets;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Locale;

public abstract class LocationTarget extends SkillTarget<Vec3> {
	protected double xoffset;
	protected double yoffset;
	protected double zoffset;
	protected double forwardOffset;
	protected double sideOffset;
	protected double rotateX;
	protected double rotateY;
	protected double rotateZ;
	protected double length;
	protected double coordinateX;
	protected double coordinateY;
	protected double coordinateZ;
	protected float coordinateYaw;
	protected float coordinatePitch;
	protected String blockTypes;
	protected String blockIgnores;
	protected boolean statics = false;
	protected boolean offsets = false;
	protected boolean advOffset = false;
	protected boolean rotated = false;
	protected boolean centered = false;
	protected boolean faulty;
	private int limit = 0;
	private FilterSorter sorter;

	public LocationTarget(List<String> aliases) {
		super(aliases);
	}

	/*public Vec3 mutate(Vec3 location) {
		if (this.offsets) {
			location = location.clone().add(this.xoffset, this.yoffset, this.zoffset);
		}

		if (this.advOffset) {
			location = MythicUtil.move(this.faulty, location, this.forwardOffset, 0.0, this.sideOffset);
		}

		double len = this.length;
		if (len != 0.0) {
			location = location.add(location.getDirection().clone().multiply(len));
		}

		double bX;
		double cY;
		if (this.statics) {
			bX = this.coordinateX;
			if (bX != 0.0) {
				location.xRot((float) bX);
			}

			cY = this.coordinateY;
			if (cY != 0.0) {
				location.yRot((float) cY);
			}

			double cZ = this.coordinateZ;
			if (cZ != 0.0) {
				location.zRot((float) cZ);
			}

			*//*float cYaw = this.coordinateYaw;
			if (cYaw != 0.0F) {
				location.(cYaw);
			}

			float cPitch = this.coordinatePitch;
			if (cPitch != 0.0F) {
				location.setPitch(cPitch);
			}*//*
		}

		if (this.centered) {
			bX = (double)location.x();
			cY = (double)location.z();
//			locatio.c = bX + 0.5;
//			location.setZ(cY + 0.5);
		}

		return location;
	}*/

	public static boolean blockMatches(Block mat, List<String> list) {
		for (String wantType : list) {
			wantType = wantType.toUpperCase();
			switch (wantType.charAt(0)) {
				case '#':
					if (mat.toString().contains(wantType.substring(1))) {
						return true;
					}
					break;
				case '*':
					ResourceKey<Block> tagKey = ResourceKey.create(Registries.BLOCK, Identifier.parse(wantType.toLowerCase().substring(1)));
					return mat.defaultBlockState().is(tagKey);
				case '@':
					if (mat.toString().startsWith(wantType.substring(1))) {
						return true;
					}
					break;
				default:
					if (mat == BuiltInRegistries.BLOCK.getValue(Identifier.parse(wantType.toLowerCase(Locale.ROOT)))) {
						return true;
					}
			}
		}

		return false;
	}

	private static enum FilterSorter {
		NONE,
		RANDOM,
		NEAREST,
		FURTHEST;

		private FilterSorter() {
		}
	}
}