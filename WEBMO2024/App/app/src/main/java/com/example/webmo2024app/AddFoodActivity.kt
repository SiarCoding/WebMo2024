package com.example.webmo2024app

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.webmo2024app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.webmo2024app.network.ApiService
import com.example.webmo2024app.model.Essen

class AddFoodActivity : AppCompatActivity() {

    private lateinit var inputName: EditText
    private lateinit var inputPrice: EditText
    private lateinit var spinnerType: Spinner
    private lateinit var buttonAddFood: Button
    private lateinit var buttonShowFood: Button
    private lateinit var textMessage: TextView

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_essen)

        // Initialize the ApiService
        apiService = RetrofitClient.create(applicationContext)

        // Initialize UI components
        inputName = findViewById(R.id.inputName)
        inputPrice = findViewById(R.id.inputPrice)
        spinnerType = findViewById(R.id.spinnerType)
        buttonAddFood = findViewById(R.id.buttonAddFood)
        buttonShowFood = findViewById(R.id.buttonShowFood)
        textMessage = findViewById(R.id.textMessage)

        // Fill spinner with options
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.typ, // Make sure you have an array resource named 'typ' in your strings.xml
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter

        // Add button listeners
        buttonAddFood.setOnClickListener {
            addFood()
        }

        buttonShowFood.setOnClickListener {
            // Navigation logic to go to the food list
            // startActivity(Intent(this, FoodListActivity::class.java))
        }
    }

    private fun addFood() {
        val name = inputName.text.toString().trim()
        val priceText = inputPrice.text.toString().trim()

        // Konvertiere den Preis in Float
        val price = priceText.toFloatOrNull()

        val type = spinnerType.selectedItem.toString()

        // Eingabevalidierung
        if (name.isEmpty() || price == null || price <= 0 || type.isEmpty()) {
            textMessage.text = getString(R.string.please_fill_all_fields)
            Log.e("AddFoodActivity", "Ungültige Eingabedaten: Name: $name, Price: $priceText, Type: $type")
            return
        }

        // Token abrufen
        val token = getToken() ?: ""
        if (token.isEmpty()) {
            textMessage.text = getString(R.string.error_token_missing)
            return
        }

        // Neues Essen-Objekt erstellen
        val foodData = Essen(
            id = 0, // ID wird auf 0 gesetzt oder nicht verwendet
            name = name,
            price = price,
            type = type
        )

        Log.d("AddFoodActivity", "Sende Daten an API: $foodData")

        // API-Aufruf mit dem richtigen Token
        apiService.addEssen("Bearer $token", foodData).enqueue(object : Callback<Essen> {
            override fun onResponse(call: Call<Essen>, response: Response<Essen>) {
                if (response.isSuccessful) {
                    textMessage.text = getString(R.string.food_added_successfully)
                    inputName.text.clear()
                    inputPrice.text.clear()
                    spinnerType.setSelection(0)
                } else {
                    Log.e("AddFoodActivity", "Fehler beim Hinzufügen des Essens: ${response.errorBody()?.string()}")
                    textMessage.text = getString(R.string.error_adding_food)
                }
            }

            override fun onFailure(call: Call<Essen>, t: Throwable) {
                Log.e("AddFoodActivity", "Serverfehler: ${t.message}")
                textMessage.text = getString(R.string.server_error)
            }
        })
    }



    private fun getToken(): String? {
        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPref.getString("token", null)
    }
}
