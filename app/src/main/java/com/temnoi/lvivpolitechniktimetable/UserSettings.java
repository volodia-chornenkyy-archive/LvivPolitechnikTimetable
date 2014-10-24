package com.temnoi.lvivpolitechniktimetable;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * Created by Volodia on 24.10.2014.
 */
public class UserSettings extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);



        final ListPreference listPreference = (ListPreference) findPreference("language");

        // THIS IS REQUIRED IF YOU DON'T HAVE 'entries' and 'entryValues' in your XML
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
        CharSequence[] entries = { "English", "French" };
        CharSequence[] entryValues = {"1" , "2"};
        lp.setEntries(entries);
        lp.setDefaultValue("1");
        lp.setEntryValues(entryValues);
    }
}
