package com.example.app2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    //Creamos canal para las notificaciones, en este caso para la de FIREBASE
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {

        if (message.getNotification() != null){

            NotificationManager elManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(MyFirebaseMessagingService.this, "id_canal");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel elCanal = new NotificationChannel("id_canal", "MensajeriaFCM", NotificationManager.IMPORTANCE_DEFAULT);
                elManager.createNotificationChannel(elCanal);
            }

            //personalizamos la notificacion
            elBuilder.setSmallIcon(android.R.drawable.btn_star)
                    .setContentTitle(message.getNotification().getTitle())
                    .setContentText(message.getNotification().getBody())
                    .setVibrate(new long[] {0, 1000, 500, 1000})
                    .setAutoCancel(false);
            elManager.notify(1, elBuilder.build());


        }

    }
}