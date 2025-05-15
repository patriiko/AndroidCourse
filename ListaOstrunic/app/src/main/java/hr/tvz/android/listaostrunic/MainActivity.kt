package hr.tvz.android.listaostrunic

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var beerRecyclerView: RecyclerView
    private lateinit var powerReceiver: PowerConnectedReceiver
    private lateinit var shareReceiver: ShareReceiver

    private val beerList = listOf(
        Beer("Ožujsko", "Lager", 5.3, R.drawable.ozujsko, "https://ozujsko.com/"),
        Beer("Kozel", "Lager", 4.6, R.drawable.kozel, "https://www.kozel.hr/"),
        Beer("Guinness", "Stout", 4.2, R.drawable.guinness, "https://www.guinness.com/")
    )

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        shareReceiver = ShareReceiver()
        registerReceiver(shareReceiver, IntentFilter("hr.tvz.android.listaostrunic.SHARE_BROADCAST"))

        powerReceiver = PowerConnectedReceiver()
        registerReceiver(powerReceiver, android.content.IntentFilter(Intent.ACTION_POWER_CONNECTED))

        beerRecyclerView = findViewById(R.id.recyclerView)
        beerRecyclerView.layoutManager = LinearLayoutManager(this)
        beerRecyclerView.adapter = BeerAdapter(beerList) { selectedBeer ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("beer", selectedBeer)
            startActivity(intent)
        }
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showShareDialog() {
        AlertDialog.Builder(this)
            .setTitle("Dijeli")
            .setMessage("Želite li podijeliti sadržaj?")
            .setPositiveButton("Da") { _, _ ->
                sendBroadcast(Intent("hr.tvz.android.listaostrunic.SHARE_BROADCAST"))
            }
            .setNegativeButton("Ne", null)
            .show()
    }

}
