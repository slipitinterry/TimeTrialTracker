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
public class TTRiderStatsCursorAdapter extends CursorAdapter {

    public TTRiderStatsCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.listview_rider_stats_layout, parent, false);

        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // here we are setting our data
        // that means, take the data from the cursor and put it in views

        TextView textViewRiderNum = (TextView) view.findViewById(R.id.rider_number);
        textViewRiderNum.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))));

        TextView textViewRiderName = (TextView) view.findViewById(R.id.rider_name);
        textViewRiderName.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));

        TextView textViewRiderAvgLap = (TextView) view.findViewById(R.id.rider_avglap);
        String strAvgLap = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(4)));
        if(strAvgLap == null || strAvgLap.isEmpty()){ strAvgLap = "0"; }
        TimeString tsAvgLap = Utils.floatToTimeString(Float.parseFloat(strAvgLap));
        textViewRiderAvgLap.setText(tsAvgLap.getCurrentElapsedTime());

        TextView textViewRiderStdDev = (TextView) view.findViewById(R.id.rider_std_dev);
        textViewRiderStdDev.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(5))));

    }


}
