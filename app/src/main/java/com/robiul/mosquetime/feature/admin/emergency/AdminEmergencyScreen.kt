package com.robiul.mosquetime.feature.admin.emergency

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Radio
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.robiul.mosquetime.core.auth.AdminUser
import com.robiul.mosquetime.data.model.EmergencyAlert
import com.robiul.mosquetime.data.model.JanazaNotice
import com.robiul.mosquetime.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEmergencyScreen(
    currentAdmin: AdminUser?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminEmergencyViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionMessage by viewModel.actionMessage.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Janaza, 1 = Emergency Alerts
    var showCreateJanazaDialog by remember { mutableStateOf(false) }
    var editingJanaza by remember { mutableStateOf<JanazaNotice?>(null) }
    var janazaToDelete by remember { mutableStateOf<JanazaNotice?>(null) }

    var showCreateAlertDialog by remember { mutableStateOf(false) }
    var editingAlert by remember { mutableStateOf<EmergencyAlert?>(null) }
    var alertToDelete by remember { mutableStateOf<EmergencyAlert?>(null) }

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
                            text = "জরুরি জানাজা ও জরুরি অ্যালার্ট",
                            style = AppTypography.screenTitle,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "জরুরি মৃত্যু সংবাদ ও রক্তের আবেদন প্রচার",
                            fontSize = 11.sp,
                            color = RedDigital,
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
                    IconButton(
                        onClick = {
                            if (selectedTab == 0) showCreateJanazaDialog = true
                            else showCreateAlertDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "নতুন ঘোষণা",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface,
                    titleContentColor = TextWhite
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) showCreateJanazaDialog = true
                    else showCreateAlertDialog = true
                },
                containerColor = RedDigital,
                contentColor = TextWhite
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "নতুন অ্যালার্ট")
            }
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is AdminEmergencyUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = RedDigital)
                }
            }
            is AdminEmergencyUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.message, color = RedDigital)
                }
            }
            is AdminEmergencyUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Two Tabs: Janaza vs Emergency Alerts
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = DarkSurface,
                        contentColor = TextWhite,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = if (selectedTab == 0) PrimaryGreen else RedDigital
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Text(
                                    text = "জানাজা ঘোষণা (${state.janazaList.size})",
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == 0) PrimaryGreen else TextMuted,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Text(
                                    text = "জরুরি আবেদন (${state.emergencyAlerts.size})",
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == 1) RedDigital else TextMuted,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            }
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
                    ) {
                        if (selectedTab == 0) {
                            if (state.janazaList.isEmpty()) {
                                item {
                                    EmptyStateCard("বর্তমানে কোনো জানাজা বিজ্ঞপ্তি নেই")
                                }
                            } else {
                                items(state.janazaList, key = { it.id }) { janaza ->
                                    AdminJanazaCard(
                                        janaza = janaza,
                                        onEdit = { editingJanaza = janaza },
                                        onDelete = { janazaToDelete = janaza },
                                        onCallFamily = {
                                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                                data = Uri.parse("tel:${janaza.contactFamilyPhone}")
                                            }
                                            context.startActivity(intent)
                                        },
                                        onShare = {
                                            val shareText = "ইন্না লিল্লাহি ওয়া ইন্না ইলাইহি রাজিউন।\n" +
                                                    "মৃত্যু সংবাদ ও জানাজার ঘোষণা:\n" +
                                                    "মরহুম/মরহুমা: ${janaza.deceasedNameBn}\n" +
                                                    "ইন্তেকালের সময়: ${janaza.demiseTimeBn}\n" +
                                                    "জানাজার সময়: ${janaza.janazaTimeBn}\n" +
                                                    "জানাজার স্থান: ${janaza.janazaLocationBn}\n" +
                                                    "দাফন: ${janaza.graveyardBn}\n" +
                                                    "পরিবারের যোগাযোগ: ${janaza.contactFamilyPhone}\n" +
                                                    "দোয়ার আবেদন: ${janaza.specialMessageBn}"
                                            val sendIntent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, shareText)
                                                type = "text/plain"
                                            }
                                            context.startActivity(Intent.createChooser(sendIntent, "জানাজা ঘোষণা শেয়ার করুন"))
                                        }
                                    )
                                }
                            }
                        } else {
                            if (state.emergencyAlerts.isEmpty()) {
                                item {
                                    EmptyStateCard("বর্তমানে কোনো জরুরি অ্যালার্ট নেই")
                                }
                            } else {
                                items(state.emergencyAlerts, key = { it.id }) { alert ->
                                    AdminEmergencyAlertCard(
                                        alert = alert,
                                        onEdit = { editingAlert = alert },
                                        onToggleResolved = { viewModel.toggleEmergencyAlertResolved(alert) },
                                        onDelete = { alertToDelete = alert },
                                        onCallContact = {
                                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                                data = Uri.parse("tel:${alert.contactPhone}")
                                            }
                                            context.startActivity(intent)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Janaza Form Dialog
    if (showCreateJanazaDialog || editingJanaza != null) {
        val target = editingJanaza
        JanazaFormDialog(
            initialJanaza = target,
            onDismiss = {
                showCreateJanazaDialog = false
                editingJanaza = null
            },
            onSave = { name, age, residence, demise, janazaTime, location, imam, graveyard, phone, msg, broadcast ->
                viewModel.saveJanazaNotice(
                    id = target?.id,
                    deceasedName = name,
                    deceasedAge = age,
                    residence = residence,
                    demiseTime = demise,
                    janazaTime = janazaTime,
                    janazaLocation = location,
                    imamName = imam,
                    graveyard = graveyard,
                    contactPhone = phone,
                    specialMessage = msg,
                    broadcastToMusallis = broadcast
                )
                showCreateJanazaDialog = false
                editingJanaza = null
            }
        )
    }

    // Emergency Alert Form Dialog
    if (showCreateAlertDialog || editingAlert != null) {
        val target = editingAlert
        EmergencyAlertFormDialog(
            initialAlert = target,
            onDismiss = {
                showCreateAlertDialog = false
                editingAlert = null
            },
            onSave = { title, cat, desc, urgency, person, phone, broadcast ->
                viewModel.saveEmergencyAlert(
                    id = target?.id,
                    title = title,
                    category = cat,
                    description = desc,
                    urgency = urgency,
                    contactPerson = person,
                    contactPhone = phone,
                    broadcastToMusallis = broadcast
                )
                showCreateAlertDialog = false
                editingAlert = null
            }
        )
    }

    // Delete Janaza Dialog
    janazaToDelete?.let { janaza ->
        AlertDialog(
            onDismissRequest = { janazaToDelete = null },
            title = { Text("জানাজা বিজ্ঞপ্তি মুছে ফেলবেন?", color = TextWhite, fontFamily = SolaimanLipiFontFamily) },
            text = {
                Text(
                    text = "আপনি কি নিশ্চিতভাবে \"${janaza.deceasedNameBn}\"-এর জানাজার বিজ্ঞপ্তি মুছে ফেলতে চান?",
                    color = TextMuted,
                    fontFamily = SolaimanLipiFontFamily
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteJanaza(janaza.id, janaza.deceasedNameBn)
                        janazaToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDigital)
                ) {
                    Text("মুছে ফেলুন", color = TextWhite, fontFamily = SolaimanLipiFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { janazaToDelete = null }) {
                    Text("বাতিল", color = TextMuted, fontFamily = SolaimanLipiFontFamily)
                }
            },
            containerColor = DarkSurface
        )
    }

    // Delete Alert Dialog
    alertToDelete?.let { alert ->
        AlertDialog(
            onDismissRequest = { alertToDelete = null },
            title = { Text("জরুরি অ্যালার্ট মুছে ফেলবেন?", color = TextWhite, fontFamily = SolaimanLipiFontFamily) },
            text = {
                Text(
                    text = "আপনি কি নিশ্চিতভাবে \"${alert.titleBn}\" মুছে ফেলতে চান?",
                    color = TextMuted,
                    fontFamily = SolaimanLipiFontFamily
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteEmergencyAlert(alert.id, alert.titleBn)
                        alertToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDigital)
                ) {
                    Text("মুছে ফেলুন", color = TextWhite, fontFamily = SolaimanLipiFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { alertToDelete = null }) {
                    Text("বাতিল", color = TextMuted, fontFamily = SolaimanLipiFontFamily)
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
private fun EmptyStateCard(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = TextMuted,
                fontFamily = SolaimanLipiFontFamily
            )
        }
    }
}

@Composable
private fun AdminJanazaCard(
    janaza: JanazaNotice,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCallFamily: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, DarkGreenBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = RedDigital.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, RedDigital)
                ) {
                    Text(
                        text = "জানাজার ঘোষণা",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RedDigital,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontFamily = SolaimanLipiFontFamily
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onCallFamily, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Call, contentDescription = "Call", tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = GoldAccent, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextWhite, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedDigital, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Deceased Name & Age
            Text(
                text = janaza.deceasedNameBn,
                style = AppTypography.cardTitle,
                fontSize = 16.sp,
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "বাসস্থান: ${janaza.residenceBn}",
                fontSize = 12.sp,
                color = TextMuted,
                fontFamily = SolaimanLipiFontFamily
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Info Grid Card
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DarkSurfaceElevated,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "• ইন্তেকালের সময়: ${janaza.demiseTimeBn}",
                        fontSize = 12.sp,
                        color = TextWhite,
                        fontFamily = SolaimanLipiFontFamily
                    )
                    Text(
                        text = "• জানাজার সময়: ${janaza.janazaTimeBn}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                        fontFamily = SolaimanLipiFontFamily
                    )
                    Text(
                        text = "• স্থান: ${janaza.janazaLocationBn}",
                        fontSize = 12.sp,
                        color = TextWhite,
                        fontFamily = SolaimanLipiFontFamily
                    )
                    Text(
                        text = "• ইমামতি করবেন: ${janaza.imamNameBn}",
                        fontSize = 12.sp,
                        color = TextMuted,
                        fontFamily = SolaimanLipiFontFamily
                    )
                    Text(
                        text = "• দাফন: ${janaza.graveyardBn}",
                        fontSize = 12.sp,
                        color = TextMuted,
                        fontFamily = SolaimanLipiFontFamily
                    )
                }
            }

            if (janaza.specialMessageBn.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "দোয়া: ${janaza.specialMessageBn}",
                    fontSize = 11.sp,
                    color = GoldAccent,
                    fontFamily = SolaimanLipiFontFamily
                )
            }
        }
    }
}

