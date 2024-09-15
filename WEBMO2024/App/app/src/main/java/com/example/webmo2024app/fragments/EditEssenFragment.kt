package com.example.webmo2024app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.example.webmo2024app.R
import com.example.webmo2024app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.webmo2024app.network.ApiService
import com.example.webmo2024app.model.Essen


class EditEssenFragment : Fragment() {

    private lateinit var nameEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var typeEditText: EditText
    private lateinit var saveButton: Button

    private lateinit var apiService: ApiService
    private var essen: Essen? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_essen, container, false)

        // Initialisiere den API-Service
        apiService = RetrofitClient.create(requireContext())

        nameEditText = view.findViewById(R.id.editTextName)
        priceEditText = view.findViewById(R.id.editTextPrice)
        typeEditText = view.findViewById(R.id.editTextType)
        saveButton = view.findViewById(R.id.buttonSave)

        // Prüfe, ob Essen-Daten übergeben wurden
        essen = arguments?.getSerializable("essen") as? Essen

        // Wenn Essen-Daten vorhanden sind, Felder vorfüllen
        essen?.let {
            nameEditText.setText(it.name)
            priceEditText.setText(it.price.toString())
            typeEditText.setText(it.type)
        }

        saveButton.setOnClickListener {
            saveEssen()
        }

        return view
    }

    private fun saveEssen() {
        val name = nameEditText.text.toString()
        val price = priceEditText.text.toString().toFloatOrNull()
        val type = typeEditText.text.toString()

        if (name.isNotEmpty() && price != null && type.isNotEmpty()) {
            val token = getToken() ?: ""
            if (token.isEmpty()) {
                Toast.makeText(context, "Kein Authentifizierungstoken gefunden.", Toast.LENGTH_SHORT).show()
                return
            }

            // Erstelle eine Map für die zu aktualisierenden Daten
            val updatedEssenData = mapOf(
                "name" to name,
                "preis" to price,
                "art" to type
            )

            val call = apiService.updateEssen("Bearer $token", essen?.id ?: 0, updatedEssenData)
            call.enqueue(object : Callback<Essen> {
                override fun onResponse(call: Call<Essen>, response: Response<Essen>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Essen erfolgreich aktualisiert", Toast.LENGTH_SHORT).show()
                        // Zurück zur Liste navigieren oder andere Logik hier
                    } else {
                        Toast.makeText(context, "Fehler beim Aktualisieren des Essens", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Essen>, t: Throwable) {
                    Toast.makeText(context, "Netzwerkfehler: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(context, "Bitte alle Felder korrekt ausfüllen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getToken(): String? {
        val sharedPref = requireContext().getSharedPreferences("app_prefs", AppCompatActivity.MODE_PRIVATE)
        return sharedPref.getString("token", null)
    }
}
