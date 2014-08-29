package com.ridgway.timetrialtracker;

/**
 * Created by ridgway on 7/27/14.
 */
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


public class TTCursorAdapter extends CursorAdapter {

    // used to keep selected position in ListView
    private int selectedPos = -1;	// init value for not-selected


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
        textViewRiderNum.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))));

        TextView textViewRiderSplit = (TextView) view.findViewById(R.id.rider_name);
        textViewRiderSplit.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));

        TextView textViewRiderLast = (TextView) view.findViewById(R.id.rider_last_seen);
        textViewRiderLast.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2))));

        TextView textViewRiderETA = (TextView) view.findViewById(R.id.rider_eta);
        textViewRiderETA.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(3))));
    }

    public void setSelectedPosition(int pos){
        selectedPos = pos;
        // inform the view of this change
        notifyDataSetChanged();
    }

    public int getSelectedPosition(){
        return selectedPos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        // only inflate the view if it's null
        if (v == null) {
            LayoutInflater vi
                    = (LayoutInflater)v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.listview_oneline_layout, null);
        }

        // get text view
        LinearLayout label = (LinearLayout)v.findViewById(R.id.rider_layout);

        // change the row color based on selected state
        if(selectedPos == position){
            label.setBackgroundColor(Color.CYAN);
        }else{
            label.setBackgroundColor(Color.WHITE);
        }

        return(v);
    }

}