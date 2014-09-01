package com.ridgway.timetrialtracker;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by ridgway on 8/31/14.
 */
public class TTLapCursorAdapter extends CursorAdapter {

    public TTLapCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.listview_lap_layout, parent, false);

        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // here we are setting our data
        // that means, take the data from the cursor and put it in views

        TextView textViewRiderNum = (TextView) view.findViewById(R.id.lap_rider_id);
        textViewRiderNum.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))));

        TextView textViewRiderName = (TextView) view.findViewById(R.id.lap_rider_name);
        textViewRiderName.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));

        TextView textViewRiderSplit = (TextView) view.findViewById(R.id.lap_rider_split);
        String strSplit = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2)));
        if(strSplit == null || strSplit.isEmpty()){ strSplit = "0"; }
        TimeString tsRiderSplit = Utils.floatToTimeString(Float.parseFloat(strSplit));
        textViewRiderSplit.setText(tsRiderSplit.getCurrentElapsedTime());

    }


}
