package com.vietanh.dentalclinic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vietanh.dentalclinic.controllers.DoctorController
import com.vietanh.dentalclinic.models.DoctorModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorListScreen(onBackClick: () -> Unit, onBookClick: (Int) -> Unit) {
    // Quản lý trạng thái danh sách bác sĩ và trạng thái loading
    var doctors by remember { mutableStateOf<List<DoctorModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Gọi Controller để lấy dữ liệu từ cơ sở dữ liệu khi màn hình được tạo
    LaunchedEffect(Unit) {
        val controller = DoctorController()
        doctors = controller.getAllDoctors()
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đội ngũ Bác sĩ") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            // Hiển thị vòng xoay tải dữ liệu
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (doctors.isEmpty()) {
            // Hiển thị thông báo nếu không có bác sĩ nào
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Không có dữ liệu bác sĩ", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(doctors) { doctor ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar Bác sĩ (Tạm thời dùng Icon mặc định)
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            // Thông tin Bác sĩ
                            Column(modifier = Modifier.weight(1f)) {
                                Text(doctor.fullname, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("${doctor.specialization} - ${doctor.experienceYears} năm kinh nghiệm", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                            }
                            
                            // Nút Đặt lịch
                            Button(
                                onClick = { onBookClick(doctor.doctorId) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text("Đặt lịch")
                            }
                        }
                    }
                }
            }
        }
    }
}