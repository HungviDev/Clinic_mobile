package com.vietanh.dentalclinic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vietanh.dentalclinic.controllers.TreatmentHistoryController
import com.vietanh.dentalclinic.data.SessionManager
import com.vietanh.dentalclinic.models.TreatmentHistoryModel
import com.vietanh.dentalclinic.models.TreatmentStageModel
import com.vietanh.dentalclinic.ui.theme.SuccessGreen
import com.vietanh.dentalclinic.ui.theme.WarningYellow
import com.vietanh.dentalclinic.ui.theme.ErrorRed
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreatmentHistoryScreen(onBackClick: () -> Unit) {
    var historyList by remember { mutableStateOf<List<TreatmentHistoryModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var showBottomSheet by remember { mutableStateOf(false) }
    var stagesList by remember { mutableStateOf<List<TreatmentStageModel>>(emptyList()) }
    var isLoadingStages by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val controller = remember { TreatmentHistoryController() }

    LaunchedEffect(Unit) {
        val userId = SessionManager.currentUserId
        if (userId != -1) {
            historyList = controller.getTreatmentHistoryByUserId(userId)
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử điều trị") },
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
        } else if (historyList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Không có lịch sử điều trị.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(historyList) { record ->
                    TreatmentRecordCard(record, onViewStages = { routeId ->
                        if (routeId > 0) {
                            showBottomSheet = true
                            isLoadingStages = true
                            coroutineScope.launch {
                                stagesList = controller.getStagesByRouteId(routeId)
                                isLoadingStages = false
                            }
                        }
                    })
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(bottom = 32.dp)
                ) {
                    Text("Chi tiết lộ trình điều trị", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isLoadingStages) {
                        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (stagesList.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                            Text("Lộ trình này chưa có bước thực hiện nào.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(stagesList) { stage ->
                                TreatmentStageRow(stage)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TreatmentStageRow(stage: TreatmentStageModel) {
    val statusLower = stage.status.lowercase()
    val isCompleted = statusLower.contains("đã hoàn thành") || statusLower.contains("xong")
    val inProgress = statusLower.contains("đang thực hiện")
    
    val color = when {
        isCompleted -> SuccessGreen
        inProgress -> WarningYellow
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(24.dp).background(color.copy(alpha = 0.2f), shape = androidx.compose.foundation.shape.CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.size(12.dp).background(color, shape = androidx.compose.foundation.shape.CircleShape))
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(stage.stageName, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            if (stage.time.isNotEmpty()) {
                Text(stage.time, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        
        Text(stage.status, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = color)
    }
}

@Composable
fun TreatmentRecordCard(record: TreatmentHistoryModel, onViewStages: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = record.diagnosis,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                
                val statusLower = record.statusStage.lowercase()
                val statusColor = when {
                    statusLower.contains("đã hoàn thành") -> SuccessGreen
                    statusLower.contains("chưa có lộ trình") -> ErrorRed
                    else -> WarningYellow
                }
                
                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(record.statusStage, color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Text("Lộ trình:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(record.treatmentPlan, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)

            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Bác sĩ: ${record.doctorName}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ngày khám: ${record.createdAt}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            }
            
            if (record.treatmentRouteId > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { onViewStages(record.treatmentRouteId) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Xem chi tiết lộ trình")
                }
            }
        }
    }
}
