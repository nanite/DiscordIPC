package com.jagrosh.discordipc;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.function.Function;

/**
 * Utility class for JSON operations.
 */
public class Utils {
    public Utils() {
    }

    /**
     * Gets a String value from a JsonObject by key, or returns a default value if the key does not exist.
     *
     * @param obj The JsonObject to retrieve the value from.
     * @param key The key to look for in the JsonObject.
     * @param defaultValue The default value to return if the key does not exist.
     *
     * @return The String value associated with the key, or the default value.
     */
    public static String getJsonStringOrDefault(JsonObject obj, String key, String defaultValue) {
        return getJsonOrDefault(obj, key, defaultValue, JsonElement::getAsString);
    }

    /**
     * Wrapper method to get a value from a JsonObject by key, or return a default value if the key does not exist.
     *
     * @param <T> The type of the value to retrieve.
     * @param obj The JsonObject to retrieve the value from.
     * @param key The key to look for in the JsonObject.
     * @param defaultValue The default value to return if the key does not exist.
     * @param converter A function to convert the JsonElement to the desired type.
     *
     * @return The value associated with the key, or the default value.
     */
    public static <T> T getJsonOrDefault(JsonObject obj, String key, T defaultValue, Function<JsonElement, T> converter) {
        if (obj.has(key) && !obj.get(key).isJsonNull()) {
            return converter.apply(obj.get(key));
        } else {
            return defaultValue;
        }
    }
}
