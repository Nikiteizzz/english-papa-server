package Interfaces;

import org.json.simple.JSONObject;

public interface Model {
    JSONObject toJsonObject();
    void fromJsonString(String jsonString);
}
