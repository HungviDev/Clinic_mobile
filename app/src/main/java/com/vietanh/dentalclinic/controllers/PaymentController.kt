package com.vietanh.dentalclinic.controllers

import android.util.Log
import com.vietanh.dentalclinic.data.DatabaseConnection
import com.vietanh.dentalclinic.models.PaymentHistoryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class PaymentController {

    suspend fun getPaymentHistoryByUserId(userId: Int): List<PaymentHistoryModel> = withContext(Dispatchers.IO) {
        val list = mutableListOf<PaymentHistoryModel>()
        val connection = DatabaseConnection.getConnection()
        if (connection != null) {
            try {
                val query = """
                    SELECT p.amount, p.method, p.status, 
                           p.created_at, ts.stage_name AS service_name 
                    FROM payments p 
                    LEFT JOIN treatment_stages ts ON p.treatment_stage_id = ts.id 
                    WHERE p.user_id = ? 
                    ORDER BY p.created_at DESC
                """.trimIndent()

                val statement = connection.prepareStatement(query)
                statement.setInt(1, userId)
                
                val rs = statement.executeQuery()
                val dbFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val outDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                
                while (rs.next()) {
                    val amount = rs.getDouble("amount")
                    val method = rs.getString("method") ?: ""
                    val status = rs.getString("status") ?: ""
                    val serviceName = rs.getString("service_name")?.takeIf { it.isNotBlank() } ?: "Giai đoạn điều trị"
                    
                    val rawDate = rs.getString("created_at") ?: ""
                    var createdAtStr = rawDate
                    try {
                        val safeDateString = if (rawDate.length >= 19) rawDate.substring(0, 19) else rawDate
                        val parsed = dbFormat.parse(safeDateString)
                        if (parsed != null) {
                            createdAtStr = outDateFormat.format(parsed)
                        }
                    } catch (e: Exception) {
                        // Giữ nguyên chuỗi nếu parse lỗi
                    }
                    
                    list.add(
                        PaymentHistoryModel(
                            serviceName = serviceName,
                            amount = amount,
                            method = method,
                            status = status,
                            createdAt = createdAtStr
                        )
                    )
                }
                
                rs.close()
                statement.close()
            } catch (e: Exception) {
                Log.e("PaymentController", "Lỗi lấy lịch sử thanh toán: " + e.message, e)
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
