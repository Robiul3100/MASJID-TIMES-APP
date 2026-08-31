package com.example.feature.admin.meals

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.auth.AdminUser
import com.example.data.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMealsScreen(
    currentAdmin: AdminUser?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminMealsViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val statusFilter by viewModel.selectedStatusFilter.collectAsStateWithLifecycle()
    val mealTypeFilter by viewModel.selectedMealTypeFilter.collectAsStateWithLifecycle()
    val actionMessage by viewModel.actionMessage.collectAsStateWithLifecycle()

    var editingMeal by remember { mutableStateOf<MealSchedule?>(null) }
    var showAddHouseholdDialog by remember { mutableStateOf(false) }

    LaunchedEffect(currentAdmin) {
        viewModel.setCurrentAdmin(currentAdmin)
    }

    LaunchedEffect(actionMessage) {
        actionMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearActionMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "হুজুরখানা ও খানা ব্যবস্থাপনা",
                            style = AppTypography.screenTitle,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "ইমাম ও মুয়াজ্জিনের খাবারের শিডিউল নিয়ন্ত্রণ",
                            fontSize = 11.sp,
                            color = PrimaryGreen,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "ফিরে যান",
                            tint = TextWhite
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddHouseholdDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "নতুন পরিবার যুক্ত করুন",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface,
                    titleContentColor = TextWhite
                )
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is AdminMealsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
            }
            is AdminMealsUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.message, color = RedDigital)
                }
            }
            is AdminMealsUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
                ) {
                    // Weekly Statistics Metrics
                    item {
                        MealMetricsHeader(summary = state.summary)
                    }

                    // Search & Filters Card
                    item {
                        SearchAndFilterSection(
                            searchQuery = searchQuery,
                            onQueryChange = viewModel::onSearchQueryChange,
                            selectedStatus = statusFilter,
                            onStatusChange = viewModel::onStatusFilterChange,
                            selectedMealType = mealTypeFilter,
                            onMealTypeChange = viewModel::onMealTypeFilterChange
                        )
                    }

                    // Schedule Lists
                    if (state.filteredSchedules.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "কোনো শিডিউল পাওয়া যায়নি",
                                        color = TextMuted,
                                        fontFamily = SolaimanLipiFontFamily
                                    )
                                }
                            }
                        }
                    } else {
                        items(state.filteredSchedules, key = { it.dateStr }) { daySchedule ->
                            AdminDayScheduleCard(
                                daySchedule = daySchedule,
                                onEditMeal = { meal -> editingMeal = meal },
                                onStatusChange = { mealId, newStatus ->
                                    viewModel.updateMealStatus(mealId, newStatus)
                                },
                                onToggleReminder = { mealId ->
                                    viewModel.toggleReminder(mealId)
                                },
                                onCallHost = { phone ->
                                    if (phone.isNotBlank()) {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:$phone")
                                        }
                                        context.startActivity(intent)
                                    }
                                },
                                onSendSms = { meal, dateBn ->
                                    val phone = meal.phoneNumber ?: ""
                                    if (phone.isNotBlank()) {
                                        val msg = "আসসালামু আলাইকুম, অদ্য $dateBn তারিখে বায়তুল আমান জামে মসজিদের সম্মানিত ইমাম সাহেবের ${meal.mealType.titleBn} খানার দায়িত্ব আপনার। অনুগ্রহ করে যথাসময়ে খানা পৌঁছানোর ব্যবস্থা করবেন। জাযাকাল্লাহু খাইরান।"
                                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                                            data = Uri.parse("smsto:$phone")
                                            putExtra("sms_body", msg)
                                        }
                                        context.startActivity(intent)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Edit Meal Dialog
    editingMeal?.let { meal ->
        EditMealHostDialog(
            meal = meal,
            onDismiss = { editingMeal = null },
            onSave = { responsible, household, area, phone, items, notes ->
                viewModel.updateMealDetails(
                    mealId = meal.id,
                    responsiblePerson = responsible,
                    householdName = household,
                    area = area,
                    phone = phone,
                    menuItems = items,
                    notes = notes,
                    existingMeal = meal
                )
                editingMeal = null
            }
        )
    }

    // Add Household Dialog
    if (showAddHouseholdDialog) {
        AddHouseholdDialog(
            onDismiss = { showAddHouseholdDialog = false },
            onSave = { name, resp, area, phone ->
                viewModel.addHousehold(name, resp, area, phone)
                showAddHouseholdDialog = false
            }
        )
    }
}

@Composable
private fun MealMetricsHeader(summary: MealWeeklySummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, DarkGreenBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "চলতি সপ্তাহের সারসংক্ষেপ",
                style = AppTypography.cardTitle,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(label = "মোট খানা", value = "${summary.totalMeals}", color = TextWhite)
                MetricItem(label = "পৌঁছেছে", value = "${summary.deliveredCount}", color = PrimaryGreen)
                MetricItem(label = "বাকি আছে", value = "${summary.pendingCount}", color = GoldAccent)
                MetricItem(label = "মিস হয়েছে", value = "${summary.missedCount}", color = RedDigital)
            }
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            fontFamily = SolaimanLipiFontFamily
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextMuted,
            fontFamily = SolaimanLipiFontFamily
        )
    }
}

