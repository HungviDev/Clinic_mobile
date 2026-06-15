package com.vietanh.dentalclinic.models

data class AppointmentModel(
    val id: Int,
    val serviceName: String,
    val doctorName: String,
    val date: String,
    val time: String,
    val status: String
)
