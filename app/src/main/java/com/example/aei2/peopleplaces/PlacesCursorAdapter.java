package com.example.aei2.peopleplaces;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.aei2.peopleplaces.dao.PeopleLocationContract.LocationEntry;

/**
 * Created by aei2 on 2/19/2017.
 */

public class PlacesCursorAdapter extends CursorAdapter {
    public PlacesCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView addressTextView = (TextView) view.findViewById(R.id.address);
        TextView fipIDTextView = (TextView) view.findViewById(R.id.fipID);

        int addressColumnIndex = cursor.getColumnIndex(LocationEntry.ADDRESS);
        int fipIDColumnIndex = cursor.getColumnIndex(LocationEntry.BLOCKFIPS);

        String address = cursor.getString(addressColumnIndex);
        String fipID = cursor.getString(fipIDColumnIndex);
        if(TextUtils.isEmpty(address)) {
            address = context.getString(R.string.unknown_address);
        }
        addressTextView.setText(address);
        fipIDTextView.setText(fipID);
    }
}
