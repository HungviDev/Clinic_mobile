package com.vietanh.dentalclinic.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vietanh.dentalclinic.controllers.DoctorController
import com.vietanh.dentalclinic.controllers.ServiceController
import com.vietanh.dentalclinic.models.DoctorModel
import com.vietanh.dentalclinic.models.ServiceModel
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(serviceId: Int? = null, onBackClick: () -> Unit, onBookingConfirm: () -> Unit) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    var doctors by remember { mutableStateOf<List<DoctorModel>>(emptyList()) }
    var services by remember { mutableStateOf<List<ServiceModel>>(emptyList()) }

    var selectedDoctor by remember { mutableStateOf<DoctorModel?>(null) }
    var selectedService by remember { mutableStateOf<ServiceModel?>(null) }
    
    // Khởi tạo ngày và giờ với thời gian hiện tại của hệ thống
    var selectedDate by remember { 
        mutableStateOf(String.format("%02d/%02d/%d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))) 
    }
    var selectedTime by remember { 
        mutableStateOf(String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))) 
    }
    
    var note by remember { mutableStateOf("") }
    
    var expandedDoctor by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var isSubmitting by remember { mutableStateOf(false) }
    
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    
    val coroutineScope = rememberCoroutineScope()

    // Fetch data from controllers
    LaunchedEffect(Unit) {
        val doctorCtrl = DoctorController()
        val serviceCtrl = ServiceController()
        
        doctors = doctorCtrl.getAllDoctors()
        services = serviceCtrl.getAllServices()
        
        if (doctors.isNotEmpty()) {
            selectedDoctor = doctors[0]
        }
        if (services.isNotEmpty()) {
            selectedService = if (serviceId != null) services.find { it.id == serviceId } ?: services[0] else services[0]
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đặt lịch khám") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Chọn Bác sĩ
                Column {
                    Text("Bác sĩ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = expandedDoctor,
                        onExpandedChange = { expandedDoctor = !expandedDoctor }
                    ) {
                        OutlinedTextField(
                            value = selectedDoctor?.fullname ?: "Chọn bác sĩ",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDoctor) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedDoctor,
                            onDismissRequest = { expandedDoctor = false }
                        ) {
                            doctors.forEach { doctor ->
                                DropdownMenuItem(
                                    text = { Text(doctor.fullname) },
                                    onClick = {
                                        selectedDoctor = doctor
                                        expandedDoctor = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Chọn Dịch vụ
                Column {
                    Text("Dịch vụ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(services) { service ->
                            FilterChip(
                                selected = selectedService == service,
                                onClick = { selectedService = service },
                                label = { Text(service.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }

                // Chọn Ngày
                Column {
                    Text("Ngày khám", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth().clickable {
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }) {
                        OutlinedTextField(
                            value = selectedDate,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false, // Disable typing to enforce dialog usage
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }

                // Chọn Giờ
                Column {
                    Text("Giờ khám", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth().clickable {
                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true // Dùng định dạng 24h
                        ).show()
                    }) {
                        OutlinedTextField(
                            value = selectedTime,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }

                // Ghi chú
                Column {
                    Text("Ghi chú (Tùy chọn)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("Ví dụ: Đau răng số 8...") }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Button
                Button(
                    onClick = {
                        val userId = com.vietanh.dentalclinic.data.SessionManager.currentUserId
                        if (userId == -1) {
                            dialogMessage = "Bạn cần đăng nhập để đặt lịch."
                            showDialog = true
                            return@Button
                        }
                        if (selectedDoctor == null || selectedService == null) {
                            dialogMessage = "Vui lòng chọn đầy đủ Bác sĩ và Dịch vụ."
                            showDialog = true
                            return@Button
                        }
                        
                        isSubmitting = true
                        coroutineScope.launch {
                            val bookingController = com.vietanh.dentalclinic.controllers.BookingController()
                            val success = bookingController.createAppointment(
                                userId = userId,
                                doctorId = selectedDoctor!!.doctorId,
                                serviceId = selectedService!!.id,
                                dateString = selectedDate,
                                timeString = selectedTime,
                                note = note
                            )
                            isSubmitting = false
                            if (success) {
                                // Lấy thông tin user để gửi mail
                                val userCtrl = com.vietanh.dentalclinic.controllers.UserController()
                                val user = userCtrl.getUserById(userId)
                                val toEmail = user?.email ?: ""
                                val patientName = user?.fullname ?: "Khách hàng"
                                val docName = selectedDoctor!!.fullname
                                val apptDate = "$selectedTime $selectedDate"
                                
                                // Gọi API gửi mail ngầm
                                bookingController.sendMail(toEmail, docName, patientName, apptDate)

                                dialogMessage = "Đặt lịch thành công!"
                            } else {
                                dialogMessage = "Lỗi khi đặt lịch. Vui lòng thử lại sau."
                            }
                            showDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Xác nhận đặt lịch", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
    
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Thông báo") },
            text = { Text(dialogMessage) },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    if (dialogMessage == "Đặt lịch thành công!") {
                        onBookingConfirm() // Quay về màn hình trước
                    }
                }) {
                    Text("OK")
                }
            }
        )
    }
}