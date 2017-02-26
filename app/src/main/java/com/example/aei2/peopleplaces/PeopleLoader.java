package com.example.aei2.peopleplaces;

import android.content.Context;
import android.content.AsyncTaskLoader;

import com.example.aei2.peopleplaces.dao.AddressDAO;
import com.example.aei2.peopleplaces.dao.PeopleDAO;
import com.example.aei2.peopleplaces.dao.PeopleDAOStub;
import com.example.aei2.peopleplaces.dto.PeopleDTO;

import org.json.JSONException;

import java.io.IOException;


/**
 * Loads People data using an AsyncTask to perform the
 * network request to the given URL.
 */
public class PeopleLoader extends AsyncTaskLoader{
    /** Tag for log messages */
    private static final String LOG_TAG = PeopleLoader.class.getName();
    /** Query fields*/
    private String mLatitude;
    private String mLongitude;
    // Networking objects
    PeopleDAO peopleDAO = new PeopleDAO();
    PeopleDTO peopleResult;

    /**
     * Constructs a new {@link PeopleLoader}.
     *
     * @param context of the activity
     * @param latitude
     * @param longitude
     */
    public PeopleLoader(Context context, String latitude, String longitude) {
        super(context);
        mLatitude = latitude;
        mLongitude = longitude;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public PeopleDTO loadInBackground() {
        if (mLatitude.equals("0.0") && mLongitude.equals("0.0")) {
            return null;
        }
        // Access DAO, perform network request, parse and fetch json data
        try {
            peopleResult = peopleDAO.fetchPeopleData(mLatitude, mLongitude);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return peopleResult;
    }
}
