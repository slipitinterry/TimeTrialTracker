package com.ridgway.timetrialtracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 *
 */
public class TTAddRiderNameFragment extends DialogFragment {

    // Use this instance of the interface to deliver action events
    AddRiderNameFragmentListener mListener;
    private final DialogFragment dialogFrag = this;
    private AlertDialog innerDlg;
    private EditText riderName = null;

    public AlertDialog getInnerAlertDialog(){ return innerDlg; }
    public EditText getRiderName() { return riderName; }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface AddRiderNameFragmentListener {
        public void onDialogPositiveClick(DialogFragment dialog, EditText riderName);
        public void onDialogNeutralClick(DialogFragment dialog, EditText riderName);
        public void onDialogNegativeClick(DialogFragment dialog);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_add_rider_name, null);
        builder.setView(view);
        builder.setTitle(R.string.add_rider_title);

        riderName = (EditText) view.findViewById(R.id.editRiderName);

        builder.setPositiveButton(R.string.dlg_add, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Add Rider to the database!
                ((TTAddRider) getActivity()).onDialogPositiveClick(dialogFrag, riderName);
            }
        });

        builder.setNeutralButton(R.string.dlg_next, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Add Rider to the database!
                ((TTAddRider) getActivity()).onDialogNeutralClick(dialogFrag, riderName);
            }
        });

        builder.setNegativeButton(R.string.dlg_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                ((TTAddRider) getActivity()).onDialogNegativeClick(dialogFrag);
            }
        });
        // Create the AlertDialog object and return it
        innerDlg = builder.create();
        return innerDlg;

    }


}
