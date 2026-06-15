package com.vietanh.dentalclinic.models

data class UserModel(
    val id: Int,
    val fullname: String,
    val phone: String,
    val roleId: Int,
    val birthDate: String? = null,
    val password: String? = null,
    val email: String? = null,
    val address: String? = null
)
