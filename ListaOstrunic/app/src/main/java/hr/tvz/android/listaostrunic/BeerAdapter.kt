package hr.tvz.android.listaostrunic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BeerAdapter(
    private val beers: List<Beer>,
    private val onClick: (Beer) -> Unit
) : RecyclerView.Adapter<BeerAdapter.BeerViewHolder>() {

    inner class BeerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgBeer: ImageView = view.findViewById(R.id.imgBeer)
        val txtName: TextView = view.findViewById(R.id.txtName)
        val txtStyle: TextView = view.findViewById(R.id.txtStyle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_beer, parent, false)
        return BeerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BeerViewHolder, position: Int) {
        val beer = beers[position]
        holder.imgBeer.setImageResource(beer.imageResId)
        holder.txtName.text = beer.name
        holder.txtStyle.text = beer.style

        holder.itemView.setOnClickListener { onClick(beer) }
    }

    override fun getItemCount(): Int = beers.size
}
