package com.mandalalabs.chirp.fragment;

import android.app.Dialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {
    private static final String TAG = "NYTSearch";
    Calendar beginDate;

    public void setBeginDate(Calendar beginDate) {
        this.beginDate = beginDate;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        if (beginDate == null)
            beginDate = Calendar.getInstance();

        // Activity needs to implement this interface
        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getActivity();

        return new DatePickerDialog(getActivity(),
                listener,
                beginDate.get(Calendar.YEAR),
                beginDate.get(Calendar.MONTH),
                beginDate.get(Calendar.DAY_OF_MONTH));
    }
}