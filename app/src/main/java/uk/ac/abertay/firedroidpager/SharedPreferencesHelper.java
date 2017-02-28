package uk.ac.abertay.firedroidpager;

import android.content.Context;
import android.content.SharedPreferences;

class SharedPreferencesHelper {

    // Set Shared Preferences XML File Name
    private final static String FILE = "Config";

    // Set Shared Preference (String)
    public static void setSharedPreferenceString(Context context, String key, String value){
        SharedPreferences settings = context.getSharedPreferences(FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    // Set Shared Preference (Integer)
    public static void setSharedPreferenceInt(Context context, String key, int value){
        SharedPreferences settings = context.getSharedPreferences(FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    // Set Shared Preference (Booleans)
    public static void setSharedPreferenceBoolean(Context context, String key, boolean value){
        SharedPreferences settings = context.getSharedPreferences(FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    // Get Shared Preference (String)
    public static String getSharedPreferenceString(Context context, String key, String defValue){
        SharedPreferences settings = context.getSharedPreferences(FILE, 0);
        return settings.getString(key, defValue);
    }

    // Get Shared Preference (Integer)
    public static int getSharedPreferenceInt(Context context, String key, int defValue){
        SharedPreferences settings = context.getSharedPreferences(FILE, 0);
        return settings.getInt(key, defValue);
    }

    // Get Shared Preference (Boolean)
    public static boolean getSharedPreferenceBoolean(Context context, String key, boolean defValue){
        SharedPreferences settings = context.getSharedPreferences(FILE, 0);
        return settings.getBoolean(key, defValue);
    }

}
