package com.vietanh.dentalclinic.models

data class TreatmentHistoryModel(
    val createdAt: String,
    val doctorName: String,
    val diagnosis: String,
    val treatmentPlan: String,
    val statusStage: String,
    val treatmentRouteId: Int
)
