package com.puttel.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.VisibleForTesting;
import lombok.Getter;

import javax.inject.Inject;

public class PreferencesUtil {
    @VisibleForTesting
    public static final String SHARED_PREFERENCES_FILE = "shared_preferences_file";
    @VisibleForTesting
    public static final String SAVED_USERNAME = "saved_username";
    private static final String SAVED_PRIVATE_KEY = "saved_private_key";
    private static final String SAVED_PUBLIC_KEY = "saved_public_key";

    @Getter
    private final SharedPreferences preferences;

    @Inject
    public PreferencesUtil(){
        preferences=InApp.instance.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
    }
    public void saveUsername(String username) {
        preferences.edit().putString(SAVED_USERNAME, username).apply();
    }
    public String retrieveUsername() {
        return preferences.getString(SAVED_USERNAME, "");
    }
    public void clear() {
        preferences.edit().clear().apply();
    }

}
