package com.example.webmo2024app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.webmo2024app.R
import com.example.webmo2024app.model.Essen
import com.example.webmo2024app.model.Plan
import com.example.webmo2024app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.webmo2024app.network.ApiService

class EssensplanErstellenFragment : Fragment() {

    private lateinit var weekSpinner: Spinner
    private lateinit var mondaySpinner: Spinner
    private lateinit var tuesdaySpinner: Spinner
    private lateinit var wednesdaySpinner: Spinner
    private lateinit var thursdaySpinner: Spinner
    private lateinit var fridaySpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var apiService: ApiService

    private val essenList = mutableListOf<Essen>() // Liste für die Essensdaten

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_essensplan_erstellen, container, false)

        // Initialisiere die UI-Komponenten
        weekSpinner = view.findViewById(R.id.spinner_week)
        mondaySpinner = view.findViewById(R.id.spinner_monday)
        tuesdaySpinner = view.findViewById(R.id.spinner_tuesday)
        wednesdaySpinner = view.findViewById(R.id.spinner_wednesday)
        thursdaySpinner = view.findViewById(R.id.spinner_thursday)
        fridaySpinner = view.findViewById(R.id.spinner_friday)
        saveButton = view.findViewById(R.id.button_save_plan)

        // API-Service initialisieren
        apiService = RetrofitClient.create(requireContext())

        // Lade die Essensdaten vom Server
        loadEssenData()

        saveButton.setOnClickListener {
            saveEssensplan()
        }

        return view
    }

    // Funktion zum Abrufen der Essensdaten vom Server
    private fun loadEssenData() {
        val call = apiService.getAllEssen()
        call.enqueue(object : Callback<List<Essen>> {
            override fun onResponse(call: Call<List<Essen>>, response: Response<List<Essen>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        essenList.clear()
                        essenList.addAll(it)
                        populateSpinners() // Befülle die Spinner, nachdem die Daten geladen wurden
                    }
                } else {
                    Toast.makeText(context, "Fehler beim Laden der Essensdaten", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Essen>>, t: Throwable) {
                Toast.makeText(context, "Netzwerkfehler: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Funktion zum Befüllen der Spinner mit den Essensdaten
    private fun populateSpinners() {
        val essenNames = essenList.map { it.name } // Extrahiere nur die Namen der Essen
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, essenNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        mondaySpinner.adapter = adapter
        tuesdaySpinner.adapter = adapter
        wednesdaySpinner.adapter = adapter
        thursdaySpinner.adapter = adapter
        fridaySpinner.adapter = adapter
    }

    // Funktion zum Speichern des Plans
    private fun saveEssensplan() {
        val selectedWeek = weekSpinner.selectedItem.toString().toInt()
        val mondayMeal = mondaySpinner.selectedItem.toString()
        val tuesdayMeal = tuesdaySpinner.selectedItem.toString()
        val wednesdayMeal = wednesdaySpinner.selectedItem.toString()
        val thursdayMeal = thursdaySpinner.selectedItem.toString()
        val fridayMeal = fridaySpinner.selectedItem.toString()

        val plan = Plan(
            id = 0, // Beispiel-ID
            week_number = selectedWeek,
            created_at = "" // Ein gültiges Erstellungsdatum einfügen
        )

        val call = apiService.addEssensplan(plan)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Plan erfolgreich gespeichert!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Fehler beim Speichern des Plans", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Netzwerkfehler: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
