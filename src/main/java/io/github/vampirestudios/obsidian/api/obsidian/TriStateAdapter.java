package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.fabric.api.util.TriState;

import java.io.IOException;
import java.util.Locale;

/**
 * Reads a three-state flag from any of the shapes a pack writes one in: {@code true}/{@code false},
 * the strings {@code "true"}, {@code "false"} or {@code "default"}, or nothing at all.
 */
public class TriStateAdapter extends TypeAdapter<TriState> {

	@Override
	public TriState read(JsonReader reader) throws IOException {
		// Which token is next, rather than trying to read one and catching the failure: reading the wrong
		// kind throws IllegalStateException, which is not an IOException and so was never caught here.
		JsonToken token = reader.peek();
		switch (token) {
			case BOOLEAN -> {
				return TriState.of(reader.nextBoolean());
			}
			case NULL -> {
				reader.nextNull();
				return TriState.DEFAULT;
			}
			case STRING, NUMBER -> {
				String value = reader.nextString();
				return parse(value);
			}
			default -> throw new IOException("Expected true, false or a state name, but found " + token);
		}
	}

	private static TriState parse(String value) throws IOException {
		// The spellings packs actually write, alongside the state names themselves.
		return switch (value.trim().toLowerCase(Locale.ROOT)) {
			case "true", "yes", "on", "1" -> TriState.TRUE;
			case "false", "no", "off", "0" -> TriState.FALSE;
			case "default", "none", "unset", "" -> TriState.DEFAULT;
			default -> throw new IOException("\"" + value + "\" is not one of true, false or default");
		};
	}

	@Override
	public void write(JsonWriter writer, TriState state) throws IOException {
		writer.value(state.name());
	}
}
