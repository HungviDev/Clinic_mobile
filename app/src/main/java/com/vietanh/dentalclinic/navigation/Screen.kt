package com.vietanh.dentalclinic.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector?) {
    object Splash : Screen("splash", "Splash", null)
    object Login : Screen("login", "Đăng nhập", null)
    
    // Bottom Nav Screens
    object Home : Screen("home", "Trang chủ", Icons.Default.Home)
    object Booking : Screen("booking", "Đặt lịch", Icons.Default.AddCircle)
    object Appointments : Screen("appointments", "Lịch hẹn", Icons.Default.DateRange)
    object Profile : Screen("profile", "Hồ sơ", Icons.Default.Person)
    
    // Other Screens
    object DoctorList : Screen("doctors", "Bác sĩ", null)
    object ServiceList : Screen("services", "Dịch vụ", null)
}