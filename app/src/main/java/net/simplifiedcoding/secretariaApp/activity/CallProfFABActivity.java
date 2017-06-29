package net.simplifiedcoding.secretariaApp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import net.simplifiedcoding.insertintomysql.R;
import net.simplifiedcoding.secretariaApp.webservice.DownloadRespostaProf;
import net.simplifiedcoding.secretariaApp.webservice.ShowProgressDialog;
import net.simplifiedcoding.secretariaApp.webservice.UploadImage;
import net.simplifiedcoding.secretariaApp.webservice.UploadResetResposta;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CallProfFABActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageViewPhoto;
    private GoogleApiClient client;
    private String file;
    private Uri outputFileUri;
    private Intent cameraIntent;
    private Button btnEnviar;
    public int resposta = -1;
    private File newfile;
    ProgressDialog dialog;
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
        btnEnviar = (Button) findViewById(R.id.btnEnviarFoto);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void tookPhoto(View view) throws IOException {
        imageViewPhoto.setImageURI(null);
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();

        file = dir + "photo" + ".jpg";
        newfile = new File(file);
        try {
            if (newfile.exists())
                newfile.delete();
            newfile.createNewFile();
        } catch (IOException e) {
        }

        outputFileUri = Uri.fromFile(newfile);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST);
        } else {
            Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            startActivityForResult(intent2, Activity.RESULT_OK);
        }

//        System.out.println("Photo: " + outputFileUri.getPath());
    }

    public void sendPhoto(View view) {
        //uploada a foto e le Resposta.txt

        try {
            new UploadImage(this, file).execute();
            new DownloadRespostaProf(this).execute();
            new UploadResetResposta().execute();
        } catch (Exception e) {
            Log.e("Erro ----", e.getMessage());
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, cameraIntent);
        try {
            if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
                verifyStoragePermissions(this);
                Toast.makeText(this, "Foto tirada!", Toast.LENGTH_SHORT).show();
                btnEnviar.setEnabled(true);
                ajustaFoto();

                //
                getContentResolver().notifyChange(outputFileUri, null);
                ContentResolver cr = getContentResolver();
                Bitmap photo = android.provider.MediaStore.Images.Media.getBitmap(cr, outputFileUri); //"your Bitmap image";
                photo = Bitmap.createScaledBitmap(photo, 300, 175, true);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

                File f = new File(Environment.getExternalStorageDirectory()
                        + File.separator + "Imagename.jpg");
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
                fo.close();
            } else {
                Toast.makeText(this, "Foto cancelada pelo usuário!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("Erro", "Bitmaaappp: " + e.getMessage());

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
                cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                startActivityForResult(cameraIntent, CAMERA_REQUEST);
//                if(newfile.exists())
//                    newfile.delete();
            }
            else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
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
            Log.e("aaaaaaaaaaaaaah",w+"");
            Log.e("aaaaaaaaaaaaaah",h+"");
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
        } catch (IOException e) {
            e.printStackTrace();
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
            Integer idx = 1;

            // reupera as dimensões da imagem
            w = bmp.getWidth();
            h = bmp.getHeight();

            // verifica qual a maior dimensão e divide pela lateral final para definir qual o indice de redução
            if ( w >= h){
                idx = w / lateral;
            } else {
                idx = h / lateral;
            }

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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("CallProfFAB Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
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
