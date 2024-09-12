package com.example.webmo2024app.model

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val role: String?,  // Fragezeichen bleibt, wenn das Feld null sein kann
    val token: String?  // Fragezeichen bleibt, wenn das Feld null sein kann
)

