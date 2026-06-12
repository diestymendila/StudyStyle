package com.example.studystyle.models;

import com.google.gson.annotations.SerializedName;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class BookDetail {


    @SerializedName("description")
    private JsonElement description;

    @SerializedName("title")
    private String title;


    public String getDescription() {
        if (description == null) return "";
        try {
            if (description.isJsonPrimitive()) {
                return description.getAsString();
            } else if (description.isJsonObject()) {
                JsonObject obj = description.getAsJsonObject();
                if (obj.has("value")) {
                    return obj.get("value").getAsString();
                }
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    public String getTitle() {
        return title != null ? title : "";
    }
}