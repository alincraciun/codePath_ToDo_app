package com.alinc.todo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by alinc on 8/30/15.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_screen);

        final CheckBoxPreference cb_hide3DaysOldItems = (CheckBoxPreference) getPreferenceManager().findPreference(CommonConstants.HIDE_OLD_ITEMS);
        final CheckBoxPreference cb_deleteOldItems = (CheckBoxPreference) getPreferenceManager().findPreference(CommonConstants.DELETE_OLD_ITEMS);
        cb_deleteOldItems.setChecked(false);

        cb_hide3DaysOldItems.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences customSharedPreference = getActivity().getPreferences(Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = customSharedPreference.edit();
                editor.putBoolean("pref_hide_oldItems", cb_hide3DaysOldItems.isChecked());
                editor.commit();
                return true;
            }

        });

        cb_deleteOldItems.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                if(cb_deleteOldItems.isChecked()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Delete ALL past due items?");
                    builder.setTitle("Please confirm");
                    builder.setIcon(android.R.drawable.ic_delete);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            TodoItemDatabase db = TodoItemDatabase.getInstance(getActivity());
                            int deleted = db.deleteOldItems();
                            if (deleted > 0) {
                                Intent data = new Intent();
                                data.putExtra("refresh_required", true);
                                getActivity().setResult(MainActivity.RESULT_OK, data);
                                Toast.makeText(getActivity().getBaseContext(), "Past due item list purged", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            else if(deleted == 0) {
                                Toast.makeText(getActivity().getBaseContext(), "No past due items found", Toast.LENGTH_SHORT).show();
                                cb_deleteOldItems.setChecked(false);
                            }
                            else {
                                Log.d(getClass().getName(), "An error occurred. Failed to delete items!");
                            }
                        }
                    });
                    builder.setNegativeButton("No   ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cb_deleteOldItems.setChecked(false);
                        }
                    }).show();

                }
                return false;
            }
        });

        Preference deleteAllButton = findPreference("btDeleteAll");
        deleteAllButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Do you really want to delete all items?");
                builder.setTitle("Please confirm");
                builder.setIcon(android.R.drawable.ic_delete);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TodoItemDatabase db = TodoItemDatabase.getInstance(getActivity());
                        boolean deleted = db.deleteAllItems();
                        if (deleted) {
                            // TO DO: Show Notification and notifyDataSetChanged();
                            Intent data = new Intent();
                            data.putExtra("refresh_required", true);
                            getActivity().setResult(MainActivity.RESULT_OK, data);
                            Toast.makeText(getActivity().getBaseContext(), "Item list purged", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.d(getClass().getName(), "Failed to delete items!");
                    }
                });
                builder.setNegativeButton("No   ", null).show();
                return false;
            }
        });
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
        if (key.equals(CommonConstants.HIDE_OLD_ITEMS)) {
            getActivity().setResult(MainActivity.RESULT_OK, null);
        }
    }

}
