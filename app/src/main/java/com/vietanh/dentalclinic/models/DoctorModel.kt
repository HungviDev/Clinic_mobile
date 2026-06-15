package com.vietanh.dentalclinic.models

data class DoctorModel(
    val doctorId: Int,
    val userId: Int,
    val fullname: String,
    val phone: String,
    val avatar: String?,
    val specialization: String,
    val experienceYears: Int
)
