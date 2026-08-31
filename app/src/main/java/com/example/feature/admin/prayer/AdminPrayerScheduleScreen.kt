package com.example.feature.admin.prayer

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.auth.AdminUser
import com.example.core.auth.PermissionManager
import com.example.ui.theme.SolaimanLipiFontFamily
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPrayerScheduleScreen(
    currentAdmin: AdminUser?,
    onNavigateBack: () -> Unit,
    viewModel: AdminPrayerScheduleViewModel = viewModel()
) {
    val context = LocalContext.current

    val hasPermission = currentAdmin?.let { PermissionManager.canManagePrayerTimes(it) } ?: false

    val fajrAzan by viewModel.fajrAzan.collectAsState()
    val fajrIqamah by viewModel.fajrIqamah.collectAsState()
    val dhuhrAzan by viewModel.dhuhrAzan.collectAsState()
    val dhuhrIqamah by viewModel.dhuhrIqamah.collectAsState()
    val asrAzan by viewModel.asrAzan.collectAsState()
    val asrIqamah by viewModel.asrIqamah.collectAsState()
    val maghribAzan by viewModel.maghribAzan.collectAsState()
    val maghribIqamah by viewModel.maghribIqamah.collectAsState()
    val ishaAzan by viewModel.ishaAzan.collectAsState()
    val ishaIqamah by viewModel.ishaIqamah.collectAsState()

    val jumahAzan1 by viewModel.jumahAzan1.collectAsState()
    val jumahKhutbah by viewModel.jumahKhutbah.collectAsState()
    val jumahJamath by viewModel.jumahJamath.collectAsState()

    val sehriEnd by viewModel.sehriEnd.collectAsState()
    val iftarTime by viewModel.iftarTime.collectAsState()
    val tahajjudTime by viewModel.tahajjudTime.collectAsState()
    val ishraqTime by viewModel.ishraqTime.collectAsState()
    val chashtTime by viewModel.chashtTime.collectAsState()

    val isCustomActive by viewModel.isCustomActive.collectAsState()
    val broadcastNotification by viewModel.broadcastNotification.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is PrayerScheduleUiEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, if (event.isError) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "নামাজের সময়সূচি নিয়ন্ত্রণ",
                            fontFamily = SolaimanLipiFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "৫ ওয়াক্ত ও জুমার আজান ও জামাত পরিচালনা",
                            fontFamily = SolaimanLipiFontFamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ফিরে যান"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            if (hasPermission) {
                Surface(
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.populateForm(viewModel.currentOverrides.value)
                                Toast.makeText(context, "পূর্বাবস্থায় ফেরানো হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("reset_schedule_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("রিসেট", fontFamily = SolaimanLipiFontFamily, fontSize = 16.sp)
                        }

                        Button(
                            onClick = { viewModel.saveSchedule(currentAdmin) },
                            enabled = !isLoading,
                            modifier = Modifier
                                .weight(1.6f)
                                .height(50.dp)
                                .testTag("save_schedule_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("লাইভ আপডেট করুন", fontFamily = SolaimanLipiFontFamily, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (!hasPermission) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "অনুমতি নেই",
                        fontFamily = SolaimanLipiFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "আপনার এই রোল দিয়ে নামাজের সময়সূচি পরিবর্তন করার অনুমতি নেই।",
                        fontFamily = SolaimanLipiFontFamily,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Status Card / Toggle
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCustomActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "মসজিদ ভিত্তিক নিজস্ব সময়সূচি",
                                fontFamily = SolaimanLipiFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = if (isCustomActive) "বর্তমান: অ্যাডমিন নির্ধারিত কাস্টম জামাত সময় লাইভ চলছে"
                                else "বর্তমান: স্বয়ংক্রিয় সাধারণ অ্যালগরিদম সময় সক্রিয়",
                                fontFamily = SolaimanLipiFontFamily,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isCustomActive,
                            onCheckedChange = { viewModel.isCustomActive.value = it },
                            modifier = Modifier.testTag("custom_schedule_switch")
                        )
                    }
                }
            }

            // Section 1: 5 Daily Prayers
            item {
                Text(
                    text = "৫ ওয়াক্ত নামাজের আজান ও জামাত সময়",
                    fontFamily = SolaimanLipiFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Fajr Card
            item {
                PrayerWaqtEditCard(
                    waqtName = "ফজর (Fajr)",
                    azanFlow = viewModel.fajrAzan,
                    iqamahFlow = viewModel.fajrIqamah,
                    onAdjustAzan = { offset -> viewModel.adjustTime(viewModel.fajrAzan, offset) },
                    onAdjustIqamah = { offset -> viewModel.adjustTime(viewModel.fajrIqamah, offset) },
                    tagPrefix = "fajr"
                )
            }

            // Dhuhr Card
            item {
                PrayerWaqtEditCard(
                    waqtName = "যোহর (Dhuhr)",
                    azanFlow = viewModel.dhuhrAzan,
                    iqamahFlow = viewModel.dhuhrIqamah,
                    onAdjustAzan = { offset -> viewModel.adjustTime(viewModel.dhuhrAzan, offset) },
                    onAdjustIqamah = { offset -> viewModel.adjustTime(viewModel.dhuhrIqamah, offset) },
                    tagPrefix = "dhuhr"
                )
            }

            // Asr Card
            item {
                PrayerWaqtEditCard(
                    waqtName = "আসর (Asr)",
                    azanFlow = viewModel.asrAzan,
                    iqamahFlow = viewModel.asrIqamah,
                    onAdjustAzan = { offset -> viewModel.adjustTime(viewModel.asrAzan, offset) },
                    onAdjustIqamah = { offset -> viewModel.adjustTime(viewModel.asrIqamah, offset) },
                    tagPrefix = "asr"
                )
            }

            // Maghrib Card
            item {
                PrayerWaqtEditCard(
                    waqtName = "মাগরিব (Maghrib)",
                    azanFlow = viewModel.maghribAzan,
                    iqamahFlow = viewModel.maghribIqamah,
                    onAdjustAzan = { offset -> viewModel.adjustTime(viewModel.maghribAzan, offset) },
                    onAdjustIqamah = { offset -> viewModel.adjustTime(viewModel.maghribIqamah, offset) },
                    tagPrefix = "maghrib"
                )
            }

            // Isha Card
            item {
                PrayerWaqtEditCard(
                    waqtName = "এশা (Isha)",
                    azanFlow = viewModel.ishaAzan,
                    iqamahFlow = viewModel.ishaIqamah,
                    onAdjustAzan = { offset -> viewModel.adjustTime(viewModel.ishaAzan, offset) },
                    onAdjustIqamah = { offset -> viewModel.adjustTime(viewModel.ishaIqamah, offset) },
                    tagPrefix = "isha"
                )
            }

            // Section 2: Jumah Schedule
            item {
                Text(
                    text = "পবিত্র জুমার নামাজের সময়সূচি",
                    fontFamily = SolaimanLipiFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("জুমার বিশেষ সময় নির্ধারণ", fontFamily = SolaimanLipiFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = jumahAzan1,
                                onValueChange = { viewModel.jumahAzan1.value = it },
                                label = { Text("১ম আজান", fontFamily = SolaimanLipiFontFamily) },
                                modifier = Modifier.weight(1f).testTag("input_jumah_azan1"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = jumahKhutbah,
                                onValueChange = { viewModel.jumahKhutbah.value = it },
                                label = { Text("খুতবা শুরু", fontFamily = SolaimanLipiFontFamily) },
                                modifier = Modifier.weight(1f).testTag("input_jumah_khutbah"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = jumahJamath,
                                onValueChange = { viewModel.jumahJamath.value = it },
                                label = { Text("জুমার জামাত", fontFamily = SolaimanLipiFontFamily) },
                                modifier = Modifier.weight(1f).testTag("input_jumah_jamath"),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            // Section 3: Extra Times (Sehri, Iftar, Tahajjud, Ishraq, Chasht)
            item {
                Text(
                    text = "নফল, রোজা ও বিশেষ সময়সূচি",
                    fontFamily = SolaimanLipiFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = sehriEnd,
                                onValueChange = { viewModel.sehriEnd.value = it },
                                label = { Text("সেহরি শেষ সময়", fontFamily = SolaimanLipiFontFamily) },
                                modifier = Modifier.weight(1f).testTag("input_sehri"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = iftarTime,
                                onValueChange = { viewModel.iftarTime.value = it },
                                label = { Text("সূর্যাস্ত ও ইফতার", fontFamily = SolaimanLipiFontFamily) },
                                modifier = Modifier.weight(1f).testTag("input_iftar"),
                                singleLine = true
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = ishraqTime,
                                onValueChange = { viewModel.ishraqTime.value = it },
                                label = { Text("ইশরাক শুরু", fontFamily = SolaimanLipiFontFamily) },
                                modifier = Modifier.weight(1f).testTag("input_ishraq"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = chashtTime,
                                onValueChange = { viewModel.chashtTime.value = it },
                                label = { Text("চাশত (দুহা)", fontFamily = SolaimanLipiFontFamily) },
                                modifier = Modifier.weight(1f).testTag("input_chasht"),
                                singleLine = true
                            )
                        }

                        OutlinedTextField(
                            value = tahajjudTime,
                            onValueChange = { viewModel.tahajjudTime.value = it },
                            label = { Text("তাহাজ্জুদের সর্বোত্তম সময়", fontFamily = SolaimanLipiFontFamily) },
                            modifier = Modifier.fillMaxWidth().testTag("input_tahajjud"),
                            singleLine = true
                        )
                    }
                }
            }

            // Notification option toggle
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = broadcastNotification,
                        onCheckedChange = { viewModel.broadcastNotification.value = it },
                        modifier = Modifier.testTag("broadcast_checkbox")
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "পরিবর্তনটি মসজিদের নোটিশ ও লাইভ নোটিফিকেশনে প্রচার করুন",
                        fontFamily = SolaimanLipiFontFamily,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PrayerWaqtEditCard(
    waqtName: String,
    azanFlow: MutableStateFlow<String>,
    iqamahFlow: MutableStateFlow<String>,
    onAdjustAzan: (Int) -> Unit,
    onAdjustIqamah: (Int) -> Unit,
    tagPrefix: String
) {
    val azanValue by azanFlow.collectAsState()
    val iqamahValue by iqamahFlow.collectAsState()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = waqtName,
                    fontFamily = SolaimanLipiFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            // Azan Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = azanValue,
                    onValueChange = { azanFlow.value = it },
                    label = { Text("আজান", fontFamily = SolaimanLipiFontFamily) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_${tagPrefix}_azan"),
                    singleLine = true
                )

                QuickAdjustButton(text = "-৫", onClick = { onAdjustAzan(-5) })
                QuickAdjustButton(text = "+৫", onClick = { onAdjustAzan(5) })
            }

            // Iqamah Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = iqamahValue,
                    onValueChange = { iqamahFlow.value = it },
                    label = { Text("জামাত", fontFamily = SolaimanLipiFontFamily) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_${tagPrefix}_iqamah"),
                    singleLine = true
                )

                QuickAdjustButton(text = "-৫", onClick = { onAdjustIqamah(-5) })
                QuickAdjustButton(text = "+৫", onClick = { onAdjustIqamah(5) })
                QuickAdjustButton(text = "+১০", onClick = { onAdjustIqamah(10) })
            }
        }
    }
}

@Composable
private fun QuickAdjustButton(text: String, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        modifier = Modifier.height(36.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
