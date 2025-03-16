package com.schoolbus.app.utils;

public class Constants {
    // Firebase Database Paths
    public static final String USERS_PATH = "users";
    public static final String BUSES_PATH = "buses";
    public static final String ROUTES_PATH = "routes";
    public static final String STOPS_PATH = "stops";
    public static final String SCHOOLS_PATH = "schools";
    public static final String NOTIFICATIONS_PATH = "notifications";
    
    // User Types
    public static final String USER_TYPE_PARENT = "parent";
    public static final String USER_TYPE_DRIVER = "driver";
    public static final String USER_TYPE_ADMIN = "admin";
    
    // Bus Status
    public static final String BUS_STATUS_ACTIVE = "active";
    public static final String BUS_STATUS_INACTIVE = "inactive";
    public static final String BUS_STATUS_MAINTENANCE = "maintenance";
    
    // Notification Types
    public static final String NOTIFICATION_TYPE_DELAY = "delay";
    public static final String NOTIFICATION_TYPE_ROUTE_CHANGE = "route_change";
    public static final String NOTIFICATION_TYPE_EMERGENCY = "emergency";
    public static final String NOTIFICATION_TYPE_GENERAL = "general";
    
    // Intent Extras
    public static final String EXTRA_USER_ID = "extra_user_id";
    public static final String EXTRA_BUS_ID = "extra_bus_id";
    public static final String EXTRA_ROUTE_ID = "extra_route_id";
    public static final String EXTRA_NOTIFICATION_ID = "extra_notification_id";
    
    // Map Constants
    public static final float DEFAULT_ZOOM = 15f;
    public static final int LOCATION_UPDATE_INTERVAL = 10000; // 10 seconds
    public static final int FASTEST_LOCATION_INTERVAL = 5000; // 5 seconds
    
    // Permissions
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
    // Shared Preferences
    public static final String PREF_NAME = "school_bus_prefs";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_TYPE = "user_type";
    public static final String KEY_NOTIFICATION_ENABLED = "notification_enabled";
    public static final String KEY_LOCATION_TRACKING_ENABLED = "location_tracking_enabled";
    public static final String KEY_LAST_SYNC = "last_sync";
    public static final String KEY_SELECTED_LANGUAGE = "selected_language";
    public static final String KEY_DARK_MODE = "dark_mode";

    private Constants() {
        // Private constructor to prevent instantiation
    }
} 