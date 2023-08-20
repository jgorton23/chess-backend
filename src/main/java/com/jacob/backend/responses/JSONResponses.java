package com.jacob.backend.responses;

import java.util.List;

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

    public static <T extends JsonConvertible> JsonArray ListToJsonArray(List<T> list) {
        JsonArray array = new JsonArray();
        for (T obj : list) {
            array.add(obj.toJson());
        }
        return array;
    }

    public static JsonArray StringListToJsonArray(List<String> list) {
        JsonArray array = new JsonArray();
        for (String s : list) {
            array.add(s);
        }
        return array;
    }

}
