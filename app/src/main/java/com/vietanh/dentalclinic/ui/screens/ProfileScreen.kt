package com.vietanh.dentalclinic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
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

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToPaymentHistory: () -> Unit,
    onNavigateToTreatmentHistory: () -> Unit
) {
    var userName by remember { mutableStateOf("Đang tải...") }
    var userPhone by remember { mutableStateOf("") }
    val userController = remember { UserController() }

    // Tải dữ liệu thật từ DB theo ID đăng nhập
    LaunchedEffect(Unit) {
        val userId = SessionManager.currentUserId
        if (userId != -1) {
            val user = userController.getUserById(userId)
            if (user != null) {
                userName = user.fullname
                userPhone = user.phone
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
            .verticalScroll(androidx.compose.foundation.rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hồ sơ", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(32.dp))

        // Avatar
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = "Avatar", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(userName, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        if (userPhone.isNotEmpty()) {
            Text(userPhone, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        
        Spacer(modifier = Modifier.height(48.dp))

        // Menu
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                ProfileMenuItem(icon = Icons.Default.Person, title = "Chỉnh sửa thông tin cá nhân", onClick = onNavigateToEditProfile)
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                
                // For demonstration, using placeholder icons for history
                ProfileMenuItem(icon = Icons.Default.Settings, title = "Lịch sử thanh toán", onClick = onNavigateToPaymentHistory)
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                
                ProfileMenuItem(icon = Icons.Default.Settings, title = "Lịch sử điều trị", onClick = onNavigateToTreatmentHistory)
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                ProfileMenuItem(icon = Icons.Default.Settings, title = "Cài đặt") { }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // Xoá session khi đăng xuất
                SessionManager.currentUserId = -1
                onLogout()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Đăng xuất", tint = MaterialTheme.colorScheme.onError)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Đăng xuất", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onError)
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontSize = 16.sp, color = color)
    }
}