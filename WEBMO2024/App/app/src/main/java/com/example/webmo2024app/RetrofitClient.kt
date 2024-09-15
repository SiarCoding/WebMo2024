package com.example.webmo2024app.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:3001/" // Korrekte Basis-URL für Emulator

    fun create(context: Context): ApiService {
        // Interceptor zum Loggen der HTTP-Anfragen und -Antworten für das Debuggen
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // HttpClient für die Autorisierung und Logging konfigurieren
        val httpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()

                // Token aus SharedPreferences abrufen
                val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val token = sharedPref.getString("token", null)

                if (!token.isNullOrEmpty()) {
                    requestBuilder.header("Authorization", "Bearer $token")
                }

                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .addInterceptor(logging) // Füge den Logging-Interceptor hinzu
            .connectTimeout(30, TimeUnit.SECONDS) // Timeout für die Verbindung
            .readTimeout(30, TimeUnit.SECONDS) // Timeout für die Antwort lesen
            .build()

        // Erstelle die Retrofit-Instanz
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
