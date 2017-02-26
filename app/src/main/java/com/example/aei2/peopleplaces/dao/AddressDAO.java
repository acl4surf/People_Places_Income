package com.example.aei2.peopleplaces.dao;

import android.net.Uri;
import android.util.Log;

import com.example.aei2.peopleplaces.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aei2 on 2/16/2017.
 */

public class AddressDAO {
    private final NetworkDAO networkDAO;

    // constructor, initialize NetworkDAO
    public AddressDAO() {
        networkDAO = new NetworkDAO();
    }

    /**
     * URL Google API for reverse geolocation
     */
    private static final String ADDRESS_REQUEST_URL =
            "https://maps.googleapis.com/maps/api/geocode/json?";
    private final String API_KEY = BuildConfig.MAP_API_KEY;

    /**
     * Method for fetching reverse geolocation, receive json response
     * @param latitude
     * @param longitude
     * @return String
     */
    public String fetchAddress(String latitude, String longitude) {
        Uri baseUri = Uri.parse(ADDRESS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("latlng", latitude+","+longitude);
        uriBuilder.appendQueryParameter("key", API_KEY);
        Log.v("UriMap Modified", uriBuilder.toString());
        String mapUri = uriBuilder.toString();
        String addressJsonStr = networkDAO.request(mapUri);
       // Log.v("testing AddressDAO: ", addressJsonStr);
        try {
            return getAddressFromJson(addressJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getAddressFromJson (String addressJsonStr) throws JSONException {
        // Names of JSON objects we need to extract
        final String FCC_RESULTS = "results";
        final String FCC_ADDRESS = "formatted_address";
        JSONObject addressJson = new JSONObject(addressJsonStr);
        JSONArray addressArray = addressJson.getJSONArray(FCC_RESULTS);
        JSONObject addressObject = addressArray.getJSONObject(0);
        String addressStr = addressObject.getString(FCC_ADDRESS);

        return addressStr;
    }
}
