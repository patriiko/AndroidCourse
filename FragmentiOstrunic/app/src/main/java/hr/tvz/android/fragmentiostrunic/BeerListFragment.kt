package hr.tvz.android.fragmentiostrunic

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class BeerListFragment : Fragment() {

    private var listener: OnBeerSelectedListener? = null
    private lateinit var beerRepository: BeerRepository
    private lateinit var beerAdapter: BeerAdapter
    private var beerList = mutableListOf<Beer>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnBeerSelectedListener) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        beerRepository = BeerRepository()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beer_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)
        loadBeersFromFirebase()
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        beerAdapter = BeerAdapter(beerList) { selectedBeer ->
            val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

            if (isLandscape && listener != null) {
                listener?.onBeerSelected(selectedBeer)
            } else {
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra("beer", selectedBeer)
                startActivity(intent)
            }
        }

        recyclerView.adapter = beerAdapter
    }

    private fun loadBeersFromFirebase() {
        lifecycleScope.launch {
            beerRepository.getAllBeers().collect { beers ->
                Log.d("BeerListFragment", "Received ${beers.size} beers from Firebase")

                if (beers.isEmpty()) {
                    // Ako nema piva u bazi, dodaj početna piva
                    Log.d("BeerListFragment", "No beers found, initializing default beers")
                    beerRepository.initializeDefaultBeers()
                } else {
                    // Ažuriraj listu
                    beerList.clear()
                    beerList.addAll(beers)
                    beerAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
