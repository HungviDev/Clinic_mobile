package com.vietanh.dentalclinic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vietanh.dentalclinic.controllers.UserController
import com.vietanh.dentalclinic.data.SessionManager
import com.vietanh.dentalclinic.navigation.Screen

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit
) {
    var userName by remember { mutableStateOf("Đang tải...") }
    val userController = remember { UserController() }

    LaunchedEffect(Unit) {
        val userId = SessionManager.currentUserId
        if (userId != -1) {
            val user = userController.getUserById(userId)
            if (user != null) {
                userName = user.fullname
            } else {
                userName = "Khách hàng"
            }
        } else {
            userName = "Khách hàng"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Avatar", tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Xin chào,", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(userName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
            IconButton(onClick = { /* Notifications */ }) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Giảm 20% Dịch vụ Niềng răng", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { onNavigate(Screen.Booking.route) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.primary)) {
                        Text("Đặt lịch ngay")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Dịch vụ của chúng tôi", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // Grid Menu
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            item { MenuCard("Đặt lịch", Icons.Default.AddCircle, MaterialTheme.colorScheme.primary) { onNavigate(Screen.Booking.route) } }
            item { MenuCard("Bác sĩ", Icons.Default.Person, MaterialTheme.colorScheme.secondary) { onNavigate(Screen.DoctorList.route) } }
            item { MenuCard("Dịch vụ", Icons.Default.List, MaterialTheme.colorScheme.tertiary) { onNavigate(Screen.ServiceList.route) } }
            item { MenuCard("Lịch hẹn", Icons.Default.DateRange, MaterialTheme.colorScheme.error) { onNavigate(Screen.Appointments.route) } }
        }
    }
}

@Composable
fun MenuCard(title: String, icon: ImageVector, color: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Medium)
        }
    }
}