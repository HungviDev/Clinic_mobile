package com.vietanh.dentalclinic.controllers

import android.util.Log
import com.vietanh.dentalclinic.data.DatabaseConnection
import com.vietanh.dentalclinic.models.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserController {
    
    suspend fun login(phone: String, pass: String): UserModel? = withContext(Dispatchers.IO) {
        var user: UserModel? = null
        val connection = DatabaseConnection.getConnection()
        if (connection != null) {
            try {
                // Sử dụng PreparedStatement để bảo mật và tránh SQL Injection
                val query = "SELECT id, fullname, phone, role_id FROM users WHERE phone = ? AND password = ?"
                val statement = connection.prepareStatement(query)
                statement.setString(1, phone)
                statement.setString(2, pass)
                
                val resultSet = statement.executeQuery()
                if (resultSet.next()) {
                    user = UserModel(
                        id = resultSet.getInt("id"),
                        fullname = resultSet.getString("fullname") ?: "Unknown",
                        phone = resultSet.getString("phone") ?: "",
                        roleId = resultSet.getInt("role_id")
                    )
                }
                resultSet.close()
                statement.close()
            } catch (e: Exception) {
                Log.e("UserController", "Lỗi xử lý đăng nhập: " + e.message, e)
            } finally {
                try {
                    connection.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return@withContext user
    }

    suspend fun getUserById(id: Int): UserModel? = withContext(Dispatchers.IO) {
        var user: UserModel? = null
        val connection = DatabaseConnection.getConnection()
        if (connection != null) {
            try {
                val query = "SELECT id, fullname, phone, role_id, birth_date, password, email, address FROM users WHERE id = ?"
                val statement = connection.prepareStatement(query)
                statement.setInt(1, id)
                
                val resultSet = statement.executeQuery()
                if (resultSet.next()) {
                    user = UserModel(
                        id = resultSet.getInt("id"),
                        fullname = resultSet.getString("fullname") ?: "",
                        phone = resultSet.getString("phone") ?: "",
                        roleId = resultSet.getInt("role_id"),
                        birthDate = resultSet.getString("birth_date") ?: "",
                        password = resultSet.getString("password") ?: "",
                        email = resultSet.getString("email") ?: "",
                        address = resultSet.getString("address") ?: ""
                    )
                }
                resultSet.close()
                statement.close()
            } catch (e: Exception) {
                Log.e("UserController", "Lỗi lấy thông tin người dùng: " + e.message, e)
            } finally {
                try {
                    connection.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return@withContext user
    }

    suspend fun updateUser(id: Int, fullname: String, birthDate: String, phone: String, pass: String, email: String, address: String): Boolean = withContext(Dispatchers.IO) {
        var isSuccess = false
        val connection = DatabaseConnection.getConnection()
        if (connection != null) {
            try {
                val query = "UPDATE users SET fullname = ?, birth_date = ?, phone = ?, password = ?, email = ?, address = ? WHERE id = ?"
                val statement = connection.prepareStatement(query)
                statement.setString(1, fullname)
                statement.setString(2, birthDate)
                statement.setString(3, phone)
                statement.setString(4, pass)
                statement.setString(5, email)
                statement.setString(6, address)
                statement.setInt(7, id)
                
                val rowsAffected = statement.executeUpdate()
                isSuccess = rowsAffected > 0
                statement.close()
            } catch (e: Exception) {
                Log.e("UserController", "Lỗi cập nhật người dùng: " + e.message, e)
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
}
