package ru.practicum.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, Duration value) throws IOException {
        if (value == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(value.toString());
        }
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        String nullOrNot = jsonReader.nextString();
        if (nullOrNot.equals("null")) {
            return null;
        }
        return Duration.parse(nullOrNot);
    }
}