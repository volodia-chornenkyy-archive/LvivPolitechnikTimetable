package com.temnoi.lvivpolitechniktimetable.ui.setup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.temnoi.lvivpolitechniktimetable.R;
import com.temnoi.lvivpolitechniktimetable.ui.setup.university.UniversitiesFragment;

public class SetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
