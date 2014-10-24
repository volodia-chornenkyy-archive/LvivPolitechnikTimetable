package com.temnoi.lvivpolitechniktimetable;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Volodia on 24.10.2014.
 */
public class UserSettings extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
    }
}
