package com.example.webmo2024app.model

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val role: String?,  // Kann null sein, wenn der Benutzer keine Rolle hat
    val token: String?  // Kann null sein, wenn kein Token zurückgegeben wird
)


