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
    private Context context;


    public TTCursorAdapter(Context context, Cursor c) {
        super(context, c);
        this.context = context;
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
        View view = convertView;

        // only inflate the view if it's null
        if (view == null) {
            LayoutInflater vi
                    = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.listview_oneline_layout, null);
        }

       Cursor cursor = (Cursor) getItem(position);

       TextView textViewRiderNum = (TextView) view.findViewById(R.id.rider_number);
       textViewRiderNum.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))));

       TextView textViewRiderSplit = (TextView) view.findViewById(R.id.rider_name);
       textViewRiderSplit.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));

       TextView textViewRiderLast = (TextView) view.findViewById(R.id.rider_last_seen);
       String strLast = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2)));
       if(strLast == null || strLast.isEmpty()){ strLast = "0"; }
       TimeString tsRiderLast = Utils.floatToTimeString(Float.parseFloat(strLast));
       textViewRiderLast.setText(tsRiderLast.getCurrentElapsedTime());

       TextView textViewRiderETA = (TextView) view.findViewById(R.id.rider_eta);
       String strETA = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(3)));
       if(strETA == null || strETA.isEmpty()){ strETA = "0"; }
       TimeString tsRiderETA = Utils.floatToTimeString(Float.parseFloat(strETA));
       textViewRiderETA.setText(tsRiderETA.getCurrentElapsedTime());

       // get text view
       LinearLayout rider = (LinearLayout)view.findViewById(R.id.rider_layout);
       LinearLayout data = (LinearLayout)view.findViewById(R.id.rider_data);

        // change the row color based on selected state
        if(selectedPos == position){
            rider.setBackgroundColor(Color.CYAN);
            data.setBackgroundColor(Color.CYAN);
        }else{
            rider.setBackgroundColor(Color.WHITE);
            data.setBackgroundColor(Color.WHITE);
        }

        return(view);
    }

}