@Composable
private fun AdminEmergencyAlertCard(
    alert: EmergencyAlert,
    onEdit: () -> Unit,
    onToggleResolved: () -> Unit,
    onDelete: () -> Unit,
    onCallContact: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (alert.isResolved) DarkSurface.copy(alpha = 0.6f) else DarkSurface
        ),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            if (alert.isResolved) DarkGreenBorder else RedDigital.copy(alpha = 0.6f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (alert.isResolved) DarkGreenBorder else RedDigital.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, if (alert.isResolved) DarkGreenBorder else RedDigital)
                    ) {
                        Text(
                            text = alert.categoryBn,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (alert.isResolved) TextMuted else RedDigital,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }

                    if (alert.isResolved) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = PrimaryGreen.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, PrimaryGreen)
                        ) {
                            Text(
                                text = "সমাধান হয়েছে",
                                fontSize = 10.sp,
                                color = PrimaryGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontFamily = SolaimanLipiFontFamily
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onCallContact, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Call, contentDescription = "Call", tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onToggleResolved, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (alert.isResolved) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = "Resolve",
                            tint = if (alert.isResolved) PrimaryGreen else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextWhite, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedDigital, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = alert.titleBn,
                style = AppTypography.cardTitle,
                fontSize = 15.sp,
                color = if (alert.isResolved) TextMuted else TextWhite
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = alert.descriptionBn,
                fontSize = 12.sp,
                color = TextMuted,
                lineHeight = 18.sp,
                fontFamily = SolaimanLipiFontFamily
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "যোগাযোগ: ${alert.contactPerson} (${alert.contactPhone})",
                    fontSize = 11.sp,
                    color = PrimaryGreen,
                    fontFamily = SolaimanLipiFontFamily
                )
                Text(
                    text = alert.dateBn,
                    fontSize = 10.sp,
                    color = TextMuted,
                    fontFamily = SolaimanLipiFontFamily
                )
            }
        }
    }
}

