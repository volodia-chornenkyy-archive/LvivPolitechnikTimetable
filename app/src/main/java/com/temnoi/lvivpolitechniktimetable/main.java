package com.temnoi.lvivpolitechniktimetable;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class main extends ActionBarActivity implements ActionBar.OnNavigationListener {

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    String url_politeh = "http://lp.edu.ua/node/40?inst=8&group=7008&semestr=0&semest_part=1";

    public static final String[] DAYS = new String[]{
            "Пн", "Вт", "Ср", "Чт", "Пт"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[]{
                                getString(R.string.title_section1),
                                getString(R.string.title_section2),
                                getString(R.string.title_section3),
                        }),
                this);
    }

    public void DisplayTitle(Cursor c) {
        Toast.makeText(this, "id: " + c.getString(0) + "\n" +
                "ISBN: " + c.getString(1) + "\n" +
                "TITLE: " + c.getString(2) + "\n" +
                "PUBLISHER: " + c.getString(3), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getSupportActionBar().getSelectedNavigationIndex());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
        return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }


    public void getText(View view) {
        new Timetable().execute();
    }

    public void setDB(View view) {
        DBAdapter dbAdapter = new DBAdapter(this);

        Log.d("Insert: ", "Inserting ..");
        dbAdapter.addLesson(new Lesson("Monday", "1", "Programming", "Programming", "Theory", "Philosophy"));
        Log.d("Reading: ", "Reading all contacts..");
        List<Lesson> contacts = dbAdapter.getAllContacts();

        for (Lesson cn : contacts) {
            String log = "Id: " + cn.getID() + " ,Name: " + cn.getDay() + " ,Phone: " + cn.getGroup1Week1();
            // Writing Contacts to log
            Log.d("Name: ", log);
        }

        Toast.makeText(main.this, "DONE", Toast.LENGTH_SHORT).show();
    }

    /*Class for working with source of html page with timetable*/
    class Timetable extends AsyncTask<Void, Integer, String> {
        @Override
        protected String doInBackground(Void... params) {
            //Get source of html-page from its source code
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url_politeh);
            try {
                // Get source of page from line to line
                HttpResponse response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;
                Boolean read_flag = false;
                // Work with source
                while ((line = bufferedReader.readLine()) != null) {
                    if ((!read_flag) && (line.contains("<td align=\"center\" valign=\"middle\" rowspan=\"4\" class=\"leftcell\">Пн"))) {
                        read_flag = true;
                    } else if ((read_flag) && (line.equals("<div style=\"padding-top:20px;\"> Останнє оновлення: 17 вересня 2014 р. о 18:35</div></div>"))) {
                        read_flag = false;
                    }
                    if (read_flag) {
                        //Check chyselnuk/pidgrypa
                        //line = deleteTag(line);
                        if (!line.trim().equals("")) {
                            stringBuilder.append(line).append('\n');
                        }
                    }
                }
                inputStream.close();

                Lesson lesson = new Lesson();
                ArrayList<Lesson> timetable = new ArrayList<Lesson>();
                lesson = getLessons(stringBuilder);

                return stringBuilder.toString();
            } catch (IOException e) {
                return "ERROR";
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String par) {
            EditText text = (EditText) findViewById(R.id.editText2);
            text.setText(par);
            if (Arrays.asList(DAYS).contains("Пн"))
                Toast.makeText(main.this, Integer.toString(par.length()), Toast.LENGTH_LONG).show();
        }

        private String deleteTag(String line) {
            if ((line.indexOf('>') - line.indexOf('<')) > 0) {
                int start_index = line.indexOf('<');
                int finish_index = line.indexOf('>') + 1;

                line = line.replace(line.substring(start_index, finish_index), "");

                return deleteTag(line);
            } else {
                return line;
            }
        }

        Lesson lesson = new Lesson();

        private Lesson getLessons(StringBuilder stringBuilder) {
            String[] lines = stringBuilder.toString().split("\\n");

            Boolean table_start = false;
            Integer line_index = 0;
            String day = "e";
            String number = "e";
            String g1w1 = "e";//e - empty
            String g1w2 = "e";
            String g2w1 = "e";
            String g2w2 = "e";
            Byte td_count = 0;
            Byte lesson_number = 0;
            Boolean rowspan_check = false;

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
                    formatLesson(g1w1, g1w2, g2w1, g2w2);
                    lesson_number = 0;
                    System.out.println("day: " + lesson.getDay() + "\nnumber: " + lesson.getLessonNumber()
                            + "\ng1w1: " + lesson.getGroup1Week1() + "\ng2w1: " + lesson.getGroup2Week1()
                            + "\ng1w2: " + lesson.getGroup1Week2() + "\ng2w2: " + lesson.getGroup2Week2());
                    g2w2 = g1w2 = g2w1 = g1w1 = "e";//e - empty
                    lesson = lesson.clear();
                    System.out.println("********************");
                }
                if (table_start) {
                    if (s.contains("<table") && s.contains("<td colspan=\"2\" class=\"maincell\">")) {
                        s = s.replace("<td colspan=\"2\" class=\"maincell\">", "");
                    }
                    if (s.contains("<td") || s.contains("<div")) {
                        if (s.contains("<td")) {
                            td_count++;
                            if (s.contains("rowspan")) {
                                rowspan_check = true;
                            }
                        }
                        if (lines[line_index].contains("<div")) {
                            //continue;
                        }
                        if (td_count == 3) {
                            td_count = 1; //     !!!!!!!!!!!BAG solver!!!!!!!!!!!!!
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
            return null;
        }

        private void formatLesson(String g1w1, String g1w2, String g2w1, String g2w2) {
            // Odna para
            if (!g1w1.equals(g1w2) && g1w2.equals(g2w1) && g2w1.equals(g2w2) && g2w2.equals("e")) {
                g1w2 = g2w1 = g2w2 = g1w1;
                lesson.setGroup1Week1(g1w1);
                lesson.setGroup1Week2(g1w2);
                lesson.setGroup2Week1(g2w1);
                lesson.setGroup2Week2(g2w2);
                return;
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
                return;
            }
            // Chyselnuk BAG solver
            if (g1w1.equals(g1w2) && g1w1.equals("") && !g1w1.equals("e") && !g2w1.equals("") && !g2w1.equals(g2w2) && g2w2.equals("e")) {
                g1w1 = g2w1;
                g2w2 = "";
                lesson.setGroup1Week1(g1w1);
                lesson.setGroup1Week2(g1w2);
                lesson.setGroup2Week1(g2w1);
                lesson.setGroup2Week2(g2w2);
                return;
            }
            // Znamennuk
            if (!g1w1.equals(g1w2) && !g1w2.equals(g2w1) && g1w1.equals("") && g2w1.equals(g2w2) && g2w2.equals("e")) {
                g2w2 = g1w2;
                g2w1 = g1w1;
                lesson.setGroup1Week1(g1w1);
                lesson.setGroup1Week2(g1w2);
                lesson.setGroup2Week1(g2w1);
                lesson.setGroup2Week2(g2w2);
                return;
            }
            // Znamennuk BAG solver
            if (!g1w2.equals("") && !g1w2.equals("e") && g2w2.equals("e") && g1w1.equals("") && g2w1.equals(g1w1)) {
                g2w2 = g1w2;
                lesson.setGroup1Week1(g1w1);
                lesson.setGroup1Week2(g1w2);
                lesson.setGroup2Week1(g2w1);
                lesson.setGroup2Week2(g2w2);
                return;
            }
            // Razom
            if (!g1w1.equals(g1w2) && !g1w1.equals("") && !g1w2.equals("") && g2w1.equals(g2w2) && g2w2.equals("e")) {
                g2w1 = g1w1;
                g2w2 = g1w2;
                lesson.setGroup1Week1(g1w1);
                lesson.setGroup1Week2(g1w2);
                lesson.setGroup2Week1(g2w1);
                lesson.setGroup2Week2(g2w2);
                return;
            }
            // Razom BAG solver
            if (g1w1.equals("") && g2w2.equals("e") && !g2w1.equals("") && !g2w1.equals("e") && !g1w2.equals("") && !g1w2.equals("e")) {
                g1w1 = g2w1;
                g2w2 = g1w2;
                lesson.setGroup1Week1(g1w1);
                lesson.setGroup1Week2(g1w2);
                lesson.setGroup2Week1(g2w1);
                lesson.setGroup2Week2(g2w2);
                return;
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
                return;
            }
            // Persha BAG solver
            if (g1w1.equals("") && g1w2.equals(g1w1) && g2w2.equals("e") && !g2w1.equals("") && !g2w1.equals("e")) {
                g1w1 = g1w2 = g2w1;
                g2w1 = g2w2 = "";
                lesson.setGroup1Week1(g1w1);
                lesson.setGroup1Week2(g1w2);
                lesson.setGroup2Week1(g2w1);
                lesson.setGroup2Week2(g2w2);
                return;
            }
            // Dryga
            if (!g2w1.equals("") && !g2w1.equals("e") && g1w2.equals(g2w2) && g2w2.equals("e") && g1w1.equals("")) {
                g1w2 = g1w1;
                g2w2 = g2w1;
                lesson.setGroup1Week1(g1w1);
                lesson.setGroup1Week2(g1w2);
                lesson.setGroup2Week1(g2w1);
                lesson.setGroup2Week2(g2w2);
                return;
            }
            // Razom
            if (g1w2.equals(g2w2) && g2w2.equals("e") && !g1w1.equals("e") && !g1w1.equals("") && !g2w1.equals("e") && !g2w1.equals("")) {
                g1w2 = g1w1;
                g2w2 = g2w1;
                lesson.setGroup1Week1(g1w1);
                lesson.setGroup1Week2(g1w2);
                lesson.setGroup2Week1(g2w1);
                lesson.setGroup2Week2(g2w2);
                return;
            }
            // Podil na 4 BAG solver
            // from 1(g1w1) to 4(g2w2)
            if (!g1w1.equals("") && !g1w1.equals("e") && g2w1.equals("") && g1w2.equals(g2w1) && g2w2.equals(g1w2)) {
                g2w2 = g1w1;
                g1w1 = "";
                lesson.setGroup1Week1(g1w1);
                lesson.setGroup1Week2(g1w2);
                lesson.setGroup2Week1(g2w1);
                lesson.setGroup2Week2(g2w2);
                return;
            }
            // from 3(g2w1) to 1
            if (!g2w1.equals("") && !g2w1.equals("e") && g1w1.equals("") && g1w2.equals(g1w1) && g2w2.equals(g1w2)) {
                g1w1 = g2w1;
                g2w1 = "";
                lesson.setGroup1Week1(g1w1);
                lesson.setGroup1Week2(g1w2);
                lesson.setGroup2Week1(g2w1);
                lesson.setGroup2Week2(g2w2);
                return;
            }
            // Default
            lesson.setGroup1Week1(g1w1);
            lesson.setGroup1Week2(g1w2);
            lesson.setGroup2Week1(g2w1);
            lesson.setGroup2Week2(g2w2);
        }
    }
}
