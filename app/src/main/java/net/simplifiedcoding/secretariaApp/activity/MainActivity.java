package net.simplifiedcoding.secretariaApp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import net.simplifiedcoding.insertintomysql.R;
import net.simplifiedcoding.secretariaApp.webservice.DownloadEvents;
import net.simplifiedcoding.secretariaApp.webservice.DownloadStatus;
//import net.simplifiedcoding.secretariaApp.webservice.DownloadEvents;
//import net.simplifiedcoding.secretariaApp.webservice.DownloadStatus;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private Button btnChamar, btnAgenda;
    private TimerTask task;
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
        btnAgenda = (Button) findViewById(R.id.btnAgendar);
        btnChamar = (Button) findViewById(R.id.btnChamar);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        setRepeatingAsyncTask();
    }

    private void setRepeatingAsyncTask() {

        final Handler handler = new Handler();
        Timer timer = new Timer();

        task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {

                        try {
                            DownloadStatus jsonTask = new DownloadStatus();
                            Integer status = jsonTask.execute().get();
                            if (status == 1) {
                                btnChamar.setEnabled(false);
                            } else {
                                btnChamar.setEnabled(true);
                            }
                        } catch (Exception e) {
                            Log.e("Erro", e.getMessage());
                        }
                    }
                });
            }
        };

        timer.schedule(task, 0, 3000);  // interval of five seconds

    }

    @Override
    protected void onResume() {
        super.onResume();

        task.run();
    }

    @Override
    protected void onPause() {
        super.onPause();
        task.run();
    }

    public void chamarProf(View view) {
        Intent intent = new Intent(this, CallProfFABActivity.class);
//        task.cancel();
        startActivity(intent);
    }

    public void agendar(View view) {
        Intent intent = new Intent(this, CalendarActivity.class);
        task.cancel();

        try {
            int ok = new DownloadEvents(this).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        startActivity(intent);

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Inicio Page") // TODO: Define a title for the content shown.
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
