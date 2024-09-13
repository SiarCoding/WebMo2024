package com.example.webmo2024app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.webmo2024app.R
import com.example.webmo2024app.model.Essen
import com.example.webmo2024app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.webmo2024app.EssenAdapter
import com.example.webmo2024app.network.ApiService

class EssenListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EssenAdapter
    private var essenList: MutableList<Essen> = mutableListOf()

    // Initialisiere den ApiService
    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_essen_list, container, false)

        // Initialisiere den ApiService
        apiService = RetrofitClient.create(requireContext())

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Adapter initialisieren und Methoden für Bearbeiten und Löschen hinzufügen
        adapter = EssenAdapter(
            essenList,
            onEditClick = { essen -> editEssen(essen) },
            onDeleteClick = { essen -> deleteEssen(essen) }
        )
        recyclerView.adapter = adapter

        // Rufe die Methode fetchEssenData() auf, um die Daten zu laden
        fetchEssenData()
        return view
    }

    // Methode zum Abrufen der Essensdaten vom Server
    private fun fetchEssenData() {
        // Verwende den initialisierten apiService
        val call = apiService.getAllEssen()
        call.enqueue(object : Callback<List<Essen>> {
            override fun onResponse(call: Call<List<Essen>>, response: Response<List<Essen>>) {
                if (response.isSuccessful) {
                    essenList.clear()
                    response.body()?.let { essenList.addAll(it) }
                    adapter.notifyDataSetChanged()
                } else {
                    // Fehlerbehandlung, z.B. Toast oder Log
                    Toast.makeText(context, "Fehler beim Laden der Daten", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Essen>>, t: Throwable) {
                // Fehlerbehandlung, z.B. Toast oder Log
                Toast.makeText(context, "Netzwerkfehler: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Methode zum Bearbeiten eines Essens
    private fun editEssen(essen: Essen) {
        // Hier die Logik zum Bearbeiten eines Essens implementieren
        Toast.makeText(context, "${essen.name} bearbeiten", Toast.LENGTH_SHORT).show()
    }

    // Methode zum Löschen eines Essens
    private fun deleteEssen(essen: Essen) {
        val call = apiService.deleteEssen(essen.id!!)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Lösche das Essen aus der Liste und aktualisiere den Adapter
                    essenList.remove(essen)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(context, "Essen erfolgreich gelöscht", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Fehler beim Löschen des Essens", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Netzwerkfehler: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
