package com.example.app2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class Widget extends AppCompatActivity {

    static ImageView iconoWidget = null;
    private static String uriFinal = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba_w);
        iconoWidget = findViewById(R.id.imageViewWidget);

        //Necesario para no perder la imagen al rotar la pantalla o recibir alguna llamada etc
        if(savedInstanceState!=null){
            uriFinal=savedInstanceState.getString("uri");
        }

        if (!uriFinal.equals("")){
            Uri u = Uri.parse(uriFinal);
            Picasso.get().load(u).into(iconoWidget);
        }

        Button btnCargarImagen = findViewById(R.id.btnCargar_Imagen_De_Galeria);
        Button btnCrearWidget = findViewById(R.id.btnWidget);



        //entramos a la galeria del telefono y seleccionamos una imagen
        btnCargarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cargarImagen();

                AppWidgetManager appWidgetManager = view.getContext().getSystemService(AppWidgetManager.class);
                Intent intent = new Intent(getApplication(), MiWidget.class);
                int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(getApplication(), MiWidget.class));
                intent.setAction(appWidgetManager.ACTION_APPWIDGET_UPDATE);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                //intent.putExtra("imagenUri",uriFinal);
                sendBroadcast(intent);


            }

        });

        //creamos el widget
        btnCrearWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppWidgetManager appWidgetManager = view.getContext().getSystemService(AppWidgetManager.class);
                ComponentName myProvider = new ComponentName(view.getContext(), MiWidget.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (appWidgetManager.isRequestPinAppWidgetSupported()) {
                        appWidgetManager.requestPinAppWidget(myProvider, null, null);
                    }
                }
            }
        });

    }

    private void cargarImagen() {
        Intent intentGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intentGaleria.setType("image/");
        startActivityForResult(intentGaleria.createChooser(intentGaleria,"Seleccione la app"),10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Uri path = data.getData();
            uriFinal = path.toString();
            iconoWidget.setImageURI(path);

        }
    }

    //Guardamos la URI de la imagen en formato string para mantenerla en las rotaciones, ...
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("uri",uriFinal);

    }
}