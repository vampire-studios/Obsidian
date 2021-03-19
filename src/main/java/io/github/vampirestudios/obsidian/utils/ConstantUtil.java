package io.github.vampirestudios.obsidian.utils;

import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ConstantUtil {
	private static Map<String, Integer> cachedKeys;

	public static int getKey(String name) {
		if (cachedKeys == null) init();
		if (!cachedKeys.containsKey(name)) {
			return GLFW.GLFW_KEY_CAPS_LOCK;
		}
		return cachedKeys.get(name);
	}

	private static void init() {
		cachedKeys = new HashMap<>();
		cachedKeys.putAll(getConstants(GLFW.class.getDeclaredFields(), Integer.class, "GLFW_KEY_", true));
	}

	public static <T> Map<String, T> getConstants(Field[] fields, Class<T> type, String prefix, boolean removePrefix) {
		Map<String, T> cache = new HashMap<>();
		for (Field field : fields) {
			String fieldName = field.getName();
			if (!prefix.equals("") && !fieldName.startsWith(prefix)) continue;
			if (!fieldName.toUpperCase().equals(fieldName)) continue;
			T material = null;
			try {
				material = type.cast(field.get(null));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			String name = (removePrefix) ? fieldName.replace(prefix, "") : fieldName;
			cache.put(name.toLowerCase().replaceAll("_", " "), material);
		}
		return cache;
	}

	public static <T> Map<String, T> getConstants(Field[] fields, Class<T> type) {
		return getConstants(fields, type, "", false);
	}
}