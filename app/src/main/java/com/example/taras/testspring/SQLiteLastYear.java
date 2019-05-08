package com.example.taras.testspring;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteLastYear extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "declarationsDBLastYear";
    public static final String TABLE_NAME = "declaretionslastYear";

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_SURNAME = "surname";
    public static final String KEY_FATHERNAME = "fathername";
    public static final String KEY_SALARY = "salary";
    public static final String KEY_REALTYTOTAL = "realtyTotal";
    public static final String KEY_MOVABLESWITHOUTCARSTOTAL = "movablesWithoutCarsTotal";
    public static final String KEY_MOVABLESCARSTOTAL = "movablesCarsTotal";
    public static final String KEY_PAPERTOTAL = "paperTotal";
    public static final String KEY_INCOMETOTAL = "incomeTotal";
    public static final String KEY_PRICEOFALLPROPERTY = "priceOfAllProperty";
    public static final String KEY_SALARYCOF = "salaryCof";
    public static final String KEY_INCOMECOF = "incomeCof";


    public SQLiteLastYear(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_NAME + " TEXT NOT NULL, " +//
                KEY_SURNAME + " TEXT NOT NULL, " +//
                KEY_FATHERNAME + " TEXT NOT NULL, " +//
                KEY_SALARY + " INTEGER NOT NULL, " +//
                KEY_REALTYTOTAL + " INTEGER NOT NULL, " +//
                KEY_MOVABLESWITHOUTCARSTOTAL + " INTEGER NOT NULL, " +//
                KEY_MOVABLESCARSTOTAL + " INTEGER NOT NULL, " +//
                KEY_PAPERTOTAL + " INTEGER NOT NULL, " +//
                KEY_INCOMETOTAL + " INTEGER NOT NULL, " +//
                KEY_PRICEOFALLPROPERTY + " INTEGER NOT NULL, " +//
                KEY_SALARYCOF + " REAL NOT NULL, " +//
                KEY_INCOMECOF + " REAL NOT NULL" +//
                ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }



}