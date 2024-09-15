package com.example.webmo2024app.model

data class PlanResponse(
    val plan_id: Int,
    val week_number: Int,
    val days: Map<String, Meal>,
    val total_price: Double
)


