package hr.tvz.android.fragmentiostrunic

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val imageResId = intent.getIntExtra("imageResId", -1)
        val beerName = intent.getStringExtra("beerName") ?: ""

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = beerName

        val imgView = findViewById<ImageView>(R.id.imgBeerFull)
        if (imageResId != -1) {
            imgView.setImageResource(imageResId)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
