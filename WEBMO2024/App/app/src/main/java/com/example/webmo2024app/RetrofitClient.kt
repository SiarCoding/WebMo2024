package com.example.webmo2024app.network

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:3001/api/"

    fun create(context: Context): ApiService {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor { chain ->
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

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
