package net.simplifiedcoding.secretariaApp.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import net.simplifiedcoding.insertintomysql.R;
import net.simplifiedcoding.secretariaApp.webservice.DownloadRespostaProf;
import net.simplifiedcoding.secretariaApp.webservice.UploadImage;
import net.simplifiedcoding.secretariaApp.webservice.UploadResetResposta;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class CallProfFABActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;

    private ImageView imageViewPhoto;
    private Button btnEnviarFoto;

    private GoogleApiClient client;
    private String file;
    private Uri outputFileUri;
    private Intent cameraIntent;
    File newFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_prof_fab);

        imageViewPhoto = (ImageView) findViewById(R.id.imageView2);
        btnEnviarFoto = (Button) findViewById(R.id.btnEnviarFoto);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Identifique-se!");
        }
    }

    public void tookPhoto(View view) throws IOException {
        imageViewPhoto.setImageURI(null);
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newDir = new File(dir);
        newDir.mkdirs();

        file = dir + "photo" + ".jpg";
        newFile = new File(file);
        try {
            if (newFile.exists())
                newFile.delete();
            newFile.createNewFile();
        } catch (IOException e) {
            Log.e("Erro em tookPhoto","Não conseguiu criar o arquivo! "+e.getMessage());
            e.printStackTrace();
        }

        outputFileUri = Uri.fromFile(newFile);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST);
        } else {
            cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            startActivityForResult(cameraIntent, 555);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 555) {
            getContentResolver().notifyChange(outputFileUri, null);
            ContentResolver cr = getContentResolver();
            try {
                Toast.makeText(this, "Foto tirada!", Toast.LENGTH_SHORT).show();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(cr, outputFileUri);
                Bitmap.createScaledBitmap(bitmap, 300, 175, true);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, new ByteArrayOutputStream());
                imageViewPhoto.setRotation(180);
                imageViewPhoto.setImageBitmap(bitmap);
                btnEnviarFoto.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("CallProfFab","Erro mesmo ó! "+e.getMessage());
            }
        }
    }

    public void sendPhoto(View view) {
        //uploada a foto e le Resposta.txt

        try {
            new UploadImage(this, file).execute();
            new DownloadRespostaProf(this).execute();
            new UploadResetResposta().execute();
        } catch (Exception e) {
            Log.d("Erro ----", e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("CallProfFAB","Veio no onRequestPermissionsResult?");
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
                cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        }
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("CallProfFAB Page")
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
