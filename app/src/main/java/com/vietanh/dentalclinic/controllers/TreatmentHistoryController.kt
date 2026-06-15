package com.vietanh.dentalclinic.controllers

import android.util.Log
import com.vietanh.dentalclinic.data.DatabaseConnection
import com.vietanh.dentalclinic.models.TreatmentHistoryModel
import com.vietanh.dentalclinic.models.TreatmentStageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class TreatmentHistoryController {
    suspend fun getTreatmentHistoryByUserId(userId: Int): List<TreatmentHistoryModel> = withContext(Dispatchers.IO) {
        val list = mutableListOf<TreatmentHistoryModel>()
        val connection = DatabaseConnection.getConnection()
        if (connection != null) {
            try {
                val query = """
                    SELECT m.created_at, 
                           u.fullname AS doctor_name, 
                           m.diagnosis, 
                           tr.title AS treatment_plan, 
                           m.treatment_route_id, 
                           CASE 
                              WHEN tr.id IS NULL THEN N'Chưa có lộ trình' 
                              WHEN ts.stage_name IS NULL THEN N'Đã hoàn thành' 
                              ELSE ts.stage_name 
                           END AS stage_name 
                    FROM medical_records m 
                    LEFT JOIN doctors d ON m.doctor_id = d.id 
                    LEFT JOIN users u ON d.user_id = u.id 
                    LEFT JOIN treatment_routes tr ON m.treatment_route_id = tr.id 
                    OUTER APPLY ( 
                        SELECT TOP 1 stage_name 
                        FROM treatment_stages 
                        WHERE treatment_route_id = tr.id 
                          AND status IN (N'Chưa thực hiện', N'Đang thực hiện') 
                        ORDER BY sequence_order ASC 
                    ) ts 
                    WHERE m.user_id = ? 
                    ORDER BY m.created_at DESC
                """.trimIndent()

                val statement = connection.prepareStatement(query)
                statement.setInt(1, userId)
                val rs = statement.executeQuery()
                
                val dbFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val outDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                
                while (rs.next()) {
                    val rawDate = rs.getString("created_at") ?: ""
                    var createdAtStr = rawDate
                    try {
                        val safeDateString = if (rawDate.length >= 19) rawDate.substring(0, 19) else rawDate
                        val parsed = dbFormat.parse(safeDateString)
                        if (parsed != null) createdAtStr = outDateFormat.format(parsed)
                    } catch (e: Exception) {}

                    val doctorName = rs.getString("doctor_name") ?: "Không rõ bác sĩ"
                    val diagnosis = rs.getString("diagnosis") ?: "Không rõ chẩn đoán"
                    val treatmentPlan = rs.getString("treatment_plan") ?: "Không có lộ trình"
                    val stageName = rs.getString("stage_name") ?: "Chưa có trạng thái"
                    val treatmentRouteId = rs.getInt("treatment_route_id")

                    list.add(
                        TreatmentHistoryModel(
                            createdAt = createdAtStr,
                            doctorName = doctorName,
                            diagnosis = diagnosis,
                            treatmentPlan = treatmentPlan,
                            statusStage = stageName,
                            treatmentRouteId = treatmentRouteId
                        )
                    )
                }
                rs.close()
                statement.close()
            } catch (e: Exception) {
                Log.e("TreatmentController", "Lỗi lấy lịch sử điều trị: " + e.message, e)
            } finally {
                try { connection.close() } catch (e: Exception) {}
            }
        }
        return@withContext list
    }

    suspend fun getStagesByRouteId(routeId: Int): List<TreatmentStageModel> = withContext(Dispatchers.IO) {
        val list = mutableListOf<TreatmentStageModel>()
        val connection = DatabaseConnection.getConnection()
        if (connection != null) {
            try {
                // Select * để tự linh động lấy cột thời gian nếu có (updated_at hoặc created_at)
                val query = "SELECT * FROM treatment_stages WHERE treatment_route_id = ? ORDER BY sequence_order ASC"
                val statement = connection.prepareStatement(query)
                statement.setInt(1, routeId)
                val rs = statement.executeQuery()
                
                val outDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                while (rs.next()) {
                    val stageName = rs.getString("stage_name") ?: "Không rõ"
                    val status = rs.getString("status") ?: "Không rõ"
                    val sequenceOrder = rs.getInt("sequence_order")
                    
                    var timeStr = ""
                    try {
                        timeStr = rs.getString("updated_at") ?: ""
                    } catch (e: Exception) {
                        try {
                            timeStr = rs.getString("created_at") ?: ""
                        } catch (e2: Exception) {}
                    }
                    
                    if (timeStr.length >= 10) {
                        try {
                            val dbFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val parsed = dbFormat.parse(timeStr.substring(0, 10))
                            if (parsed != null) timeStr = outDateFormat.format(parsed)
                        } catch (e: Exception) {}
                    }

                    list.add(TreatmentStageModel(stageName, status, sequenceOrder, timeStr))
                }
                rs.close()
                statement.close()
            } catch (e: Exception) {
                Log.e("TreatmentController", "Lỗi lấy chi tiết lộ trình: " + e.message, e)
            } finally {
                try { connection.close() } catch (e: Exception) {}
            }
        }
        return@withContext list
    }
}
