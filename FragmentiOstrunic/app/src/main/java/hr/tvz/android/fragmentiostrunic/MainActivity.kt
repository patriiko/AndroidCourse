package hr.tvz.android.fragmentiostrunic

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity(), OnBeerSelectedListener {

    private lateinit var powerReceiver: PowerConnectedReceiver
    private lateinit var shareReceiver: ShareReceiver

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Register receivers
        shareReceiver = ShareReceiver()
        registerReceiver(shareReceiver, IntentFilter("hr.tvz.android.fragmentiostrunic.SHARE_BROADCAST"))

        powerReceiver = PowerConnectedReceiver()
        registerReceiver(powerReceiver, android.content.IntentFilter(Intent.ACTION_POWER_CONNECTED))

        // Request notification permission
        requestNotificationPermission()

        // Get Firebase token
        getFirebaseToken()

        // Load fragments only if not already loaded
        if (savedInstanceState == null) {
            setupFragments()
        }
    }

    private fun setupFragments() {
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        Log.d("MainActivity", "setupFragments: isLandscape = $isLandscape")
        Log.d("MainActivity", "Current orientation = ${resources.configuration.orientation}")
        Log.d("MainActivity", "LANDSCAPE = ${Configuration.ORIENTATION_LANDSCAPE}")
        Log.d("MainActivity", "PORTRAIT = ${Configuration.ORIENTATION_PORTRAIT}")

        if (isLandscape) {
            Log.d("MainActivity", "Setting up landscape fragments")

            // Provjeri da li detail container postoji
            val detailContainer = findViewById<View>(R.id.fragment_container_detail)
            val listContainer = findViewById<View>(R.id.fragment_container)

            Log.d("MainActivity", "Detail container found: ${detailContainer != null}")
            Log.d("MainActivity", "List container found: ${listContainer != null}")

            if (detailContainer != null && listContainer != null) {
                // Landscape mode - dva fragmenta
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, BeerListFragment())
                    .replace(R.id.fragment_container_detail, BeerDetailFragment())
                    .commit()
                Log.d("MainActivity", "Landscape fragments added successfully")
            } else {
                Log.e("MainActivity", "Landscape containers not found!")
            }
        } else {
            Log.d("MainActivity", "Setting up portrait fragments")

            val container = findViewById<View>(R.id.fragment_container)
            Log.d("MainActivity", "Portrait container found: ${container != null}")

            if (container != null) {
                // Portrait mode - samo lista
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, BeerListFragment())
                    .commit()
                Log.d("MainActivity", "Portrait fragment added successfully")
            } else {
                Log.e("MainActivity", "Portrait container not found!")
            }
        }
    }



    override fun onBeerSelected(beer: Beer) {
        // Ova metoda se poziva samo u landscape mode-u
        val detailFragment = BeerDetailFragment.newInstance(beer)

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,  // enter animation
                android.R.anim.fade_out, // exit animation
                android.R.anim.fade_in,  // pop enter animation
                android.R.anim.fade_out  // pop exit animation
            )
            .replace(R.id.fragment_container_detail, detailFragment)
            .commit()
    }


    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
    }

    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("MainActivity", "FCM Registration Token: $token")

            // You can display this token or send it to your server
            // For testing purposes, you can copy this token from Logcat
        }
    }

    private fun createTestNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "beer_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Beer Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Dohvati Ožujsko pivo iz Firebase baze
        val beerRepository = BeerRepository()
        val database = FirebaseDatabase.getInstance().reference
        val ozujskoRef = database.child("beers").child("Ozujsko")

        ozujskoRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Dohvati podatke iz Firebase
                val name = snapshot.child("name").getValue(String::class.java) ?: "Ožujsko"
                val style = snapshot.child("style").getValue(String::class.java) ?: "Lager"
                val abv = snapshot.child("abv").getValue(Double::class.java) ?: 5.3
                val imageResId = snapshot.child("imageResId").getValue(Int::class.java) ?: R.drawable.ozujsko
                val websiteUrl = snapshot.child("websiteUrl").getValue(String::class.java) ?: "https://ozujsko.com/"

                val beer = Beer(name, style, abv, imageResId, websiteUrl)

                // Stvori intent s pivom iz baze
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra("beer", beer)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Pivo iz Firebase baze!")
                    .setContentText("Klikni da vidiš ${name} dohvaćeno iz baze")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build()

                notificationManager.notify(999, notification)

            } else {
                Log.d("MainActivity", "Ožujsko pivo nije pronađeno u bazi")
                // Fallback - koristi hardkodiran Beer objekt
                createFallbackNotification(notificationManager, channelId)
            }
        }.addOnFailureListener { exception ->
            Log.e("MainActivity", "Greška pri dohvaćanju piva iz baze", exception)
            // Fallback - koristi hardkodiran Beer objekt
            createFallbackNotification(notificationManager, channelId)
        }
    }

    private fun createFallbackNotification(notificationManager: NotificationManager, channelId: String) {
        val beer = Beer("Ožujsko", "Lager", 5.3, R.drawable.ozujsko, "https://ozujsko.com/")
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("beer", beer)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Pivo (fallback)")
            .setContentText("Klikni da vidiš Ožujsko (hardkodiran)")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(999, notification)
    }



    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(powerReceiver)
        unregisterReceiver(shareReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                showShareDialog()
                true
            }
            R.id.action_test_notification -> {
                createTestNotification()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showShareDialog() {
        AlertDialog.Builder(this)
            .setTitle("Dijeli")
            .setMessage("Želite li podijeliti sadržaj?")
            .setPositiveButton("Da") { _, _ ->
                sendBroadcast(Intent("hr.tvz.android.fragmentiostrunic.SHARE_BROADCAST"))
            }
            .setNegativeButton("Ne", null)
            .show()
    }
}
