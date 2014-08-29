package com.ridgway.timetrialtracker;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by ridgway on 8/27/14.
 */
public class TTRiderCursorAdapter extends CursorAdapter {

    public TTRiderCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.listview_rider_layout, parent, false);

        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // here we are setting our data
        // that means, take the data from the cursor and put it in views

        TextView textViewRiderNum = (TextView) view.findViewById(R.id.add_rider_number);
        textViewRiderNum.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))));

        TextView textViewRiderName = (TextView) view.findViewById(R.id.add_rider_name);
        textViewRiderName.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));

    }


}
