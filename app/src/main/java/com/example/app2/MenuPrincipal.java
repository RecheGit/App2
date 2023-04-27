package com.example.app2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MenuPrincipal extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;

    private static final int REQUEST_PERMISSION_WRITE_STORAGE = 200;
    private ImageView imageView;
    String currentPhotoPath;
    private StorageReference myStorage;
    private static String uriFinal = "";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        imageView = findViewById(R.id.imageView);


        //Necesario para no perder la imagen al rotar la pantalla o recibir alguna llamada etc
        if(savedInstanceState!=null){
            uriFinal=savedInstanceState.getString("uri");
        }

        if (!uriFinal.equals("")){
            Uri u = Uri.parse(uriFinal);
            Picasso.get().load(u).into(imageView);
        }

        myStorage = FirebaseStorage.getInstance().getReference();

        Button btnSacarFoto = findViewById(R.id.btnSacarFoto);
        Button btnCrearWidget = findViewById(R.id.btnCrearWidget);




        //Al pulsar el boton de sacar foto:
        btnSacarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                askCameraPermissions();//Comprobamos que tiene los permisos necesarios para usar la camara, sino los añadimos
            }
        });

        //Vamos a la pantalla de crear el widget
        btnCrearWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuPrincipal.this, Widget.class);

                startActivity(intent);
            }
        });
    }
    private void askCameraPermissions() {

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
        }
        else {

            dispachTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                dispachTakePictureIntent();
            }
            else{
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    //Creamos la ruta de la imagen una vez esta se ha realizad0
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                //imageView.setImageURI(Uri.fromFile(f));
                Log.d("path","Ruta absoluta: "+ Uri.fromFile(f));
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
                uploadImageToFirebase(f.getName(),contentUri);//la subimos al storage de firebase
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //Subimos la imagen al storage de firebase
    private void uploadImageToFirebase(String name, Uri contentUri) {

         StorageReference image = myStorage.child("pictures/"+ name);//creamos la carpeta en el storage y añadimos el nombre de la imagen

         image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
             @Override
             public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                 image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                     @Override
                     public void onSuccess(Uri uri) {
                         uriFinal=uri.toString();
                         Picasso.get().load(uri).into(imageView);//Una vez subida, la volvemos a traer a nuestra app y la mostramos
                         Toast.makeText(MenuPrincipal.this,"image uploaded",Toast.LENGTH_SHORT);

                     }

                 });

             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Toast.makeText(MenuPrincipal.this,"Upload Failed",Toast.LENGTH_SHORT).show();
             }
         });
    }


    //Damos valores para el path de la imagen
    private  File createImageFile() throws IOException{

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return  image;
    }


    private void dispachTakePictureIntent(){

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePictureIntent.resolveActivity(getPackageManager())!=null){

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e){}

            if (photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(this,"com.example.android.fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    //Guardamos la URI de la imagen en formato string para mantenerla en las rotaciones, ...
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("uri",uriFinal);

    }
}




