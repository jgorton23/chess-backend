package com.jacob.backend.data;

import jakarta.json.JsonObject;

public interface JsonConvertible {
    public JsonObject toJson();
}
