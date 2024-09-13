package com.example.webmo2024app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.webmo2024app.model.Essen

class EssenAdapter(
    private var essenList: List<Essen>,
    private val onEditClick: (Essen) -> Unit,
    private val onDeleteClick: (Essen) -> Unit
) : RecyclerView.Adapter<EssenAdapter.EssenViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EssenViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_essen, parent, false)
        return EssenViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EssenViewHolder, position: Int) {
        val essen = essenList[position]
        holder.textViewEssenName.text = essen.name
        holder.textViewEssenType.text = essen.type
        holder.textViewEssenPrice.text = essen.price.toString()

        // Setze Klicklistener für die Buttons
        holder.buttonBearbeiten.setOnClickListener { onEditClick(essen) }
        holder.buttonLoeschen.setOnClickListener { onDeleteClick(essen) }
    }

    override fun getItemCount() = essenList.size

    fun updateData(newEssenList: List<Essen>) {
        essenList = newEssenList
        notifyDataSetChanged()
    }

    inner class EssenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewEssenName: TextView = itemView.findViewById(R.id.textViewEssenName)
        val textViewEssenType: TextView = itemView.findViewById(R.id.textViewEssenType)
        val textViewEssenPrice: TextView = itemView.findViewById(R.id.textViewEssenPrice)
        val buttonBearbeiten: Button = itemView.findViewById(R.id.buttonBearbeiten)
        val buttonLoeschen: Button = itemView.findViewById(R.id.buttonLoeschen)
    }
}
