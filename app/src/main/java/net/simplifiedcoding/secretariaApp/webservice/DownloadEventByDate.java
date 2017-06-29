package net.simplifiedcoding.secretariaApp.webservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import net.simplifiedcoding.secretariaApp.calendario.EventObjects;
import net.simplifiedcoding.secretariaApp.calendario.Util;

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
 * Created by Toshiba on 29/01/2017.
 */

public class DownloadEventByDate extends AsyncTask<String, Void, Integer>{
    ProgressDialog dialog;
    Context c;
    public static ArrayList<EventObjects> eventos;

    public DownloadEventByDate(Context c) {
        this.c = c;
        eventos = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(c, "Aguarde", "Baixando dados, Por favor aguarde!");
    }
    @Override
    protected Integer doInBackground(String... params) {
        String server_response;
        try {
            URL url;
            String date = params[0];
            HttpURLConnection urlConnection = null;
            url = new URL("http://tccmari.esy.es/selectbyDate.php?Data=" + date);
            Log.e("Date ", date);
            urlConnection = (HttpURLConnection) url.openConnection();

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String json = readStream(urlConnection.getInputStream());

                JSONArray eventosL = new JSONArray(json);
                JSONObject evento;

                for (int i = 0; i < eventosL.length(); i++) {
                    evento = new JSONObject(eventosL.getString(i));
                    eventos.add(new EventObjects(Integer.parseInt(evento.getString("Evento_id")), evento.getString("Titulo"), Util.stringToDateComplete(evento.getString("Data_inicio")), Util.stringToDateComplete(evento.getString("Data_fim"))));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Erro mesmo", "Erro - " + e.getMessage());
        }
        return 1;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        dialog.dismiss();
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
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