@Composable
private fun JanazaFormDialog(
    initialJanaza: JanazaNotice?,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        age: String,
        residence: String,
        demise: String,
        janazaTime: String,
        location: String,
        imam: String,
        graveyard: String,
        phone: String,
        msg: String,
        broadcast: Boolean
    ) -> Unit
) {
    var name by remember { mutableStateOf(initialJanaza?.deceasedNameBn ?: "") }
    var age by remember { mutableStateOf(initialJanaza?.deceasedAge ?: "") }
    var residence by remember { mutableStateOf(initialJanaza?.residenceBn ?: "") }
    var demiseTime by remember { mutableStateOf(initialJanaza?.demiseTimeBn ?: "আজ ভোর ৫:৩০ মিনিট") }
    var janazaTime by remember { mutableStateOf(initialJanaza?.janazaTimeBn ?: "আজ বাদ আসর (বিকাল ৫:১৫)") }
    var location by remember { mutableStateOf(initialJanaza?.janazaLocationBn ?: "বায়তুল আমান জামে মসজিদ মাঠ") }
    var imamName by remember { mutableStateOf(initialJanaza?.imamNameBn ?: "মুফতি মাওলানা আব্দুল ওয়াদুদ") }
    var graveyard by remember { mutableStateOf(initialJanaza?.graveyardBn ?: "স্থানীয় কবরস্থান") }
    var phone by remember { mutableStateOf(initialJanaza?.contactFamilyPhone ?: "") }
    var specialMsg by remember { mutableStateOf(initialJanaza?.specialMessageBn ?: "সকল ধর্মপ্রাণ মুসল্লিদের জানাজায় শরিক হওয়ার বিনীত অনুরোধ রইল।") }
    var broadcast by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialJanaza == null) "নতুন জানাজার বিজ্ঞপ্তি প্রচার" else "জানাজা বিজ্ঞপ্তি সম্পাদনা",
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
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("মরহুম / মরহুমার নাম (যেমন: মরহুম আলহাজ্ব রফিকুল ইসলাম)", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text("বয়স", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("পরিবারের মোবাইল", fontSize = 11.sp) },
                        modifier = Modifier.weight(2f),
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = residence,
                    onValueChange = { residence = it },
                    label = { Text("বাসস্থান / এলাকা", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = demiseTime,
                    onValueChange = { demiseTime = it },
                    label = { Text("ইন্তেকালের সময় (যেমন: আজ ভোর ৫:৩০)", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = janazaTime,
                    onValueChange = { janazaTime = it },
                    label = { Text("জানাজার সময় (যেমন: আজ বাদ আসর)", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("জানাজার স্থান", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = imamName,
                    onValueChange = { imamName = it },
                    label = { Text("ইমামতি করবেন", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = graveyard,
                    onValueChange = { graveyard = it },
                    label = { Text("দাফন / কবরস্থান", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("সকল মুসল্লিকে পুশ নোটিফিকেশন পাঠান", fontSize = 11.sp, color = RedDigital, fontFamily = SolaimanLipiFontFamily)
                    Switch(
                        checked = broadcast,
                        onCheckedChange = { broadcast = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = RedDigital)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && janazaTime.isNotBlank()) {
                        onSave(name, age, residence, demiseTime, janazaTime, location, imamName, graveyard, phone, specialMsg, broadcast)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RedDigital),
                enabled = name.isNotBlank() && janazaTime.isNotBlank()
            ) {
                Text("সম্প্রচার করুন", color = TextWhite, fontFamily = SolaimanLipiFontFamily)
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
private fun EmergencyAlertFormDialog(
    initialAlert: EmergencyAlert?,
    onDismiss: () -> Unit,
    onSave: (title: String, category: String, description: String, urgency: String, contactPerson: String, phone: String, broadcast: Boolean) -> Unit
) {
    var title by remember { mutableStateOf(initialAlert?.titleBn ?: "") }
    var category by remember { mutableStateOf(initialAlert?.categoryBn ?: "রক্তদান আহ্বান") }
    var description by remember { mutableStateOf(initialAlert?.descriptionBn ?: "") }
    var urgency by remember { mutableStateOf(initialAlert?.urgencyLevel ?: "HIGH") }
    var contactPerson by remember { mutableStateOf(initialAlert?.contactPerson ?: "") }
    var contactPhone by remember { mutableStateOf(initialAlert?.contactPhone ?: "") }
    var broadcast by remember { mutableStateOf(true) }

    val categories = listOf("রক্তদান আহ্বান", "হারানো বিজ্ঞপ্তি", "জরুরি সহায়তা", "দুর্যোগ ত্রাণ", "চিকিৎসা সহায়তা")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialAlert == null) "নতুন জরুরি অ্যালার্ট জারি" else "অ্যালার্ট সম্পাদনা",
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
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("শিরোনাম (যেমন: জরুরি 'O-' রক্তের প্রয়োজন)", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Category selector chips
                Text("বিভাগ নির্বাচন করুন:", fontSize = 11.sp, color = TextMuted)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.take(3).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RedDigital,
                                selectedLabelColor = TextWhite
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("বিস্তারিত বিবরণ", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                OutlinedTextField(
                    value = contactPerson,
                    onValueChange = { contactPerson = it },
                    label = { Text("যোগাযোগের ব্যক্তি", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = contactPhone,
                    onValueChange = { contactPhone = it },
                    label = { Text("যোগাযোগের মোবাইল নম্বর", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("তাৎক্ষণিক পুশ নোটিফিকেশন পাঠান", fontSize = 11.sp, color = RedDigital, fontFamily = SolaimanLipiFontFamily)
                    Switch(
                        checked = broadcast,
                        onCheckedChange = { broadcast = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = RedDigital)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank()) {
                        onSave(title, category, description, urgency, contactPerson, contactPhone, broadcast)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RedDigital),
                enabled = title.isNotBlank() && description.isNotBlank()
            ) {
                Text("জারি করুন", color = TextWhite, fontFamily = SolaimanLipiFontFamily)
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
