package com.example.webmo2024app




import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.webmo2024app.model.Essen
import com.example.webmo2024app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddFoodActivity : AppCompatActivity() {

    private lateinit var inputName: EditText
    private lateinit var inputPrice: EditText
    private lateinit var spinnerType: Spinner
    private lateinit var buttonAddFood: Button
    private lateinit var buttonShowFood: Button
    private lateinit var textMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_essen)

        // UI-Komponenten initialisieren
        inputName = findViewById(R.id.inputName)
        inputPrice = findViewById(R.id.inputPrice)
        spinnerType = findViewById(R.id.spinnerType)
        buttonAddFood = findViewById(R.id.buttonAddFood)
        buttonShowFood = findViewById(R.id.buttonShowFood)
        textMessage = findViewById(R.id.textMessage)

        // Spinner mit Optionen füllen
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.typ,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter

        // Button-Listener hinzufügen
        buttonAddFood.setOnClickListener {
            addFood()
        }

        buttonShowFood.setOnClickListener {
            // Navigationslogik, um zur Liste der Essen zu gelangen
            // startActivity(Intent(this, FoodListActivity::class.java))
        }
    }

    private fun addFood() {
        val name = inputName.text.toString().trim() // Entferne unnötige Leerzeichen
        val priceText = inputPrice.text.toString()
        val price = priceText.toDoubleOrNull() ?: 0.0
        val type = spinnerType.selectedItem.toString()

        if (name.isEmpty() || priceText.isEmpty() || price <= 0 || type.isEmpty()) {
            textMessage.text = "Bitte alle Felder ausfüllen."
            return
        }

        // Restlicher Code bleibt unverändert



    val token = getToken() // Token abrufen
        if (token.isNullOrEmpty()) {
            textMessage.text = "Kein Authentifizierungstoken gefunden."
            return
        }

        val food = Essen(name, price, type)
        RetrofitClient.apiService.addEssen(food).enqueue(object : Callback<Essen> {
            override fun onResponse(call: Call<Essen>, response: Response<Essen>) {
                if (response.isSuccessful) {
                    textMessage.text = "Essen erfolgreich hinzugefügt!"
                    inputName.text.clear()
                    inputPrice.text.clear()
                    spinnerType.setSelection(0)
                } else {
                    textMessage.text = "Fehler beim Hinzufügen des Essens."
                }
            }

            override fun onFailure(call: Call<Essen>, t: Throwable) {
                textMessage.text = "Serverfehler: ${t.message}"
            }
        })
    }

    private fun getToken(): String? {
        // Abrufen des gespeicherten Tokens, z.B. von SharedPreferences
        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPref.getString("token", null)
    }
}
