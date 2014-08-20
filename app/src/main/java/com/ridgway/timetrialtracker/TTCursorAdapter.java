package com.ridgway.timetrialtracker;

/**
 * Created by ridgway on 7/27/14.
 */
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


public class TTCursorAdapter extends CursorAdapter {

    public TTCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.listview_oneline_layout, parent, false);

        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // here we are setting our data
        // that means, take the data from the cursor and put it in views

        TextView textViewRiderNum = (TextView) view.findViewById(R.id.rider_number);
        textViewRiderNum.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));

        TextView textViewRiderSplit = (TextView) view.findViewById(R.id.rider_time);
        textViewRiderSplit.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2))));
    }
}