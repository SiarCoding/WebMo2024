package com.example.webmo2024app

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.webmo2024app.model.Essen
import com.example.webmo2024app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.webmo2024app.EssenAdapter


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var essenAdapter: EssenAdapter // Annahme: Du hast einen Adapter für die Anzeige der Daten

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // RecyclerView initialisieren
        recyclerView = findViewById(R.id.recyclerView) // Stelle sicher, dass du eine RecyclerView in activity_main.xml hast
        recyclerView.layoutManager = LinearLayoutManager(this)
        essenAdapter = EssenAdapter(listOf()) // Initialisiere den Adapter mit einer leeren Liste
        recyclerView.adapter = essenAdapter

        // API-Anfrage ausführen
        fetchEssenList()
    }

    private fun fetchEssenList() {
        RetrofitClient.apiService.getAllEssen().enqueue(object : Callback<List<Essen>> {
            override fun onResponse(call: Call<List<Essen>>, response: Response<List<Essen>>) {
                if (response.isSuccessful) {
                    val essenList = response.body()
                    if (essenList != null) {
                        // Verarbeite die Liste der Essen
                        essenAdapter.updateData(essenList) // Update-Methode im Adapter aufrufen
                        Toast.makeText(this@MainActivity, "Daten erfolgreich geladen!", Toast.LENGTH_SHORT).show()
                        Log.d("MainActivity", "Daten erfolgreich geladen: ${essenList.size} Einträge")
                    } else {
                        Toast.makeText(this@MainActivity, "Leere Antwort erhalten!", Toast.LENGTH_SHORT).show()
                        Log.e("MainActivity", "Antwort ist leer")
                    }
                } else {
                    // Fehlerbehandlung
                    Toast.makeText(this@MainActivity, "Fehler beim Laden der Daten!", Toast.LENGTH_SHORT).show()
                    Log.e("MainActivity", "Fehlerhafte Antwort: ${response.errorBody()?.string()}")
                }
            }


            override fun onFailure(call: Call<List<Essen>>, t: Throwable) {
                // Fehlerbehandlung
                Toast.makeText(this@MainActivity, "Verbindungsfehler!", Toast.LENGTH_SHORT).show()
                Log.e("MainActivity", "API-Anfrage fehlgeschlagen: ${t.message}", t)
            }
        })
    }
}
