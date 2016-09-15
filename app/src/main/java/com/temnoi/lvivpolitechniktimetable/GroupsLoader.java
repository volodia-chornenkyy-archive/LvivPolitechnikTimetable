package com.temnoi.lvivpolitechniktimetable;

import android.os.AsyncTask;
import android.text.TextUtils;

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
 * @since 15.09.2016
 */

// TODO: 14.09.2016 simply rewrite it in rx
public class GroupsLoader {

    private final String TIMETABLE_PAGE_URL;

    private AsyncTask<Void, Void, List<Group>> currentAsyncTask;

    public GroupsLoader(String universityId) {
        TIMETABLE_PAGE_URL = String.format("http://www.lp.edu.ua/rozklad-dlya-studentiv?inst=%s&group=", universityId);
    }

    public void load(final Callback<List<Group>> callback) {
        currentAsyncTask = new AsyncTask<Void, Void, List<Group>>() {

            @Override
            protected List<Group> doInBackground(Void... voids) {
                List<Group> groups = new ArrayList<>();

                try {
                    // TODO: 14.09.2016 save the html source to the file for cache
                    Document doc = Jsoup.connect(TIMETABLE_PAGE_URL).get();
                    Elements groupElements = doc.select("select[name=\"group\"]").first().children();

                    for (Element element : groupElements) {
                        String groupId = element.attr("value");
                        String groupName = element.html();

                        if (!TextUtils.isEmpty(groupId) && !TextUtils.isEmpty(groupName)) {
                            groups.add(new Group(groupId, groupName));
                        }
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
