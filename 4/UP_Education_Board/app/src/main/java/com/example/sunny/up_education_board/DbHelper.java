package com.example.sunny.up_education_board;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Sunny on 4/20/2017.
 */

public class DbHelper extends SQLiteOpenHelper {
    public static final String TAG = DbHelper.class.getSimpleName();
    public static final String DB_NAME = "myapp.db";
    public static final int DB_VERSION = 1;

    public static final String USER_TABLE = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ADMIN = "identity";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASS = "password";
    /*
    create table users(
        id integer primary key autoincrement,
        email text,
        password text);
     */
    public static final String CREATE_TABLE_USERS = "CREATE TABLE " + USER_TABLE + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_ADMIN + " TEXT,"
            + COLUMN_EMAIL + " TEXT,"
            + COLUMN_PASS + " TEXT );";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */

    public void addUser(String identity , String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASS, password);
        values.put(COLUMN_ADMIN, identity);
        long id = db.insert(USER_TABLE, null, values);
        db.close();

        Log.d(TAG, "user inserted" + id);
    }

    public boolean getUser(String identy){
        //HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "select * from  " + USER_TABLE + " where " +
                COLUMN_ADMIN+ " = " + "'"+identy +"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            //cursor.getString(0).toString() ;
           ;
            LoginActivity.sname = cursor.getString(2).toString();
            LoginActivity.schoolcode =  cursor.getString(3).toString();

            return true;
        }
        cursor.close();
        db.close();

        return false;
    }
}
