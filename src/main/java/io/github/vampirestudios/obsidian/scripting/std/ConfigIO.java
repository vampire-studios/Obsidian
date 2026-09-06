// ConfigIO.java
package io.github.vampirestudios.obsidian.scripting.std;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigIO {
	private static final long MAX_BYTES = 256 * 1024;
	private final Path cfgDir;
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private static final class Entry {
		final Path path;
		volatile long lastModified;
		volatile Map<String, Object> data;

		Entry(Path p, long lm, Map<String, Object> d) {
			path = p;
			lastModified = lm;
			data = d;
		}
	}

	private final Map<String, Entry> cache = new ConcurrentHashMap<>();

	public ConfigIO(Path packRoot) throws IOException {
		this.cfgDir = packRoot.resolve("config").normalize();
		Files.createDirectories(cfgDir);
	}

	/** Read (from cache or disk). */
	@SuppressWarnings("unchecked")
	public Map<String, Object> read(String file) throws IOException {
		Path p = safePath(file);
		long lm = Files.exists(p) ? Files.getLastModifiedTime(p).toMillis() : 0L;
		Entry e = cache.get(file);
		if (e == null || lm != e.lastModified) {
			Map<String, Object> obj = Files.exists(p) ? readJson(p) : new HashMap<>();
			e = new Entry(p, lm, obj);
			cache.put(file, e);
		}
		return e.data;
	}

	/** Write to disk (optional feature). */
	public void write(String file, Map<String, Object> obj) throws IOException {
		Path p = safePath(file);
		String json = gson.toJson(obj);
		byte[] bytes = json.getBytes(java.nio.charset.StandardCharsets.UTF_8);
		if (bytes.length > MAX_BYTES) throw new IOException("config too large (>256KB)");
		Files.createDirectories(p.getParent());
		Files.write(p, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		cache.put(file, new Entry(p, Files.getLastModifiedTime(p).toMillis(), obj));
	}

	/** Poll for changes and refresh cache (call every ~20 ticks). */
	public void pollReload() {
		for (var it : cache.entrySet()) {
			String name = it.getKey();
			Entry e = it.getValue();
			try {
				if (!Files.exists(e.path)) continue;
				long lm = Files.getLastModifiedTime(e.path).toMillis();
				if (lm != e.lastModified) {
					e.data = readJson(e.path);
					e.lastModified = lm;
					// (optional) notify listeners here
				}
			} catch (IOException ignored) {
			}
		}
	}

	// -------- helpers --------

	private Path safePath(String file) throws IOException {
		if (file == null || file.isBlank() || file.contains("\0")) throw new IOException("bad filename");
		Path p = cfgDir.resolve(file).normalize();
		if (!p.startsWith(cfgDir)) throw new IOException("path escapes config dir");
		if (!file.endsWith(".json")) throw new IOException("only .json allowed");
		return p;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> readJson(Path p) throws IOException {
		long size = Files.size(p);
		if (size > MAX_BYTES) throw new IOException("config too large (>256KB)");
		String s = Files.readString(p);
		Map<String, Object> m = new Gson().fromJson(s, Map.class);
		return (m == null) ? new HashMap<>() : m;
	}
}
