package com.example.webmo2024app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.webmo2024app.R
import com.example.webmo2024app.model.PlanDetail
import com.example.webmo2024app.model.PlanEntry
import com.example.webmo2024app.network.ApiService
import com.example.webmo2024app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.app.AppCompatActivity
import com.example.webmo2024app.model.Essen
import com.example.webmo2024app.model.PlanResponse

class EditEssensplanFragment : Fragment() {

    private lateinit var spinnerMontag: Spinner
    private lateinit var spinnerDienstag: Spinner
    private lateinit var spinnerMittwoch: Spinner
    private lateinit var spinnerDonnerstag: Spinner
    private lateinit var spinnerFreitag: Spinner
    private lateinit var btnSavePlan: Button

    private lateinit var apiService: ApiService
    private var currentPlanId: Int? = null // Aktuelle Plan-ID, die bearbeitet wird
    private val essenList = mutableListOf<Essen>()
    private var selectedWeek: Int = 1 // Standardwert auf 1 gesetzt

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_essensplan, container, false)

        // Initialisiere UI-Elemente
        spinnerMontag = view.findViewById(R.id.spinnerMontag)
        spinnerDienstag = view.findViewById(R.id.spinnerDienstag)
        spinnerMittwoch = view.findViewById(R.id.spinnerMittwoch)
        spinnerDonnerstag = view.findViewById(R.id.spinnerDonnerstag)
        spinnerFreitag = view.findViewById(R.id.spinnerFreitag)
        btnSavePlan = view.findViewById(R.id.btnSavePlan)

        // API-Service initialisieren
        apiService = RetrofitClient.create(requireContext())

        // Erhalte die übergebene Plan-ID
        currentPlanId = arguments?.getInt("planId")

        // Lade Essen und Plan-Details
        loadEssen()
        if (currentPlanId != null) {
            loadPlanDetails(currentPlanId!!)
        }

        // Klick-Listener für "Speichern"-Button
        btnSavePlan.setOnClickListener {
            savePlan()
        }

        return view
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
                    setupEssenSpinners()
                } else {
                    showMessage("Fehler beim Laden der Essen")
                }
            }

            override fun onFailure(call: Call<List<Essen>>, t: Throwable) {
                showMessage("Fehler beim Laden der Essen: ${t.message}")
            }
        })
    }

    private fun loadPlanDetails(planId: Int) {
        val token = getToken() ?: ""
        if (token.isEmpty()) {
            showMessage("Kein Authentifizierungstoken gefunden.")
            return
        }

        apiService.getPlanDetails("Bearer $token", planId).enqueue(object : Callback<PlanDetail> {
            override fun onResponse(call: Call<PlanDetail>, response: Response<PlanDetail>) {
                if (response.isSuccessful) {
                    val planDetail = response.body()
                    if (planDetail != null) {
                        updateUIWithPlanDetails(planDetail)
                    } else {
                        showMessage("Fehler beim Laden des Plans")
                    }
                } else {
                    showMessage("Fehler beim Laden des Plans")
                }
            }

            override fun onFailure(call: Call<PlanDetail>, t: Throwable) {
                showMessage("Fehler beim Laden des Plans: ${t.message}")
            }
        })
    }

    private fun updateUIWithPlanDetails(planDetail: PlanDetail) {
        val daysToSpinners = mapOf(
            "Montag" to spinnerMontag,
            "Dienstag" to spinnerDienstag,
            "Mittwoch" to spinnerMittwoch,
            "Donnerstag" to spinnerDonnerstag,
            "Freitag" to spinnerFreitag
        )

        planDetail.plan.forEach { entry ->
            daysToSpinners[entry.tag]?.setSelection(
                essenList.indexOfFirst { it.id == entry.essen_id }
            )
        }
    }

    private fun savePlan() {
        val token = getToken() ?: ""
        if (token.isEmpty()) {
            showMessage("Kein Authentifizierungstoken gefunden.")
            return
        }

        val planEntries = listOf(
            PlanEntry(tag = "Montag", essen_id = essenList[spinnerMontag.selectedItemPosition].id),
            PlanEntry(tag = "Dienstag", essen_id = essenList[spinnerDienstag.selectedItemPosition].id),
            PlanEntry(tag = "Mittwoch", essen_id = essenList[spinnerMittwoch.selectedItemPosition].id),
            PlanEntry(tag = "Donnerstag", essen_id = essenList[spinnerDonnerstag.selectedItemPosition].id),
            PlanEntry(tag = "Freitag", essen_id = essenList[spinnerFreitag.selectedItemPosition].id)
        )

        val planDetail = PlanDetail(
            plan_id = currentPlanId ?: 0,  // Verwende die tatsächliche Plan-ID
            wochennummer = selectedWeek,
            plan = planEntries
        )

        apiService.updatePlan("Bearer $token", planDetail).enqueue(object : Callback<PlanResponse> {
            override fun onResponse(call: Call<PlanResponse>, response: Response<PlanResponse>) {
                if (response.isSuccessful) {
                    showMessage("Plan erfolgreich aktualisiert!")
                } else {
                    showMessage("Fehler beim Aktualisieren des Plans")
                }
            }

            override fun onFailure(call: Call<PlanResponse>, t: Throwable) {
                showMessage("Fehler beim Aktualisieren des Plans: ${t.message}")
            }
        })
    }

    private fun getToken(): String? {
        val sharedPref = requireContext().getSharedPreferences("app_prefs", AppCompatActivity.MODE_PRIVATE)
        return sharedPref.getString("token", null)
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupEssenSpinners() {
        val essenNames = essenList.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, essenNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        listOf(spinnerMontag, spinnerDienstag, spinnerMittwoch, spinnerDonnerstag, spinnerFreitag).forEach {
            it.adapter = adapter
        }
    }
}
