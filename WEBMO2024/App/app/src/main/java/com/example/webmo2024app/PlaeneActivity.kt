package com.example.webmo2024app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.webmo2024app.model.Plan
import com.example.webmo2024app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.webmo2024app.PlansAdapter
import com.example.webmo2024app.network.ApiService

class PlaeneActivity : AppCompatActivity() {

    private lateinit var buttonAddPlan: Button
    private lateinit var recyclerViewPlans: RecyclerView
    private lateinit var textViewMessage: TextView
    private lateinit var plansAdapter: PlansAdapter
    private var essensplaene = mutableListOf<Plan>()

    // Initialisiere apiService hier
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plaene)

        // Initialisiere apiService
        apiService = RetrofitClient.create(applicationContext)

        // IDs korrekt referenzieren
        buttonAddPlan = findViewById(R.id.btnAddPlan)
        recyclerViewPlans = findViewById(R.id.recyclerViewPlans)
        textViewMessage = findViewById(R.id.textViewMessage)

        // RecyclerView konfigurieren
        plansAdapter = PlansAdapter(essensplaene, this::editPlan, this::deletePlan)
        recyclerViewPlans.layoutManager = LinearLayoutManager(this)
        recyclerViewPlans.adapter = plansAdapter

        // Button Listener
        buttonAddPlan.setOnClickListener {
            addPlan()
        }

        loadPlans() // Lade die vorhandenen Pläne aus dem Backend
    }

    // Lädt die Essenspläne vom Backend
    private fun loadPlans() {
        val call = apiService.getAllEssensplaene()
        call.enqueue(object : Callback<List<Plan>> {
            override fun onResponse(call: Call<List<Plan>>, response: Response<List<Plan>>) {
                if (response.isSuccessful) {
                    essensplaene.clear()
                    response.body()?.let { essensplaene.addAll(it) }
                    plansAdapter.notifyDataSetChanged()
                } else {
                    showMessage(getString(R.string.error_loading_plans))
                }
            }

            override fun onFailure(call: Call<List<Plan>>, t: Throwable) {
                showMessage(getString(R.string.error_loading_plans))
            }
        })
    }

    // Löscht einen Plan basierend auf der ID
    private fun deletePlan(planId: Int) {
        val call = apiService.deleteEssensplan(planId)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showMessage(getString(R.string.plan_deleted))
                    loadPlans()
                } else {
                    showMessage(getString(R.string.error_deleting_plan))
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showMessage(getString(R.string.error_deleting_plan))
            }
        })
    }

    // Startet die Activity zum Hinzufügen eines neuen Plans
    private fun addPlan() {
        startActivity(Intent(this, EssensplanActivity::class.java))
    }

    // Startet die Activity zum Bearbeiten eines Plans
    private fun editPlan(plan: Plan) {
        val intent = Intent(this, EditEssensplanActivity::class.java)
        intent.putExtra("planId", plan.id)
        startActivity(intent)
    }

    // Zeigt eine Nachricht an (Fehler oder Erfolg)
    private fun showMessage(message: String) {
        textViewMessage.text = message
        textViewMessage.visibility = View.VISIBLE
    }
}
