package com.temnoi.lvivpolitechniktimetable.old;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.temnoi.lvivpolitechniktimetable.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class main extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    public static final String[] UNIVERSITY = new String[]{
            "ІАРХ*1", "ІБІД*2", "ІГДГ*3", "ІГСН*4", "ІЕПТ*19", "ІЕСК*6", "ІІМТ*7", "ІКНІ*8",
            "ІКТА*9", "ІМФН*10", "ІНЕМ*5", "ІНПП*18", "ІТРЕ*11", "ІХХТ*12"
    };
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    String url_politeh = "http://lp.edu.ua/node/40?inst=8&group=7009&semestr=0&semest_part=1";
    private int current_day;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().setDisplayShowHomeEnabled(false);
        setContentView(R.layout.activity_my);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        // Change color of action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#29b6f6")));

//        fabButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new TimetableRenew().execute();
//            }
//        });

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (settings.getBoolean("update", true)) {
            new TimetableRenew().execute();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the my content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, TimeTableFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
        }
        current_day = number;
        new TimetableRefresh().execute();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    // WTF????
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my, menu);
        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(getApplicationContext(), UserSettings.class);
                startActivityForResult(i, 1);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addToDatabase(ArrayList<Lesson> lesson) {
        DBAdapter dbAdapter = new DBAdapter(this);
        dbAdapter.clear();
        lesson.trimToSize();
        for (int i = 0; i < lesson.size() - 1; i++) {
            if (!lesson.get(i).getGroup1Week1().equals("e")
                    && !lesson.get(i).getGroup1Week2().equals("e")
                    && !lesson.get(i).getGroup2Week1().equals("e")
                    && !lesson.get(i).getGroup2Week2().equals("e"))
                dbAdapter.addLesson(lesson.get(i));
        }
        dbAdapter.close();
        Toast.makeText(main.this, "Work with DB complete", Toast.LENGTH_SHORT).show();
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==1)
            showSettings();
    }

    public void showSettings(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences settings = getSharedPreferences("MyPrefsFile",0);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nUser name: ").append(preferences.getString("user_name","NULL"));
        stringBuilder.append("\nCheckBox: ").append(preferences.getBoolean("update", true));
        stringBuilder.append("\nList: ").append(preferences.getString("list","NULL"));
        TextView textView = (TextView)findViewById(R.id.textView);
        textView.setText(stringBuilder.toString());
    }
