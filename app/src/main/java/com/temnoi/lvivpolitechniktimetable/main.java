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
import android.view.MenuInflater;
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
import java.util.List;


public class main extends ActionBarActivity {
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    String url_politeh = "http://lp.edu.ua/node/40?inst=8&group=7009&semestr=0&semest_part=1";

    public static final String[] DAYS = new String[]{
            "Пн", "Вт", "Ср", "Чт", "Пт"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void DisplayTitle(Cursor c) {
        Toast.makeText(this, "id: " + c.getString(0) + "\n" +
                "ISBN: " + c.getString(1) + "\n" +
                "TITLE: " + c.getString(2) + "\n" +
                "PUBLISHER: " + c.getString(3), Toast.LENGTH_LONG).show();
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_settings :
                setContentView(R.layout.settings);
                return true;
            case R.id.action_refresh:
                new Timetable().execute();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addToDatabase(ArrayList<Lesson> lesson) {
        DBAdapter dbAdapter = new DBAdapter(this);
        dbAdapter.clear();
        lesson.trimToSize();
        for (int i = 0; i<lesson.size()-1;i++) {
            if (!lesson.get(i).getGroup1Week1().equals("e")
                    && !lesson.get(i).getGroup1Week2().equals("e")
                    && !lesson.get(i).getGroup2Week1().equals("e")
                    && !lesson.get(i).getGroup2Week2().equals("e"))
            dbAdapter.addLesson(lesson.get(i));
        }

        /*List<Lesson> contacts = dbAdapter.getAllContacts();
        for (Lesson cn : contacts) {
            String log = "Id: " + cn.getID() + " ,Name: " + cn.getDay() + " ,Phone: " + cn.getGroup1Week1();
        }*/

        Toast.makeText(main.this, "Work with DB complete", Toast.LENGTH_SHORT).show();
    }

    /*Class for working with source of html page with timetable*/
    class Timetable extends AsyncTask<Void, Integer, ArrayList<Lesson>> {
        @Override
        protected ArrayList<Lesson> doInBackground(Void... params) {
            //Get source of html-page from its source code
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url_politeh);
            try {
                // Get source of page from line to line
                HttpResponse response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();

                // Variables
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;
                Boolean read_flag = false;
                Lesson lesson = new Lesson();
                ArrayList<Lesson> list = new ArrayList<Lesson>();

                // Work with source
                while ((line = bufferedReader.readLine()) != null) {
                    if ((!read_flag) && (line.contains("<td align=\"center\" valign=\"middle\" rowspan=\"4\" class=\"leftcell\">Пн"))) {
                        read_flag = true;
                    } else if ((read_flag) && (line.equals("<div style=\"padding-top:20px;\"> Останнє оновлення: 17 вересня 2014 р. о 18:35</div></div>"))) {
                        read_flag = false;
                    }
                    if (read_flag) {
                        String temp = line;
                        if (line.contains("</table")){
                            line = line.substring(0,line.indexOf("</table>")+8);
                            stringBuilder.append(line).append('\n');
                            lesson = getLessons(stringBuilder);
                            list.add(lesson);
                            stringBuilder.delete(0, stringBuilder.length());
                            line = temp.substring(temp.indexOf("</table>")+8, temp.length());
                            stringBuilder.append(line).append('\n');
                        } else {
							stringBuilder.append(line).append('\n');
						}
                    }
                }
                inputStream.close();
                return list;
            } catch (IOException e) {
                Log.d(this.getClass().getName(),"***Smth goes wrong when data was took form internet***");
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
            Toast.makeText(main.this, "All source was got....for now;)", Toast.LENGTH_LONG).show();
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

        // Glob var for lesson save/work
        String day = "e";
        String number = "e";

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
                    if (s.indexOf("<table")>0){
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