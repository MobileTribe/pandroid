package com.leroymerlin.pandroid.persistence;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

/**
 * Created by adrienleroy on 05/01/15.
 */
public class SharedPrefConfigurationManager implements ConfigurationManager {

    private final SharedPreferences preferenceFile;
    private final Gson gsonParser;


    public SharedPrefConfigurationManager(Context context, String preferenceFileName) {
        preferenceFile = context.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE);
        gsonParser = new GsonBuilder().create();

    }

    public void remove(Config config) {
        preferenceFile.edit().remove(config.getName()).apply();
    }

    public <T> T getConfig(Config field) {
        if (field.getType() == Integer.class)
            return (T)(Object)preferenceFile.getInt(field.getName(), ((int) field.getDefaultValue()));
        else if (field.getType() == Long.class)
            return (T) (Object)preferenceFile.getLong(field.getName(), ((Long) field.getDefaultValue()));
        else if (field.getType() == Float.class)
            return (T) (Object)preferenceFile.getFloat(field.getName(), ((Float) field.getDefaultValue()));
        else if (field.getType() == String.class)
            return (T) (Object) preferenceFile.getString(field.getName(), ((String) field.getDefaultValue()));
        else if (field.getType() == Boolean.class)
            return (T) (Object) preferenceFile.getBoolean(field.getName(), ((Boolean) field.getDefaultValue()));
        else{
            if(preferenceFile.contains(field.getName())){
                return (T) parseString(preferenceFile.getString(field.getName(), null), field.getType());
            }else{
                return (T) field.getDefaultValue();
            }

        }
    }

    /**
     * Retrieve all values from the preferences.
     */
    public Map<String, ?> getAll() {
        return preferenceFile.getAll();

    }

    /**
     *  return size of sharedPref
     * @return
     */
    public int getSize() {
        return preferenceFile.getAll().size();
    }

    /**
     *
     * Clear SharedPreference
     *
     */
    @SuppressLint("CommitPrefEdits")
    public void clear() {
        preferenceFile.edit().clear();
    }

    public void setConfig(Config field, Object object) {
        SharedPreferences.Editor editor = preferenceFile.edit();
        if (field.getType() == Integer.class)
            editor.putInt(field.getName(), (Integer) object);
        else if (field.getType() == Long.class)
            editor.putLong(field.getName(), (Long) object);
        else if (field.getType() == String.class)
            editor.putString(field.getName(), (String) object);
        else if (field.getType() == Boolean.class)
            editor.putBoolean(field.getName(), (Boolean) object);
        else if (field.getType() == Float.class)
            editor.putFloat(field.getName(), (Float) object);
        else {
            editor.putString(field.getName(), objectToString(object));
        }
        editor.apply();
    }

    private String objectToString(Object object) {
        if (object == null) {
            return null;
        } else {
            return gsonParser.toJson(object);
        }
    }

    private  <T> T parseString(String value, Class<T> a) {
        if (value == null) {
            return null;
        } else {
            return gsonParser.fromJson(value, a);
        }
    }
}