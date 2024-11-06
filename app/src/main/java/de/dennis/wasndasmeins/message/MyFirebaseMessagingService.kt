import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import de.dennis.wasndasmeins.MainActivity
import de.dennis.wasndasmeins.R
import java.net.HttpURLConnection
import java.net.URL

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Überprüfen, ob die Nachricht Daten enthält
        remoteMessage.data.isNotEmpty().let {
            val imageUrl = remoteMessage.data["imageUrl"] // Bild-URL von der Nachricht
            sendNotification(imageUrl)
        }
    }

    private fun sendNotification(imageUrl: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        // Standard-Benachrichtigungssound
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, "default_channel")
            .setSmallIcon(R.drawable.ic_notification)  // Benachrichtigungssymbol
            .setContentTitle("Neues Bild hochgeladen")
            .setContentText("Klicke, um das Bild anzuzeigen")
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        // Bild laden und der Benachrichtigung hinzufügen
        if (imageUrl != null) {
            val bitmap = getBitmapFromUrl(imageUrl)
            if (bitmap != null) {
                notificationBuilder.setStyle(
                    NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null as Bitmap)
                )
            }
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8.0 und höher benötigen einen Notification-Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default_channel",
                "Standardbenachrichtigungen",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun getBitmapFromUrl(src: String): Bitmap? {
        return try {
            val url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
