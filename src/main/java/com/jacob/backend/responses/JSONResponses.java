package com.jacob.backend.responses;

import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

import com.jacob.backend.data.JsonConvertible;

public class JSONResponses {
    private static JsonBuilderFactory builderFactory = Json.createBuilderFactory(null);

    public static JsonObject success() {
        return builderFactory.createObjectBuilder().add("msg", "success").build();
    }

    public static JsonObject error(String msg) {
        return builderFactory.createObjectBuilder().add("msg", "Error: %s".formatted(msg)).build();
    }

    public static String unauthorized() {
        return error("UNAUTHORIZED").toString();
    }

    public static <T extends JsonConvertible> JsonArrayBuilder ListToJsonArray(List<T> list) {
        JsonArrayBuilder arrayBuilder = builderFactory.createArrayBuilder();
        for (T obj : list) {
            arrayBuilder.add(obj.toJson());
        }
        return arrayBuilder;
    }

    public static JsonArrayBuilder StringListToJsonArray(List<String> list) {
        JsonArrayBuilder arrayBuilder = builderFactory.createArrayBuilder();
        for (String s : list) {
            arrayBuilder.add(s);
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
