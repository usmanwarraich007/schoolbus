package com.schoolbus.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static PreferenceManager instance;
    private final SharedPreferences sharedPreferences;
    
    public static synchronized PreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceManager(context.getApplicationContext());
        }
        return instance;
    }
    
    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }
    
    // User ID
    public void setUserId(String userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_USER_ID, userId);
        editor.apply();
    }
    
    public String getUserId() {
        return sharedPreferences.getString(Constants.KEY_USER_ID, null);
    }
    
    // User Type
    public void setUserType(String userType) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_USER_TYPE, userType);
        editor.apply();
    }
    
    public String getUserType() {
        return sharedPreferences.getString(Constants.KEY_USER_TYPE, null);
    }
    
    // Notification Enabled
    public void setNotificationEnabled(boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.KEY_NOTIFICATION_ENABLED, enabled);
        editor.apply();
    }
    
    public boolean isNotificationEnabled() {
        return sharedPreferences.getBoolean(Constants.KEY_NOTIFICATION_ENABLED, true);
    }
    
    // Location Tracking Enabled
    public void setLocationTrackingEnabled(boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.KEY_LOCATION_TRACKING_ENABLED, enabled);
        editor.apply();
    }
    
    public boolean isLocationTrackingEnabled() {
        return sharedPreferences.getBoolean(Constants.KEY_LOCATION_TRACKING_ENABLED, true);
    }
    
    // Last Sync
    public void setLastSync(long timestamp) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(Constants.KEY_LAST_SYNC, timestamp);
        editor.apply();
    }
    
    public long getLastSync() {
        return sharedPreferences.getLong(Constants.KEY_LAST_SYNC, 0);
    }
    
    // Selected Language
    public void setSelectedLanguage(String language) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_SELECTED_LANGUAGE, language);
        editor.apply();
    }
    
    public String getSelectedLanguage() {
        return sharedPreferences.getString(Constants.KEY_SELECTED_LANGUAGE, "en");
    }
    
    // Dark Mode
    public void setDarkMode(boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.KEY_DARK_MODE, enabled);
        editor.apply();
    }
    
    public boolean isDarkMode() {
        return sharedPreferences.getBoolean(Constants.KEY_DARK_MODE, false);
    }
    
    // Clear all preferences
    public void clearAll() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
} 