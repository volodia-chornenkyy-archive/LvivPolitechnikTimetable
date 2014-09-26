package com.temnoi.lvivpolitechniktimetable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Volodia on 16.09.2014.
 */
public class DBAdapter extends SQLiteOpenHelper{
    private SQLiteDatabase db;

    //Columns names
    private static final String KEY_ROWID = "_id";
    private static final String KEY_DAY = "day";
    private static final String KEY_LESS_NUM = "lessonNumber";
    private static final String KEY_G1W1 = "group1week1";
    private static final String KEY_G2W1 = "group2week1";
    private static final String KEY_G1W2 = "group1week2";
    private static final String KEY_G2W2 = "group2week2";

    private static final String DATABASE_NAME = "LvivPolitechnikTimetable.db";
    private static final String DATABASE_TABLE = "currentTimetable";
    private static final int DATABASE_VERSION = 1;

    public DBAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String DATABASE_CREATE =
                "CREATE TABLE " + DATABASE_TABLE + " ("
                        + KEY_ROWID + " INTEGER PRIMARY KEY," + KEY_DAY + " TEXT,"
                        + KEY_LESS_NUM + " TEXT," + KEY_G1W1 + " TEXT,"
                        + KEY_G2W1 + " TEXT," + KEY_G1W2 + " TEXT," + KEY_G2W2 + " TEXT)";
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS" + DATABASE_NAME);
        onCreate(db);
    }

    // Adding new contact
    public void addLesson(Lesson lesson) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DAY, lesson.getDay());
        initialValues.put(KEY_LESS_NUM, lesson.getLessonNumber());
        initialValues.put(KEY_G1W1, lesson.getGroup1Week1());
        initialValues.put(KEY_G2W1, lesson.getGroup2Week1());
        initialValues.put(KEY_G1W2, lesson.getGroup1Week2());
        initialValues.put(KEY_G2W2, lesson.getGroup2Week2());
        db.insert(DATABASE_TABLE, null, initialValues);
        db.close(); // Closing database connection
    }

    // Getting single contact
    public Lesson getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DATABASE_NAME, new String[] {
                        KEY_ROWID,KEY_DAY, KEY_LESS_NUM, KEY_G1W1, KEY_G1W2, KEY_G2W1, KEY_G2W2 },
                        KEY_ROWID + "=?",
                        new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Lesson contact = new Lesson(Integer.parseInt(cursor.getString(0)),cursor.getString(1),
                cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),
                cursor.getString(6));
        // return contact
        return contact;
    }

    // Getting All Contacts
    public List<Lesson> getAllContacts() {
        List<Lesson> contactList = new ArrayList<Lesson>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Lesson contact = new Lesson();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setDay(cursor.getString(1));
                contact.setLessonNumber(cursor.getString(2));
                contact.setGroup1Week1(cursor.getString(3));
                contact.setGroup2Week1(cursor.getString(4));
                contact.setGroup1Week2(cursor.getString(5));
                contact.setGroup2Week2(cursor.getString(6));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // Updating single contact
    public int updateContact(Lesson lesson) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DAY, lesson.getDay());
        initialValues.put(KEY_LESS_NUM, lesson.getLessonNumber());
        initialValues.put(KEY_G1W1, lesson.getGroup1Week1());
        initialValues.put(KEY_G2W1, lesson.getGroup2Week1());
        initialValues.put(KEY_G1W2, lesson.getGroup1Week2());
        initialValues.put(KEY_G2W2, lesson.getGroup2Week2());

        // updating row
        return db.update(DATABASE_NAME, initialValues, KEY_ROWID + " = ?",
                new String[] { String.valueOf(lesson.getID()) });
    }

    // Deleting single contact
    public void deleteContact(Lesson contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE, KEY_ROWID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
        db.close();
    }
    /*
    //---opens the database---
    public DBAdapter open() throws SQLException{
        db = DBAdapter.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close(){
        DBAdapter.close();
    }

    //---insert a title into the database---
    public long insertLesson(String day, String lesson_number, String group1week1, String group2week1,
                            String group1week2, String group2week2){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DAY, day);
        initialValues.put(KEY_LESS_NUM, lesson_number);
        initialValues.put(KEY_G1W1, group1week1);
        initialValues.put(KEY_G2W1, group2week1);
        initialValues.put(KEY_G1W2, group1week2);
        initialValues.put(KEY_G2W2, group2week2);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //---deletes a particular title---
    public boolean deleteTimetable(long rowId){
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    //---retrieves all the titles---
    public Cursor getAllLesson(){
        return db.query(DATABASE_TABLE, new String[] {
                        KEY_ROWID,
                        KEY_DAY,
                        KEY_LESS_NUM,
                        KEY_G1W1,
                        KEY_G2W1,
                        KEY_G1W2,
                        KEY_G2W2},
                null,
                null,
                null,
                null,
                null,
                null);
    }

    //---retrieves a particular title---
    public Cursor getLesson(long rowId) throws SQLException{
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {
                                KEY_ROWID,
                                KEY_DAY,
                                KEY_LESS_NUM,
                                KEY_G1W1,
                                KEY_G2W1,
                                KEY_G1W2,
                                KEY_G2W2
                        },
                        KEY_ROWID + "=" + rowId,
                        null,
                        null,
                        null,
                        null,
                        null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a title---
    public boolean updateLesson(long rowId, String day, String lesson_number, String group1week1,
                               String group2week1, String group1week2, String group2week2){
        ContentValues args = new ContentValues();
        args.put(KEY_DAY, day);
        args.put(KEY_LESS_NUM, lesson_number);
        args.put(KEY_G1W1, group1week1);
        args.put(KEY_G2W1, group2week1);
        args.put(KEY_G1W2, group1week2);
        args.put(KEY_G2W2, group2week2);
        return db.update(DATABASE_TABLE, args,
                KEY_ROWID + "=" + rowId, null) > 0;
    }*/
}
