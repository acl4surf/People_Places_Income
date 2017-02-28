package com.example.aei2.peopleplaces;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.aei2.peopleplaces.dao.PeopleLocationContract.LocationEntry;
import com.example.aei2.peopleplaces.dto.PeopleDTO;


public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private PeopleDTO selectDTO;
    private Uri mCurrentPlaceUri;
    private static final int EXISTING_LOCATION_LOADER = 0;
    private static final int CURRENT_PEOPLE_LOADER = 1;
    private static final String LOG_TAG = CatalogActivity.class.getSimpleName();
    PlacesCursorAdapter mCursorAdapter;
    private static final String APP_SHARE_HASHTAG = " #PeoplePlacesIncomeApp";
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        // Instantiate the list view.
        ListView catalogListView = (ListView) findViewById(R.id.list);
        registerForContextMenu(catalogListView);
        // Attach an empty view.
        View emptyView = findViewById(R.id.empty_view);
        catalogListView.setEmptyView(emptyView);
        // Initialize the cursor adapter.
        mCursorAdapter = new PlacesCursorAdapter(this, null);
        catalogListView.setAdapter(mCursorAdapter);
        catalogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, MainActivity.class);
                // Append id to Content URI for list item clicked.
                mCurrentPlaceUri = ContentUris.withAppendedId(LocationEntry.CONTENT_URI, id);
                // Initialize a loader to read from db table at selected location
                getLoaderManager().initLoader(CURRENT_PEOPLE_LOADER, null, CatalogActivity.this);
                startActivity(intent);
            }
        });
        // Initialize a loader to read from the location table.
        getLoaderManager().initLoader(EXISTING_LOCATION_LOADER, null, this);
    }

    /**
     * Context Menu
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.list) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete_item:
                // Fetch URI via id for a single row in the location table.
                Uri currentLocationUri = ContentUris
                        .withAppendedId(LocationEntry.CONTENT_URI, info.id);
                // Delete a row in db at the given Content URI.
                int lRowsDeleted = getContentResolver().delete(currentLocationUri, null, null);
                // Show toast on deletion status.
                if (lRowsDeleted == 0) {
                    Toast.makeText(this, getString(R.string.delete_address_failed),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, getString(R.string.delete_address_successful),
                            Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from xml.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        //retrieve the share menu item
        MenuItem item = menu.findItem(R.id.action_share);
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle selected menu item.
        switch (item.getItemId()) {
            case R.id.action_delete:
                // Method to delete all addresses
                deleteAll();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, APP_SHARE_HASHTAG);
        return shareIntent;
    }

    private void deleteAll() {
        int locRowsDeleted = getContentResolver().delete(LocationEntry.CONTENT_URI, null, null);
        Log.v(LOG_TAG, locRowsDeleted + " rows deleted from location table");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> loader = null;
        switch (id) {
            case 0:
                // Projection for columns to display saved addresses
                String[] projectionL = {LocationEntry._ID, LocationEntry.BLOCKFIPS, LocationEntry.ADDRESS};
                // Loader to execute ContentProvider's query method on a background thread
                loader = new CursorLoader(this, LocationEntry.CONTENT_URI, projectionL, null, null, null);
                break;
            case 1:
                // Projection for columns to get from db
                String[] projectionP = {LocationEntry._ID, LocationEntry.BLOCKFIPS, LocationEntry.MEDIAN,
                        LocationEntry.RANGE_ZERO, LocationEntry.RANGE_ONE, LocationEntry.RANGE_TWO,
                        LocationEntry.RANGE_THREE, LocationEntry.RANGE_FOUR, LocationEntry.RANGE_FIVE,
                        LocationEntry.EDU_HS, LocationEntry.EDU_COLLEGE};
                // Loader to execute ContentProvider's query method on a background thread
                loader = new CursorLoader(this, mCurrentPlaceUri, projectionP, null, null, null);
                break;
            default:
                break;
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Exit if cursor is null or -1 row in cursor.
        if (cursor == null || cursor.getCount() < 0) return;
        switch (loader.getId()) {
            case 0:
                // Get all addresses and fips from cursor and display in adapter.
                mCursorAdapter.swapCursor(cursor);
                break;
            case 1:
                // PeopleDTO to hold returned people data
                PeopleDTO selectDTO = new PeopleDTO();
                if (cursor.moveToFirst()) {
                    int fipsColumnIndex = cursor.getColumnIndex(LocationEntry.BLOCKFIPS);
                    int medianColumnIndex = cursor.getColumnIndex(LocationEntry.MEDIAN);
                    int zeroColumnIndex = cursor.getColumnIndex(LocationEntry.RANGE_ZERO);
                    int oneColumnIndex = cursor.getColumnIndex(LocationEntry.RANGE_ONE);
                    int twoColumnIndex = cursor.getColumnIndex(LocationEntry.RANGE_TWO);
                    int threeColumnIndex = cursor.getColumnIndex(LocationEntry.RANGE_THREE);
                    int fourColumnIndex = cursor.getColumnIndex(LocationEntry.RANGE_FOUR);
                    int fiveColumnIndex = cursor.getColumnIndex(LocationEntry.RANGE_FIVE);
                    int hsColumnIndex = cursor.getColumnIndex(LocationEntry.EDU_HS);
                    int collegeColumnIndex = cursor.getColumnIndex(LocationEntry.EDU_COLLEGE);
                    // Extract values.
                    String blockFips = cursor.getString(fipsColumnIndex);
                    int median = cursor.getInt(medianColumnIndex);
                    float zero = cursor.getFloat(zeroColumnIndex);
                    float one = cursor.getFloat(oneColumnIndex);
                    float two = cursor.getFloat(twoColumnIndex);
                    float three = cursor.getFloat(threeColumnIndex);
                    float four = cursor.getFloat(fourColumnIndex);
                    float five = cursor.getFloat(fiveColumnIndex);
                    float hs = cursor.getFloat(hsColumnIndex);
                    float college = cursor.getFloat(collegeColumnIndex);
                    // Set DTO values.
                    // Populate the PeopleDTO object.
                    selectDTO.setBlockFips(blockFips);
                    selectDTO.setIncomeBelowPoverty(zero);
                    selectDTO.setMedianIncome(median);
                    selectDTO.setIncomeLessThan25(one);
                    selectDTO.setIncomeBetween25to50(two);
                    selectDTO.setIncomeBetween50to100(three);
                    selectDTO.setIncomeBetween100to200(four);
                    selectDTO.setIncomeGreater200(five);
                    selectDTO.setEducationHighSchoolGraduate(hs);
                    selectDTO.setEducationBachelorOrGreater(college);
                    if (selectDTO != null) {
                        MainActivity.transferDTO(selectDTO);
                        this.selectDTO = selectDTO;
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
