package au.com.ahbeard.sleepsense.services;

import android.content.Context;
import android.content.SharedPreferences;

import static au.com.ahbeard.sleepsense.utils.GlobalVars.SHARED_PREFERENCE;

/**
 * Created by vimal on 11/22/2016.
 */

public class SharedPreferencesStore {

    public static void PutItem(String key, String value, Context context){
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String GetItem(String key, Context context){
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return settings.getString(key, null);
    }
}
