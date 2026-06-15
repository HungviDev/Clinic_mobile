package com.vietanh.dentalclinic.models

data class PaymentHistoryModel(
    val serviceName: String,
    val amount: Double,
    val method: String,
    val status: String,
    val createdAt: String
)
