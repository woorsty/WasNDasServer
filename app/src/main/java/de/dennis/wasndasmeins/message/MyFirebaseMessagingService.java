package de.dennis.wasndasmeins.message;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.dennis.wasndasmeins.MainActivity;
import de.dennis.wasndasmeins.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (!remoteMessage.getData().isEmpty()) {
            String imageUrl = remoteMessage.getData().get("imageUrl"); // Bild-URL von der Nachricht
            sendNotification(imageUrl);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("Tag", "New Token: " + token);
    }

    private void sendNotification(String imageUrl) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("imageUrl", imageUrl);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                                                                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Standard-Benachrichtigungssound
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,
                                                                                        "default_channel")
                .setSmallIcon(R.drawable.ic_notification)  // Benachrichtigungssymbol
                .setContentTitle("Neues Bild hochgeladen")
                .setContentText("Klicke, um das Bild anzuzeigen")
//                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        // Bild laden und der Benachrichtigung hinzufügen
        if (imageUrl != null) {
            Bitmap bitmap = getBitmapFromUrl(imageUrl);
            if (bitmap != null) {
                notificationBuilder.setStyle(
                        new NotificationCompat.BigPictureStyle().bigPicture(bitmap)
                                                                .bigLargeIcon((Bitmap) null)
                );
            }
        }

//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(
                getApplicationContext());
        // Android 8.0 und höher benötigen einen Notification-Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "default_channel";
            String channelName = "Default Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(channelId,
                                                                              channelName,
                                                                              importance);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    private Bitmap getBitmapFromUrl(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
