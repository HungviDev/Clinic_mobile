package com.vietanh.dentalclinic.controllers

import android.util.Log
import com.vietanh.dentalclinic.data.DatabaseConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookingController {

    suspend fun createAppointment(
        userId: Int,
        doctorId: Int,
        serviceId: Int,
        dateString: String, // Format: "DD/MM/YYYY"
        timeString: String, // Format: "HH:mm"
        note: String
    ): Boolean = withContext(Dispatchers.IO) {
        var isSuccess = false
        val connection = DatabaseConnection.getConnection()
        if (connection != null) {
            try {
                // Chuyển đổi DD/MM/YYYY và HH:mm sang định dạng YYYY-MM-DD HH:mm:00.000 của SQL Server
                val parts = dateString.split("/")
                val sqlDateStr = if (parts.size == 3) {
                    "${parts[2]}-${parts[1]}-${parts[0]} $timeString:00.000"
                } else {
                    "2023-10-25 $timeString:00.000" // Fallback an toàn
                }

                val query = """
                    INSERT INTO appointments (user_id, doctor_id, service_id, appointment_date, status, note, created_at)
                    VALUES (?, ?, ?, ?, ?, ?, GETDATE())
                """.trimIndent()
                
                val statement = connection.prepareStatement(query)
                statement.setInt(1, userId)
                statement.setInt(2, doctorId)
                statement.setInt(3, serviceId)
                statement.setString(4, sqlDateStr)
                statement.setString(5, "Pending") // Mặc định là Approved theo yêu cầu
                
                if (note.isBlank()) {
                    statement.setNull(6, java.sql.Types.VARCHAR)
                } else {
                    statement.setString(6, note)
                }
                
                val rowsAffected = statement.executeUpdate()
                isSuccess = rowsAffected > 0
                statement.close()
            } catch (e: Exception) {
                Log.e("BookingController", "Lỗi tạo lịch hẹn: " + e.message, e)
            } finally {
                try {
                    connection.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return@withContext isSuccess
    }

    suspend fun sendMail(to: String, doctorName: String, patientName: String, appointmentDate: String) = withContext(Dispatchers.IO) {
        if (to.isBlank()) return@withContext
        try {
            // Sử dụng 10.0.2.2 để Emulator có thể gọi đến localhost của máy tính
            val url = java.net.URL("http://localhost:3000/send-mail")
            val conn = url.openConnection() as java.net.HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; utf-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true

            // Escape chuỗi nếu cần để tránh lỗi JSON, tuy nhiên ở đây giả định tên không chứa ký tự đặc biệt break JSON
            val jsonInputString = """
            {
                "to": "$to",
                "doctorName": "$doctorName",
                "patientName": "$patientName",
                "appointmentDate": "$appointmentDate"
            }
            """.trimIndent()

            conn.outputStream.use { os ->
                val input = jsonInputString.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val responseCode = conn.responseCode
            Log.d("BookingController", "Send mail response code: $responseCode")
        } catch (e: Exception) {
            Log.e("BookingController", "Lỗi gửi mail: " + e.message, e)
        }
    }
}
