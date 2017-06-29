package net.simplifiedcoding.secretariaApp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import net.simplifiedcoding.insertintomysql.R;
import net.simplifiedcoding.secretariaApp.calendario.EventObjects;
import net.simplifiedcoding.secretariaApp.calendario.Util;
import net.simplifiedcoding.secretariaApp.webservice.DownloadEventByDate;
import net.simplifiedcoding.secretariaApp.webservice.DownloadEvents;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class DayCalendarActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ImageView previousDay;
    private ImageView nextDay;
    private TextView currentDate;
    private RelativeLayout mLayout;
    private int eventIndex;
    private String date;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_calendar);
        Intent intent = getIntent();
        date = intent.getStringExtra("Date");
        mLayout = (RelativeLayout) findViewById(R.id.left_event_column);
        eventIndex = mLayout.getChildCount();
        currentDate = (TextView) findViewById(R.id.display_current_date);
        currentDate.setText(Util.stringToStringDayWeek(date));
        displayDailyEvents();
        previousDay = (ImageView) findViewById(R.id.previous_day);
        nextDay = (ImageView) findViewById(R.id.next_day);
        previousDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousCalendarDate();
            }
        });
        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextCalendarDate();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void previousCalendarDate() {
        mLayout.removeViewAt(eventIndex - 1);

        date = Util.dateToString(Util.changeDay(Util.stringToOnlyDate(date), -1));
        currentDate.setText(Util.stringToStringDayWeek(date));
        try {
            Integer ok = new DownloadEventByDate(this).execute(Util.stringToStringWS(date)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        displayDailyEvents();
    }

    private void nextCalendarDate() {
        mLayout.removeViewAt(eventIndex - 1);

        date = Util.dateToString(Util.changeDay(Util.stringToOnlyDate(date), 1));
        currentDate.setText(Util.stringToStringDayWeek(date));

        try {
            Integer ok = new DownloadEventByDate(this).execute(Util.stringToStringWS(date)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        displayDailyEvents();
    }

    private void displayDailyEvents() {
        ArrayList<EventObjects> eventos = DownloadEventByDate.eventos;
        for (EventObjects eObject : eventos) {
            Date eventDate = eObject.getDateStart();
            Date endDate = eObject.getDateEnd();
            String eventMessage = eObject.getMessage();
            int eventBlockHeight = getEventTimeFrame(eventDate, endDate);
            Log.d(TAG, "Height " + eventBlockHeight);
            displayEventSection(eventDate, eventBlockHeight, eventMessage);
        }
    }

    private int getEventTimeFrame(Date start, Date end) {
        long timeDifference = end.getTime() - start.getTime();
        Log.d(TAG, "timeDifference " + timeDifference);
        //Calendar mCal = Calendar.getInstance();
        //mCal.setTimeInMillis(timeDifference);

        int hours = (int) timeDifference / 3600000;
        timeDifference -= hours * 3600000;
        int minutes = (int) timeDifference / 60000;
        Log.d(TAG, "hours  diff " + hours);
        Log.d(TAG, "minutes diff " + minutes);
        return ((hours * 60) + minutes);
    }

    private void displayEventSection(Date eventDate, int height, String message) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String displayValue = timeFormatter.format(eventDate);
        String[] hourMinutes = displayValue.split(":");
        int hours = Integer.parseInt(hourMinutes[0]);
        int minutes = Integer.parseInt(hourMinutes[1]);
        Log.d(TAG, "Hour top " + hours);
        Log.d(TAG, "Minutes top " + minutes);
        int topViewMargin = (hours * 60) + minutes;
        Log.d(TAG, "Margin top ---> " + topViewMargin);
        createEventView(topViewMargin, height, message);
    }

    public void addNewEvent(View view) {
        Intent intent = new Intent(this, AddEventActivity.class);
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Date", date);
        this.startActivity(intent);
    }

    private void createEventView(int topMargin, int height, String message) {
        TextView mEventView = new TextView(DayCalendarActivity.this);
        RelativeLayout.LayoutParams lParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lParam.topMargin = topMargin * 4; //*2
        lParam.leftMargin = 24;

        mEventView.setLayoutParams(lParam);
        mEventView.setPadding(24, 0, 24, 0);
        mEventView.setHeight(height * 4);
        mEventView.setGravity(0x11);
        mEventView.setTextColor(Color.parseColor("#ffffff"));
        mEventView.setText(message);
        mEventView.setBackgroundColor(Color.parseColor("#3F51B5"));
        mLayout.addView(mEventView, eventIndex - 1);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("DayCalendar Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
