package com.example.app2;

import androidx.appcompat.app.AppCompatActivity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import android.os.Bundle;

public class MiWidget extends AppWidgetProvider {

    


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context,appWidgetManager,appWidgetIds);



        // Actualizar todos los widgets
        for (int appWidgetId : appWidgetIds) {

            Intent intent = new Intent(context, MenuPrincipal.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0, intent, PendingIntent.FLAG_MUTABLE);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.activity_mi_widget);
            remoteViews.setOnClickPendingIntent(R.id.boton, pendingIntent);//Boton para volver a la app

            //modificamos los valores de dentro del widget
            SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
            String strDate = mdformat.format(Calendar.getInstance().getTime());
            remoteViews.setTextViewText(R.id.txtHora, strDate);
            remoteViews.setImageViewResource(R.id.imagen_widget,R.drawable.iconoapp);

            // Actualizar el widget
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }
}