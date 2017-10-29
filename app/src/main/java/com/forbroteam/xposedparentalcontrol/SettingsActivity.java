package com.forbroteam.xposedparentalcontrol;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * Created by andres on 29/10/17.
 */

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            addPreferencesFromResource(R.xml.settings);
        } catch (Exception e) {

        }

        final ListPreference listPreference = (ListPreference) findPreference("whitelisted_stuff");

        setListPreferenceData(listPreference);

        listPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                setListPreferenceData(listPreference);
                return false;
            }
        });
    }

    protected static void setListPreferenceData(ListPreference lp) {
        CharSequence[] entries = { "www.youtube.com", "www.kidsstufftoys.co.uk" };
        CharSequence[] entryValues = {"1" , "2"};
        lp.setEntries(entries);
        lp.setEntryValues(entryValues);
    }
}
