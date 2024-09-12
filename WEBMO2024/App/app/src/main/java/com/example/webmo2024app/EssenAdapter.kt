package com.example.webmo2024app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.webmo2024app.model.Essen

class EssenAdapter(private var essenList: List<Essen>) : RecyclerView.Adapter<EssenAdapter.EssenViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EssenViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_essen, parent, false)
        return EssenViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EssenViewHolder, position: Int) {
        val essen = essenList[position]
        holder.textViewEssenName.text = essen.name // Verwende die richtige ID für Name
        holder.textViewEssenBeschreibung.text = essen.type // Beispiel für andere Daten
        // Weitere Setups für andere Views
    }

    override fun getItemCount() = essenList.size

    // Methode zum Aktualisieren der Daten im Adapter
    fun updateData(newEssenList: List<Essen>) {
        essenList = newEssenList
        notifyDataSetChanged() // Benachrichtigt den Adapter, dass sich die Daten geändert haben
    }

    inner class EssenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewEssenName: TextView = itemView.findViewById(R.id.textViewEssenName)
        val textViewEssenBeschreibung: TextView = itemView.findViewById(R.id.textViewEssenBeschreibung)
    }
}