@Composable
private fun SearchAndFilterSection(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    selectedStatus: MealStatus?,
    onStatusChange: (MealStatus?) -> Unit,
    selectedMealType: MealType?,
    onMealTypeChange: (MealType?) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, DarkGreenBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("নাম, বাড়ি, পাড়া দিয়ে খুঁজুন...", fontSize = 13.sp, color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryGreen) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = DarkGreenBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Status Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    FilterChip(
                        selected = selectedStatus == null,
                        onClick = { onStatusChange(null) },
                        label = { Text("সকল স্ট্যাটাস", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreen,
                            selectedLabelColor = DarkBackground,
                            containerColor = DarkSurfaceElevated,
                            labelColor = TextWhite
                        )
                    )
                }
                items(MealStatus.values()) { status ->
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = { onStatusChange(if (selectedStatus == status) null else status) },
                        label = { Text(status.titleBn, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreen,
                            selectedLabelColor = DarkBackground,
                            containerColor = DarkSurfaceElevated,
                            labelColor = TextWhite
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminDayScheduleCard(
    daySchedule: DayMealSchedule,
    onEditMeal: (MealSchedule) -> Unit,
    onStatusChange: (mealId: String, newStatus: MealStatus) -> Unit,
    onToggleReminder: (mealId: String) -> Unit,
    onCallHost: (phone: String) -> Unit,
    onSendSms: (meal: MealSchedule, dateBn: String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (daySchedule.isToday) DarkSurfaceElevated else DarkSurface
        ),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            if (daySchedule.isToday) PrimaryGreen else DarkGreenBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${daySchedule.dayNameBn}, ${daySchedule.dateBn}",
                        style = AppTypography.cardTitle,
                        fontSize = 15.sp,
                        color = if (daySchedule.isToday) PrimaryGreen else TextWhite
                    )
                    if (daySchedule.isToday) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = PrimaryGreen.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, PrimaryGreen)
                        ) {
                            Text(
                                text = "আজকের দিন",
                                fontSize = 10.sp,
                                color = PrimaryGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontFamily = SolaimanLipiFontFamily
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3 Meals Row (Morning, Lunch, Dinner)
            AdminMealRow(
                meal = daySchedule.morningMeal,
                dateBn = daySchedule.dateBn,
                onEdit = { onEditMeal(daySchedule.morningMeal) },
                onStatusChange = { onStatusChange(daySchedule.morningMeal.id, it) },
                onToggleReminder = { onToggleReminder(daySchedule.morningMeal.id) },
                onCallHost = { onCallHost(daySchedule.morningMeal.phoneNumber ?: "") },
                onSendSms = { onSendSms(daySchedule.morningMeal, daySchedule.dateBn) }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = DarkGreenBorder.copy(alpha = 0.5f)
            )

            AdminMealRow(
                meal = daySchedule.lunchMeal,
                dateBn = daySchedule.dateBn,
                onEdit = { onEditMeal(daySchedule.lunchMeal) },
                onStatusChange = { onStatusChange(daySchedule.lunchMeal.id, it) },
                onToggleReminder = { onToggleReminder(daySchedule.lunchMeal.id) },
                onCallHost = { onCallHost(daySchedule.lunchMeal.phoneNumber ?: "") },
                onSendSms = { onSendSms(daySchedule.lunchMeal, daySchedule.dateBn) }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = DarkGreenBorder.copy(alpha = 0.5f)
            )

            AdminMealRow(
                meal = daySchedule.dinnerMeal,
                dateBn = daySchedule.dateBn,
                onEdit = { onEditMeal(daySchedule.dinnerMeal) },
                onStatusChange = { onStatusChange(daySchedule.dinnerMeal.id, it) },
                onToggleReminder = { onToggleReminder(daySchedule.dinnerMeal.id) },
                onCallHost = { onCallHost(daySchedule.dinnerMeal.phoneNumber ?: "") },
                onSendSms = { onSendSms(daySchedule.dinnerMeal, daySchedule.dateBn) }
            )
        }
    }
}

@Composable
private fun AdminMealRow(
    meal: MealSchedule,
    dateBn: String,
    onEdit: () -> Unit,
    onStatusChange: (MealStatus) -> Unit,
    onToggleReminder: () -> Unit,
    onCallHost: () -> Unit,
    onSendSms: () -> Unit
) {
    var showStatusMenu by remember { mutableStateOf(false) }

    val statusColor = when (meal.status) {
        MealStatus.DELIVERED -> PrimaryGreen
        MealStatus.PENDING -> GoldAccent
        MealStatus.UPCOMING -> TextMuted
        MealStatus.MISSED -> RedDigital
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceElevated)
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = meal.mealType.titleBn,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    fontFamily = SolaimanLipiFontFamily
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "(${meal.mealType.timeRangeBn})",
                    fontSize = 10.sp,
                    color = TextMuted,
                    fontFamily = SolaimanLipiFontFamily
                )
            }

            // Status Badge with Dropdown
            Box {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, statusColor),
                    modifier = Modifier.clickable { showStatusMenu = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = meal.status.titleBn,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }
                }

                DropdownMenu(
                    expanded = showStatusMenu,
                    onDismissRequest = { showStatusMenu = false },
                    modifier = Modifier.background(DarkSurface)
                ) {
                    MealStatus.values().forEach { status ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = status.titleBn,
                                    color = when (status) {
                                        MealStatus.DELIVERED -> PrimaryGreen
                                        MealStatus.PENDING -> GoldAccent
                                        MealStatus.UPCOMING -> TextWhite
                                        MealStatus.MISSED -> RedDigital
                                    },
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            },
                            onClick = {
                                onStatusChange(status)
                                showStatusMenu = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Responsible Host & Area
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meal.responsiblePersonName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite,
                    fontFamily = SolaimanLipiFontFamily
                )
                Text(
                    text = "${meal.householdName} • ${meal.area}",
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontFamily = SolaimanLipiFontFamily
                )
                if (meal.deliveredAt != null) {
                    Text(
                        text = "পৌঁছেছে: ${meal.deliveredAt}",
                        fontSize = 10.sp,
                        color = PrimaryGreen,
                        fontFamily = SolaimanLipiFontFamily
                    )
                }
            }

            // Action Buttons (Call, SMS, Reminder, Edit)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onCallHost,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Call",
                        tint = PrimaryGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onSendSms,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sms,
                        contentDescription = "SMS",
                        tint = GoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onToggleReminder,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (meal.isReminderActive) Icons.Outlined.NotificationsActive else Icons.Outlined.Notifications,
                        contentDescription = "Reminder",
                        tint = if (meal.isReminderActive) GoldAccent else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Host",
                        tint = TextWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EditMealHostDialog(
    meal: MealSchedule,
    onDismiss: () -> Unit,
    onSave: (responsible: String, household: String, area: String, phone: String, items: List<String>, notes: String) -> Unit
) {
    var responsibleName by remember { mutableStateOf(meal.responsiblePersonName) }
    var householdName by remember { mutableStateOf(meal.householdName) }
    var area by remember { mutableStateOf(meal.area) }
    var phone by remember { mutableStateOf(meal.phoneNumber ?: "") }
    var menuText by remember { mutableStateOf(meal.specialItems.joinToString(", ")) }
    var notes by remember { mutableStateOf(meal.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "${meal.mealType.titleBn} খানার দায়িত্ব পরিবর্তন",
                style = AppTypography.cardTitle,
                fontSize = 16.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = responsibleName,
                    onValueChange = { responsibleName = it },
                    label = { Text("দায়িত্বপ্রাপ্ত ব্যক্তির নাম", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = householdName,
                    onValueChange = { householdName = it },
                    label = { Text("বাড়ির নাম (হাজী বাড়ি ইত্যাদি)", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text("মহল্লা / পাড়ার নাম", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("মোবাইল নম্বর", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = menuText,
                    onValueChange = { menuText = it },
                    label = { Text("মেনু / বিশেষ খাবার (কমা দিয়ে লিখুন)", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("বিশেষ নোট", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val items = menuText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    onSave(responsibleName, householdName, area, phone, items, notes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text("সংরক্ষণ করুন", color = DarkBackground, fontFamily = SolaimanLipiFontFamily)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল", color = TextMuted, fontFamily = SolaimanLipiFontFamily)
            }
        },
        containerColor = DarkSurface
    )
}

@Composable
private fun AddHouseholdDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, responsible: String, area: String, phone: String) -> Unit
) {
    var houseName by remember { mutableStateOf("") }
    var responsibleName by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "নতুন পরিবার যুক্ত করুন",
                style = AppTypography.cardTitle,
                fontSize = 16.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = houseName,
                    onValueChange = { houseName = it },
                    label = { Text("বাড়ির নাম (যেমন: হাজী বাড়ি)", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = responsibleName,
                    onValueChange = { responsibleName = it },
                    label = { Text("কর্তা / দায়িত্বশীলের নাম", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text("মহল্লা / পাড়া (যেমন: উত্তর পাড়া)", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("মোবাইল নম্বর", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (houseName.isNotBlank() && responsibleName.isNotBlank()) {
                        onSave(houseName, responsibleName, area, phone)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                enabled = houseName.isNotBlank() && responsibleName.isNotBlank()
            ) {
                Text("যুক্ত করুন", color = DarkBackground, fontFamily = SolaimanLipiFontFamily)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল", color = TextMuted, fontFamily = SolaimanLipiFontFamily)
            }
        },
        containerColor = DarkSurface
    )
}

