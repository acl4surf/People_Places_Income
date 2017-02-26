package com.example.aei2.peopleplaces.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.aei2.peopleplaces.dao.PeopleLocationContract.LocationEntry;


/**
 * Created by aei2 on 2/17/2017.
 */

public class PeoplePlacesDb extends SQLiteOpenHelper{
    /**
     *  Name of the database file
     */
    private static final String DATABASE_NAME = "peopleplaces.db";
    /**
     * Database version. Increment database version if database schema is modified.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructor {@link PeoplePlacesDb}
     */
    public PeoplePlacesDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Method called only once, add other tables inside this method
        String createLocation = "CREATE TABLE " + LocationEntry.TABLE_NAME + " ( "
                + LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + LocationEntry.BLOCKFIPS + " TEXT, "
                + LocationEntry.ADDRESS + " TEXT, "
                + LocationEntry.MEDIAN + " INTEGER, "
                + LocationEntry.RANGE_ZERO + " REAL, "
                + LocationEntry.RANGE_ONE + " REAL, "
                + LocationEntry.RANGE_TWO + " REAL, "
                + LocationEntry.RANGE_THREE + " REAL, "
                + LocationEntry.RANGE_FOUR + " REAL, "
                + LocationEntry.RANGE_FIVE + " REAL, "
                + LocationEntry.EDU_HS + " REAL, "
                + LocationEntry.EDU_COLLEGE + " REAL "+ " );";
        db.execSQL(createLocation);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
