package com.luismisanve.langtosql.ui.run;

import androidx.lifecycle.ViewModel;
import org.json.JSONArray;

public class RunViewModel extends ViewModel {
    // Attributes
    private JSONArray cachedJson;
    private String map;

    // Getters
    public JSONArray getJson() {
        return cachedJson;
    }
    public String getMap() {
        return map;
    }
    // Setters
    public void setJson(JSONArray cachedJson) {
        this.cachedJson = cachedJson;
    }
    public void setMap(String map) {
        this.map = map;
    }
}