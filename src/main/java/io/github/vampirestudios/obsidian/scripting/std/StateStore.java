package io.github.vampirestudios.obsidian.scripting.std;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class StateStore {
	private final Path dataDir;
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private Map<String, Object> global = new HashMap<>();
	private Map<String, Map<String, Object>> players = new HashMap<>();
	private boolean dirty = false;

	public StateStore(Path packRoot) throws IOException {
		this.dataDir = packRoot.resolve("data");
		Files.createDirectories(dataDir);
		load();
	}

	@SuppressWarnings("unchecked")
	private void load() throws IOException {
		Path g = dataDir.resolve("global.json");
		Path p = dataDir.resolve("players.json");
		if (Files.exists(g)) global = gson.fromJson(Files.readString(g), Map.class);
		if (Files.exists(p)) players = gson.fromJson(Files.readString(p), Map.class);
		if (global == null) global = new HashMap<>();
		if (players == null) players = new HashMap<>();
	}

	public void markDirty() {
		dirty = true;
	}

	public void flush() {
		if (!dirty) return;
		try {
			Files.writeString(dataDir.resolve("global.json"), gson.toJson(global));
			Files.writeString(dataDir.resolve("players.json"), gson.toJson(players));
			dirty = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Map<String, Object> global() {
		return global;
	}

	public Map<String, Object> player(String uuid) {
		return players.computeIfAbsent(uuid, k -> new HashMap<>());
	}
}
