package com.vietanh.dentalclinic.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vietanh.dentalclinic.data.MockData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(onBackClick: () -> Unit, onBookingConfirm: () -> Unit) {
    var selectedDoctor by remember { mutableStateOf(MockData.doctors[0]) }
    var selectedService by remember { mutableStateOf(MockData.services[0]) }
    var selectedDate by remember { mutableStateOf("25/10/2023") }
    var selectedTime by remember { mutableStateOf("09:00") }
    var note by remember { mutableStateOf("") }
    
    var expandedDoctor by remember { mutableStateOf(false) }

    val dates = listOf("25/10/2023", "26/10/2023", "27/10/2023", "28/10/2023")
    val timeSlots = listOf("08:00", "09:00", "10:00", "11:00", "14:00", "15:00", "16:00", "17:00")

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
                        value = selectedDoctor.name,
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
                        MockData.doctors.forEach { doctor ->
                            DropdownMenuItem(
                                text = { Text(doctor.name) },
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
                    items(MockData.services) { service ->
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
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(dates) { date ->
                        val isSelected = selectedDate == date
                        Box(
                            modifier = Modifier
                                .clickable { selectedDate = date }
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                date.substring(0, 5), // Show DD/MM
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Chọn Giờ
            Column {
                Text("Giờ khám", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(120.dp) // Fixed height for nested scroll
                ) {
                    items(timeSlots) { time ->
                        val isSelected = selectedTime == time
                        Box(
                            modifier = Modifier
                                .clickable { selectedTime = time }
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                time,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
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
                onClick = onBookingConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Xác nhận đặt lịch", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}