package com.vietanh.dentalclinic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vietanh.dentalclinic.data.AppointmentStatus
import com.vietanh.dentalclinic.data.MockData
import com.vietanh.dentalclinic.ui.theme.ErrorRed
import com.vietanh.dentalclinic.ui.theme.SuccessGreen
import com.vietanh.dentalclinic.ui.theme.WarningYellow

@Composable
fun AppointmentsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text("Lá»‹ch háº¹n cá»§a tÃ´i", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(MockData.appointments) { appointment ->
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
                            
                            val statusColor = when(appointment.status) {
                                AppointmentStatus.PENDING -> WarningYellow
                                AppointmentStatus.CONFIRMED -> SuccessGreen
                                AppointmentStatus.CANCELLED -> ErrorRed
                            }
                            val statusText = when(appointment.status) {
                                AppointmentStatus.PENDING -> "Chá» xÃ¡c nháº­n"
                                AppointmentStatus.CONFIRMED -> "ÄÃ£ xÃ¡c nháº­n"
                                AppointmentStatus.CANCELLED -> "ÄÃ£ há»§y"
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
                        
                        Text(appointment.doctorName, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        
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