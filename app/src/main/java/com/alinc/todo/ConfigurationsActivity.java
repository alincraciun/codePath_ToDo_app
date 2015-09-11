package com.alinc.todo;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by alinc on 9/7/15.
 */
public class ConfigurationsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new ConfigurationsFragment()).commit();
        PreferenceManager.setDefaultValues(ConfigurationsActivity.this, R.xml.settings_screen, false);
    }

    @Override
    protected boolean isValidFragment(String fragmentName)
    {
        return ConfigurationsFragment.class.getName().equals(fragmentName);
    }

}
