package net.simplifiedcoding.secretariaApp.webservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import net.simplifiedcoding.secretariaApp.calendario.EventObjects;
import net.simplifiedcoding.secretariaApp.calendario.Util;
import net.simplifiedcoding.secretariaApp.classesBasicas.OnTaskComplete;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Toshiba on 02/02/2017.
 */

public class DownloadStatus extends AsyncTask<Void, Void, Integer> {
    OnTaskComplete taskComplete;

    public DownloadStatus(OnTaskComplete taskComplete) {
        this.taskComplete = taskComplete;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        try {
            URL url;
            HttpURLConnection urlConnection;
            url = new URL("http://ufam-automation.net/marcosmoura/Status.txt");
            urlConnection = (HttpURLConnection) url.openConnection();

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String json = readStream(urlConnection.getInputStream());
                Log.d("DownloadStatus", "Retorna Status..."+json);
                return Integer.parseInt(json);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DownloadStatus", "Erro - " + e.getMessage());
        };
        return 1;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        taskComplete.onTaskComplete(integer);
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }
}
