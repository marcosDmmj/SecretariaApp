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
                HttpURLConnection urlConnection;
                url = new URL("http://ufam-automation.net/marcosmoura/Resposta.txt");
                urlConnection = (HttpURLConnection) url.openConnection();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String json = readStream(urlConnection.getInputStream());
                    Log.d("DownloadRespostaProf", "Retorna resposta..." + json);
                    result = Integer.parseInt(json);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("DownloadRespostaProf", "Erro - " + e.getMessage());
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
                            new setaStatusAsync(c).execute("http://ufam-automation.net/marcosmoura/setStatus.php?Status=1");
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

    private class setaStatusAsync extends AsyncTask<String, Void, Void> {
        Context c;
        HttpURLConnection urlConnection;

        public setaStatusAsync(Context c) {
            this.c = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(c, "Aguarde", "Atualizando status, Por favor aguarde!");
        }

        @Override
        protected Void doInBackground(String... strings) {

            try {
                URL url;
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    int status = Integer.parseInt(result.toString());
                    Log.d("setaStatusAsync","Pegou o resultado de boas! "+status);
                } else {
                    Log.d("setaStatusAsync","A conexão não tá OK! code = "+responseCode);
                }
            }catch (Exception e) {
                Log.e("MainActivity","Erro Status.txt= "+e.getMessage());
            }

            dialog.dismiss();
            return null;
        }
    }
}