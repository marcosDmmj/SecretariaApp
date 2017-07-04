package net.simplifiedcoding.secretariaApp.activity;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.app.DialogFragment;
import android.app.Dialog;
import java.util.Date;

import android.widget.TimePicker;

import net.simplifiedcoding.insertintomysql.R;
import net.simplifiedcoding.secretariaApp.calendario.Util;

@SuppressLint("ValidFragment")
public class TimePickerFragmentStart extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    private Date initDate;
    public TimePickerFragmentStart(CharSequence text) {
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
        TextView tv = (TextView) getActivity().findViewById(R.id.txtHoraInicio);
        tv.setText(pad(hourOfDay)
                + ":" + pad(minute));

        TextView txtHoraFim = (TextView) getActivity().findViewById(R.id.txtHoraFim);
        if(hourOfDay == 23) {
            txtHoraFim.setText(pad(0)
                    + ":" + pad(minute));
        } else {
            txtHoraFim.setText(pad(hourOfDay + 1)
                    + ":" + pad(minute));
        }
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
}
