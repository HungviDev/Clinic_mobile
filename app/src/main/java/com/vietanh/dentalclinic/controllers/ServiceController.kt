package com.vietanh.dentalclinic.controllers

import android.util.Log
import com.vietanh.dentalclinic.data.DatabaseConnection
import com.vietanh.dentalclinic.models.ServiceModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServiceController {
    
    suspend fun getAllServices(): List<ServiceModel> = withContext(Dispatchers.IO) {
        val services = mutableListOf<ServiceModel>()
        val connection = DatabaseConnection.getConnection()
        if (connection != null) {
            try {
                val query = "SELECT id, name, description, price, image FROM services"
                val statement = connection.createStatement()
                val resultSet = statement.executeQuery(query)
                
                while (resultSet.next()) {
                    services.add(
                        ServiceModel(
                            id = resultSet.getInt("id"),
                            name = resultSet.getString("name") ?: "",
                            description = resultSet.getString("description") ?: "",
                            price = resultSet.getDouble("price"),
                            image = resultSet.getString("image")
                        )
                    )
                }
                resultSet.close()
                statement.close()
            } catch (e: Exception) {
                Log.e("ServiceController", "Lỗi lấy danh sách dịch vụ: " + e.message, e)
            } finally {
                try {
                    connection.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return@withContext services
    }
}