*/

    public String deleteTag(String line) {
        if ((line.indexOf('>') - line.indexOf('<')) > 0) {
            int start_index = line.indexOf('<');
            int finish_index = line.indexOf('>') + 1;

            line = line.replace(line.substring(start_index, finish_index), "");

            return deleteTag(line);
        } else {
            return line;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */


    class TimetableRefresh extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(Void p) {
            readFromDatabase();
        }

        private void readFromDatabase() {
            DBAdapter dbAdapter = new DBAdapter(main.this);
            List<Lesson> lessonList = dbAdapter.getAllContacts();
            int i = 0;
            String day_name = "";
            switch (current_day) {
                case 1:
                    day_name = "Пн";
                    break;
                case 2:
                    day_name = "Вт";
                    break;
                case 3:
                    day_name = "Ср";
                    break;
                case 4:
                    day_name = "Чт";
                    break;
                case 5:
                    day_name = "Пт";
                    break;
            }
            for (Lesson cn : lessonList) {
                if (cn.getDay().equals(day_name)) {
                    if (cn.getGroup1Week1().equals("")) {
                        continue;
                    }
                    switch (i) {
                        case 0:
                            TextView textView1 = (TextView) findViewById(R.id.text_title1);
                            textView1.setText(cn.getGroup1Week1());
                            TextView textNumber1 = (TextView) findViewById(R.id.text_number1);
                            textNumber1.setText(cn.getLessonNumber() + " ");
                            break;
                        case 1:
                            TextView textView2 = (TextView) findViewById(R.id.text_title2);
                            textView2.setText(cn.getGroup1Week1());
                            TextView textNumber2 = (TextView) findViewById(R.id.text_number2);
                            textNumber2.setText(cn.getLessonNumber() + " ");
                            break;
                        case 2:
                            TextView textView3 = (TextView) findViewById(R.id.text_title3);
                            textView3.setText(cn.getGroup1Week1());
                            TextView textNumber3 = (TextView) findViewById(R.id.text_number3);
                            textNumber3.setText(cn.getLessonNumber() + " ");
                            break;
                        case 3:
                            TextView textView4 = (TextView) findViewById(R.id.text_title4);
                            textView4.setText(cn.getGroup1Week1());
                            TextView textNumber4 = (TextView) findViewById(R.id.text_number4);
                            textNumber4.setText(cn.getLessonNumber() + " ");
                            break;
                    }
                    i++;
                }
            }
        }
    }

    /*Class for working with source of html page with timetable*/
    class TimetableRenew extends AsyncTask<Void, Integer, ArrayList<Lesson>> {
        // Glob var for lesson save/work
        String day = "e";
        String number = "e";

        @Override
        protected ArrayList<Lesson> doInBackground(Void... params) {
            //Get source of html-page from its source code
            OkHttpClient httpClient = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url_politeh)
                    .build();

            InputStream pageStream = null;
            try {
                Response response = httpClient.newCall(request).execute();
                pageStream = response.body().byteStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Variables
            if (pageStream != null) {
                BufferedReader bufferedReader = null;
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(pageStream, "UTF-8"), 8);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                Boolean lesson_read_flag = false;
                Boolean group_read_flag = false;
                Lesson lesson = new Lesson();
                ArrayList<Lesson> list = new ArrayList<Lesson>();

                // Work with source
                if (bufferedReader != null) {
                    try {
                        while ((line = bufferedReader.readLine()) != null) {
                            // Get groups
                            if (line.contains("<option value") && !line.contains("День</th><th class=\"zagol2\">Пара")) {

                            }
                            // Get lessons
                            if ((!lesson_read_flag) && (line.contains("<td align=\"center\" valign=\"middle\" rowspan=\"4\" class=\"leftcell\">Пн"))) {
                                lesson_read_flag = true;
                            } else if ((lesson_read_flag) && (line.equals("<div style=\"padding-top:20px;\"> Останнє оновлення: 17 вересня 2014 р. о 18:35</div></div>"))) {
                                lesson_read_flag = false;
                            }
                            if (lesson_read_flag) {
                                String temp = line;
                                if (line.contains("</table")) {
                                    line = line.substring(0, line.indexOf("</table>") + 8);
                                    stringBuilder.append(line).append('\n');
                                    lesson = getLessons(stringBuilder);
                                    list.add(lesson);
                                    stringBuilder.delete(0, stringBuilder.length());
                                    line = temp.substring(temp.indexOf("</table>") + 8, temp.length());
                                    stringBuilder.append(line).append('\n');
                                } else {
                                    stringBuilder.append(line).append('\n');
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return list;
            } else {
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(ArrayList<Lesson> par) {
            // Add to DB
            addToDatabase(par);
            par.clear(); //need or not ???

            new TimetableRefresh().execute();
        }

        private Lesson getLessons(StringBuilder stringBuilder) {
            String[] lines = stringBuilder.toString().split("\\n");

            Boolean table_start = false;
            Integer line_index = 0;
            String g1w1 = "e";//e - empty
            String g1w2 = "e";
            String g2w1 = "e";
            String g2w2 = "e";
            Byte td_count = 0;
            Byte lesson_number = 0;
            Boolean rowspan_check = false;
            Lesson lesson = new Lesson();

            for (String s : lines) {
                line_index++;
                // Get day
                if (s.contains("<td align=\"center\" valign=\"middle\" rowspan=\"")) {
                    day = deleteTag(s).trim();
                    day = day.substring(0, 2); // remove last char=number
                }
                lesson.setDay(day);
                // Get lesson number
                if (s.contains("align=\"center\" valign=\"middle\" class=\"leftcell\">")) {
                    number = deleteTag(s).trim();
                    number = number.replaceAll("[^0-9]", ""); // remove all letters
                }
                // Get lesson
                if (s.contains("<table")) {
                    table_start = true;
                    lesson.setLessonNumber(number);
                } else if (s.contains("</table")) {
                    table_start = false;
                    lesson = formatLesson(lesson, g1w1, g1w2, g2w1, g2w2);
                    lesson_number = 0;
                    break;
                }
                if (table_start) {
                    if (s.indexOf("<table") > 0) {
                        s = s.substring(s.indexOf("<table"));
                    }
                    if (s.contains("<td") || s.contains("<div")) {
                        if (s.contains("<td")) {
                            td_count++;
                            if (td_count == 3) {
                                td_count = 1; //     !!!!!!!!!!!BAG solver!!!!!!!!!!!!!
                            }
                            if (s.contains("rowspan")) {
                                rowspan_check = true;
                            }
                        }
                        if (lines[line_index].contains("<div")) {
                            //continue;
                        }

                        switch (lesson_number) {
                            case 0:
                                g1w1 = deleteTag(s).trim();
                                if (rowspan_check) { // якщо перша підгрупа
                                    g1w2 = g1w1;
                                    lesson_number++; // наступний запис на lesson_number = 2
                                }
                                lesson_number++;
                                break;
                            case 1:
                                if (td_count == 2) { // якщо поділ на підгрупи
                                    g2w1 = deleteTag(s).trim();
                                    if (rowspan_check) { // якщо друга підгрупа
                                        g2w2 = g2w1;
                                    }
                                } else {
                                    g1w2 = deleteTag(s).trim();
                                    if (rowspan_check) {
                                        g1w1 = g1w2;
                                    }
                                }
                                lesson_number++;
                                break;
                            case 2:
                                if (td_count == 1) {
                                    g1w2 = deleteTag(s).trim();
                                } else {
                                    g2w1 = deleteTag(s).trim();
                                }
                                lesson_number++;
                                break;
                            case 3:
                                g2w2 = deleteTag(s).trim();
                                lesson_number = 0;
                                break;
                        }
                    }
                }
                if (s.contains("</tr")) {
                    td_count = 0;
                    rowspan_check = false;
                }
            }
            return lesson;
        }

        private Lesson formatLesson(Lesson lesson, String g1w1, String g1w2, String g2w1, String g2w2) {
            // Odna para
            if (!g1w1.equals(g1w2) && g1w2.equals(g2w1) && g2w1.equals(g2w2) && g2w2.equals("e")) {
                g1w2 = g2w1 = g2w2 = g1w1;
                lesson.setGroup1Week1(g1w1);
                lesson.setGroup1Week2(g1w2);
                lesson.setGroup2Week1(g2w1);
                lesson.setGroup2Week2(g2w2);
                return lesson;
            }
            // Chyselnuk i znamennuk:
            // Chyselnuk
            if (!g1w1.equals(g1w2) && !g1w2.equals(g2w1) && g1w2.equals("") && g2w1.equals(g2w2) && g2w2.equals("e")) {
                g2w1 = g1w1;
                g2w2 = g1w2;
                lesson.setGroup1Week1(g1w1);
                lesson.setGroup1Week2(g1w2);
                lesson.setGroup2Week1(g2w1);
                lesson.setGroup2Week2(g2w2);
                return lesson;
            }
            // Znamennuk
            if (!g1w1.equals(g1w2) && !g1w2.equals(g2w1) && g1w1.equals("") && g2w1.equals(g2w2) && g2w2.equals("e")) {
                g2w2 = g1w2;
                g2w1 = g1w1;
                lesson.setGroup1Week1(g1w1);
                lesson.setGroup1Week2(g1w2);
                lesson.setGroup2Week1(g2w1);
                lesson.setGroup2Week2(g2w2);
                return lesson;
            }
            // Razom
            if (!g1w1.equals(g1w2) && !g1w1.equals("") && !g1w2.equals("") && g2w1.equals(g2w2) && g2w2.equals("e")) {
                g2w1 = g1w1;
                g2w2 = g1w2;
                lesson.setGroup1Week1(g1w1);
                lesson.setGroup1Week2(g1w2);
                lesson.setGroup2Week1(g2w1);
                lesson.setGroup2Week2(g2w2);
                return lesson;
            }
            // Dvi pidgypu:
            // Persha
            if (!g1w1.equals("") && !g1w1.equals("e") && g1w2.equals(g2w2) && g2w2.equals("e") && g2w1.equals("")) {
                g1w2 = g1w1;
                g2w2 = g2w1;
                lesson.setGroup1Week1(g1w1);
                lesson.setGroup1Week2(g1w2);
                lesson.setGroup2Week1(g2w1);
                lesson.setGroup2Week2(g2w2);
                return lesson;
            }
            // Dryga
            if (!g2w1.equals("") && !g2w1.equals("e") && g1w2.equals(g2w2) && g2w2.equals("e") && g1w1.equals("")) {
                g1w2 = g1w1;
                g2w2 = g2w1;
                lesson.setGroup1Week1(g1w1);
                lesson.setGroup1Week2(g1w2);
                lesson.setGroup2Week1(g2w1);
                lesson.setGroup2Week2(g2w2);
                return lesson;
            }
            // Razom
            if (g1w2.equals(g2w2) && g2w2.equals("e") && !g1w1.equals("e") && !g1w1.equals("") && !g2w1.equals("e") && !g2w1.equals("")) {
                g1w2 = g1w1;
                g2w2 = g2w1;
                lesson.setGroup1Week1(g1w1);
                lesson.setGroup1Week2(g1w2);
                lesson.setGroup2Week1(g2w1);
                lesson.setGroup2Week2(g2w2);
                return lesson;
            }
            // from 3(g2w1) to 1
            if (!g2w1.equals("") && !g2w1.equals("e") && g1w1.equals("") && g1w2.equals(g1w1) && g2w2.equals(g1w2)) {
                g1w1 = g2w1;
                g2w1 = "";
                lesson.setGroup1Week1(g1w1);
                lesson.setGroup1Week2(g1w2);
                lesson.setGroup2Week1(g2w1);
                lesson.setGroup2Week2(g2w2);
                return lesson;
            }
            // Default
            lesson.setGroup1Week1(g1w1);
            lesson.setGroup1Week2(g1w2);
            lesson.setGroup2Week1(g2w1);
            lesson.setGroup2Week2(g2w2);
            return lesson;
        }
    }
}