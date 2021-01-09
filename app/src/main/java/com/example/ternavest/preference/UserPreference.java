package com.example.ternavest.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class UserPreference {
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    private static final String PREFERENCE_NAME = "user_preference";
    private static final String USER_LEVEL = "user_level";

    @SuppressLint("CommitPrefEdits")
    public UserPreference(Context context){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setUserLevel(String userLevel){
        editor.putString(USER_LEVEL, userLevel);
        editor.apply();
    }

    public String getUserLevel(){
        return sharedPreferences.getString(USER_LEVEL, null);
    }
}
