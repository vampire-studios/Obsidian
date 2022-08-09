package io.github.vampirestudios.obsidian.utils;

import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;

import java.util.Random;

public class MathHelper {
	private static final Vec3i[] RANDOM_OFFSETS = new Vec3i[3 * 3 * 3 - 1];
	private static final float RAD_TO_DEG = 57.295779513082320876798154814105F;
	public static final float PHI = (float) (Math.PI * (3 - Math.sqrt(5)));
	public static final float PI2 = (float) (Math.PI * 2);
	public static final Random RANDOM = new Random();

	public static int randRange(int min, int max, Random random) {
		return min + random.nextInt(max - min + 1);
	}
	
	public static double randRange(double min, double max, Random random) {
		return min + random.nextDouble() * (max - min);
	}

	public static float randRange(float min, float max, Random random) {
		return min + random.nextFloat() * (max - min);
	}

	public static byte setBit(byte source, int pos, boolean value) {
		return value ? setBitTrue(source, pos) : setBitFalse(source, pos);
	}

	public static byte setBitTrue(byte source, int pos) {
		source |= 1 << pos;
		return source;
	}

	public static byte setBitFalse(byte source, int pos) {
		source &= ~(1 << pos);
		return source;
	}

	public static boolean getBit(byte source, int pos) {
		return ((source >> pos) & 1) == 1;
	}

	public static float wrap(float x, float side) {
		return x - floor(x / side) * side;
	}

	public static int floor(double x) {
		return x < 0 ? (int) (x - 1) : (int) x;
	}

	public static int min(int a, int b) {
		return Math.min(a, b);
	}
	
	public static int min(int a, int b, int c) {
		return min(a, min(b, c));
	}
	
	public static int max(int a, int b) {
		return Math.max(a, b);
	}
	
	public static float min(float a, float b) {
		return Math.min(a, b);
	}
	
	public static float max(float a, float b) {
		return Math.max(a, b);
	}
	
	public static float max(float a, float b, float c) {
		return max(a, max(b, c));
	}
	
	public static int max(int a, int b, int c) {
		return max(a, max(b, c));
	}
	
	public static boolean isEven(int num) {
		return (num & 1) == 0;
	}
	
	public static float lengthSqr(float x, float y, float z) {
		return x * x + y * y + z * z;
	}
	
	public static double lengthSqr(double x, double y, double z) {
		return x * x + y * y + z * z;
	}
	
	public static float length(float x, float y, float z) {
		return (float) Math.sqrt(lengthSqr(x, y, z));
	}
	
	public static double length(double x, double y, double z) {
		return Math.sqrt(lengthSqr(x, y, z));
	}
	
	public static float lengthSqr(float x, float y) {
		return x * x + y * y;
	}
	
	public static double lengthSqr(double x, double y) {
		return x * x + y * y;
	}
	
	public static float length(float x, float y) {
		return (float) Math.sqrt(lengthSqr(x, y));
	}
	
	public static double length(double x, double y) {
		return Math.sqrt(lengthSqr(x, y));
	}
	
	public static float dot(float x1, float y1, float z1, float x2, float y2, float z2) {
		return x1 * x2 + y1 * y2 + z1 * z2;
	}
	
	public static float dot(float x1, float y1, float x2, float y2) {
		return x1 * x2 + y1 * y2;
	}
	
	public static int getRandom(int x, int z) {
		int h = x * 374761393 + z * 668265263;
		h = (h ^ (h >> 13)) * 1274126177;
		return h ^ (h >> 16);
	}
	
	public static int getSeed(int seed, int x, int y) {
		int h = seed + x * 374761393 + y * 668265263;
		h = (h ^ (h >> 13)) * 1274126177;
		return h ^ (h >> 16);
	}

	public static int getSeed(int seed, int x, int y, int z) {
		int h = seed + x * 374761393 + y * 668265263 + z;
		h = (h ^ (h >> 13)) * 1274126177;
		return h ^ (h >> 16);
	}
	
	public static <T> void shuffle(T[] array, Random random) {
		for (int i = 0; i < array.length; i++) {
			int i2 = random.nextInt(array.length);
			T element = array[i];
			array[i] = array[i2];
			array[i2] = element;
		}
	}

	public static int sqr(int i) {
		return i * i;
	}
	
	public static float sqr(float f) {
		return f * f;
	}
	
	public static double sqr(double d) {
		return d * d;
	}
	
	public static final float radiansToDegrees(float value) {
		return value * RAD_TO_DEG;
	}
	
	public static final float degreesToRadians(float value) {
		return value / RAD_TO_DEG;
	}
	
	public static Vec3f cross(Vec3f vec1, Vec3f vec2)
	{
		float cx = vec1.getY() * vec2.getZ() - vec1.getZ() * vec2.getY();
		float cy = vec1.getZ() * vec2.getX() - vec1.getX() * vec2.getZ();
		float cz = vec1.getX() * vec2.getY() - vec1.getY() * vec2.getX();
		return new Vec3f(cx, cy, cz);
	}
	
	public static Vec3f normalize(Vec3f vec) {
		float length = lengthSqr(vec.getX(), vec.getY(), vec.getZ());
		if (length > 0) {
			length = (float) Math.sqrt(length);
			float x = vec.getX() / length;
			float y = vec.getY() / length;
			float z = vec.getZ() / length;
			vec.set(x, y, z);
		}
		return vec;
	}
	
	public static float angle(Vec3f vec1, Vec3f vec2) {
		float dot = vec1.getX() * vec2.getX() + vec1.getY() * vec2.getY() + vec1.getZ() * vec2.getZ();
		float length1 = lengthSqr(vec1.getX(), vec1.getY(), vec1.getZ());
		float length2 = lengthSqr(vec2.getX(), vec2.getY(), vec2.getZ());
		return (float) Math.acos(dot / Math.sqrt(length1 * length2));
	}
	
	public static Vec3f randomHorizontal(Random random) {
		float angleY = randRange(0, PI2, random);
		float vx = (float) Math.sin(angleY);
		float vz = (float) Math.cos(angleY);
		return new Vec3f(vx, 0, vz);
	}
	
	public static Vec3i[] getOffsets(Random random) {
		shuffle(RANDOM_OFFSETS, random);
		return RANDOM_OFFSETS;
	}

	public static float catmullrom(float f, float g, float h, float i, float j) {
		return 0.5F * (2.0F * h + (i - g) * f + (2.0F * g - 5.0F * h + 4.0F * i - j) * f * f + (3.0F * h - g - 3.0F * i + j) * f * f * f);
	}

	public static float triangleWave(float f, float g) {
		return (Math.abs(f % g - g * 0.5F) - g * 0.25F) / (g * 0.25F);
	}
	
	static {
		int index = 0;
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					if (x != 0 || y != 0 || z != 0) {
						RANDOM_OFFSETS[index++] = new Vec3i(x, y, z);
					}
				}
			}
		}
	}

	public static float pow(float value, float power) {
		return (float) Math.pow(value, power);
	}
}