package ru.yandex.practicum.filmorate.utils;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.io.IOException;

@JsonComponent
public class RatingConverter {
    static RatingStorage storage;

    @Autowired
    public RatingConverter(RatingStorage storage) {
        RatingConverter.storage = storage;
    }

    public static class Deserialize extends JsonDeserializer<Rating> {
        @Override
        public Rating deserialize(com.fasterxml.jackson.core.JsonParser jp, DeserializationContext ctxt) throws IOException {
            JsonNode node = jp.getCodec().readTree(jp);
            long id = node.get("id").longValue();
            return storage.getRating(id);

        }
    }

}
