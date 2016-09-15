package com.temnoi.lvivpolitechniktimetable.data;

import android.os.AsyncTask;

import com.temnoi.lvivpolitechniktimetable.model.Group;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chornenkyy@gmail.com
 * @since 9/15/16
 */

public class TimetableLoader {

    private final String TIMETABLE_PAGE_URL;

    private AsyncTask<Void, Void, List<Group>> currentAsyncTask;

    public TimetableLoader(String universityId, String groupId) {
        TIMETABLE_PAGE_URL = String.format("http://www.lp.edu.ua/rozklad-dlya-studentiv?inst=%s&group=%s&semestr=0&semest_part=1", universityId, groupId);
    }

    public void load(final Callback<List<Group>> callback) {
        currentAsyncTask = new AsyncTask<Void, Void, List<Group>>() {

            @Override
            protected List<Group> doInBackground(Void... voids) {
                List<Group> groups = new ArrayList<>();

                try {
                    // TODO: 14.09.2016 save the html source to the file for cache
                    Document doc = Jsoup.connect(TIMETABLE_PAGE_URL).get();
                    Elements timetableContent = doc.select("div[id=\"stud\"]");
                    Elements elements = timetableContent.select("tr").after("<th");
                    if (elements.size() > 1) {
                        for (Element element : elements) {

                        }
                    } else {
                        // TODO: 9/15/16 do somethig when timetable not exist
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onFailure(e);
                }

                return groups;
            }

            @Override
            protected void onPostExecute(List<Group> universities) {
                super.onPostExecute(universities);

                callback.onSuccess(universities);
            }
        };
        currentAsyncTask.execute();
    }

    public void interruptLoading() {
        if (currentAsyncTask != null && !currentAsyncTask.isCancelled()) {
            currentAsyncTask.cancel(true);
            currentAsyncTask = null;
        }
    }
}
