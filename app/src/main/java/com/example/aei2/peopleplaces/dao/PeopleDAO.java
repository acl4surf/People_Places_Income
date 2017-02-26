package com.example.aei2.peopleplaces.dao;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.aei2.peopleplaces.BuildConfig;
import com.example.aei2.peopleplaces.dto.PeopleDTO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aei2 on 2/2/2017.
 */

public class PeopleDAO {
    private final NetworkDAO networkDAO;
    AddressDAO addressDAO = new AddressDAO();

    // constructor, initialize NetworkDAO
    public PeopleDAO() {
        networkDAO = new NetworkDAO();
    }

    /**
     * URL for Census data from broadbandmap.gov
     */
    private static final String NBM_REQUEST_URL =
            "https://www.broadbandmap.gov/broadbandmap/demographic/2014/coordinates?";

    /**
     * Perform the network request given geo, parse the response, and extract data
     * TODO abstract refactor into super class
     *
     * @param latitude
     * @param longitude
     * @return List<PeopleDTO> object populated with json data from web
     */
    public PeopleDTO fetchPeopleData(String latitude, String longitude) {
        Uri baseUri = Uri.parse(NBM_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("latitude", latitude);
        uriBuilder.appendQueryParameter("longitude", longitude);
        uriBuilder.appendQueryParameter("format", "json");
        Log.v("Uri Modified", uriBuilder.toString());
        String uri = uriBuilder.toString();
        // Receive FCC JSON data
        String jsonResponse = networkDAO.request(uri);
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        // fetch address string
        String addressResult =  addressDAO.fetchAddress(latitude, longitude);
        // create a PeopleDTO object to populate w JSON data
        PeopleDTO peopleDTO = new PeopleDTO();
        peopleDTO.setLatitude(latitude);
        peopleDTO.setLongitude(longitude);
        if (!TextUtils.isEmpty(addressResult)) {
            peopleDTO.setAddress(addressResult);
        }
        // parse the entire JSON string
        try {
            // Parse FCC json string
            JSONObject root = new JSONObject(jsonResponse);
            JSONObject results = root.getJSONObject("Results");
            long blockFips = results.getLong("blockFips");
            double incomeBelowPoverty = results.getDouble("incomeBelowPoverty");
            int medianIncome = results.getInt("medianIncome");
            double incomeLessThan25 = results.getDouble("incomeLessThan25");
            double incomeBetween25to50 = results.getDouble("incomeBetween25to50");
            double incomeBetween50to100 = results.getDouble("incomeBetween50to100");
            double incomeBetween100to200 = results.getDouble("incomeBetween100to200");
            double incomeGreater200 = results.getDouble("incomeGreater200");
            double educationHighSchoolGraduate = results.getDouble("educationHighSchoolGraduate");
            double educationBachelorOrGreater = results.getDouble("educationBachelorOrGreater");
            // Populate the PeopleDTO object
            peopleDTO.setBlockFips(String.valueOf(blockFips));
            peopleDTO.setIncomeBelowPoverty(incomeBelowPoverty);
            peopleDTO.setMedianIncome(medianIncome);
            peopleDTO.setIncomeLessThan25(incomeLessThan25);
            peopleDTO.setIncomeBetween25to50(incomeBetween25to50);
            peopleDTO.setIncomeBetween50to100(incomeBetween50to100);
            peopleDTO.setIncomeBetween100to200(incomeBetween100to200);
            peopleDTO.setIncomeGreater200(incomeGreater200);
            peopleDTO.setEducationHighSchoolGraduate(educationHighSchoolGraduate);
            peopleDTO.setEducationBachelorOrGreater(educationBachelorOrGreater);
        } catch (JSONException e) {
            Log.e("PeopleDAO", "Problem parsing the FCC JSON results", e);
        }
        return peopleDTO;
    }
}
