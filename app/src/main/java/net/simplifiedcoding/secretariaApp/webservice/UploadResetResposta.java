package net.simplifiedcoding.secretariaApp.webservice;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Toshiba on 05/02/2017.
 */

public class UploadResetResposta extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
        try {
            // Resete da resposta do prof, pois ele acabou de responder náo mexer nisso.
            URL url;
            HttpURLConnection urlConnection = null;
            url = new URL("http://ufam-automation.net/marcosmoura/setResposta.php?Resposta=-1");
            urlConnection = (HttpURLConnection) url.openConnection();

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                urlConnection.getInputStream();
                Log.e("Resposta do prof reset", "status == -1");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Erro mesmo", "Erro - " + e.getMessage());
        }
        return null;
    }
}
