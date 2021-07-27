package io.github.vampirestudios.obsidian.utils;

import io.github.vampirestudios.obsidian.Obsidian;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.ColorHelper;
import net.minecraft.util.math.MathHelper;

public class ColorUtil {
	private static final float[] FLOAT_BUFFER = new float[4];
	private static final int ALPHA = 255 << 24;

	public static int color(int r, int g, int b) {
		return ALPHA | (r << 16) | (g << 8) | b;
	}

	public static int toIntRgb(float[] rgb) {
		int r = (int) (rgb[0] * 255f);
		int g = (int) (rgb[1] * 255f);
		int b = (int) (rgb[2] * 255f);
		return (r << 16) | (g << 8) | b;
	}

	public static int color(String hex) {
		int r = Integer.parseInt(hex.substring(0, 2), 16);
		int g = Integer.parseInt(hex.substring(2, 4), 16);
		int b = Integer.parseInt(hex.substring(4, 6), 16);
		return color(r, g, b);
	}

	public static int[] toIntArray(int color) {
		return new int[] {
				(color >> 24) & 255,
				(color >> 16) & 255,
				(color >> 8) & 255,
				color & 255
		};
	}

	public static float[] toFloatArray(int color) {
		FLOAT_BUFFER[0] = ((color >> 16 & 255) / 255.0F);
		FLOAT_BUFFER[1] = ((color >> 8 & 255) / 255.0F);
		FLOAT_BUFFER[2] = ((color & 255) / 255.0F);
		FLOAT_BUFFER[3] = ((color >> 24 & 255) / 255.0F);

		return FLOAT_BUFFER;
	}

	public static float[] RGBtoHSB(int r, int g, int b, float[] hsbvals) {
		float hue, saturation, brightness;
		if (hsbvals == null) {
			hsbvals = FLOAT_BUFFER;
		}
		int cmax = Math.max(r, g);
		if (b > cmax) cmax = b;
		int cmin = Math.min(r, g);
		if (b < cmin) cmin = b;

		brightness = ((float) cmax) / 255.0F;
		if (cmax != 0)
			saturation = ((float) (cmax - cmin)) / ((float) cmax);
		else
			saturation = 0;
		if (saturation == 0)
			hue = 0;
		else {
			float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
			float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
			float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
			if (r == cmax)
				hue = bluec - greenc;
			else if (g == cmax)
				hue = 2.0F + redc - bluec;
			else
				hue = 4.0F + greenc - redc;
			hue = hue / 6.0F;
			if (hue < 0)
				hue = hue + 1.0F;
		}
		hsbvals[0] = hue;
		hsbvals[1] = saturation;
		hsbvals[2] = brightness;
		return hsbvals;
	}

	public static int HSBtoRGB(float hue, float saturation, float brightness) {
		int r = 0, g = 0, b = 0;
		if (saturation == 0) {
			r = g = b = (int) (brightness * 255.0F + 0.5F);
		}
		else {
			float h = (hue - (float) Math.floor(hue)) * 6.0F;
			float f = h - (float) java.lang.Math.floor(h);
			float p = brightness * (1.0F - saturation);
			float q = brightness * (1.0F - saturation * f);
			float t = brightness * (1.0F - (saturation * (1.0F - f)));
			switch ((int) h) {
				case 0 -> {
					r = (int) (brightness * 255.0F + 0.5F);
					g = (int) (t * 255.0F + 0.5F);
					b = (int) (p * 255.0F + 0.5F);
				}
				case 1 -> {
					r = (int) (q * 255.0F + 0.5F);
					g = (int) (brightness * 255.0F + 0.5F);
					b = (int) (p * 255.0F + 0.5F);
				}
				case 2 -> {
					r = (int) (p * 255.0F + 0.5F);
					g = (int) (brightness * 255.0F + 0.5F);
					b = (int) (t * 255.0F + 0.5F);
				}
				case 3 -> {
					r = (int) (p * 255.0F + 0.5F);
					g = (int) (q * 255.0F + 0.5F);
					b = (int) (brightness * 255.0F + 0.5F);
				}
				case 4 -> {
					r = (int) (t * 255.0F + 0.5F);
					g = (int) (p * 255.0F + 0.5F);
					b = (int) (brightness * 255.0F + 0.5F);
				}
				case 5 -> {
					r = (int) (brightness * 255.0F + 0.5F);
					g = (int) (p * 255.0F + 0.5F);
					b = (int) (q * 255.0F + 0.5F);
				}
			}
		}
		return 0xFF000000 | (r << 16) | (g << 8) | b;
	}

	public static int parseHex(String hexColor) {
		int len = hexColor.length();
		if (len < 6 || len > 8 || len % 2 > 0) {
			return -1;
		}

		int color, shift;
		if (len == 6) {
			color = 0xFF000000;
			shift = 16;
		}
		else {
			color = 0;
			shift = 24;
		}

		try {
			String[] splited = hexColor.split("(?<=\\G.{2})");
			for (String digit : splited) {
				color |= Integer.valueOf(digit, 16) << shift;
				shift -= 8;
			}
		}
		catch (NumberFormatException ex) {
			Obsidian.LOGGER.catching(ex);
			return -1;
		}

		return color;
	}

	public static int toABGR(int color) {
		int r = (color >> 16) & 255;
		int g = (color >> 8) & 255;
		int b = color & 255;
		return 0xFF000000 | b << 16 | g << 8 | r;
	}

	public static int ABGRtoARGB(int color) {
		int a = (color >> 24) & 255;
		int b = (color >> 16) & 255;
		int g = (color >> 8) & 255;
		int r = color & 255;
		return a << 24 | r << 16 | g << 8 | b;
	}

	public static int colorBrigtness(int color, float val) {
		RGBtoHSB((color >> 16) & 255, (color >> 8) & 255, color & 255, FLOAT_BUFFER);
		FLOAT_BUFFER[2] += val / 10.0F;
		FLOAT_BUFFER[2] = MathHelper.clamp(FLOAT_BUFFER[2], 0.0F, 1.0F);
		return HSBtoRGB(FLOAT_BUFFER[0], FLOAT_BUFFER[1], FLOAT_BUFFER[2]);
	}

	public static int applyTint(int color, int tint) {
		return colorBrigtness(ColorHelper.multiplyColor(color, tint), 1.5F);
	}

}