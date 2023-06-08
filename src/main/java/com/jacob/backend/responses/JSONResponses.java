package com.jacob.backend.responses;

import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

public class JSONResponses {
    private static JsonBuilderFactory builderFactory;

    public static JsonObject success() {
        return builderFactory.createObjectBuilder().add("msg", "success").build();
    }

    public static JsonObject error(String msg) {
        return builderFactory.createObjectBuilder().add("msg", String.format("Error: %v", msg)).build();
    }
}
