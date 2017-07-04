package net.simplifiedcoding.secretariaApp.webservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadNewEvent extends AsyncTask<String, Void, Integer> {
    private Context c;
    private ProgressDialog dialog;

    public UploadNewEvent(Context c) {
        this.c = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(c, "Aguarde", "Salvando evento. Por favor aguarde!");
    }

    @Override
    protected Integer doInBackground(String... params) {
        try {
            URL url;
            HttpURLConnection urlConnection;
            String s = "http://ufam-automation.net/marcosmoura/addEventTemp.php?" +
                    "Titulo=" + params[0] +
                    "&Data_inicio=" + params[1] +
                    "&Data_fim=" + params[2] +
                    "&Email=" + params[3] +
                    "&Nome=" + params[4];
            url = new URL(s);
            Log.d("UploadNewEvent", "ulr = "+s);
            urlConnection = (HttpURLConnection) url.openConnection();

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d("UploadNewEvent", "Está tudo ok na conexão!");
                Log.d("UploadNewEvent", "Resultado = "+readStream(urlConnection.getInputStream()));
                dialog.dismiss();
                return 1;
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Erro mesmo", "Erro - "+e.getMessage());
        }

        dialog.dismiss();
        Log.d("UploadNewEvent", "Retornou not ok");
        return 0;
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();
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
