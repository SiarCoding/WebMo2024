package com.example.webmo2024app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.webmo2024app.model.PlanResponse
import com.example.webmo2024app.network.ApiService
import com.example.webmo2024app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.app.AppCompatActivity
import com.example.webmo2024app.R
import java.util.Locale

class EssensplanFragment : Fragment() {

    private lateinit var tableLayout: TableLayout
    private lateinit var tvTotalPrice: TextView
    private lateinit var tvSelectedPlanWeek: TextView
    private lateinit var btnPreviousWeek: Button
    private lateinit var btnNextWeek: Button
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button

    private lateinit var apiService: ApiService
    private var selectedWeek = 1
    private var currentPlanId: Int? = null  // Variable zur Speicherung der aktuellen Plan-ID

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_essensplan, container, false)

        // UI-Elemente initialisieren
        tableLayout = view.findViewById(R.id.tablePlan)
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice)
        tvSelectedPlanWeek = view.findViewById(R.id.tvSelectedPlanWeek)
        btnPreviousWeek = view.findViewById(R.id.btnPreviousWeek)
        btnNextWeek = view.findViewById(R.id.btnNextWeek)
        btnEdit = view.findViewById(R.id.btnEdit)
        btnDelete = view.findViewById(R.id.btnDelete)

        apiService = RetrofitClient.create(requireContext())

        updateWeekTextView()
        loadPlanForSelectedWeek()

        btnPreviousWeek.setOnClickListener {
            if (selectedWeek > 1) {
                selectedWeek--
                updateWeekTextView()
                loadPlanForSelectedWeek()
            }
        }

        btnNextWeek.setOnClickListener {
            if (selectedWeek < 8) {
                selectedWeek++
                updateWeekTextView()
                loadPlanForSelectedWeek()
            }
        }

        btnEdit.setOnClickListener {
            openEditPlanFragment()
        }

        btnDelete.setOnClickListener {
            deletePlanForSelectedWeek()
        }

        return view
    }

    private fun updateWeekTextView() {
        val weekText = getString(R.string.week_number, selectedWeek)
        tvSelectedPlanWeek.text = weekText
    }

    private fun loadPlanForSelectedWeek() {
        val token = getToken() ?: ""
        if (token.isEmpty()) {
            showMessage(getString(R.string.error_token_missing))
            return
        }

        apiService.getPlanForWeek("Bearer $token", selectedWeek).enqueue(object : Callback<PlanResponse> {
            override fun onResponse(call: Call<PlanResponse>, response: Response<PlanResponse>) {
                if (response.isSuccessful) {
                    val planResponse = response.body()
                    // Überprüfe, ob die plan_id gültig ist
                    if (planResponse?.plan_id != null && planResponse.plan_id > 0) {
                        currentPlanId = planResponse.plan_id
                    } else {
                        currentPlanId = null
                        showMessage("Ungültige Plan-ID erhalten.")
                    }
                    println("Server response: ${response.body()}")
                    updatePlanUI(planResponse)
                } else {
                    showMessage("Fehler beim Laden des Plans")
                    updatePlanUI(null)
                }
            }

            override fun onFailure(call: Call<PlanResponse>, t: Throwable) {
                showMessage(getString(R.string.general_error, t.message ?: "Unbekannter Fehler"))
                updatePlanUI(null)
            }
        })
    }

    private fun updatePlanUI(planResponse: PlanResponse?) {
        // Überprüfe, ob es Zeilen gibt, die entfernt werden sollen
        if (tableLayout.childCount > 1) {
            tableLayout.removeViews(1, tableLayout.childCount - 1)
        }

        if (planResponse == null) return

        var totalPrice = 0.0

        val daysAndMeals = listOf(
            "Montag" to planResponse.days["Montag"],
            "Dienstag" to planResponse.days["Dienstag"],
            "Mittwoch" to planResponse.days["Mittwoch"],
            "Donnerstag" to planResponse.days["Donnerstag"],
            "Freitag" to planResponse.days["Freitag"]
        )

        daysAndMeals.forEach { (day, meal) ->
            val tableRow = TableRow(context)
            val dayTextView = TextView(context).apply {
                text = day
                setPadding(8, 8, 8, 8)
                setTextColor(context?.getColor(R.color.black) ?: resources.getColor(R.color.black))
            }

            val nameTextView = TextView(context).apply {
                text = meal?.food_name ?: "Nicht ausgewählt"
                setPadding(8, 8, 8, 8)
                setTextColor(context?.getColor(R.color.black) ?: resources.getColor(R.color.black))
            }

            val priceTextView = TextView(context).apply {
                text = String.format(Locale.getDefault(), "%.2f €", meal?.price ?: 0.0)
                setPadding(8, 8, 8, 8)
                setTextColor(context?.getColor(R.color.black) ?: resources.getColor(R.color.black))
            }

            tableRow.addView(dayTextView)
            tableRow.addView(nameTextView)
            tableRow.addView(priceTextView)
            tableLayout.addView(tableRow)

            totalPrice += meal?.price ?: 0.0
        }

        tvTotalPrice.text = String.format(Locale.getDefault(), "Gesamtpreis: %.2f €", totalPrice)
    }

    private fun deletePlanForSelectedWeek() {
        val token = getToken() ?: ""
        if (token.isEmpty()) {
            showMessage(getString(R.string.error_token_missing))
            return
        }

        val planId = currentPlanId  // Verwende die tatsächlich existierende Plan-ID

        if (planId == null || planId <= 0) {
            showMessage("Kein gültiger Plan gefunden, um ihn zu löschen.")
            return
        }

        apiService.deleteEssensplan("Bearer $token", planId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showMessage("Essensplan erfolgreich gelöscht")
                    loadPlanForSelectedWeek()
                } else {
                    showMessage("Fehler beim Löschen des Essensplans")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showMessage(getString(R.string.general_error, t.message ?: "Unbekannter Fehler"))
            }
        })
    }

    private fun openEditPlanFragment() {
        val fragment = EditEssensplanFragment()
        val bundle = Bundle()
        bundle.putInt("planId", currentPlanId ?: 0) // Übergibt die Plan-ID an das Fragment
        bundle.putInt("weekNumber", selectedWeek) // Übergibt die Woche an das Fragment
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun getToken(): String? {
        val sharedPref = requireContext().getSharedPreferences("app_prefs", AppCompatActivity.MODE_PRIVATE)
        return sharedPref.getString("token", null)
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
