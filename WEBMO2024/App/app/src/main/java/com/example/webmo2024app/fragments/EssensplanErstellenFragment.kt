package com.example.webmo2024app.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.example.webmo2024app.R
import com.example.webmo2024app.model.Essen
import com.example.webmo2024app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.webmo2024app.network.ApiService
import com.example.webmo2024app.model.PlanDetail
import com.example.webmo2024app.model.PlanEntry
import com.example.webmo2024app.model.PlanResponse // Import hinzugefügt

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

        // Wochen Spinner initialisieren
        val weekAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.weeks,
            android.R.layout.simple_spinner_item
        )
        weekAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        weekSpinner.adapter = weekAdapter

        // Lade die Essensdaten vom Server
        loadEssenData()

        saveButton.setOnClickListener {
            saveEssensplan()
        }

        return view
    }

    // Methode zum Abrufen des Tokens
    private fun getToken(): String? {
        val sharedPref =
            requireContext().getSharedPreferences("app_prefs", AppCompatActivity.MODE_PRIVATE)
        return sharedPref.getString("token", null)
    }

    // Funktion zum Abrufen der Essensdaten vom Server
    private fun loadEssenData() {
        val token = getToken() ?: ""
        val call = apiService.getAllEssen("Bearer $token")
        call.enqueue(object : Callback<List<Essen>> {
            override fun onResponse(call: Call<List<Essen>>, response: Response<List<Essen>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        essenList.clear()
                        essenList.addAll(it)
                        populateSpinners() // Befülle die Spinner, nachdem die Daten geladen wurden
                    }
                } else {
                    Toast.makeText(context, "Fehler beim Laden der Essensdaten", Toast.LENGTH_SHORT)
                        .show()
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
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, essenNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        mondaySpinner.adapter = adapter
        tuesdaySpinner.adapter = adapter
        wednesdaySpinner.adapter = adapter
        thursdaySpinner.adapter = adapter
        fridaySpinner.adapter = adapter
    }

    // Funktion zum Speichern des Plans
    private fun saveEssensplan() {
        val token = getToken() ?: ""

        val selectedWeek =
            weekSpinner.selectedItem?.toString()?.split(" ")?.get(1)?.toIntOrNull() ?: 0
        val mondayMeal = mondaySpinner.selectedItem?.toString() ?: ""
        val tuesdayMeal = tuesdaySpinner.selectedItem?.toString() ?: ""
        val wednesdayMeal = wednesdaySpinner.selectedItem?.toString() ?: ""
        val thursdayMeal = thursdaySpinner.selectedItem?.toString() ?: ""
        val fridayMeal = fridaySpinner.selectedItem?.toString() ?: ""

        // Überprüfen, ob die Woche gültig ausgewählt wurde
        if (selectedWeek == 0) {
            Toast.makeText(context, "Bitte wählen Sie eine gültige Woche aus.", Toast.LENGTH_SHORT)
                .show()
            return
        }

        // Planeinträge erstellen und sicherstellen, dass die IDs nicht null sind
        val planEntries = listOfNotNull(
            createPlanEntry("Montag", mondayMeal),
            createPlanEntry("Dienstag", tuesdayMeal),
            createPlanEntry("Mittwoch", wednesdayMeal),
            createPlanEntry("Donnerstag", thursdayMeal),
            createPlanEntry("Freitag", fridayMeal)
        )

        if (planEntries.isEmpty()) {
            Toast.makeText(
                context,
                "Bitte wählen Sie gültige Essen für die Woche aus.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val planDetail = PlanDetail(
            plan_id = 0,  // Setze hier den initialen Wert oder nutze eine serverseitig erzeugte ID
            wochennummer = selectedWeek,
            plan = planEntries
        )

        // API-Aufruf mit dem richtigen Token und Anfragekörper
        val call = apiService.savePlanForWeek("Bearer $token", planDetail)
        call.enqueue(object : Callback<PlanResponse> {
            override fun onResponse(call: Call<PlanResponse>, response: Response<PlanResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val planId = responseBody?.plan_id ?: 0 // Erhalte die plan_id vom Server
                    Toast.makeText(context, "Plan erfolgreich gespeichert! ID: $planId", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Log.e(
                        "EssensplanErstellenFragment",
                        "Fehler beim Speichern des Plans: ${response.errorBody()?.string()} | Code: ${response.code()}"
                    )
                    Toast.makeText(context, "Fehler beim Speichern des Plans", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<PlanResponse>, t: Throwable) {
                Log.e("EssensplanErstellenFragment", "Netzwerkfehler: ${t.message}")
                Toast.makeText(context, "Netzwerkfehler: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Hilfsfunktion, um sicherzustellen, dass die Planeinträge gültig sind
    private fun createPlanEntry(tag: String, mealName: String): PlanEntry? {
        val mealId = essenList.find { it.name == mealName }?.id
        return if (mealId != null) {
            PlanEntry(tag = tag, essen_id = mealId)
        } else {
            null
        }
    }
}
