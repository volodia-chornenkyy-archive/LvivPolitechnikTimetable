package com.temnoi.lvivpolitechniktimetable.ui.setup;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

/**
 * @author chornenkyy@gmail.com
 * @since 9/15/16
 */

public class BaseFragment extends Fragment {

    protected void updateToolbarTitle(@StringRes int titleResId) {
        if (getActivity() instanceof AppCompatActivity) {
            ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setTitle(titleResId);
            }
        }
    }
}
