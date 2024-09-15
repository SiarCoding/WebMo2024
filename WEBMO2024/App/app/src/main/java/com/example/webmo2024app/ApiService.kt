package com.example.webmo2024app.network

import retrofit2.Call
import retrofit2.http.*
import com.example.webmo2024app.model.*

interface ApiService {

    // Endpunkt für das Login
    @POST("/login")
    fun login(@Body credentials: UserCredentials): Call<LoginResponse>

    // Endpunkte für die Verwaltung von Essen (entsprechend der Tabelle 'food')
    @GET("/api/essen")
    fun getAllEssen(@Header("Authorization") token: String): Call<List<Essen>>

    @GET("/api/essen/{id}")
    fun getEssenById(@Header("Authorization") token: String, @Path("id") id: Int): Call<Essen>

    @POST("/api/essen")
    fun addEssen(
        @Header("Authorization") token: String,
        @Body foodData: Essen
    ): Call<Essen>

    @PUT("/api/essen/{id}")
    fun updateEssen(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body foodData: Map<String, Any>
    ): Call<Essen>

    @DELETE("/api/essen/{id}")
    fun deleteEssen(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<Void>

    // Endpunkte für die Verwaltung von Essensplänen (entsprechend der Tabelle 'foodplan')
    @GET("/api/essensplan")
    fun getAllEssensplaene(@Header("Authorization") token: String): Call<List<Plan>>

    @GET("/api/essensplan/{week}")
    fun getPlanForWeek(
        @Header("Authorization") token: String,
        @Path("week") week: Int
    ): Call<PlanResponse>

    @POST("/api/essensplan")
    fun savePlanForWeek(
        @Header("Authorization") token: String,
        @Body planDetails: PlanDetail
    ): Call<PlanResponse>  // Rückgabewert auf PlanResponse ändern

    @GET("/api/essensplan/{id}")
    fun getPlanDetails(
        @Header("Authorization") token: String,
        @Path("id") planId: Int
    ): Call<PlanDetail>

    @PUT("/api/essensplan")
    fun updatePlan(
        @Header("Authorization") token: String,
        @Body planDetail: PlanDetail
    ): Call<PlanResponse>




    @PUT("/api/essensplan/{week}")
    fun updateEssensplan(
        @Header("Authorization") token: String,
        @Path("week") week: Int,
        @Body planData: Map<String, Any>
    ): Call<Void>

    @DELETE("/api/essensplan/{id}")
    fun deleteEssensplan(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<Void>
}
