package net.simplifiedcoding.secretariaApp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import net.simplifiedcoding.secretariaApp.R;
import net.simplifiedcoding.secretariaApp.classesBasicas.OnTaskComplete;
import net.simplifiedcoding.secretariaApp.webservice.DownloadStatus;

import java.util.Timer;
import java.util.TimerTask;

//import net.simplifiedcoding.secretariaApp.webservice.DownloadEvents;
//import net.simplifiedcoding.secretariaApp.webservice.DownloadStatus;

public class MainActivity extends AppCompatActivity {
    private Button btnChamar;
    //private TimerTask task;
    private Timer timer;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
//    public static ArrayList<EventObjects> eventos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        btnChamar = (Button) findViewById(R.id.btnChamar);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setRepeatingAsyncTask() {

        final Handler handler = new Handler();
        timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            new DownloadStatus(new OnTaskComplete() {
                                @Override
                                public void onTaskComplete(Integer status) {
                                    String message;
                                    if (status == 0) {
                                        message = "Falar com o professor";
                                    } else {
                                        message = "Professor indisponível";
                                    }
                                    btnChamar.setEnabled(status == 0);
                                    btnChamar.setText(message);
                                }
                            }).execute();
                        } catch (Exception e) {
                            Log.e("Erro", e.getMessage());
                        }
                    }
                });
            }
        };

        timer.schedule(task, 0, 5000);  // interval of five seconds
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRepeatingAsyncTask();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    public void chamarProf(View view) {
        timer.cancel();
        Intent intent = new Intent(this, CallProfFABActivity.class);
        startActivity(intent);
    }

    public void agendar(View view) {
        timer.cancel();
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Inicio Page")
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
