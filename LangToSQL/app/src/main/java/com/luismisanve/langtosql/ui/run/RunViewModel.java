package com.luismisanve.langtosql.ui.run;

import androidx.lifecycle.ViewModel;
import org.json.JSONArray;

public class RunViewModel extends ViewModel {

    private JSONArray cachedJson;

    public void setJson(JSONArray cachedJson) {
        this.cachedJson = cachedJson;
    }

    public JSONArray getJson() {
        return cachedJson;
    }
}