package de.dennis.wasndasmeins

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessaging
import de.dennis.wasndasmeins.api.API
import de.dennis.wasndasmeins.glide.IPv6UrlLoaderFactory
import de.dennis.wasndasmeins.model.Image
import de.dennis.wasndasmeins.ui.theme.WasNDasMeinsTheme
import java.io.InputStream
import java.net.URI
import java.net.URLDecoder


class MainActivity : ComponentActivity() {
    private lateinit var tokenView: TextView
    private lateinit var currentImage: Image
    private lateinit var okButton: Button
    private lateinit var nopeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Glide.with(this)
        Glide.get(this).registry.replace<String, InputStream>(
            String::class.java,
            InputStream::class.java,
            IPv6UrlLoaderFactory()
        )


        tokenView = findViewById(R.id.tokenView)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM Token", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get the FCM token
            val token = task.result

            tokenView.text = token

            // Log and show the token
            Log.d("FCM Token", "FCM Token: $token")
            // You can send the token to your server here

            API.sendTokenToAPI(this, token)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }

        okButton = findViewById(R.id.ok_button);
        okButton.setOnClickListener { API.sendAnswer(this, true, currentImage.filename) }
        okButton.visibility = INVISIBLE;
        nopeButton = findViewById(R.id.nope_button);
        nopeButton.setOnClickListener { API.sendAnswer(this, false, currentImage.filename) }
        nopeButton.visibility = INVISIBLE;
        val refreshButton: Button = findViewById(R.id.refresh_button)
        refreshButton.setOnClickListener {
            API.getLatestImageUrl(this)
        }

        val imageUrl = intent.getStringExtra(null)
        if (imageUrl == null) {
            API.getLatestImageUrl(this);
        } else {
            if (intent.getBooleanExtra("finish", false)) {
                showToast("Fertig")
            }
        }
    }

    fun showToast(text: String) {
        // Fehler bei der Antwort
        runOnUiThread {
            tokenView.text = text
            Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        }
    }

    fun showImage(image: Image) {
        runOnUiThread {
            try {
                val imageView: ImageView = findViewById(R.id.imageView)
                val handler = Handler(Looper.getMainLooper())
                
                val utf8 = URLDecoder.decode(image.imageUrl, "UTF-8");
                val uri = URI(utf8)
                val url = uri.toASCIIString() // URL in ein Glide-kompatibles Format konvertieren

                Glide.with(this)
                    .load(url)
                    .into(imageView)
                handler.post {
                }
                if (image.isDone) {
                    okButton.visibility = INVISIBLE;
                    nopeButton.visibility = INVISIBLE;
                } else {
                    okButton.visibility = VISIBLE;
                    nopeButton.visibility = VISIBLE;
                }

                currentImage = image
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WasNDasMeinsTheme {
        Greeting("Android")
    }
}
