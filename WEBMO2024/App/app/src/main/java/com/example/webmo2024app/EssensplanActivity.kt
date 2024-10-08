package com.example.webmo2024app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.webmo2024app.model.Essen
import com.example.webmo2024app.model.PlanResponse
import com.example.webmo2024app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EssensplanActivity : AppCompatActivity() {

    private lateinit var weekSpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var viewPlansButton: Button
    private lateinit var successMessage: TextView
    private lateinit var errorMessage: TextView
    private val plan: MutableMap<String, String?> = mutableMapOf(
        "Montag" to null,
        "Dienstag" to null,
        "Mittwoch" to null,
        "Donnerstag" to null,
        "Freitag" to null
    )

    private var selectedWeek = 1
    private val essenList = mutableListOf<Essen>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_essensplan)

        weekSpinner = findViewById(R.id.weekSpinner)
        saveButton = findViewById(R.id.saveButton)
        viewPlansButton = findViewById(R.id.viewPlansButton)
        successMessage = findViewById(R.id.successMessage)
        errorMessage = findViewById(R.id.errorMessage)

        setupWeekSpinner()
        loadEssen()

        saveButton.setOnClickListener { savePlan() }
        viewPlansButton.setOnClickListener { viewPlans() }
    }

    private fun setupWeekSpinner() {
        val weeks = (1..8).map { "Woche $it" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, weeks)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        weekSpinner.adapter = adapter
        weekSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedWeek = position + 1
                loadPlan()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadEssen() {
        RetrofitClient.apiService.getAllEssen().enqueue(object : Callback<List<Essen>> {
            override fun onResponse(call: Call<List<Essen>>, response: Response<List<Essen>>) {
                if (response.isSuccessful) {
                    essenList.clear()
                    essenList.addAll(response.body() ?: emptyList())
                    // UI-Update, wenn Daten erfolgreich geladen werden
                } else {
                    errorMessage.text = "Fehler beim Laden der Essen"
                }
            }

            override fun onFailure(call: Call<List<Essen>>, t: Throwable) {
                errorMessage.text = "Fehler beim Laden der Essen: ${t.message}"
            }
        })
    }

    private fun loadPlan() {
        // API-Aufruf, um den Plan für die ausgewählte Woche zu laden
        RetrofitClient.apiService.getPlanForWeek(selectedWeek).enqueue(object : Callback<PlanResponse> {
            override fun onResponse(call: Call<PlanResponse>, response: Response<PlanResponse>) {
                if (response.isSuccessful) {
                    val planResponse = response.body()
                    // Aktualisiere den Plan basierend auf den geladenen Daten
                    plan["Montag"] = planResponse?.monday
                    plan["Dienstag"] = planResponse?.tuesday
                    plan["Mittwoch"] = planResponse?.wednesday
                    plan["Donnerstag"] = planResponse?.thursday
                    plan["Freitag"] = planResponse?.friday
                    successMessage.text = "Plan erfolgreich geladen!"
                } else {
                    errorMessage.text = "Fehler beim Laden des Plans"
                }
            }

            override fun onFailure(call: Call<PlanResponse>, t: Throwable) {
                errorMessage.text = "Fehler beim Laden des Plans: ${t.message}"
            }
        })
    }

    private fun savePlan() {
        // Beispielhafte Logik, um den Plan zu speichern
        RetrofitClient.apiService.savePlanForWeek(selectedWeek, plan).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    successMessage.text = "Plan erfolgreich gespeichert!"
                    successMessage.visibility = View.VISIBLE
                    errorMessage.visibility = View.GONE
                } else {
                    errorMessage.text = "Fehler beim Speichern des Plans"
                    errorMessage.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                errorMessage.text = "Fehler beim Speichern des Plans: ${t.message}"
                errorMessage.visibility = View.VISIBLE
            }
        })
    }

    private fun viewPlans() {
        startActivity(Intent(this, ViewPlansActivity::class.java))
    }
}
