package com.alinc.todo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import java.util.Map;

/**
 * Created by alinc on 9/7/15.
 */
public class ConfigurationsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_screen);

        final com.alinc.todo.FontPreference fontPreference = (com.alinc.todo.FontPreference) getPreferenceManager().findPreference("lp_font");
        fontPreference.setSummary(getPreferenceManager().getSharedPreferences().getString("fontName", "Daniel"));

        Map<String,?> keys = getPreferenceManager().getSharedPreferences().getAll();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            setPrioritiesSummary(entry.getKey());
        }

        // Print all preference key and values
        /* Map<String,?> keys = getPreferenceManager().getSharedPreferences().getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("map values", entry.getKey() + ": " +
                    entry.getValue().toString());
        } */
    }

    @Override
    public void onResume(){
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        com.alinc.todo.FontPreference fontPreference = (com.alinc.todo.FontPreference) findPreference("lp_font");
        fontPreference.setSummary(sharedPreferences.getString("fontName", "Daniel"));

        setPrioritiesSummary(key);

        getActivity().setResult(MainActivity.RESULT_OK, null);
    }

    public void setPrioritiesSummary(String key) {
        Preference pref = getPreferenceManager().findPreference(key);

        if (pref instanceof EditTextPreference) {
            EditTextPreference editTextPreference = (EditTextPreference) pref;
            pref.setSummary(editTextPreference.getText());
        }
    }
}
