package io.github.vampirestudios.obsidian.scripting.std;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Scoped variables for the script runtime.
 * - define(k,v): write only to the CURRENT scope (fluent).
 * - set(k,v):    assign into the nearest scope that already contains k; else define in current.
 * - get(k):      read from nearest scope that contains k.
 * - pushScope/popScope: block scoping.
 *
 * Map view is a merged snapshot with "nearest scope wins".
 */
public final class ObsVars implements Map<String, Object> {

	private final Deque<Map<String, Object>> scopes = new ArrayDeque<>();

	public ObsVars() {
		scopes.push(new HashMap<>()); // global / initial scope
	}

	public ObsVars(Map<String, Object> base) {
		this();
		scopes.peek().putAll(base);
	}

	/** Fluent helper: write only in the current scope and return this. */
	public ObsVars define(String key, Object value) {
		scopes.peek().put(key, value);
		return this;
	}

	/** Assign into the nearest scope that already defines the key; else define in current. */
	public void set(String key, Object value) {
		for (Map<String, Object> s : scopes) {
			if (s.containsKey(key)) { s.put(key, value); return; }
		}
		scopes.peek().put(key, value);
	}

	public void pushScope() { scopes.push(new HashMap<>()); }

	public void popScope() {
		if (scopes.size() <= 1) throw new IllegalStateException("Cannot pop the global scope");
		scopes.pop();
	}

	/* ---------- lookups across scopes ---------- */

	@Override
	public Object get(Object key) {
		for (Map<String, Object> s : scopes) {
			if (s.containsKey(key)) return s.get(key);
		}
		return null;
	}

	@Override
	public boolean containsKey(Object key) {
		for (Map<String, Object> s : scopes) if (s.containsKey(key)) return true;
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		// Check the merged, nearest-wins view
		for (Entry<String, Object> e : entrySet()) {
			if (Objects.equals(e.getValue(), value)) return true;
		}
		return false;
	}

	/* ---------- Map interface minimal plumbing ---------- */
	// We expose a merged snapshot; each call rebuilds it (simple & safe).

	@Override
	public int size() { return entrySet().size(); }

	@Override
	public boolean isEmpty() { return entrySet().isEmpty(); }

	@Override
	public Set<Entry<String, Object>> entrySet() {
		LinkedHashMap<String, Object> merged = new LinkedHashMap<>();
		// Iterate from top (nearest scope) to bottom; keep first occurrence
		for (Map<String, Object> s : scopes) {
			for (Entry<String, Object> e : s.entrySet()) merged.putIfAbsent(e.getKey(), e.getValue());
		}
		return merged.entrySet();
	}

	@Override
	public Collection<Object> values() {
		ArrayList<Object> vals = new ArrayList<>();
		for (Entry<String, Object> e : entrySet()) vals.add(e.getValue());
		return vals;
	}

	@Override
	public Set<String> keySet() {
		LinkedHashSet<String> keys = new LinkedHashSet<>();
		for (Entry<String, Object> e : entrySet()) keys.add(e.getKey());
		return keys;
	}

	/** Map.put: write to current scope, return previous merged value (nearest-wins semantics). */
	@Override
	public Object put(String key, Object value) {
		Object prev = get(key);
		scopes.peek().put(key, value);
		return prev;
	}

	@Override
	public void putAll(@NotNull Map<? extends String, ?> m) {
		scopes.peek().putAll(m);
	}

	@Override
	public Object remove(Object key) {
		for (Map<String, Object> s : scopes) {
			if (s.containsKey(key)) return s.remove(key);
		}
		return null;
	}

	@Override
	public void clear() {
		scopes.clear();
		scopes.push(new HashMap<>());
	}
}
