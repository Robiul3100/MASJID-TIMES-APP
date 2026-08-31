package com.robiul.mosquetime.feature.admin.notifications

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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.robiul.mosquetime.core.auth.AdminUser
import com.robiul.mosquetime.data.model.AppNotification
import com.robiul.mosquetime.data.model.NotificationCategory
import com.robiul.mosquetime.ui.theme.*

import com.robiul.mosquetime.feature.admin.components.AdminEditDialog
import com.robiul.mosquetime.service.MasjidFirebaseMessagingService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNotificationScreen(
    currentAdmin: AdminUser?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminNotificationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionMessage by viewModel.actionMessage.collectAsStateWithLifecycle()

    var showBroadcastDialog by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

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
                            text = "পুশ নোটিফিকেশন সেন্টার",
                            style = AppTypography.screenTitle,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "FCM রিয়েলটাইম ব্রডকাস্ট",
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
                    IconButton(onClick = { showClearConfirmDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.ClearAll,
                            contentDescription = "সকল মুছুন",
                            tint = RedDigital
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
            is AdminNotificationUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
            }
            is AdminNotificationUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.message, color = RedDigital)
                }
            }
            is AdminNotificationUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
                ) {
                    // 1. Instant Test Push Notification Banner
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    MasjidFirebaseMessagingService.sendLocalTestPushNotification(context)
                                    Toast.makeText(context, "টেস্ট পুশ নোটিফিকেশন পাঠানো হয়েছে!", Toast.LENGTH_SHORT).show()
                                },
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = GoldAccent.copy(alpha = 0.15f),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Outlined.Notifications,
                                            contentDescription = null,
                                            tint = GoldAccent,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "⚡ ডিভাইসে টেস্ট নোটিফিকেশন পাঠান",
                                        style = AppTypography.cardTitle,
                                        fontSize = 14.5.sp,
                                        color = GoldAccent
                                    )
                                    Text(
                                        text = "১-ক্লিকেই আপনার ফোনে নোটিফিকেশন যাচাই করুন",
                                        fontSize = 11.sp,
                                        color = TextMuted,
                                        fontFamily = SolaimanLipiFontFamily
                                    )
                                }
                            }
                        }
                    }

                    // 2. Broadcast Composer Trigger Banner
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showBroadcastDialog = true },
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, PrimaryGreen)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = PrimaryGreen.copy(alpha = 0.2f),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Send,
                                            contentDescription = null,
                                            tint = PrimaryGreen,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "নতুন নোটিফিকেশন ব্রডকাস্ট করুন",
                                        style = AppTypography.cardTitle,
                                        fontSize = 15.sp,
                                        color = PrimaryGreen
                                    )
                                    Text(
                                        text = "নামাজ, জুমা, বিশেষ ওয়াজ মাহফিল বা চাঁদের ঘোষণা পাঠান",
                                        fontSize = 11.sp,
                                        color = TextMuted,
                                        fontFamily = SolaimanLipiFontFamily
                                    )
                                }
                            }
                        }
                    }

                    // Sent History Header
                    item {
                        Text(
                            text = "সম্প্রচারিত নোটিফিকেশন হিস্ট্রি (${state.notifications.size})",
                            style = AppTypography.cardTitle,
                            fontSize = 14.sp,
                            color = TextWhite,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }


                    if (state.notifications.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "এখনও কোনো নোটিফিকেশন পাঠানো হয়নি",
                                    color = TextMuted,
                                    fontSize = 13.sp,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            }
                        }
                    } else {
                        items(state.notifications) { notif ->
                            NotificationHistoryCard(notif = notif)
                        }
                    }
                }
            }
        }
    }

    // Broadcast Composer Dialog
    if (showBroadcastDialog) {
        BroadcastNotificationDialog(
            onDismiss = { showBroadcastDialog = false },
            onSend = { title, msg, category, route ->
                viewModel.broadcastCustomNotification(title, msg, category, route)
                showBroadcastDialog = false
            }
        )
    }

    // Clear History Dialog
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("হিস্ট্রি মুছে ফেলুন", color = TextWhite, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily) },
            text = { Text("আপনি কি সকল নোটিফিকেশন হিস্ট্রি মুছে ফেলতে চান?", color = TextMuted, fontFamily = SolaimanLipiFontFamily) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllNotifications()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDigital)
                ) {
                    Text("মুছে ফেলুন", color = Color.White, fontFamily = SolaimanLipiFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("বাতিল", color = TextMuted, fontFamily = SolaimanLipiFontFamily)
                }
            },
            containerColor = DarkSurfaceElevated,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun NotificationHistoryCard(
    notif: AppNotification
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, DarkGreenBorder.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (notif.category) {
                        NotificationCategory.PRAYER -> PrimaryGreen.copy(alpha = 0.2f)
                        NotificationCategory.JUMAH -> GoldAccent.copy(alpha = 0.2f)
                        NotificationCategory.SPECIAL -> RedDigital.copy(alpha = 0.2f)
                        NotificationCategory.EVENT -> PrimaryGreen.copy(alpha = 0.2f)
                        else -> DarkSurfaceElevated
                    },
                    border = BorderStroke(
                        1.dp,
                        when (notif.category) {
                            NotificationCategory.PRAYER -> PrimaryGreen
                            NotificationCategory.JUMAH -> GoldAccent
                            NotificationCategory.SPECIAL -> RedDigital
                            NotificationCategory.EVENT -> PrimaryGreen
                            else -> DarkGreenBorder
                        }
                    )
                ) {
                    Text(
                        text = when (notif.category) {
                            NotificationCategory.PRAYER -> "নামাজ"
                            NotificationCategory.JUMAH -> "জুমা"
                            NotificationCategory.NOTICE -> "নোটিশ"
                            NotificationCategory.EVENT -> "অনুষ্ঠান"
                            NotificationCategory.SPECIAL -> "বিশেষ"
                            NotificationCategory.ALL -> "সকল"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (notif.category) {
                            NotificationCategory.PRAYER -> PrimaryGreen
                            NotificationCategory.JUMAH -> GoldAccent
                            NotificationCategory.SPECIAL -> RedDigital
                            NotificationCategory.EVENT -> PrimaryGreen
                            else -> TextWhite
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontFamily = SolaimanLipiFontFamily
                    )
                }

                Text(
                    text = notif.timestamp,
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontFamily = SolaimanLipiFontFamily
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = notif.title,
                style = AppTypography.cardTitle,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = notif.message,
                fontSize = 12.sp,
                color = TextMuted,
                lineHeight = 18.sp,
                fontFamily = SolaimanLipiFontFamily
            )

            if (!notif.targetRoute.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "ট্যাপ করলে ওপেন হবে: ${notif.targetRoute}",
                    fontSize = 10.sp,
                    color = PrimaryGreen,
                    fontFamily = SolaimanLipiFontFamily
                )
            }
        }
    }
}

