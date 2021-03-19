//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.github.vampirestudios.obsidian.utils;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.configPack.ConfigHelper;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonModMetadata;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.discovery.ModCandidate;
import net.fabricmc.loader.util.UrlUtil;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class Utils {
	public Utils() {
	}

	public static String toTitleCase(String lowerCase) {
		return "" + Character.toUpperCase(lowerCase.charAt(0)) + lowerCase.substring(1);
	}

	public static String nameToId(String name, Map<String, String> specialCharMap) {
		Entry specialChar;
		for (Iterator var2 = specialCharMap.entrySet().iterator(); var2.hasNext(); name = name.replace((CharSequence) specialChar.getKey(), (CharSequence) specialChar.getValue())) {
			specialChar = (Entry) var2.next();
		}

		return name.toLowerCase(Locale.ENGLISH);
	}

	public static void registerAddon(Reader reader, File file) {
		ObsidianAddonInfo obsidianAddonInfo = Obsidian.GSON.fromJson(reader, ObsidianAddonInfo.class);
		ObsidianAddon obsidianAddon = new ObsidianAddon(obsidianAddonInfo, file);
		if (!ConfigHelper.OBSIDIAN_ADDONS.contains(obsidianAddon) && obsidianAddon.getConfigPackInfo().addonVersion == ConfigHelper.PACK_VERSION) {
			ConfigHelper.OBSIDIAN_ADDONS.add(obsidianAddon);
			Obsidian.LOGGER.info(String.format("[Obsidian] Registering obsidian addon: %s", obsidianAddon.getConfigPackInfo().displayName));
		} else {
			Obsidian.LOGGER.info(String.format("[Obsidian] Found incompatible obsidian addon: %s with a version of %d", obsidianAddon.getConfigPackInfo().displayName, obsidianAddon.getConfigPackInfo().addonVersion));
		}
	}

	public static void registerAddon(Reader reader) {
		ObsidianAddonInfo obsidianAddonInfo = Obsidian.GSON.fromJson(reader, ObsidianAddonInfo.class);
		ObsidianAddon obsidianAddon = new ObsidianAddon(obsidianAddonInfo);
		if (!ConfigHelper.OBSIDIAN_ADDONS.contains(obsidianAddon) && obsidianAddon.getConfigPackInfo().addonVersion == ConfigHelper.PACK_VERSION) {
			ConfigHelper.OBSIDIAN_ADDONS.add(obsidianAddon);
			Obsidian.LOGGER.info(String.format("[Obsidian] Registering obsidian addon: %s", obsidianAddon.getConfigPackInfo().displayName));
		} else {
			Obsidian.LOGGER.info(String.format("[Obsidian] Found incompatible obsidian addon: %s with a version of %d", obsidianAddon.getConfigPackInfo().displayName, obsidianAddon.getConfigPackInfo().addonVersion));
		}
	}

	public static void registerMod(ObsidianAddon addon, File file, String namespace) {
		try {
			FabricLoader loader = FabricLoader.getInstance();
			Method addMod = net.fabricmc.loader.FabricLoader.class.getDeclaredMethod("addMod", ModCandidate.class);
			addMod.setAccessible(true);
			ModCandidate candidate = new ModCandidate(new ObsidianAddonModMetadata(addon), UrlUtil.asUrl(file), 0, false);
			addMod.invoke(loader, candidate);
			Optional<ModContainer> optional = loader.getModContainer(namespace);
			if (optional.isPresent()) {
				Method setupRootPath = net.fabricmc.loader.ModContainer.class.getDeclaredMethod("setupRootPath");
				setupRootPath.setAccessible(true);
				setupRootPath.invoke(optional.get());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Identifier appendToPath(Identifier identifier, String suffix) {
		return new Identifier(identifier.getNamespace(), identifier.getPath() + suffix);
	}

	public static Identifier prependToPath(Identifier identifier, String prefix) {
		return new Identifier(identifier.getNamespace(), prefix + identifier.getPath());
	}

	public static Identifier appendAndPrependToPath(Identifier identifier, String prefix, String suffix) {
		return new Identifier(identifier.getNamespace(), prefix + identifier.getPath() + suffix);
	}

	public static double dist(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x2 - x1, 2.0D) + Math.pow(y2 - y1, 2.0D));
	}

	public static boolean checkBitFlag(int toCheck, int flag) {
		return (toCheck & flag) == flag;
	}
}
