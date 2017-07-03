package net.simplifiedcoding.secretariaApp.webservice;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import net.simplifiedcoding.secretariaApp.calendario.EventObjects;
import net.simplifiedcoding.secretariaApp.calendario.Util;

public class DownloadEvents extends AsyncTask<Void, Void, Integer> {
    ProgressDialog dialog;
    Context c;
    public static ArrayList<EventObjects> eventos;

    public DownloadEvents(Context c) {
        this.c = c;
        eventos = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(c, "Aguarde", "Baixando dados, Por favor aguarde!");
    }
    @Override
    protected Integer doInBackground(Void... params) {
        String server_response;
        try {
            URL url;
            HttpURLConnection urlConnection = null;
            url = new URL("http://ufam-automation.net/marcosmoura/getEvents.php");
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
        dialog.dismiss();
        return 1;
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
