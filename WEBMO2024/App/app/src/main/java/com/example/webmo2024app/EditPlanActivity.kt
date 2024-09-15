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
import com.example.webmo2024app.network.ApiService
import com.example.webmo2024app.model.PlanDetail
import com.example.webmo2024app.model.PlanEntry
import com.example.webmo2024app.model.PlanResponse // Import hinzufügen

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

    private lateinit var apiService: ApiService
    private var selectedWeek: Int = 1 // Initialisiere die Variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_plan)

        // API-Service initialisieren
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
        // Token abrufen
        val token = getToken() ?: ""
        if (token.isEmpty()) {
            tvErrorMessage.text = "Kein Authentifizierungstoken gefunden."
            tvErrorMessage.visibility = View.VISIBLE
            return
        }

        apiService.getAllEssen("Bearer $token").enqueue(object : Callback<List<Essen>> {
            override fun onResponse(call: Call<List<Essen>>, response: Response<List<Essen>>) {
                if (response.isSuccessful) {
                    essenList.clear()
                    essenList.addAll(response.body() ?: emptyList())
                    val adapter = ArrayAdapter(
                        this@EditPlanActivity,
                        android.R.layout.simple_spinner_item,
                        essenList.map { it.name }
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerMeals.adapter = adapter
                } else {
                    tvErrorMessage.text = "Fehler beim Laden der Essen"
                    tvErrorMessage.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<List<Essen>>, t: Throwable) {
                tvErrorMessage.text = "Fehler beim Laden der Essen: ${t.message}"
                tvErrorMessage.visibility = View.VISIBLE
            }
        })
    }

    private fun saveEditedPlan() {
        // Token abrufen
        val token = getToken() ?: ""
        if (token.isEmpty()) {
            tvErrorMessage.text = "Kein Authentifizierungstoken gefunden."
            tvErrorMessage.visibility = View.VISIBLE
            return
        }

        // Beispielplan für den Aufruf
        val planId = 0 // Hier sollte die tatsächliche plan_id stehen

        // Erstelle das PlanDetail-Objekt
        val planDetails = PlanDetail(
            plan_id = planId,  // Setze hier die korrekte plan_id
            wochennummer = selectedWeek, // Verwende die initialisierte Variable selectedWeek
            plan = listOf(
                PlanEntry(tag = "Montag", essen_id = 1),
                PlanEntry(tag = "Dienstag", essen_id = 2)
            )
        )

        apiService.savePlanForWeek("Bearer $token", planDetails).enqueue(object : Callback<PlanResponse> {
            override fun onResponse(call: Call<PlanResponse>, response: Response<PlanResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val planId = responseBody?.plan_id ?: 0 // Erhalte die plan_id vom Server
                    tvSuccessMessage.text = "Plan erfolgreich gespeichert! ID: $planId"
                    tvSuccessMessage.visibility = View.VISIBLE
                    tvErrorMessage.visibility = View.GONE
                } else {
                    tvErrorMessage.text = "Fehler beim Speichern des Plans"
                    tvErrorMessage.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<PlanResponse>, t: Throwable) {
                tvErrorMessage.text = "Fehler beim Speichern des Plans: ${t.message}"
                tvErrorMessage.visibility = View.VISIBLE
            }
        })
    }

    private fun viewPlans() {
        // Navigiere zur Essensplan-Seite
    }

    private fun getToken(): String? {
        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPref.getString("token", null)
    }
}
