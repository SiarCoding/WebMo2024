package com.example.webmo2024app

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
import com.example.webmo2024app.fragments.EditEssensplanFragment

class PlaeneActivity : AppCompatActivity() {

    private lateinit var buttonAddPlan: Button
    private lateinit var recyclerViewPlans: RecyclerView
    private lateinit var textViewMessage: TextView
    private lateinit var plansAdapter: PlansAdapter
    private var essensplaene = mutableListOf<Plan>()

    private lateinit var apiService: ApiService
    private var userRole: String? = null // Variable für die Benutzerrolle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plaene)

        apiService = RetrofitClient.create(applicationContext)

        buttonAddPlan = findViewById(R.id.btnAddPlan)
        recyclerViewPlans = findViewById(R.id.recyclerViewPlans)
        textViewMessage = findViewById(R.id.textViewMessage)

        // Benutzerrolle aus SharedPreferences abrufen
        userRole = getSharedPreferences("app_prefs", MODE_PRIVATE).getString("role", "user")

        // Adapter mit Benutzerrolle initialisieren
        plansAdapter = PlansAdapter(essensplaene, this::editPlan, this::deletePlan, this, userRole)

        recyclerViewPlans.layoutManager = LinearLayoutManager(this)
        recyclerViewPlans.adapter = plansAdapter

        // Admin-spezifische Sichtbarkeit festlegen
        if (userRole == "admin") {
            buttonAddPlan.visibility = View.VISIBLE
        } else {
            buttonAddPlan.visibility = View.GONE
        }

        buttonAddPlan.setOnClickListener {
            addPlan()
        }

        loadPlans()
    }

    private fun loadPlans() {
        val token = getToken() ?: ""
        if (token.isEmpty()) {
            showMessage("Kein Authentifizierungstoken gefunden.")
            return
        }

        val call = apiService.getAllEssensplaene("Bearer $token")
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

    private fun deletePlan(planId: Int) {
        if (userRole != "admin") {
            showMessage("Nur Admins dürfen Pläne löschen.")
            return
        }

        val token = getToken() ?: ""
        if (token.isEmpty()) {
            showMessage("Kein Authentifizierungstoken gefunden.")
            return
        }

        val call = apiService.deleteEssensplan("Bearer $token", planId)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showMessage(getString(R.string.plan_deleted))
                    loadPlans()
                } else {
                    showMessage("Fehler beim Löschen des Plans: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showMessage("Fehler beim Löschen des Plans: ${t.message}")
            }
        })
    }

    private fun addPlan() {
        if (userRole != "admin") {
            showMessage("Nur Admins dürfen Pläne hinzufügen.")
            return
        }

        val fragment = EditEssensplanFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun editPlan(plan: Plan) {
        if (userRole != "admin") {
            showMessage("Nur Admins dürfen Pläne bearbeiten.")
            return
        }

        val fragment = EditEssensplanFragment()
        val bundle = Bundle()
        bundle.putInt("planId", plan.plan_id)
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showMessage(message: String) {
        textViewMessage.text = message
        textViewMessage.visibility = View.VISIBLE
    }

    private fun getToken(): String? {
        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPref.getString("token", null)
    }
}
