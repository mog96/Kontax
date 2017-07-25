package com.mog.kontax.kontax;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by mateogarcia on 7/25/17.
 */

// Class for the custom "Take/Choose" dialof that allows a user to select the source for a
// new contact image.
public class SelectImageSourceDialogFragment extends DialogFragment {

    public interface SelectImageSourceDialogListener {
        void onTakePictureSelected();
        void onChoosePictureSelected();
    }

    SelectImageSourceDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        String[] dialogItems = {"Take", "Choose"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Photo Source?")
                .setItems(dialogItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            mListener.onTakePictureSelected();
                        } else {
                            mListener.onChoosePictureSelected();
                        }
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (SelectImageSourceDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
