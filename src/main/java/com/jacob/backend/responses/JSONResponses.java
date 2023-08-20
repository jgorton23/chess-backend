package com.jacob.backend.responses;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JSONResponses {

    public static String success() {
        JsonObject success = new JsonObject();
        success.addProperty("msg", "success");
        return success.toString();
    }

    public static String success(String message) {
        JsonObject success = new JsonObject();
        success.addProperty("msg", message);
        return success.toString();
    }

    public static String error(String msg) {
        JsonObject error = new JsonObject();
        error.addProperty("msg", "Error: %s".formatted(msg));
        return error.toString();
    }

    public static String unauthorized() {
        return error("UNAUTHORIZED").toString();
    }

    public static String toJson(Object src) {
        return new Gson().toJson(src);
    }

}
