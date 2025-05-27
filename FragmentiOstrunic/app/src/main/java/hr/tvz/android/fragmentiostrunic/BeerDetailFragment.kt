package hr.tvz.android.fragmentiostrunic

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class BeerDetailFragment : Fragment() {

    private var beer: Beer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beer_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ako nema piva, prikaži placeholder tekst
        if (beer == null) {
            view.findViewById<TextView>(R.id.beerName)?.text = "Odaberite pivo iz liste"
            view.findViewById<TextView>(R.id.beerStyle)?.text = ""
            view.findViewById<TextView>(R.id.beerAbv)?.text = ""
            view.findViewById<ImageView>(R.id.beerImage)?.setImageResource(android.R.drawable.ic_menu_help)
            view.findViewById<Button>(R.id.websiteButton)?.visibility = View.GONE
        } else {
            displayBeer(view, beer!!)
        }
    }

    fun updateBeer(beer: Beer) {
        this.beer = beer
        view?.let { displayBeer(it, beer) }
    }

    private fun displayBeer(view: View, beer: Beer) {
        view.findViewById<TextView>(R.id.beerName)?.text = beer.name
        view.findViewById<TextView>(R.id.beerStyle)?.text = "Stil: ${beer.style}"
        view.findViewById<TextView>(R.id.beerAbv)?.text = "ABV: ${beer.abv}%"

        val imageView = view.findViewById<ImageView>(R.id.beerImage)
        imageView?.setImageResource(beer.imageResId)

        // Dodaj klik listener na sliku - koristi isti pristup kao DetailActivity
        imageView?.setOnClickListener {
            val intent = Intent(requireContext(), ImageActivity::class.java)
            intent.putExtra("imageResId", beer.imageResId)  // Pošalji samo image resource ID
            intent.putExtra("beerName", beer.name)          // I naziv piva
            startActivity(intent)
        }

        val button = view.findViewById<Button>(R.id.websiteButton)
        button?.visibility = View.VISIBLE
        button?.text = "OTVORI WEB STRANICU"
        button?.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(beer.websiteUrl))
            startActivity(intent)
        }
    }


    companion object {
        fun newInstance(beer: Beer): BeerDetailFragment {
            val fragment = BeerDetailFragment()
            fragment.beer = beer
            return fragment
        }
    }
}
