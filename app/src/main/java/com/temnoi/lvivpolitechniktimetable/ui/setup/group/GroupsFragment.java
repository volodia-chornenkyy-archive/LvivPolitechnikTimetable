package com.temnoi.lvivpolitechniktimetable.ui.setup.group;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.temnoi.lvivpolitechniktimetable.Callback;
import com.temnoi.lvivpolitechniktimetable.GroupsLoader;
import com.temnoi.lvivpolitechniktimetable.R;
import com.temnoi.lvivpolitechniktimetable.model.Group;
import com.temnoi.lvivpolitechniktimetable.ui.BaseFragment;

import java.util.List;

/**
 * @author chornenkyy@gmail.com
 * @since 15.09.2016
 */

public class GroupsFragment extends BaseFragment {

    public final static String TAG = GroupsFragment.class.getSimpleName();

    private static final String ARG_UNIVERSITY_ID = "arg_university_id";

    private GroupsAdapter groupsAdapter;
    private GroupsLoader groupsLoader;

    public static GroupsFragment newInstance(String universityId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_UNIVERSITY_ID, universityId);

        GroupsFragment groupsFragment = new GroupsFragment();
        groupsFragment.setArguments(bundle);

        return groupsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_groups, container, false);

        setupUi(rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadGroups(getArguments().getString(ARG_UNIVERSITY_ID));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (groupsLoader != null) {
            groupsLoader.interruptLoading();
        }
    }

    private void loadGroups(String universityId) {
        groupsLoader = new GroupsLoader(universityId);
        groupsLoader.load(new Callback<List<Group>>() {
            @Override
            public void onSuccess(List<Group> data) {
                groupsAdapter.update(data);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void setupUi(View rootView) {
        updateToolbarTitle(R.string.setup_group_title);

        RecyclerView rvUniversities = (RecyclerView) rootView.findViewById(R.id.rv_groups);
        groupsAdapter = new GroupsAdapter();
        rvUniversities.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUniversities.setAdapter(groupsAdapter);

        FloatingActionButton fabGoToGroups = (FloatingActionButton) rootView.findViewById(R.id.fab_done);
        fabGoToGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Group selectedItem = groupsAdapter.getSelectedItem();

                if (selectedItem != null) {
                    // go to main screen
                    Toast.makeText(getContext(), "" + selectedItem.getShortName(), Toast.LENGTH_SHORT).show();
                } else {
                    // TODO: 9/15/16 show message to select university
                }

            }
        });
    }
}
