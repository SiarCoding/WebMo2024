package com.example.webmo2024app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.webmo2024app.model.LoginResponse
import com.example.webmo2024app.model.UserCredentials
import com.example.webmo2024app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import com.example.webmo2024app.network.ApiService // Importiere ApiService

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var spinnerRole: Spinner
    private lateinit var buttonLogin: Button
    private lateinit var textViewError: TextView

    // Initialisiere apiService
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialisiere die API-Service
        apiService = RetrofitClient.create(applicationContext)

        // Initialisiere die UI-Komponenten
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        spinnerRole = findViewById(R.id.spinnerRole)
        buttonLogin = findViewById(R.id.buttonLogin)
        textViewError = findViewById(R.id.textViewError)

        // Spinner für Rollenoptionen einrichten
        val roles = arrayOf("Rolle wählen", "Admin", "User")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRole.adapter = adapter

        // Login-Button Listener
        buttonLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val username = editTextUsername.text.toString()
        val password = editTextPassword.text.toString()
        var role = spinnerRole.selectedItem.toString()

        if (username.isEmpty() || password.isEmpty()) {
            textViewError.visibility = View.VISIBLE
            textViewError.text = getString(R.string.fill_all_fields)
            return
        }

        if (role == "Rolle wählen") {
            textViewError.visibility = View.VISIBLE
            textViewError.text = getString(R.string.select_role)
            return
        }

        // Konvertiere die Rolle in Kleinbuchstaben
        role = role.lowercase()

        // Erstelle das UserCredentials-Objekt
        val credentials = UserCredentials(username, password, role)

        // Führe API-Anfrage für Login durch
        val call = apiService.login(credentials)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val token = response.body()?.token
                    if (!token.isNullOrEmpty()) {
                        getSharedPreferences("app_prefs", MODE_PRIVATE).edit()
                            .putString("token", token)
                            .apply()

                        Toast.makeText(this@LoginActivity, getString(R.string.login_success), Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@LoginActivity, EssenActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        textViewError.visibility = View.VISIBLE
                        textViewError.text = getString(R.string.error_token_missing)
                    }
                } else {
                    textViewError.visibility = View.VISIBLE
                    val errorMsg = response.errorBody()?.string() ?: getString(R.string.error_message)
                    textViewError.text = errorMsg
                    Log.e("LoginActivity", "Login fehlgeschlagen: $errorMsg")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginActivity", "Fehler bei der API-Anfrage: ${t.message}", t)
                textViewError.visibility = View.VISIBLE
                textViewError.text = getString(R.string.error_message)
            }
        })
    }
}
