package com.temnoi.lvivpolitechniktimetable.ui.setup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.temnoi.lvivpolitechniktimetable.Callback;
import com.temnoi.lvivpolitechniktimetable.R;
import com.temnoi.lvivpolitechniktimetable.UniversitiesLoader;
import com.temnoi.lvivpolitechniktimetable.model.University;

import java.util.List;

public class UniversitiesFragment extends Fragment {

    private UniversitiesAdapter universitiesAdapter;
    private UniversitiesLoader universitiesLoader;

    public static UniversitiesFragment newInstance() {
        return new UniversitiesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_universities, container, false);

        setupUi(rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadUniversities();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // TODO: 15.09.2016 don't really sure if I need it
        if (universitiesLoader != null) {
            universitiesLoader.interruptLoading();
        }
    }

    private void loadUniversities() {
        universitiesLoader = new UniversitiesLoader();
        universitiesLoader.load(new Callback<List<University>>() {
            @Override
            public void onSuccess(List<University> data) {
                universitiesAdapter.update(data);
            }

            @Override
            public void onFailure(Throwable t) {
                // TODO: 15.09.2016 handle failure
            }
        });
    }

    private void setupUi(View rootView) {
        RecyclerView rvUniversities = (RecyclerView) rootView.findViewById(R.id.rv_universities);
        universitiesAdapter = new UniversitiesAdapter();
        rvUniversities.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUniversities.setAdapter(universitiesAdapter);
    }
}