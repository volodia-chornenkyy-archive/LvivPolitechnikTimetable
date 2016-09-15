package com.temnoi.lvivpolitechniktimetable.ui.setup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.temnoi.lvivpolitechniktimetable.R;

public class SetupActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(
                            R.id.setup_activity_container,
                            UniversitiesFragment.newInstance(),
                            UniversitiesFragment.TAG)
                    .commit();
        }
    }
}
