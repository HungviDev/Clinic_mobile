package com.vietanh.dentalclinic.controllers

import android.util.Log
import com.vietanh.dentalclinic.data.DatabaseConnection
import com.vietanh.dentalclinic.models.DoctorModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DoctorController {
    
    suspend fun getAllDoctors(): List<DoctorModel> = withContext(Dispatchers.IO) {
        val doctorList = mutableListOf<DoctorModel>()
        val connection = DatabaseConnection.getConnection()
        if (connection != null) {
            try {
                // Sử dụng INNER JOIN để kết nối bảng doctors và users
                val query = """
                    SELECT d.id AS doctor_id, u.id AS user_id, u.fullname, u.phone, u.avatar, 
                           d.specialization, d.experience_years
                    FROM doctors d
                    INNER JOIN users u ON d.user_id = u.id
                """.trimIndent()
                
                val statement = connection.createStatement()
                val resultSet = statement.executeQuery(query)
                
                while (resultSet.next()) {
                    val doctor = DoctorModel(
                        doctorId = resultSet.getInt("doctor_id"),
                        userId = resultSet.getInt("user_id"),
                        fullname = resultSet.getString("fullname") ?: "Unknown",
                        phone = resultSet.getString("phone") ?: "",
                        avatar = resultSet.getString("avatar"),
                        specialization = resultSet.getString("specialization") ?: "",
                        experienceYears = resultSet.getInt("experience_years")
                    )
                    doctorList.add(doctor)
                }
                resultSet.close()
                statement.close()
            } catch (e: Exception) {
                Log.e("DoctorController", "Lỗi lấy danh sách bác sĩ: " + e.message, e)
            } finally {
                try {
                    connection.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return@withContext doctorList
    }
}
