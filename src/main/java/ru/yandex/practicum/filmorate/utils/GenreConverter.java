package ru.yandex.practicum.filmorate.utils;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.io.IOException;

@JsonComponent
public class GenreConverter {
    static GenreStorage storage;

    @Autowired
    public GenreConverter(GenreStorage storage) {
        GenreConverter.storage = storage;
    }

    public static class Deserialize extends JsonDeserializer<Genre> {
        @Override
        public Genre deserialize(com.fasterxml.jackson.core.JsonParser jp, DeserializationContext ctxt) throws IOException {
            JsonNode node = jp.getCodec().readTree(jp);
            long id = node.get("id").longValue();
            return storage.getGenre(id);

        }
    }

}
