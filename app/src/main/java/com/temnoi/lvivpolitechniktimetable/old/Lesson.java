package com.temnoi.lvivpolitechniktimetable.old;

/**
 * Created by Volodia on 17.09.2014.
 */
public class Lesson {
    private int _id;
    private String _day;
    private String _less_num;
    private String _g1w1;
    private String _g2w1;
    private String _g1w2;
    private String _g2w2;

    public Lesson() {

    }

    public Lesson(int id, String day, String less_num, String g1w1, String g2w1, String g1w2, String g2w2) {
        this._id = id;
        this._day = day;
        this._less_num = less_num;
        this._g1w1 = g1w1;
        this._g2w1 = g2w1;
        this._g1w2 = g1w2;
        this._g2w2 = g2w2;
    }

    public Lesson(String day, String less_num, String g1w1, String g2w1, String g1w2, String g2w2) {
        this._day = day;
        this._less_num = less_num;
        this._g1w1 = g1w1;
        this._g2w1 = g2w1;
        this._g1w2 = g1w2;
        this._g2w2 = g2w2;
    }

    public int getID() {
        return this._id;
    }

    public void setID(int id) {
        this._id = id;
    }

    public String getDay() {
        return this._day;
    }

    // setting name
    public void setDay(String day) {
        this._day = day;
    }

    // getting phone number
    public String getLessonNumber() {
        return this._less_num;
    }

    // setting phone number
    public void setLessonNumber(String lessonNumber) {
        this._less_num = lessonNumber;
    }

    public String getGroup1Week1() {
        return this._g1w1;
    }

    public void setGroup1Week1(String g1w1) {
        this._g1w1 = g1w1;
    }

    // getting phone number
    public String getGroup2Week1() {
        return this._g2w1;
    }

    public void setGroup2Week1(String g2w1) {
        this._g2w1 = g2w1;
    }

    // getting phone number
    public String getGroup1Week2() {
        return this._g1w2;
    }

    public void setGroup1Week2(String g1w2) {
        this._g1w2 = g1w2;
    }

    // getting phone number
    public String getGroup2Week2() {
        return this._g2w2;
    }

    public void setGroup2Week2(String g2w2) {
        this._g2w2 = g2w2;
    }

    public Lesson clear() {
        return new Lesson("", "", "", "", "", "");
    }
}
