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

    // Endpunkte für die Essen-Verwaltung
    @GET("/api/essen")
    fun getAllEssen(): Call<List<Essen>>

    @POST("/api/essen")
    fun addEssen(@Body essen: Essen): Call<Essen>

    @PUT("/api/essen/{id}")
    fun updateEssen(@Path("id") id: Int, @Body essen: Essen): Call<Essen>

    @DELETE("/api/essen/{id}")
    fun deleteEssen(@Path("id") id: Int): Call<Void>

    // Endpunkte für die Plan-Verwaltung
    @GET("/api/plan/{week}")
    fun getPlanForWeek(@Path("week") week: Int): Call<PlanResponse>

    @POST("/api/plan/{week}")
    fun savePlanForWeek(@Path("week") week: Int, @Body plan: Map<String, String?>): Call<Void>

    // Endpunkte für die Essenspläne
    @GET("/api/essensplane")
    fun getAllEssensplaene(): Call<List<Plan>>

    @POST("/api/essensplan")
    fun addEssensplan(@Body plan: Plan): Call<Void>

    @DELETE("/api/essensplan/{id}")
    fun deleteEssensplan(@Path("id") id: Int): Call<Void>
}
