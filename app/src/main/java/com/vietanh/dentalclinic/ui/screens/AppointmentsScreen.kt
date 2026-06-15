package com.vietanh.dentalclinic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vietanh.dentalclinic.controllers.AppointmentController
import com.vietanh.dentalclinic.data.SessionManager
import com.vietanh.dentalclinic.models.AppointmentModel
import com.vietanh.dentalclinic.ui.theme.ErrorRed
import com.vietanh.dentalclinic.ui.theme.SuccessGreen
import com.vietanh.dentalclinic.ui.theme.WarningYellow

@Composable
fun AppointmentsScreen() {
    var appointments by remember { mutableStateOf<List<AppointmentModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val userId = SessionManager.currentUserId
        if (userId != -1) {
            val controller = AppointmentController()
            appointments = controller.getAppointmentsByUserId(userId)
        }
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text("Lịch hẹn của tôi", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (appointments.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Bạn chưa có lịch hẹn nào.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(appointments) { appointment ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(appointment.serviceName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                
                                // Ép về chữ thường để so sánh như yêu cầu
                                val statusLower = appointment.status.lowercase()
                                val (statusColor, statusText) = when (statusLower) {
                                    "pending" -> Pair(WarningYellow, "Chờ duyệt")
                                    "approved" -> Pair(SuccessGreen, "Đã duyệt")
                                    "completed", "done" -> Pair(MaterialTheme.colorScheme.primary, "Hoàn thành")
                                    "cancelled", "cancel" -> Pair(ErrorRed, "Đã hủy") // Hoặc "Từ chối"
                                    else -> Pair(MaterialTheme.colorScheme.onSurfaceVariant, appointment.status)
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(statusText, color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text("Bác sĩ: ${appointment.doctorName}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("${appointment.time} - ${appointment.date}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}