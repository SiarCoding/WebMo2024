package com.example.webmo2024app.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.example.webmo2024app.R
import com.example.webmo2024app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.webmo2024app.network.ApiService
import com.example.webmo2024app.model.Essen

class AddFoodFragment : Fragment() {

    private lateinit var nameEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var typeSpinner: Spinner
    private lateinit var addButton: Button
    private lateinit var messageTextView: TextView
    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_food, container, false)

        // Initialize the UI components
        nameEditText = view.findViewById(R.id.editTextName)
        priceEditText = view.findViewById(R.id.editTextPrice)
        typeSpinner = view.findViewById(R.id.spinnerType)
        addButton = view.findViewById(R.id.buttonAdd)
        messageTextView = view.findViewById(R.id.textMessage)

        // Initialize the ApiService
        apiService = RetrofitClient.create(requireContext())

        // Set up the spinner with options
        val types = arrayOf("Vegan", "Mit Fleisch", "Vegetarisch")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = adapter

        addButton.setOnClickListener {
            addFood()
        }

        return view
    }

    private fun addFood() {
        val name = nameEditText.text.toString().trim()
        val priceText = priceEditText.text.toString().trim()
        val price = priceText.toFloatOrNull() ?: 0f
        val type = typeSpinner.selectedItem.toString()

        // Input validation
        if (name.isEmpty() || priceText.isEmpty() || price <= 0 || type.isEmpty()) {
            messageTextView.text = getString(R.string.please_fill_all_fields)
            return
        }

        // Retrieve token
        val token = getToken() ?: ""
        if (token.isEmpty()) {
            messageTextView.text = getString(R.string.error_token_missing)
            return
        }

        // Create a new Essen object
        val foodData = Essen(
            id = 0, // Set id to 0 or any default value if the ID is not needed for creation
            name = name,
            price = price,
            type = type
        )

        // API call with the correct token
        apiService.addEssen("Bearer $token", foodData).enqueue(object : Callback<Essen> {
            override fun onResponse(call: Call<Essen>, response: Response<Essen>) {
                if (response.isSuccessful) {
                    messageTextView.text = getString(R.string.food_added_successfully)
                    nameEditText.text.clear()
                    priceEditText.text.clear()
                    typeSpinner.setSelection(0)
                } else {
                    Log.e("AddFoodFragment", "Fehler beim Hinzufügen des Essens: ${response.errorBody()?.string()}")
                    messageTextView.text = getString(R.string.error_adding_food)
                }
            }

            override fun onFailure(call: Call<Essen>, t: Throwable) {
                Log.e("AddFoodFragment", "Serverfehler: ${t.message}")
                messageTextView.text = getString(R.string.server_error)
            }
        })
    }

    private fun getToken(): String? {
        val sharedPref = requireContext().getSharedPreferences("app_prefs", AppCompatActivity.MODE_PRIVATE)
        return sharedPref.getString("token", null)
    }
}
