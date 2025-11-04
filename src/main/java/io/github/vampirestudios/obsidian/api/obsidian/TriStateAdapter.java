package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.fabric.api.util.TriState;

import java.io.IOException;
import java.util.Locale;

public class TriStateAdapter extends TypeAdapter<TriState> {
    @Override
    public TriState read(JsonReader reader) throws IOException {
        try {
            return TriState.of(reader.nextBoolean());
        } catch(IOException e) { }
        
        String s = reader.nextString();
        try {
            return TriState.valueOf(s.toUpperCase(Locale.ROOT));
        } catch(IllegalArgumentException e) {
            throw new IOException(e);
        }
    }
    @Override
    public void write(JsonWriter writer, TriState state) throws IOException {
        writer.value(state.name());
    }
}