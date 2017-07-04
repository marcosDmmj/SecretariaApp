package net.simplifiedcoding.secretariaApp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class CallProfFABActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageViewPhoto;
    private GoogleApiClient client;
    private String file;
    private Uri outputFileUri;
    private Intent cameraIntent;
    File newFile;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_prof_fab);

        imageViewPhoto = (ImageView) findViewById(R.id.imageView2);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
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
                ajustaFoto();
            //    Bitmap bitmap = MediaStore.Images.Media.getBitmap(cr, outputFileUri);
            //    Bitmap.createScaledBitmap(bitmap, 300, 175, true);
            //    bitmap.compress(Bitmap.CompressFormat.JPEG, 40, new ByteArrayOutputStream());
               // imageViewPhoto.setImageBitmap(bitmap);
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

    public static void  verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
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

    protected void ajustaFoto(){
        getContentResolver().notifyChange(outputFileUri, null);
        ContentResolver cr = getContentResolver();
        Bitmap bitmap = null;
        int w = 0;
        int h = 0;
        Matrix mtx = new Matrix();

// Ajusta orientação da imagem
        try {
// joga a imagem em uma variável
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, outputFileUri);

// captura as dimensões da imagem
            w = bitmap.getWidth();
            h = bitmap.getHeight();
            Log.d("CallProfFAB ajusta foto",w+"");
            Log.d("CallProfFAB ajusta foto",h+"");
            mtx = new Matrix();

// pega o caminho onda a imagem está salva
            ExifInterface exif = new ExifInterface(file);

// pega a orientação real da imagem
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

// gira a imagem de acordo com a orientação
            switch(orientation) {
                case 3: // ORIENTATION_ROTATE_180
                    mtx.postRotate(180);
                    break;
                case 6: //ORIENTATION_ROTATE_90
                    mtx.postRotate(90);
                    break;
                case 8: //ORIENTATION_ROTATE_270
                    mtx.postRotate(270);
                    break;
                default: //ORIENTATION_ROTATE_0
                    mtx.postRotate(0);
                    break;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("Erro ajusta foto","File Not found... "+e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Erro ajusta foto","IOException... "+e.getMessage());
        }

// cria variável com a imagem rotacionada
        Bitmap rotatedBmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
        BitmapDrawable bmpd = new BitmapDrawable(rotatedBmp);

// redimensiona a imagem
        Integer lateral = 1024; // tamanho final da dimensão maior da imagem
        try {
// cria um stream pra salvar o arquivo
            FileOutputStream out = new FileOutputStream(file);

// uma nova instancia do bitmap rotacionado
            Bitmap bmp = bmpd.getBitmap();

//define um indice = 1 pois se der erro vai manter a imagem como está.
            Integer idx;

// reupera as dimensões da imagem
            w = bmp.getWidth();
            h = bmp.getHeight();

// verifica qual a maior dimensão e divide pela lateral final para definir qual o indice de redução
            if ( w >= h){
                idx = w / lateral;
            } else {
                idx = h / lateral;
            }
            Log.d("CallProfFAB ajusta foto",idx+"");
            // TODO o idx está dando 0

// acplica o indice de redução nas novas dimensões
            w = w / idx;
            h = h / idx;

// cria nova instancia da imagem já redimensionada
            Bitmap bmpReduzido = Bitmap.createScaledBitmap(bmp, w, h, true);

// salva a imagem reduzida no disco
            bmpReduzido.compress(Bitmap.CompressFormat.PNG, 90, out);
            imageViewPhoto.setImageBitmap(bmpReduzido);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
