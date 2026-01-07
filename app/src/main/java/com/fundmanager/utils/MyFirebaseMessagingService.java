package com.fundmanager.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.fundmanager.R;
import com.fundmanager.activities.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "community_fund_channel";
    private static final String CHANNEL_NAME = "Community Fund Notifications";
    private static final String CHANNEL_DESCRIPTION = "Notifications for deposits and expenses";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            sendNotification(title, body);
        }

        // Check if message contains a data payload
        if (remoteMessage.getData().size() > 0) {
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");
            sendNotification(title, message);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Send token to server if needed
        sendTokenToServer(token);
    }

    private void sendTokenToServer(String token) {
        // TODO: Implement sending token to your server
        // This is useful if you want to send targeted notifications to specific users
    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        // Use appropriate flags based on Android version
        int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M 
                ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                : PendingIntent.FLAG_UPDATE_CURRENT;
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 
                0, 
                intent,
                flags
        );

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}
