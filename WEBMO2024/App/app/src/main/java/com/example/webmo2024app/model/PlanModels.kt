
// In einem neuen Datei, z.B., com/example/webmo2024app/model/PlanDetail.kt
package com.example.webmo2024app.model

data class PlanDetail(
    val plan_id: Int,  // plan_id hinzufügen
    val wochennummer: Int,
    val plan: List<PlanEntry>
)




