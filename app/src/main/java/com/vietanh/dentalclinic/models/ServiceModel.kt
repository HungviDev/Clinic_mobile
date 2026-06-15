package com.vietanh.dentalclinic.models

data class ServiceModel(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val image: String?
)
