package com.jacob.backend.responses;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.jacob.backend.data.JsonConvertible;

public class JSONResponses {

    public static JsonObject success() {
        JsonObject success = new JsonObject();
        success.addProperty("msg", "success");
        return success;
    }

    public static JsonObject error(String msg) {
        JsonObject error = new JsonObject();
        error.addProperty("msg", "Error: %s".formatted(msg));
        return error;
    }

    public static String unauthorized() {
        return error("UNAUTHORIZED").toString();
    }

    public static <T extends JsonConvertible> JsonArrayBuilder ListToJsonArray(List<T> list) {
        JsonArray array = new JsonArray();
        for (T obj : list) {
            array.add(obj.toJson());
        }
        return array;
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
