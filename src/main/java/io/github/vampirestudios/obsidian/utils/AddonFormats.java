package io.github.vampirestudios.obsidian.utils;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.hocon.HoconFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.toml.TomlFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.configPack.LegacyObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Reads an addon module file in whatever format it is written in.
 * <p>
 * The file extension decides the format, so a single addon can mix json, yaml, toml, hjson and hocon
 * files. Packs that leave their files extension-less fall back to the format declared in their
 * {@code addon.info.json}, and legacy packs fall back to json.
 * <p>
 * Everything is normalised into a Gson tree before binding, so every format goes through the same
 * {@link BaseGson#GSON} type adapters.
 */
public final class AddonFormats {

	public static final Map<String, ObsidianAddonInfo.Format> EXTENSIONS = Map.of(
			"json", ObsidianAddonInfo.Format.JSON,
			"yaml", ObsidianAddonInfo.Format.YAML,
			"yml", ObsidianAddonInfo.Format.YAML,
			"toml", ObsidianAddonInfo.Format.TOML,
			"hjson", ObsidianAddonInfo.Format.HJSON,
			"conf", ObsidianAddonInfo.Format.HOCON,
			"hocon", ObsidianAddonInfo.Format.HOCON
	);

	private AddonFormats() {
	}

	/**
	 * @return true when the file extension is one of the supported addon formats.
	 */
	public static boolean isSupported(File file) {
		return EXTENSIONS.containsKey(extension(file.getName()));
	}

	/**
	 * The file name without its format extension, used to derive identifiers.
	 */
	public static String baseName(File file) {
		return baseName(file.getName());
	}

	public static String baseName(String fileName) {
		String extension = extension(fileName);
		if (EXTENSIONS.containsKey(extension)) {
			return fileName.substring(0, fileName.length() - extension.length() - 1);
		}
		return fileName;
	}

	private static String extension(String fileName) {
		int dot = fileName.lastIndexOf('.');
		if (dot < 0) return "";
		return fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
	}

	/**
	 * The format of a single module file: its extension when it has a known one, otherwise the
	 * format the pack declared.
	 */
	public static ObsidianAddonInfo.Format formatOf(IAddonPack addon, File file) {
		ObsidianAddonInfo.Format byExtension = EXTENSIONS.get(extension(file.getName()));
		if (byExtension != null) return byExtension;
		return packFormat(addon);
	}

	public static ObsidianAddonInfo.Format packFormat(IAddonPack addon) {
		if (addon != null && addon.getConfigPackInfo() instanceof ObsidianAddonInfo addonInfo && addonInfo.format != null) {
			return addonInfo.format;
		}
		return ObsidianAddonInfo.Format.JSON;
	}

	/**
	 * @return true when the pack is a legacy (pre-schema) addon.
	 */
	public static boolean isLegacy(IAddonPack addon) {
		return addon != null && addon.getConfigPackInfo() instanceof LegacyObsidianAddonInfo;
	}

	/**
	 * Finds {@code baseName} inside {@code directory} in any supported format, preferring json.
	 *
	 * @return the file that exists, or the json one when none of them do.
	 */
	public static File resolve(File directory, String baseName) {
		File json = new File(directory, baseName + ".json");
		if (json.exists()) return json;
		for (String extension : EXTENSIONS.keySet()) {
			File candidate = new File(directory, baseName + "." + extension);
			if (candidate.exists()) return candidate;
		}
		return json;
	}

	public static JsonElement readTree(File file) throws IOException {
		return readTree((IAddonPack) null, file);
	}

	public static <T> T read(File file, Class<T> type) throws IOException {
		return read(null, file, type);
	}

	public static JsonElement readTree(IAddonPack addon, File file) throws IOException {
		return readTree(formatOf(addon, file), file);
	}

	public static JsonElement readTree(ObsidianAddonInfo.Format format, File file) throws IOException {
		return switch (format) {
			case JSON -> {
				try (Reader reader = reader(file)) {
					yield JsonParser.parseReader(reader);
				}
			}
			case HJSON -> {
				try (Reader reader = reader(file)) {
					yield JsonParser.parseString(org.hjson.JsonValue.readHjson(reader).toString(org.hjson.Stringify.PLAIN));
				}
			}
			case YAML -> jacksonTree(new ObjectMapper(new YAMLFactory()), file);
			case TOML -> jacksonTree(new ObjectMapper(new TomlFactory()), file);
			case HOCON -> hoconTree(file);
		};
	}

	/**
	 * The file's contents normalised to json text, for the modules that bind with Jackson instead of
	 * Gson.
	 */
	public static String readAsJsonString(IAddonPack addon, File file) throws IOException {
		return readTree(addon, file).toString();
	}

	public static JsonObject readObject(IAddonPack addon, File file) throws IOException {
		JsonElement parsed = readTree(addon, file);
		if (parsed == null || !parsed.isJsonObject()) {
			throw new JsonParseException("Expected " + file.getName() + " to contain an object");
		}
		return parsed.getAsJsonObject();
	}

	/**
	 * Reads a module file and binds it with {@link BaseGson#GSON}, whatever format it is written in.
	 */
	public static <T> T read(IAddonPack addon, File file, Class<T> type) throws IOException {
		if (JsonElement.class.isAssignableFrom(type)) {
			return type.cast(type == JsonObject.class ? readObject(addon, file) : readTree(addon, file));
		}
		return BaseGson.GSON.fromJson(readTree(addon, file), type);
	}

	public static <T> T read(IAddonPack addon, File file, Type type) throws IOException {
		return BaseGson.GSON.fromJson(readTree(addon, file), type);
	}

	private static Reader reader(File file) throws IOException {
		return Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);
	}

	private static JsonElement jacksonTree(ObjectMapper mapper, File file) throws IOException {
		mapper.findAndRegisterModules();
		return BaseGson.GSON.toJsonTree(mapper.readValue(file, Object.class));
	}

	private static JsonElement hoconTree(File file) {
		try (FileConfig config = FileConfig.of(file, HoconFormat.instance())) {
			config.load();
			return nightConfigValue(config);
		}
	}

	private static JsonElement nightConfigValue(Object value) {
		if (value instanceof UnmodifiableConfig config) {
			JsonObject object = new JsonObject();
			for (UnmodifiableConfig.Entry entry : config.entrySet()) {
				object.add(entry.getKey(), entry.isNull() ? JsonNull.INSTANCE : nightConfigValue(entry.getRawValue()));
			}
			return object;
		}
		if (value instanceof List<?> list) {
			JsonArray array = new JsonArray();
			for (Object entry : list) array.add(nightConfigValue(entry));
			return array;
		}
		return BaseGson.GSON.toJsonTree(value);
	}
}
