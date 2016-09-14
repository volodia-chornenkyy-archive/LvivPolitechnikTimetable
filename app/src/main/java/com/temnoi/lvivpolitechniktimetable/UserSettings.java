package com.temnoi.lvivpolitechniktimetable;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Volodia on 24.10.2014.
 */
public class UserSettings extends PreferenceActivity {

    protected static void setListPreferenceData(ListPreference lp, StringBuilder entr,
                                                StringBuilder entrValues) {
        CharSequence[] entries = entr.toString().split("\\n");
        CharSequence[] entryValues = entrValues.toString().split("\\n");
        lp.setEntries(entries);
        lp.setDefaultValue(entries[0]);
        lp.setEntryValues(entryValues);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String url_politeh = "http://lp.edu.ua/node/40?inst=" + 8 + "&group=" + 7009 + "&semestr=0&semest_part=1";
        addPreferencesFromResource(R.xml.settings);

        class GroupObtain extends AsyncTask<Void, Integer, Void> {
            @Override
            protected Void doInBackground(Void... params) {
                // get university
                ListPreference listUniver = (ListPreference) findPreference("university");

                OkHttpClient httpClient = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://lp.edu.ua/node/40?inst=" + listUniver.getValue()
                                + "&group=&semestr=0&semest_part=1")
                        .build();

                try {
                    InputStream inputStream = httpClient.newCall(request).execute().body().byteStream();

                    // Variables
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(inputStream, "UTF-8"), 8);
                    String line;
                    StringBuilder sbName = new StringBuilder(); //group name КН-24
                    StringBuilder sbCode = new StringBuilder(); //group code 7008
                    boolean read_flag = false;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.contains("<select name=\"inst\" onchange=\"submit();\">")) {
                            // delete unnecessary parts
                            line = line.replace(line.substring(0, line.indexOf("<select name=\"group\" onchange=\"submit();\">"
                                    + "<option></option>") + 59), "");
                            line = line.replace(line.substring(line.indexOf("</select>") + 9, line.length()), "");

                            // get group name&code
                            sbName = getTagValues(line, sbName);
                            sbCode = getTagParameterValues(line, sbCode);
                            break;
                            //sbCode.append(getTagParamaterValue(line, "value")).append('\n');
                        }
                    }
                    //dynamic group-list fill
                    final ListPreference listGroup = (ListPreference) findPreference("group");
                    setListPreferenceData(listGroup, sbName, sbCode);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {

            }

            @Override
            protected void onPostExecute(Void p) {
                Toast.makeText(UserSettings.this, url_politeh, Toast.LENGTH_LONG).show();
            }
        }

        new GroupObtain().execute();

        /*final ListPreference listGroup = (ListPreference) findPreference("group");
        listPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                setListPreferenceData(listPreference);
                return false;
            }
        });*/
    }

    private String getTagParamaterValue(String s, String par) {
        if (s.contains(par)) {
            return s.substring(s.indexOf(par + "=\"") + par.length() + 2, s.indexOf("\"", s.indexOf(par + "=\"") + par.length() + 2));
        }
        return "empty";
    }

    public StringBuilder getTagParameterValues(String line, StringBuilder sb) {
        try {
            if ((line.indexOf('>') - line.indexOf('<')) > 0) {
                sb.append(line.substring(line.indexOf("\""), line.indexOf("\"") + 5)).append('\n');
                line = line.replace(line.substring(line.indexOf("<o"), line.indexOf("</") + 9), "");
                return getTagParameterValues(line, sb);
            }
        } catch (IndexOutOfBoundsException e) {
        }
        return sb;
    }

    public StringBuilder getTagValues(String line, StringBuilder sb) {
        try {
            if ((line.indexOf('>') - line.indexOf('<')) > 0) {
                line = line.replace(line.substring(line.indexOf('<'), line.indexOf('>') + 1), "");
                if (!line.substring(0, line.indexOf("<")).equals("")) {
                    sb.append(line.substring(0, line.indexOf("<"))).append('\n');
                }
                line = line.replace(line.substring(0, line.indexOf("<")), "");
                return getTagValues(line, sb);
            }
        } catch (IndexOutOfBoundsException e) {
        }
        return sb;
    }
}
