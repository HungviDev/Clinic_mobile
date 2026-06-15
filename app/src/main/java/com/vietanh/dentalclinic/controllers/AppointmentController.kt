package com.vietanh.dentalclinic.controllers

import android.util.Log
import com.vietanh.dentalclinic.data.DatabaseConnection
import com.vietanh.dentalclinic.models.AppointmentModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class AppointmentController {
    suspend fun getAppointmentsByUserId(userId: Int): List<AppointmentModel> = withContext(Dispatchers.IO) {
        val list = mutableListOf<AppointmentModel>()
        val connection = DatabaseConnection.getConnection()
        if (connection != null) {
            try {
                // JOIN appointments với services và doctors/users để lấy tên bác sĩ và dịch vụ
                val query = """
                    SELECT a.id, s.name AS service_name, u.fullname AS doctor_name, 
                           a.appointment_date, a.status 
                    FROM appointments a
                    INNER JOIN services s ON a.service_id = s.id
                    INNER JOIN doctors d ON a.doctor_id = d.id
                    INNER JOIN users u ON d.user_id = u.id
                    WHERE a.user_id = ?
                    ORDER BY a.appointment_date DESC
                """.trimIndent()
                
                val statement = connection.prepareStatement(query)
                statement.setInt(1, userId)
                
                val resultSet = statement.executeQuery()
                
                val dbFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val outDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val outTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                
                while (resultSet.next()) {
                    val rawDate = resultSet.getString("appointment_date") ?: ""
                    var dateStr = ""
                    var timeStr = ""
                    try {
                        // Thử parse định dạng chuẩn của SQL Server (bỏ đi phần mili giây nếu có)
                        val safeDateString = if (rawDate.length >= 19) rawDate.substring(0, 19) else rawDate
                        val parsed = dbFormat.parse(safeDateString)
                        if (parsed != null) {
                            dateStr = outDateFormat.format(parsed)
                            timeStr = outTimeFormat.format(parsed)
                        }
                    } catch (e: Exception) {
                        dateStr = rawDate
                        timeStr = ""
                    }

                    list.add(
                        AppointmentModel(
                            id = resultSet.getInt("id"),
                            serviceName = resultSet.getString("service_name") ?: "",
                            doctorName = resultSet.getString("doctor_name") ?: "",
                            date = dateStr,
                            time = timeStr,
                            status = resultSet.getString("status") ?: "unknown"
                        )
                    )
                }
                resultSet.close()
                statement.close()
            } catch (e: Exception) {
                Log.e("AppointmentController", "Lỗi lấy danh sách lịch hẹn: " + e.message, e)
            } finally {
                try {
                    connection.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return@withContext list
    }
}
