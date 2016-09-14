package com.temnoi.lvivpolitechniktimetable;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.temnoi.lvivpolitechniktimetable.model.University;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chornenkyy@gmail.com
 * @since 14.09.2016
 */

// TODO: 14.09.2016 simply rewrite it in rx
public class UniversitiesLoader {

    private static final String TIMETABLE_PAGE_URL = "http://www.lp.edu.ua/rozklad-dlya-studentiv?inst=&group=";

    private AsyncTask<Void, Void, List<University>> currentAsyncTask;

    public void load(final Callback<List<University>> callback) {
        currentAsyncTask = new AsyncTask<Void, Void, List<University>>() {

            @Override
            protected List<University> doInBackground(Void... voids) {
                List<University> universities = new ArrayList<>();

                try {
                    // TODO: 14.09.2016 save the html source to the file for cache
                    Document doc = Jsoup.connect(TIMETABLE_PAGE_URL).get();
                    Elements universityElements = doc.select("select[name=\"inst\"]").first().children();

                    for (Element element : universityElements) {
                        String universityId = element.attr("value");
                        String universityShortName = element.html();

                        if (!TextUtils.isEmpty(universityId) && !TextUtils.isEmpty(universityShortName)) {
                            universities.add(new University(universityId, universityShortName));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onFailure(e);
                }

                return universities;
            }

            @Override
            protected void onPostExecute(List<University> universities) {
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
