package com.example.webmo2024app.model

data class Plan(
    val id: Int,  // Beispiel für ein Feld, passe es entsprechend deiner API an
    val week: Int,
    val days: Map<String, String?>  // Eine Map, die die Wochentage und die zugeordneten Essen enthält
)
