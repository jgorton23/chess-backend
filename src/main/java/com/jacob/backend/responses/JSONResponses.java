package com.jacob.backend.responses;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.jacob.backend.data.JsonConvertible;

public class JSONResponses {
    private static JsonBuilderFactory builderFactory = Json.createBuilderFactory(null);

    public static JsonObject success() {
        return builderFactory.createObjectBuilder().add("msg", "success").build();
    }

    public static JsonObject error(String msg) {
        return builderFactory.createObjectBuilder().add("msg", String.format("Error: %s", msg)).build();
    }

    public static <T extends JsonConvertible> JsonArrayBuilder ListToJsonArray(List<T> list) {
        JsonArrayBuilder arrayBuilder = builderFactory.createArrayBuilder();
        for (T obj : list) {
            arrayBuilder.add(obj.toJson());
        }
        return arrayBuilder;
    }

    public static JsonArrayBuilder arrayBuilder() {
        return builderFactory.createArrayBuilder();
    }

    public static JsonObjectBuilder objectBuilder() {
        return builderFactory.createObjectBuilder();
    }
}
