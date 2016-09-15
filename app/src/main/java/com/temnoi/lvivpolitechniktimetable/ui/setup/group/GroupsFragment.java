package com.temnoi.lvivpolitechniktimetable.ui.setup.group;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * @author chornenkyy@gmail.com
 * @since 15.09.2016
 */

public class GroupsFragment extends Fragment {

    private static final String ARG_UNIVERSITY_ID = "arg_university_id";

    public static GroupsFragment newInstance(String universityId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_UNIVERSITY_ID, universityId);

        GroupsFragment groupsFragment = new GroupsFragment();
        groupsFragment.setArguments(bundle);

        return groupsFragment;
    }
}
