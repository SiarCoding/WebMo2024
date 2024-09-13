package com.example.webmo2024app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.webmo2024app.R
import com.example.webmo2024app.model.Essen
import com.example.webmo2024app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.webmo2024app.network.ApiService

class AddFoodFragment : Fragment() {

    private lateinit var nameEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var typeEditText: EditText
    private lateinit var addButton: Button

    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_food, container, false)

        // Initialisiere den ApiService
        apiService = RetrofitClient.create(requireContext())

        // Initialisiere die UI-Komponenten
        nameEditText = view.findViewById(R.id.editTextName)
        priceEditText = view.findViewById(R.id.editTextPrice)
        typeEditText = view.findViewById(R.id.editTextType)
        addButton = view.findViewById(R.id.buttonAdd)

        addButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val price = priceEditText.text.toString().toFloatOrNull()
            val type = typeEditText.text.toString()

            // Überprüfen, ob alle Felder korrekt ausgefüllt sind
            if (name.isNotEmpty() && price != null && type.isNotEmpty()) {
                val newEssen = Essen(
                    id = 0,  // Setze die ID auf 0 oder lasse sie null, wenn sie vom Server generiert wird
                    name = name,
                    price = price,
                    type = type
                )
                addEssen(newEssen)
            } else {
                Toast.makeText(context, "Bitte füllen Sie alle Felder korrekt aus", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun addEssen(essen: Essen) {
        // Verwende den initialisierten apiService
        val call = apiService.addEssen(essen)
        call.enqueue(object : Callback<Essen> {
            override fun onResponse(call: Call<Essen>, response: Response<Essen>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Essen erfolgreich hinzugefügt", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Fehler beim Hinzufügen des Essens", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Essen>, t: Throwable) {
                Toast.makeText(context, "Netzwerkfehler: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
