package com.example.aei2.peopleplaces.dao;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by aei2 on 2/18/2017.
 */

public class PeopleLocationContract {

    // private constructor for security
    private PeopleLocationContract() {
    }

    /**
     * Content Authority using package name
     */
    public static final String CONTENT_AUTHORITY = "com.example.aei2.peopleplaces";
    /**
     * Base URI for contacting content provider
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * Path appended to base Content URI for possible URIs ie. content://package/location
     */
    public static final String PATH_LOCATION = "location";


    /**
     * Inner class for constant values for location db table
     */
    public static final class LocationEntry implements BaseColumns {
        /**
         * Content URI to access people data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_LOCATION);
        /**
         * MIME type CONTENT_URI for a list of people
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        /**
         * MIME type CONTENT_URI for a single people row
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        /**
         * DB table name
         */
        public static final String TABLE_NAME = "location";
        /**
         * Unique primary ID Type:INTEGER
         */
        public static final String _ID = BaseColumns._ID;
        /**
         * Column blockfips Type:INTEGER
         */
        public static final String BLOCKFIPS = "blockfips";
        /**
         * Column address Type:TEXT
         */
        public static final String ADDRESS = "address";

        /**
         * Column median Type:INTEGER
         */
        public static final String MEDIAN = "median";
        /**
         * Column range_zero Type:REAL
         */
        public static final String RANGE_ZERO = "range_zero";
        /**
         * Column range_one Type:REAL
         */
        public static final String RANGE_ONE = "range_one";
        /**
         * Column range_two Type:REAL
         */
        public static final String RANGE_TWO = "range_two";
        /**
         * Column range_three Type:REAL
         */
        public static final String RANGE_THREE = "range_three";
        /**
         * Column range_four Type:REAL
         */
        public static final String RANGE_FOUR = "range_four";
        /**
         * Column range_five Type:REAL
         */
        public static final String RANGE_FIVE = "range_five";
        /**
         * Column edu_hs Type:REAL
         */
        public static final String EDU_HS = "edu_hs";
        /**
         * Column edu_college Type:REAL
         */
        public static final String EDU_COLLEGE = "edu_college";
    }
}
