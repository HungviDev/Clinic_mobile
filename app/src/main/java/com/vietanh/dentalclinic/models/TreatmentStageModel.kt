package com.vietanh.dentalclinic.models

data class TreatmentStageModel(
    val stageName: String,
    val status: String,
    val sequenceOrder: Int,
    val time: String
)
