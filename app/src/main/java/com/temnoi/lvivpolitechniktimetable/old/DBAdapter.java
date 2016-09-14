package com.temnoi.lvivpolitechniktimetable.old;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Volodia on 16.09.2014.
 */
public class DBAdapter extends SQLiteOpenHelper {
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
    private static final String DATABASE_CREATE = "CREATE TABLE " + DATABASE_TABLE + " ("
            + KEY_ROWID + " INTEGER PRIMARY KEY," + KEY_DAY + " TEXT,"
            + KEY_LESS_NUM + " TEXT," + KEY_G1W1 + " TEXT,"
            + KEY_G2W1 + " TEXT," + KEY_G1W2 + " TEXT," + KEY_G2W2 + " TEXT)";
    private SQLiteDatabase db;

    public DBAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }

    public void clear() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
        db.close();
    }

    // Adding new contact
    public void addLesson(Lesson lesson) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DAY, lesson.getDay());
        initialValues.put(KEY_LESS_NUM, lesson.getLessonNumber());
        initialValues.put(KEY_G1W1, lesson.getGroup1Week1());
        initialValues.put(KEY_G2W1, lesson.getGroup2Week1());
        initialValues.put(KEY_G1W2, lesson.getGroup1Week2());
        initialValues.put(KEY_G2W2, lesson.getGroup2Week2());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(DATABASE_TABLE, null, initialValues);
        db.close();
    }

    // Getting single contact
    public Lesson getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DATABASE_NAME, new String[]{
                        KEY_ROWID, KEY_DAY, KEY_LESS_NUM, KEY_G1W1, KEY_G1W2, KEY_G2W1, KEY_G2W2},
                KEY_ROWID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Lesson contact = new Lesson(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),
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
                new String[]{String.valueOf(lesson.getID())});
    }

    // Deleting single contact
    public void deleteContact(Lesson contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE, KEY_ROWID + " = ?",
                new String[]{String.valueOf(contact.getID())});
        db.close();
    }
}
