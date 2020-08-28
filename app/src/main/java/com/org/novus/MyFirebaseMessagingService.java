package com.org.novus;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());

    }
    public void showNotification(String title,String message){
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,"MyNotifications").setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.ic_launcher_background).setAutoCancel(true);

        NotificationManagerCompat manager= NotificationManagerCompat.from(this);
        manager.notify(999,builder.build());
    }
}