package com.example.webmo2024app.network

import retrofit2.Call
import retrofit2.http.*
import com.example.webmo2024app.model.UserCredentials
import com.example.webmo2024app.model.LoginResponse
import com.example.webmo2024app.model.Essen
import com.example.webmo2024app.model.PlanResponse
import com.example.webmo2024app.model.Plan

interface ApiService {

    // Endpunkt für das Login
    @POST("/login")
    fun login(@Body credentials: UserCredentials): Call<LoginResponse>

    // Endpunkte für die Verwaltung von Essen (entsprechend der Tabelle 'food')
    @GET("/api/essen") // Endpoint korrigiert von '/api/food' zu '/api/essen'
    fun getAllEssen(): Call<List<Essen>>

    @POST("/api/essen") // Endpoint korrigiert von '/api/food' zu '/api/essen'
    fun addEssen(@Body essen: Essen): Call<Essen>

    @PUT("/api/essen/{id}") // Endpoint korrigiert von '/api/food/{id}' zu '/api/essen/{id}'
    fun updateEssen(@Path("id") id: Int, @Body essen: Essen): Call<Essen>

    @DELETE("/api/essen/{id}") // Endpoint korrigiert von '/api/food/{id}' zu '/api/essen/{id}'
    fun deleteEssen(@Path("id") id: Int): Call<Void>

    // Endpunkte für die Plan-Verwaltung (entsprechend der Tabelle 'foodplan')
    @GET("/api/essensplan/{week}") // Endpoint korrigiert von '/api/foodplan/{week}' zu '/api/essensplan/{week}'
    fun getPlanForWeek(@Path("week") week: Int): Call<PlanResponse>

    @POST("/api/essensplan/{week}") // Endpoint korrigiert von '/api/foodplan/{week}' zu '/api/essensplan/{week}'
    fun savePlanForWeek(@Path("week") week: Int, @Body plan: Map<String, String?>): Call<Void>

    // Endpunkte für die Essenspläne (entsprechend der Tabellen 'foodplan' und 'food_in_plan')
    @GET("/api/essensplan") // Endpoint korrigiert von '/api/foodplans' zu '/api/essensplan'
    fun getAllEssensplaene(): Call<List<Plan>>

    @POST("/api/essensplan") // Endpoint korrigiert von '/api/foodplan' zu '/api/essensplan'
    fun addEssensplan(@Body plan: Plan): Call<Void>

    @DELETE("/api/essensplan/{id}") // Endpoint korrigiert von '/api/foodplan/{id}' zu '/api/essensplan/{id}'
    fun deleteEssensplan(@Path("id") id: Int): Call<Void>

}
