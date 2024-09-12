package com.example.webmo2024app.model

data class UserCredentials(
    val username: String,
    val password: String,
    val role: String  // Das Feld 'role' sollte nur enthalten sein, wenn es von der API benötigt wird
)