@Composable
private fun BroadcastNotificationDialog(
    onDismiss: () -> Unit,
    onSend: (title: String, msg: String, category: NotificationCategory, targetRoute: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(NotificationCategory.SPECIAL) }
    var targetRoute by remember { mutableStateOf("home") }

    val presetMessages = listOf(
        "আজকের তারাবীহ নামাজের খতমে কুরআন অনুষ্ঠিত হবে।" to NotificationCategory.SPECIAL,
        "আগামীকাল জুমার নামাজের প্রথম আজান ১২:৪৫ মিনিটে অনুষ্ঠিত হবে।" to NotificationCategory.JUMAH,
        "পবিত্র ঈদুল ফিতরের প্রথম জামাত সকাল ৭:৩০ মিনিটে।" to NotificationCategory.EVENT,
        "আসরের জামাত সময় পরিবর্তন: বিকাল ৪:৪৫ মিনিট।" to NotificationCategory.PRAYER
    )

    AdminEditDialog(
        title = "নতুন নোটিফিকেশন ব্রডকাস্ট",
        subtitle = "সকল মুসল্লির ডিভাইসে সরাসরি পুশ পাঠানো হবে",
        icon = Icons.Default.Campaign,
        iconTint = PrimaryGreen,
        onDismissRequest = onDismiss,
        onConfirm = {
            if (title.isNotBlank() && message.isNotBlank()) {
                onSend(title, message, category, targetRoute)
            }
        },
        confirmButtonText = "ব্রডকাস্ট পাঠান",
        confirmButtonEnabled = title.isNotBlank() && message.isNotBlank()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("নোটিফিকেশনের শিরোনাম", fontSize = 12.sp, fontFamily = SolaimanLipiFontFamily) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = DarkGreenBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                shape = RoundedCornerShape(10.dp)
            )

            // Category Chips
            Text("বিভাগ নির্বাচন করুন:", fontSize = 11.5.sp, color = TextMuted, fontFamily = SolaimanLipiFontFamily)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(NotificationCategory.values().filter { it != NotificationCategory.ALL }) { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { category = cat },
                        label = {
                            Text(
                                text = when (cat) {
                                    NotificationCategory.PRAYER -> "নামাজ"
                                    NotificationCategory.JUMAH -> "জুমা"
                                    NotificationCategory.NOTICE -> "নোটিশ"
                                    NotificationCategory.EVENT -> "অনুষ্ঠান"
                                    NotificationCategory.SPECIAL -> "বিশেষ"
                                    NotificationCategory.ALL -> "সকল"
                                },
                                fontSize = 10.5.sp,
                                fontFamily = SolaimanLipiFontFamily
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreen,
                            selectedLabelColor = DarkBackground,
                            containerColor = DarkSurfaceElevated,
                            labelColor = TextWhite
                        )
                    )
                }
            }

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("বার্তার বিবরণ", fontSize = 12.sp, fontFamily = SolaimanLipiFontFamily) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = DarkGreenBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                shape = RoundedCornerShape(10.dp)
            )

            // Quick Presets
            Text("দ্রুত টেমপ্লেট নির্বাচন করুন:", fontSize = 11.5.sp, color = TextMuted, fontFamily = SolaimanLipiFontFamily)
            presetMessages.forEach { (preset, cat) ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarkSurfaceElevated,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            title = "মসজিদের জরুরি বার্তা"
                            message = preset
                            category = cat
                        }
                ) {
                    Text(
                        text = "• $preset",
                        fontSize = 11.sp,
                        color = TextWhite,
                        modifier = Modifier.padding(8.dp),
                        fontFamily = SolaimanLipiFontFamily
                    )
                }
            }
        }
    }
}
