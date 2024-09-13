package com.example.webmo2024app

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.webmo2024app.model.Essen
import com.example.webmo2024app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.webmo2024app.network.ApiService // Füge diesen Import hinzu

class EditPlanActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var spinnerDays: Spinner
    private lateinit var spinnerMeals: Spinner
    private lateinit var btnSavePlan: Button
    private lateinit var btnViewPlans: Button
    private lateinit var tvSuccessMessage: TextView
    private lateinit var tvErrorMessage: TextView
    private val essenList = mutableListOf<Essen>()
    private val plan = mutableMapOf<String, String?>()

    // Initialisiere apiService
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_plan)

        // Initialisiere den API-Service
        apiService = RetrofitClient.create(applicationContext)

        // UI-Komponenten initialisieren
        tvTitle = findViewById(R.id.tvTitle)
        spinnerDays = findViewById(R.id.spinnerDays)
        spinnerMeals = findViewById(R.id.spinnerMeals)
        btnSavePlan = findViewById(R.id.btnSavePlan)
        btnViewPlans = findViewById(R.id.btnViewPlans)
        tvSuccessMessage = findViewById(R.id.tvSuccessMessage)
        tvErrorMessage = findViewById(R.id.tvErrorMessage)

        // Essen-Liste laden
        loadEssen()

        // Button Listener hinzufügen
        btnSavePlan.setOnClickListener {
            saveEditedPlan()
        }

        btnViewPlans.setOnClickListener {
            viewPlans()
        }
    }

    private fun loadEssen() {
        apiService.getAllEssen().enqueue(object : Callback<List<Essen>> {
            override fun onResponse(call: Call<List<Essen>>, response: Response<List<Essen>>) {
                if (response.isSuccessful) {
                    essenList.clear()
                    essenList.addAll(response.body() ?: emptyList())
                    val adapter = ArrayAdapter(this@EditPlanActivity, android.R.layout.simple_spinner_item, essenList.map { it.name })
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerMeals.adapter = adapter
                } else {
                    tvErrorMessage.text = "Fehler beim Laden der Essen"
                    tvErrorMessage.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<List<Essen>>, t: Throwable) {
                tvErrorMessage.text = "Fehler beim Laden der Essen"
                tvErrorMessage.visibility = View.VISIBLE
            }
        })
    }

    private fun saveEditedPlan() {
        tvSuccessMessage.text = "Plan erfolgreich gespeichert!"
        tvSuccessMessage.visibility = View.VISIBLE
        tvErrorMessage.visibility = View.GONE
    }

    private fun viewPlans() {
        // Navigiere zur Essensplan-Seite
    }
}
