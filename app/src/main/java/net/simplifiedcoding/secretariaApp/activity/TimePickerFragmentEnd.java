package net.simplifiedcoding.secretariaApp.activity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import net.simplifiedcoding.secretariaApp.R;
import net.simplifiedcoding.secretariaApp.calendario.Util;

import java.util.Date;

public class TimePickerFragmentEnd extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    private Date initDate;
    public TimePickerFragmentEnd(CharSequence text) {
        initDate = Util.stringHourToDate(text.toString());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        int hour = initDate.getHours();
        int minute = initDate.getMinutes();

        return new TimePickerDialog(getActivity(),this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        TextView tv = (TextView) getActivity().findViewById(R.id.txtHoraFim);
        tv.setText(pad(hourOfDay)
                + ":" + pad(minute));
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
}
