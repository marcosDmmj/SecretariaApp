package net.simplifiedcoding.secretariaApp.webservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Toshiba on 06/02/2017.
 */

public class ShowProgressDialog extends AsyncTask<Void, Void, Integer> {
    ProgressDialog dialog;
    Context c;

    public ShowProgressDialog(Context c) {
        this.c = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(c, "Aguarde", "Enviando foto e resposta do professor!");
    }
    @Override
    protected Integer doInBackground(Void... params) {
        Integer resposta = -1;
        while (resposta == -1) {
            try {
                //resposta = new DownloadRespostaProf().execute().get();//DownloadRespostaProf().execute().get();
                Log.e("Resposta:", resposta + "");
                Thread.sleep(1000);
            } catch (Exception e) {
                Log.e("erro na nossa thread:", e.getMessage());
            }
        }
        return null;
    }
}

