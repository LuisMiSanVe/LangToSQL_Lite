package com.luismisanve.langtosql.ui.config;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class ConfigViewModel extends ViewModel {
    // Attributes
    private final MutableLiveData<Boolean> savedOutside = new MutableLiveData<>();

    // Getters
    public LiveData<Boolean> getSavedOutside() {
        return savedOutside;
    }
    // Setters
    public void setSavedOutside(Boolean savedOutside) {
        this.savedOutside.setValue(savedOutside);
    }
}
