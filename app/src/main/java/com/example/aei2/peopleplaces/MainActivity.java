package com.example.aei2.peopleplaces;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.FloatingActionButton;

import com.example.aei2.peopleplaces.dao.PeopleLocationContract.LocationEntry;
import com.example.aei2.peopleplaces.dto.PeopleDTO;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.
        ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, LoaderCallbacks<PeopleDTO>, SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String DESC_LABEL = "Annual Household Income  U.S. Census Data 2014";
    public static final String US_FCC_URL = "https://www.broadbandmap.gov/";
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int PEOPLE_LOADER_ID = 1;
    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    public final static int MILLISECONDS_PER_SECOND = 1000;
    public final static int MINUTE = 60 * MILLISECONDS_PER_SECOND;
    private double longitude;
    private double latitude;
    private TextView lblLongitudeValue;
    private TextView lblLatitudeValue;
    private boolean paused = false;
    private Button btnPause;
    PieChart pieChart;
    private String latStr;
    private String lngStr;
    private TextView blockID;
    private String highSchoolGrad;
    private String collegeGrad;
    private PeopleDTO postDTO;
    private static PeopleDTO transferDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Setup FAB to open CatalogActivity.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postDTO != null) {
                    saveLocation();
                }
                Intent intent = new Intent(MainActivity.this, CatalogActivity.class);
                startActivity(intent);
            }
        });
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        // Initialize the location request with accuracy, frequency of GPS updates.
        locationRequest = new LocationRequest();
        locationRequest.setInterval(MINUTE);
        locationRequest.setFastestInterval(15 * MILLISECONDS_PER_SECOND);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        lblLongitudeValue = (TextView) findViewById(R.id.lblLongitudeValue);
        lblLatitudeValue = (TextView) findViewById(R.id.lblLatitudeValue);
        btnPause = (Button) findViewById(R.id.btnPause);
        // Initialize pref change listener.
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        // Check if data is new or coming from db.
        if (transferDTO == null) {
            // Check network connectivity status.
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            // Get details on currently active default data network.
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                // Initalize the loader.
                LoaderManager loaderManager = getLoaderManager();
                loaderManager.initLoader(PEOPLE_LOADER_ID, null, this);
                View loadingIndicator = findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.VISIBLE);
            } else {
                // No connection, hide the loading indicator.
                Toast.makeText(this, R.string.internet_unavailable, Toast.LENGTH_LONG).show();
            }
        }
        // Intialize FIP textView.
        blockID = (TextView) findViewById(R.id.lblBlockIDValue);
        // Initialize the Pie Chart.
        pieChartInit();
    }

    // Method to insert into LOCATION table.
    private void saveLocation() {
        // Collection to hold location data to insert into db.
        ContentValues locationValues = new ContentValues();
        locationValues.put(LocationEntry.BLOCKFIPS, postDTO.getBlockFips());
        locationValues.put(LocationEntry.ADDRESS, postDTO.getAddress());
        locationValues.put(LocationEntry.BLOCKFIPS, postDTO.getBlockFips());
        locationValues.put(LocationEntry.MEDIAN, postDTO.getMedianIncome());
        locationValues.put(LocationEntry.RANGE_ZERO, postDTO.getIncomeBelowPoverty());
        locationValues.put(LocationEntry.RANGE_ONE, postDTO.getIncomeLessThan25());
        locationValues.put(LocationEntry.RANGE_TWO, postDTO.getIncomeBetween25to50());
        locationValues.put(LocationEntry.RANGE_THREE, postDTO.getIncomeBetween50to100());
        locationValues.put(LocationEntry.RANGE_FOUR, postDTO.getIncomeBetween100to200());
        locationValues.put(LocationEntry.RANGE_FIVE, postDTO.getIncomeGreater200());
        locationValues.put(LocationEntry.EDU_HS, postDTO.getEducationHighSchoolGraduate());
        locationValues.put(LocationEntry.EDU_COLLEGE, postDTO.getEducationBachelorOrGreater());
        // Insert into LOCATION table via the provider, returning the content URI.
        Uri newUri = getContentResolver().insert(LocationEntry.CONTENT_URI, locationValues);
        // Show a toast about insertion status.
        if (newUri == null) {
            Toast.makeText(MainActivity.this, getString(R.string.insert_location_failed),
                    Toast.LENGTH_LONG).show();
        } else {
            String cacheId = newUri.getLastPathSegment();
            postDTO.setCacheID(cacheId);
            Toast.makeText(MainActivity.this, getString(R.string.insert_location_success),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void pieChartInit() {
        Log.d(LOG_TAG, "onCreate: starting to create chart");
        Description description = new Description();
        description.setText(DESC_LABEL);
        pieChart = (PieChart) findViewById(R.id.idPieChart);
        pieChart.setDescription(description);
        pieChart.setRotationEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterTextSize(10);
    }

    protected void addDataSet(PeopleDTO peopleDTO) {
        Log.d(LOG_TAG, "addDataSet method started");
        // Refresh chart before loading new DTO.
        pieChart.invalidate();
        // Education level data set
        highSchoolGrad = String.format("%.1f", (peopleDTO.getEducationHighSchoolGraduate()) * 100);
        collegeGrad = String.format("%.1f", (peopleDTO.getEducationBachelorOrGreater()) * 100);
        // Pie chart center text set
        int medianIncome = peopleDTO.getMedianIncome();
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        String median = formatter.format(medianIncome);
        pieChart.setCenterText("Median\n$" + median);
        // General data set
        String[] xData = {"< Poverty", "< 25k", "25-50k", "50-100k", "100-200k", "200k+"};
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) peopleDTO.getIncomeBelowPoverty(), xData[0]));
        entries.add(new PieEntry((float) peopleDTO.getIncomeLessThan25(), xData[1]));
        entries.add(new PieEntry((float) peopleDTO.getIncomeBetween25to50(), xData[2]));
        entries.add(new PieEntry((float) peopleDTO.getIncomeBetween50to100(), xData[3]));
        entries.add(new PieEntry((float) peopleDTO.getIncomeBetween100to200(), xData[4]));
        entries.add(new PieEntry((float) peopleDTO.getIncomeGreater200(), xData[5]));
        // Create the data set object.
        PieDataSet pieDataSet = new PieDataSet(entries, "($ Thousands)");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);
        pieDataSet.setValueFormatter(new PercentFormatter());
        // Add colors to data set.
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GRAY);
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.CYAN);
        colors.add(Color.YELLOW);
        colors.add(Color.MAGENTA);
        pieDataSet.setColors(colors);
        // Add a legend to the chart.
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        // Create the pie data object.
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    public void btnPauseClicked(View view) {
        if (paused == false) {
            // We are un-paused, we want to pause.
            pauseGPS();
            paused = true;
            Toast.makeText(this, "Paused", Toast.LENGTH_LONG).show();
            btnPause.setText(getString(R.string.lblResume));
        } else {
            // We are paused, we want to un-pause.
            resumeGPS();
            paused = false;
            Toast.makeText(this, "Resumed", Toast.LENGTH_LONG).show();
            btnPause.setText(getString(R.string.lblPause));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationProvider.requestLocationUpdates(googleApiClient,
                locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();

    }

    protected static void transferDTO(PeopleDTO selectDTO) {
        MainActivity.transferDTO = selectDTO;
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeGPS();
        // Fetch db DTO.
        if (transferDTO != null) {
            addDataSet(transferDTO);
            // Set FIP TextView to the new FIP# from db.
            blockID.setText(transferDTO.getBlockFips());
        }
    }

    private void resumeGPS() {
        if (googleApiClient.isConnected()) {
            requestLocationUpdates();
            // Refresh data.
            getLoaderManager().restartLoader(PEOPLE_LOADER_ID, null, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseGPS();
    }

    private PendingResult<Status> pauseGPS() {
        return locationProvider.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        // Location changed, get geo here.
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        saveGPSFields(latitude, longitude);
    }

    private void saveGPSFields(double latitude, double longitude) {
        latStr = Double.toString(latitude);
        lngStr = Double.toString(longitude);
        lblLatitudeValue.setText(latStr);
        lblLongitudeValue.setText(lngStr);
        if (latitude != 0.0 && longitude != 0.0) {
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.settings_latitude_key), latStr);
            editor.putString(getString(R.string.settings_longitude_key), lngStr);
            editor.commit();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        // If saved data is displayed no need to update new location.
        // Manual refresh if user chooses.
        if (transferDTO == null) {
            if (key.equals(getString(R.string.settings_latitude_key)) || key.equals(getString(R.string.settings_longitude_key))) {
                getLoaderManager().restartLoader(PEOPLE_LOADER_ID, null, this);
            }
        }
    }

    @Override
    public Loader<PeopleDTO> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPrefs = this.getPreferences(Context.MODE_PRIVATE);
        String lat = sharedPrefs.getString(
                getString(R.string.settings_latitude_key),
                getString(R.string.settings_latitude_default));
        String lng = sharedPrefs.getString(
                getString(R.string.settings_longitude_key),
                getString(R.string.settings_longitude_default));

        return new PeopleLoader(this, lat, lng);
    }

    @Override
    public void onLoadFinished(Loader<PeopleDTO> loader, PeopleDTO peopleDTO) {
        // Hide the loading indicator.
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        // Load data results to chart.
        if (peopleDTO != null) {
            addDataSet(peopleDTO);
            postDTO = peopleDTO;
            // Fill in FIP#.
            blockID.setText(peopleDTO.getBlockFips());
        }
    }


    @Override
    public void onLoaderReset(Loader<PeopleDTO> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_saved:
                // Navigate to the Saved screen.
                Intent intent = new Intent(MainActivity.this, CatalogActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_map:
                openPreferredLocationMap();
                return true;
            case R.id.education:
                showEducation();
                return true;
            case R.id.action_web:
                Uri webUrl = Uri.parse(US_FCC_URL);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webUrl);
                startActivity(webIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showEducation() {
        if (highSchoolGrad != null || collegeGrad != null) {
            Toast.makeText(this, "High School Graduate: " + highSchoolGrad + "%\n"
                            + "Bachelor Degree (+): " + collegeGrad + "%",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void openPreferredLocationMap() {
        Uri geoLocation = Uri.parse("geo:" + latStr + "," + lngStr);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        // Verify states ie. resolveActivity before startActivity.
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(LOG_TAG, "Cannot map geo location, no receiving apps.");
        }
    }

}
