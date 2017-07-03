package net.simplifiedcoding.secretariaApp.webservice;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import net.simplifiedcoding.secretariaApp.activity.CalendarActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Toshiba on 03/02/2017.
 */

public class DownloadRespostaProf extends AsyncTask<Void, Void, Integer> {
    ProgressDialog dialog;
    Context c;
    public static int resposta = -1;

    public DownloadRespostaProf(Context c) {
        this.c = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(c, "Aguarde", "Enviando foto e aguardando a resposta do professor!");
    }

    @Override
    protected Integer doInBackground(Void... params) {
        int result = -1;
        while (result == -1) {
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                url = new URL("http://ufam-automation.net/marcosmoura/Resposta.txt");
                urlConnection = (HttpURLConnection) url.openConnection();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String json = readStream(urlConnection.getInputStream());
                    Log.e("RETORNA Resposta?", "Retorna resposta..." + json);
                    result = Integer.parseInt(json);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Erro mesmo", "Erro - " + e.getMessage());
            }
        }
        dialog.dismiss();
        resposta = result;
        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if (result == 1){
            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setMessage("Solicitação aceita! Favor entrar na sala")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ((Activity)c).finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setMessage("Solicitação recusada! Favor agendar um horário")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ((Activity)c).finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

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