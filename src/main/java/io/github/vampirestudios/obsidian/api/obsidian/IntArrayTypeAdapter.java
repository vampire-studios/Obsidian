package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.vampirestudios.obsidian.utils.IntArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class IntArrayTypeAdapter extends TypeAdapter<IntArray> {

    @Override
    public void write(JsonWriter writer, IntArray intArray) throws IOException {
        if (intArray == null) {
            writer.nullValue();
            return;
        }
        // Serialize the internal array of IntArray
        writer.beginArray();
        for (int value : intArray.getArray()) {
            writer.value(value);
        }
        writer.endArray();
    }

    @Override
    public IntArray read(JsonReader reader) throws IOException {
        if (reader.peek() == com.google.gson.stream.JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        reader.beginArray();
        int firstValue = 0, secondValue = 0;
        boolean isTwoElementArray = false;
        List<Integer> values = new ArrayList<>();

        while (reader.hasNext()) {
            if (values.isEmpty()) {
                firstValue = reader.nextInt();
                values.add(firstValue);
            } else if (values.size() == 1) {
                secondValue = reader.nextInt();
                values.add(secondValue);
                isTwoElementArray = true;  // Flag when the array has exactly two elements
            } else {
                values.add(reader.nextInt());
                isTwoElementArray = false; // Not a two-element array anymore
            }
        }
        reader.endArray();

        if (isTwoElementArray) {
            // If it's a two-element array, treat it as a range
            return new IntArray(IntStream.rangeClosed(firstValue, secondValue).toArray());
        } else {
            // Otherwise, convert the list to an array normally
            return new IntArray(values.stream().mapToInt(i -> i).toArray());
        }
    }
}