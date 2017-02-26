package com.example.aei2.peopleplaces.dao;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.aei2.peopleplaces.dao.PeopleLocationContract.LocationEntry;

/**
 * Created by aei2 on 2/18/2017.
 */

public class PeoplePlacesProvider extends ContentProvider {
    public static final String LOG_TAG = PeoplePlacesProvider.class.getSimpleName();
    /**
     * URI matcher code for content URI for LOCATION table
     */
    private static final int LOCATION = 100;
    /**
     * URI matcher code for content URI for a single row in the LOCATION table
     */
    private static final int LOCATION_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //Static initializer. This is run the first time anything is called from this class.
    static {
        //Multiple rows location
        sUriMatcher.addURI(PeopleLocationContract.CONTENT_AUTHORITY,
                PeopleLocationContract.PATH_LOCATION, LOCATION);
        //Single row # placeholder for a number
        sUriMatcher.addURI(PeopleLocationContract.CONTENT_AUTHORITY,
                PeopleLocationContract.PATH_LOCATION + "/#", LOCATION_ID);

    }

    /**
     * Initialize the db helper object
     */
    private PeoplePlacesDb mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new PeoplePlacesDb(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        // cursor to hold results of the query
        Cursor cursor;
        // URI matcher to match URI to returned code ie. set path ids
        int match = sUriMatcher.match(uri);
        switch (match) {
            case LOCATION:
                // query the location table
                cursor = database.query(LocationEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            case LOCATION_ID:
                // extract the ID from URI ie. selection "_id=?" selectionArgs int[int ?]
                selection = LocationEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // perform the query where _id equals row # to return a Cursor containing that row
                cursor = database.query(LocationEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("cannot query unknown uri " + uri);
        }
        // Set notif URI on the Cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LOCATION:
                return LocationEntry.CONTENT_LIST_TYPE;
            case LOCATION_ID:
                return LocationEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LOCATION:
                return insertLocation(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertLocation(Uri uri, ContentValues values) {
        // Get writable db object
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Insert the new location with content values form DTO
        long id = database.insert(LocationEntry.TABLE_NAME, LocationEntry.ADDRESS, values);
        // If the ID -1, insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Notify listeners data has changed for specific content URI
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LOCATION:
                rowsDeleted = database.delete(LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LOCATION_ID:
                selection = LocationEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if(rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
