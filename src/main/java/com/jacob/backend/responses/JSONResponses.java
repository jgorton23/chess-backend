package com.jacob.backend.responses;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class JSONResponses {
    private static JsonBuilderFactory builderFactory = Json.createBuilderFactory(null);

    public static JsonObject success() {
        return builderFactory.createObjectBuilder().add("msg", "success").build();
    }

    public static JsonObject error(String msg) {
        return builderFactory.createObjectBuilder().add("msg", String.format("Error: %s", msg)).build();
    }

    public static JsonObjectBuilder objectBuilder() {
        return builderFactory.createObjectBuilder();
    }
}
