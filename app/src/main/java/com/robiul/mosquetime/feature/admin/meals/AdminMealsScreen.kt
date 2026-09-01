package com.robiul.mosquetime.feature.admin.meals

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robiul.mosquetime.core.auth.AdminUser
import com.robiul.mosquetime.data.model.*
import com.robiul.mosquetime.ui.theme.*


enum class AdminMealTab(val titleBn: String) {
    DAILY_SCHEDULE("দৈনিক খানা সূচি"),
    HOUSEHOLD_ROSTER("১৫ পরিবারের তালিকা")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMealsScreen(
    currentAdmin: AdminUser?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminMealsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val statusFilter by viewModel.selectedStatusFilter.collectAsStateWithLifecycle()
    val mealTypeFilter by viewModel.selectedMealTypeFilter.collectAsStateWithLifecycle()
    val actionMessage by viewModel.actionMessage.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(AdminMealTab.DAILY_SCHEDULE) }

    // Dialog States
    var editingDaySchedule by remember { mutableStateOf<DayMealSchedule?>(null) }
    var editingMeal by remember { mutableStateOf<MealSchedule?>(null) }
    var householdToEdit by remember { mutableStateOf<Household?>(null) }
    var showAddHouseholdDialog by remember { mutableStateOf(false) }
    var householdToDelete by remember { mutableStateOf<Household?>(null) }
    var dayToDelete by remember { mutableStateOf<DayMealSchedule?>(null) }
    var showGenerateRotationDialog by remember { mutableStateOf(false) }

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
                            text = "হুজুরের খানা ও মেহমানদারি ব্যবস্থাপনা",
                            style = AppTypography.screenTitle,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "১ দিনে ১ পরিবার (৩ বেলা) • ১৫ পরিবারের ৩০ দিনের রোটেশন",
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
                    if (selectedTab == AdminMealTab.HOUSEHOLD_ROSTER) {
                        IconButton(onClick = { showAddHouseholdDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "নতুন পরিবার যুক্ত করুন",
                                tint = NeonGreenGlow
                            )
                        }
                    } else {
                        IconButton(onClick = { showGenerateRotationDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Autorenew,
                                contentDescription = "রোটেশন সাজান",
                                tint = GoldAccent
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface,
                    titleContentColor = TextWhite
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = DarkSurface,
                contentColor = PrimaryGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                        color = PrimaryGreen
                    )
                }
            ) {
                AdminMealTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = {
                            Text(
                                text = tab.titleBn,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == tab) PrimaryGreen else TextMuted,
                                fontFamily = SolaimanLipiFontFamily
                            )
                        }
                    )
                }
            }

            when (val state = uiState) {
                is AdminMealsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryGreen)
                    }
                }
                is AdminMealsUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = state.message, color = RedDigital)
                    }
                }
                is AdminMealsUiState.Success -> {
                    if (selectedTab == AdminMealTab.DAILY_SCHEDULE) {
                        // TAB 1: DAILY SCHEDULE LIST
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp)
                        ) {
                            // Weekly Statistics
                            item {
                                MealMetricsHeader(summary = state.summary)
                            }

                            // Search & Filter
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

                            if (state.filteredSchedules.isEmpty()) {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxWidth().padding(24.dp),
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
                                        onEditDayHost = { editingDaySchedule = daySchedule },
                                        onDeleteDay = { dayToDelete = daySchedule },
                                        onEditMeal = { meal -> editingMeal = meal },
                                        onStatusChange = { mealId, newStatus ->
                                            viewModel.updateMealStatus(mealId, newStatus)
                                        },
                                        onToggleReminder = { mealId ->
                                            viewModel.toggleReminder(mealId)
                                        },
                                        onCallHost = { phone ->
                                            if (!phone.isNullOrBlank()) {
                                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                                    data = Uri.parse("tel:$phone")
                                                }
                                                context.startActivity(intent)
                                            }
                                        },
                                        onSendSms = { hostName, phone, dateBn ->
                                            if (!phone.isNullOrBlank()) {
                                                val msg = "আসসালামু আলাইকুম $hostName, অদ্য $dateBn তারিখে বায়তুল আমান জামে মসজিদের সম্মানিত ইমাম সাহেবের ৩ বেলার (সকাল, দুপুর ও রাত) মেহমানদারির দায়িত্ব আপনার। অনুগ্রহ করে যথাসময়ে খাবার পাঠানোর ব্যবস্থা করবেন। জাযাকাল্লাহু খাইরান।"
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
                    } else {
                        // TAB 2: 15 HOUSEHOLDS ROSTER LIST
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp)
                        ) {
                            item {
                                RosterExplanationHeader(
                                    householdsCount = state.households.size,
                                    onAddClick = { showAddHouseholdDialog = true },
                                    onAutoGenerateClick = { showGenerateRotationDialog = true }
                                )
                            }

                            items(state.households, key = { it.id }) { household ->
                                HouseholdRosterCard(
                                    household = household,
                                    onEdit = { householdToEdit = household },
                                    onDelete = { householdToDelete = household },
                                    onCall = { phone ->
                                        if (!phone.isNullOrBlank()) {
                                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                                data = Uri.parse("tel:$phone")
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
    }

    // Edit Day Host Dialog
    editingDaySchedule?.let { day ->
        val households = (uiState as? AdminMealsUiState.Success)?.households ?: emptyList()
        EditDayHostDialog(
            daySchedule = day,
            availableHouseholds = households,
            onDismiss = { editingDaySchedule = null },
            onSave = { houseName, resp, area, phone, notes ->
                viewModel.updateDayHost(
                    dateStr = day.dateStr,
                    householdName = houseName,
                    responsiblePerson = resp,
                    area = area,
                    phone = phone,
                    notes = notes
                )
                editingDaySchedule = null
            }
        )
    }

    // Edit Specific Meal Dialog
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
        AddEditHouseholdDialog(
            household = null,
            onDismiss = { showAddHouseholdDialog = false },
            onSave = { name, resp, area, phone, notes ->
                viewModel.addHousehold(name, resp, area, phone, notes)
                showAddHouseholdDialog = false
            }
        )
    }

    // Edit Household Dialog
    householdToEdit?.let { h ->
        AddEditHouseholdDialog(
            household = h,
            onDismiss = { householdToEdit = null },
            onSave = { name, resp, area, phone, notes ->
                viewModel.updateHousehold(
                    h.copy(
                        householdName = name,
                        responsiblePersonName = resp,
                        area = area,
                        phoneNumber = phone,
                        notes = notes
                    )
                )
                householdToEdit = null
            }
        )
    }

    // Delete Household Confirmation Dialog
    householdToDelete?.let { h ->
        AlertDialog(
            onDismissRequest = { householdToDelete = null },
            title = { Text("পরিবার মুছে ফেলবেন?", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "আপনি কি নিশ্চিত যে '${h.householdName}' পরিবারকে রোটেশন তালিকা থেকে মুছে ফেলতে চান?",
                    color = TextMuted,
                    fontFamily = SolaimanLipiFontFamily
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteHousehold(h.id)
                        householdToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDigital)
                ) {
                    Text("মুছে ফেলুন", color = TextWhite, fontFamily = SolaimanLipiFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { householdToDelete = null }) {
                    Text("বাতিল", color = TextMuted, fontFamily = SolaimanLipiFontFamily)
                }
            },
            containerColor = DarkSurface
        )
    }

    // Delete Day Confirmation Dialog
    dayToDelete?.let { day ->
        AlertDialog(
            onDismissRequest = { dayToDelete = null },
            title = { Text("দিনের শিডিউল মুছে ফেলবেন?", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "${day.dayNameBn}, ${day.dateBn} তারিখের খাবার শিডিউল মুছে ফেলতে চান?",
                    color = TextMuted,
                    fontFamily = SolaimanLipiFontFamily
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDaySchedule(day.dateStr)
                        dayToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDigital)
                ) {
                    Text("মুছে ফেলুন", color = TextWhite, fontFamily = SolaimanLipiFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { dayToDelete = null }) {
                    Text("বাতিল", color = TextMuted, fontFamily = SolaimanLipiFontFamily)
                }
            },
            containerColor = DarkSurface
        )
    }

    // Auto-Generate 30-Day Rotation Dialog
    if (showGenerateRotationDialog) {
        AlertDialog(
            onDismissRequest = { showGenerateRotationDialog = false },
            title = { Text("৩০ দিনের রোটেশন স্বয়ংক্রিয়ভাবে সাজান", color = GoldAccent, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "তালিকাভুক্ত পরিবারগুলোর মাধ্যমে পুরো ৩০ দিনের ১-পরিবার-১-দিন (৩ বেলা) শিডিউল স্বয়ংক্রিয়ভাবে জেনারেট করতে চান? (প্রতিটি পরিবার মাসে ২ দিন পালাক্রমে খাবার সরবরাহ করবে)",
                    color = TextWhite,
                    fontFamily = SolaimanLipiFontFamily
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.generateMonthlyRotation()
                        showGenerateRotationDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text("হ্যাঁ, রোটেশন তৈরি করুন", color = DarkBackground, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGenerateRotationDialog = false }) {
                    Text("বাতিল", color = TextMuted, fontFamily = SolaimanLipiFontFamily)
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
private fun MealMetricsHeader(summary: MealWeeklySummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, DarkGreenBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "সাপ্তাহিক ও মাসিক সারসংক্ষেপ",
                    style = AppTypography.cardTitle,
                    fontSize = 14.sp
                )
                Text(
                    text = "মোট পরিবার: ${summary.totalHouseholdsCount} টি",
                    color = PrimaryGreen,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(label = "মোট বেলা", value = "${summary.totalMeals}", color = TextWhite)
                MetricItem(label = "পৌঁছেছে", value = "${summary.deliveredCount}", color = PrimaryGreen)
                MetricItem(label = "অপেক্ষমাণ", value = "${summary.pendingCount}", color = GoldAccent)
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
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            fontFamily = SolaimanLipiFontFamily
        )
        Text(
            text = label,
            fontSize = 10.5.sp,
            color = TextMuted,
            fontFamily = SolaimanLipiFontFamily
        )
    }
}

@Composable
private fun RosterExplanationHeader(
    householdsCount: Int,
    onAddClick: () -> Unit,
    onAutoGenerateClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF142218)),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "মেহমানদারী পরিবারের রোটেশন তালিকা",
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = PrimaryGreen.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "$householdsCount টি পরিবার",
                        color = NeonGreenGlow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "💡 নিয়ম: ১ দিনে ১টি পরিবার থেকে ৩ বেলার খাবার সরবরাহ করা হয়। এভাবে ১৫টি পরিবার ৩০ দিনে ২ বার করে হুজুরের মেহমানদারী পালন করেন।",
                color = TextWhite.copy(alpha = 0.9f),
                fontSize = 11.5.sp,
                fontFamily = SolaimanLipiFontFamily
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAddClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = DarkBackground)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("পরিবার যোগ করুন", color = DarkBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onAutoGenerateClick,
                    modifier = Modifier.weight(1.3f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldAccent),
                    border = BorderStroke(1.dp, GoldAccent),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Autorenew, contentDescription = null, modifier = Modifier.size(16.dp), tint = GoldAccent)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("৩০ দিনের রোটেশন সাজান", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun HouseholdRosterCard(
    household: Household,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCall: (String?) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, DarkGreenBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Serial Circle
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PrimaryGreen.copy(alpha = 0.15f))
                    .border(1.dp, PrimaryGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${household.serialNumber}",
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = household.householdName,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "দায়িত্বশীল: ${household.responsiblePersonName} • ${household.area}",
                    color = TextMuted,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!household.notes.isNullOrBlank()) {
                    Text(
                        text = "নোট: ${household.notes}",
                        color = GoldAccent,
                        fontSize = 10.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Actions (Call, Edit, Delete)
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!household.phoneNumber.isNullOrBlank()) {
                    IconButton(
                        onClick = { onCall(household.phoneNumber) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "Call", tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                    }
                }
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = CyanBlue, modifier = Modifier.size(16.dp))
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedDigital, modifier = Modifier.size(16.dp))
                }
            }
        }
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
        Column(modifier = Modifier.padding(10.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                placeholder = { Text("পরিবারের নাম, ব্যক্তি, পাড়া দিয়ে খুঁজুন...", fontSize = 12.sp, color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
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
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    FilterChip(
                        selected = selectedStatus == null,
                        onClick = { onStatusChange(null) },
                        label = { Text("সকল স্ট্যাটাস", fontSize = 10.5.sp) },
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
                        label = { Text(status.titleBn, fontSize = 10.5.sp) },
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

/**
 * 1-Household-Per-Day Admin Card showing Day Host + 3 Meals Breakdown
 */
@Composable
private fun AdminDayScheduleCard(
    daySchedule: DayMealSchedule,
    onEditDayHost: () -> Unit,
    onDeleteDay: () -> Unit,
    onEditMeal: (MealSchedule) -> Unit,
    onStatusChange: (mealId: String, newStatus: MealStatus) -> Unit,
    onToggleReminder: (mealId: String) -> Unit,
    onCallHost: (phone: String?) -> Unit,
    onSendSms: (hostName: String, phone: String?, dateBn: String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (daySchedule.isToday) Color(0xFF14241B) else DarkSurface
        ),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            if (daySchedule.isToday) PrimaryGreen else DarkGreenBorder
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Day Header + Rotation Badge + Edit/Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${daySchedule.dayNameBn}, ${daySchedule.dateBn}",
                        style = AppTypography.cardTitle,
                        fontSize = 14.sp,
                        color = if (daySchedule.isToday) PrimaryGreen else TextWhite
                    )
                    if (daySchedule.isToday) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = PrimaryGreen.copy(alpha = 0.2f),
                            border = BorderStroke(0.8.dp, PrimaryGreen)
                        ) {
                            Text(
                                text = "আজকের দিন",
                                fontSize = 9.5.sp,
                                color = PrimaryGreen,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp),
                                fontFamily = SolaimanLipiFontFamily
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DarkSurfaceElevated,
                    border = BorderStroke(0.6.dp, GoldAccent.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = daySchedule.rotationTurnBn,
                        fontSize = 10.sp,
                        color = GoldAccent,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // DAY'S PRIMARY HOST BANNER
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF1A261E),
                border = BorderStroke(0.8.dp, DarkGreenBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = daySchedule.hostHouseholdName,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "মেহমানদারীকারী: ${daySchedule.hostResponsiblePerson} (${daySchedule.hostArea})",
                            color = TextMuted,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Quick Actions for the Host Family (Call, SMS, Edit Host, Delete Day)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!daySchedule.hostPhoneNumber.isNullOrBlank()) {
                            IconButton(
                                onClick = { onCallHost(daySchedule.hostPhoneNumber) },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = "Call", tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = { onSendSms(daySchedule.hostResponsiblePerson, daySchedule.hostPhoneNumber, daySchedule.dateBn) },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(Icons.Default.Sms, contentDescription = "SMS", tint = GoldAccent, modifier = Modifier.size(16.dp))
                            }
                        }
                        IconButton(
                            onClick = onEditDayHost,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Host", tint = CyanBlue, modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = onDeleteDay,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Day", tint = RedDigital, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3 MEALS ROW (Morning, Lunch, Dinner) under this host
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                AdminMealSlotRow(
                    meal = daySchedule.morningMeal,
                    onEdit = { onEditMeal(daySchedule.morningMeal) },
                    onStatusChange = { onStatusChange(daySchedule.morningMeal.id, it) },
                    onToggleReminder = { onToggleReminder(daySchedule.morningMeal.id) }
                )
                AdminMealSlotRow(
                    meal = daySchedule.lunchMeal,
                    onEdit = { onEditMeal(daySchedule.lunchMeal) },
                    onStatusChange = { onStatusChange(daySchedule.lunchMeal.id, it) },
                    onToggleReminder = { onToggleReminder(daySchedule.lunchMeal.id) }
                )
                AdminMealSlotRow(
                    meal = daySchedule.dinnerMeal,
                    onEdit = { onEditMeal(daySchedule.dinnerMeal) },
                    onStatusChange = { onStatusChange(daySchedule.dinnerMeal.id, it) },
                    onToggleReminder = { onToggleReminder(daySchedule.dinnerMeal.id) }
                )
            }
        }
    }
}

@Composable
private fun AdminMealSlotRow(
    meal: MealSchedule,
    onEdit: () -> Unit,
    onStatusChange: (MealStatus) -> Unit,
    onToggleReminder: () -> Unit
) {
    var showStatusMenu by remember { mutableStateOf(false) }

    val statusColor = when (meal.status) {
        MealStatus.DELIVERED -> PrimaryGreen
        MealStatus.PENDING -> GoldAccent
        MealStatus.UPCOMING -> CyanBlue
        MealStatus.MISSED -> RedDigital
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceElevated)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Meal Type + Time
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1.2f)) {
            Icon(
                imageVector = when (meal.mealType) {
                    MealType.MORNING -> Icons.Outlined.LightMode
                    MealType.LUNCH -> Icons.Outlined.WbSunny
                    MealType.DINNER -> Icons.Outlined.Bedtime
                },
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = meal.mealType.shortNameBn,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    fontFamily = SolaimanLipiFontFamily
                )
                Text(
                    text = meal.deliveredAt ?: meal.notes?.take(18) ?: meal.mealType.timeRangeBn.take(11),
                    fontSize = 9.5.sp,
                    color = if (meal.deliveredAt != null) PrimaryGreen else TextMuted,
                    fontFamily = SolaimanLipiFontFamily
                )
            }
        }

        // Status Badge (Click to Change)
        Box {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = statusColor.copy(alpha = 0.15f),
                border = BorderStroke(0.8.dp, statusColor),
                modifier = Modifier.clickable { showStatusMenu = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = meal.status.titleBn,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        fontFamily = SolaimanLipiFontFamily
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = statusColor, modifier = Modifier.size(12.dp))
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

        Spacer(modifier = Modifier.width(6.dp))

        // Edit Menu & Notes Icon
        IconButton(
            onClick = onEdit,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Edit Menu", tint = TextMuted, modifier = Modifier.size(14.dp))
        }
    }
}

/**
 * Dialog to edit or change the Day's Host family
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditDayHostDialog(
    daySchedule: DayMealSchedule,
    availableHouseholds: List<Household>,
    onDismiss: () -> Unit,
    onSave: (householdName: String, responsible: String, area: String, phone: String, notes: String) -> Unit
) {
    var houseName by remember { mutableStateOf(daySchedule.hostHouseholdName) }
    var responsibleName by remember { mutableStateOf(daySchedule.hostResponsiblePerson) }
    var area by remember { mutableStateOf(daySchedule.hostArea) }
    var phone by remember { mutableStateOf(daySchedule.hostPhoneNumber ?: "") }
    var notes by remember { mutableStateOf(daySchedule.notes ?: "") }
    var showHouseholdSelector by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "${daySchedule.dayNameBn}, ${daySchedule.dateBn} — মেহমানদারী পরিবর্তন",
                style = AppTypography.cardTitle,
                fontSize = 15.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Quick Select from Registered Roster
                if (availableHouseholds.isNotEmpty()) {
                    OutlinedButton(
                        onClick = { showHouseholdSelector = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryGreen),
                        border = BorderStroke(1.dp, PrimaryGreen)
                    ) {
                        Icon(Icons.Outlined.FormatListBulleted, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("তালিকা থেকে পরিবার নির্বাচন করুন", fontSize = 12.sp, fontFamily = SolaimanLipiFontFamily)
                    }

                    if (showHouseholdSelector) {
                        LazyColumn(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                            items(availableHouseholds) { h ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .clickable {
                                            houseName = h.householdName
                                            responsibleName = h.responsiblePersonName
                                            area = h.area
                                            phone = h.phoneNumber ?: ""
                                            showHouseholdSelector = false
                                        },
                                    color = DarkSurfaceElevated,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${h.serialNumber}. ${h.householdName} (${h.responsiblePersonName})",
                                        color = TextWhite,
                                        fontSize = 11.5.sp,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = houseName,
                    onValueChange = { houseName = it },
                    label = { Text("পরিবার / বাড়ির নাম", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = responsibleName,
                    onValueChange = { responsibleName = it },
                    label = { Text("দায়িত্বশীল ব্যক্তির নাম", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text("মহল্লা / পাড়া", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("মোবাইল নম্বর", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("বিশেষ নোট", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(houseName, responsibleName, area, phone, notes) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text("সংরক্ষণ করুন", color = DarkBackground, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
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

/**
 * Dialog to add or edit a Household in the Roster
 */
@Composable
private fun AddEditHouseholdDialog(
    household: Household?,
    onDismiss: () -> Unit,
    onSave: (name: String, responsible: String, area: String, phone: String, notes: String) -> Unit
) {
    var houseName by remember { mutableStateOf(household?.householdName ?: "") }
    var responsibleName by remember { mutableStateOf(household?.responsiblePersonName ?: "") }
    var area by remember { mutableStateOf(household?.area ?: "উত্তর পাড়া") }
    var phone by remember { mutableStateOf(household?.phoneNumber ?: "") }
    var notes by remember { mutableStateOf(household?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (household == null) "নতুন মেহমানদারী পরিবার যুক্ত করুন" else "পরিবারের তথ্য সম্পাদন",
                style = AppTypography.cardTitle,
                fontSize = 15.sp
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
                    label = { Text("বাড়ির নাম (যেমন: হাজী বাড়ি)", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = responsibleName,
                    onValueChange = { responsibleName = it },
                    label = { Text("কর্তা / দায়িত্বশীলের নাম", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text("মহল্লা / পাড়ার নাম", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("মোবাইল নম্বর", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("রোটেশন নোট (যেমন: প্রতি মাসের ১ ও ১৬ তারিখ)", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(houseName, responsibleName, area, phone, notes) },
                enabled = houseName.isNotBlank() && responsibleName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text("সংরক্ষণ করুন", color = DarkBackground, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
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

/**
 * Dialog to edit specific meal menu items / notes
 */
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
                text = "${meal.mealType.titleBn} — মেনু ও বিস্তারিত পরিবর্তন",
                style = AppTypography.cardTitle,
                fontSize = 15.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = menuText,
                    onValueChange = { menuText = it },
                    label = { Text("খাবারের মেনু (কমা দিয়ে লিখুন)", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("বিশেষ নোট", fontSize = 11.sp) },
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
