package hr.tvz.android.listaostrunic

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val beer = intent.getParcelableExtra<Beer>("beer")!!

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = beer.name

        val img = findViewById<ImageView>(R.id.imgBeerDetail)
        val name = findViewById<TextView>(R.id.txtNameDetail)
        val style = findViewById<TextView>(R.id.txtStyleDetail)
        val abv = findViewById<TextView>(R.id.txtAbvDetail)
        val btnWebsite = findViewById<Button>(R.id.btnOpenWebsite)

        img.setImageResource(beer.imageResId)
        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        img.startAnimation(animation)
        img.setOnClickListener {
            val intent = Intent(this, ImageActivity::class.java)
            intent.putExtra("imageResId", beer.imageResId)
            intent.putExtra("beerName", beer.name)
            startActivity(intent)
        }


        name.text = beer.name
        style.text = getString(R.string.style_format, beer.style)
        abv.text = getString(R.string.abv_format, beer.abv)

        btnWebsite.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(beer.websiteUrl))
            startActivity(intent)
        }
    }

    // Klik na back gumb
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
