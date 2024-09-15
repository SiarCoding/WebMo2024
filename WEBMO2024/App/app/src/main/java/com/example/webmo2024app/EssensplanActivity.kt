package com.example.webmo2024app

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.webmo2024app.model.Essen
import com.example.webmo2024app.model.PlanResponse
import com.example.webmo2024app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.webmo2024app.network.ApiService
import com.example.webmo2024app.model.Plan
import com.example.webmo2024app.model.PlanDetail
import com.example.webmo2024app.model.PlanEntry

class EssensplanActivity : AppCompatActivity() {

    private lateinit var weekSpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var viewPlansButton: Button
    private lateinit var deleteButton: Button
    private lateinit var successMessage: TextView
    private lateinit var errorMessage: TextView

    private lateinit var apiService: ApiService

    private val plan: MutableMap<String, Any?> = mutableMapOf(
        "Montag" to null,
        "Dienstag" to null,
        "Mittwoch" to null,
        "Donnerstag" to null,
        "Freitag" to null
    )

    private var selectedWeek = 1
    private val essenList = mutableListOf<Essen>()
    private var selectedPlanId: Int? = null // Variable für die ausgewählte Plan-ID
    private var lastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_essensplan)

        apiService = RetrofitClient.create(applicationContext)

        weekSpinner = findViewById(R.id.weekSpinner)
        saveButton = findViewById(R.id.saveButton)
        viewPlansButton = findViewById(R.id.viewPlansButton)
        deleteButton = findViewById(R.id.deleteButton)
        successMessage = findViewById(R.id.successMessage)
        errorMessage = findViewById(R.id.errorMessage)

        setupWeekSpinner()
        loadEssen()

        saveButton.setOnClickListener { savePlan() }
        viewPlansButton.setOnClickListener { viewPlans() }

        deleteButton.setOnClickListener {
            if (System.currentTimeMillis() - lastClickTime < 1000) {
                return@setOnClickListener
            }
            lastClickTime = System.currentTimeMillis()
            val planId = selectedPlanId
            if (planId != null) {
                deletePlan(planId)
            } else {
                showMessage("Kein gültiger Essensplan ausgewählt.")
            }
        }
    }

    private fun setupWeekSpinner() {
        val weeks = (1..8).map { "Woche $it" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, weeks)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        weekSpinner.adapter = adapter
        weekSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedWeek = position + 1
                loadPlanForSelectedWeek()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadEssen() {
        val token = getToken() ?: ""
        if (token.isEmpty()) {
            showMessage("Kein Authentifizierungstoken gefunden.")
            return
        }

        apiService.getAllEssen("Bearer $token").enqueue(object : Callback<List<Essen>> {
            override fun onResponse(call: Call<List<Essen>>, response: Response<List<Essen>>) {
                if (response.isSuccessful) {
                    essenList.clear()
                    essenList.addAll(response.body() ?: emptyList())
                } else {
                    showMessage("Fehler beim Laden der Essen")
                }
            }

            override fun onFailure(call: Call<List<Essen>>, t: Throwable) {
                showMessage("Fehler beim Laden der Essen: ${t.message}")
            }
        })
    }

    private fun loadPlans() {
        val token = getToken() ?: ""
        if (token.isEmpty()) {
            showMessage("Kein Authentifizierungstoken gefunden.")
            return
        }

        apiService.getAllEssensplaene("Bearer $token").enqueue(object : Callback<List<Plan>> {
            override fun onResponse(call: Call<List<Plan>>, response: Response<List<Plan>>) {
                if (response.isSuccessful) {
                    // Handle the response to get all plans
                } else {
                    showMessage("Fehler beim Laden der Essenspläne.")
                }
            }

            override fun onFailure(call: Call<List<Plan>>, t: Throwable) {
                showMessage("Fehler beim Laden der Essenspläne: ${t.message}")
            }
        })
    }

    private fun loadPlanForSelectedWeek() {
        val token = getToken() ?: ""
        if (token.isEmpty()) {
            showMessage("Kein Authentifizierungstoken gefunden.")
            return
        }

        apiService.getPlanForWeek("Bearer $token", selectedWeek).enqueue(object : Callback<PlanResponse> {
            override fun onResponse(call: Call<PlanResponse>, response: Response<PlanResponse>) {
                if (response.isSuccessful) {
                    val planResponse = response.body()

                    if (planResponse != null) {
                        // Speichere die tatsächliche plan_id aus der Serverantwort
                        selectedPlanId = planResponse.plan_id
                        successMessage.text = "Plan für Woche $selectedWeek erfolgreich geladen!"
                        successMessage.visibility = View.VISIBLE
                        errorMessage.visibility = View.GONE
                        Log.d("PlanActivity", "Erhaltene Plan-ID: $selectedPlanId")
                    } else {
                        showMessage("Essensplan für die ausgewählte Woche nicht gefunden.")
                        selectedPlanId = null
                    }
                } else {
                    showMessage("Fehler beim Laden der Essenspläne.")
                    selectedPlanId = null
                }
            }

            override fun onFailure(call: Call<PlanResponse>, t: Throwable) {
                showMessage("Fehler beim Laden der Essenspläne: ${t.message}")
                selectedPlanId = null
            }
        })
    }

    private fun savePlan() {
        val token = getToken() ?: ""
        if (token.isEmpty()) {
            showMessage("Kein Authentifizierungstoken gefunden.")
            return
        }

        val planEntries = listOf(
            PlanEntry(tag = "Montag", essen_id = (plan["Montag"] as? Essen)?.id ?: 0),
            PlanEntry(tag = "Dienstag", essen_id = (plan["Dienstag"] as? Essen)?.id ?: 0),
            PlanEntry(tag = "Mittwoch", essen_id = (plan["Mittwoch"] as? Essen)?.id ?: 0),
            PlanEntry(tag = "Donnerstag", essen_id = (plan["Donnerstag"] as? Essen)?.id ?: 0),
            PlanEntry(tag = "Freitag", essen_id = (plan["Freitag"] as? Essen)?.id ?: 0)
        )

        val planDetail = PlanDetail(
            plan_id = 0,
            wochennummer = selectedWeek,
            plan = planEntries
        )

        apiService.savePlanForWeek("Bearer $token", planDetail).enqueue(object : Callback<PlanResponse> {
            override fun onResponse(call: Call<PlanResponse>, response: Response<PlanResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val planId = responseBody?.plan_id ?: 0
                    selectedPlanId = planId
                    Log.d("PlanActivity", "Plan erfolgreich gespeichert. ID: $planId")
                    showMessage("Plan erfolgreich gespeichert! ID: $planId")
                } else {
                    showMessage("Fehler beim Speichern des Plans")
                }
            }

            override fun onFailure(call: Call<PlanResponse>, t: Throwable) {
                showMessage("Fehler beim Speichern des Plans: ${t.message}")
            }
        })
    }

    private fun deletePlan(planId: Int) {
        Log.d("DeletePlan", "Delete request for Plan ID: $planId")
        val token = getToken() ?: ""
        if (token.isEmpty()) {
            showMessage("Kein Authentifizierungstoken gefunden.")
            return
        }

        apiService.deleteEssensplan("Bearer $token", planId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showMessage("Plan erfolgreich gelöscht!")
                    loadPlans() // Aktualisiere die Liste der Pläne nach dem Löschen
                } else {
                    showMessage("Fehler beim Löschen des Plans")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showMessage("Fehler beim Löschen des Plans: ${t.message}")
            }
        })
    }

    private fun viewPlans() {
        loadPlans() // Lade alle Pläne, wenn die Schaltfläche "viewPlans" geklickt wird
    }

    private fun showMessage(message: String) {
        errorMessage.text = message
        errorMessage.visibility = View.VISIBLE
        successMessage.visibility = View.GONE
    }

    private fun getToken(): String? {
        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPref.getString("token", null)
    }
}
