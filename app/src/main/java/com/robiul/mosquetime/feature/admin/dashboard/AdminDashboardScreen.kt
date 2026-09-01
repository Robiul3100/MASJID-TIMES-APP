package com.robiul.mosquetime.feature.admin.dashboard

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.robiul.mosquetime.core.auth.AdminRole
import com.robiul.mosquetime.core.auth.PermissionManager
import com.robiul.mosquetime.data.firebase.MosqueAdminRepository
import com.robiul.mosquetime.feature.admin.auth.AdminAuthViewModel
import com.robiul.mosquetime.feature.admin.components.AdminBentoCard
import com.robiul.mosquetime.feature.admin.components.AdminLiveStatusBanner
import com.robiul.mosquetime.feature.admin.components.AdminMetricCard
import com.robiul.mosquetime.service.MasjidFirebaseMessagingService
import com.robiul.mosquetime.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToMosqueProfile: () -> Unit,
    onNavigateToPrayers: () -> Unit,
    onNavigateToMeals: () -> Unit,
    onNavigateToNotices: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    onNavigateToDuas: () -> Unit = {},
    onNavigateToCommittee: () -> Unit = {},
    onNavigateToFatwas: () -> Unit = {},
    onNavigateToDonations: () -> Unit = {},
    onNavigateToActivityLogs: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onExitAdmin: () -> Unit,
    modifier: Modifier = Modifier,
    authViewModel: AdminAuthViewModel = hiltViewModel(),
    adminRepo: MosqueAdminRepository = remember { MosqueAdminRepository.getInstance() }
) {
    val context = LocalContext.current
    val currentUser by authViewModel.currentUser.collectAsState()
    val adminRole = currentUser?.role ?: AdminRole.SUPER_ADMIN

    val activeDevicesCount by adminRepo.activeDevicesCount.collectAsState()
    val pendingQuestionsCount by adminRepo.pendingQuestionsCount.collectAsState()
    val monthlyDonationsTotal by adminRepo.monthlyDonationsTotal.collectAsState()
    val activeEmergencyCount by adminRepo.activeEmergencyCount.collectAsState()
    val isCloudSynced by adminRepo.isCloudSynced.collectAsState()
    val mosqueDetails by adminRepo.mosqueDetails.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "মসজিদ অ্যাডমিন ড্যাশবোর্ড",
                            style = AppTypography.screenTitle,
                            fontSize = 17.sp
                        )
                        Text(
                            text = mosqueDetails.nameBn,
                            fontSize = 11.sp,
                            color = PrimaryGreen,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onExitAdmin) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "পাবলিক অ্যাপে ফিরে যান",
                            tint = TextWhite
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Outlined.ManageAccounts,
                            contentDescription = "অ্যাডমিন ও রোল সেটিংস",
                            tint = GoldAccent
                        )
                    }
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                            contentDescription = "লগআউট",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 36.dp)
        ) {
            // 1. Hero Mosque Status & Admin Profile Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    DarkSurfaceElevated,
                                    DarkSurface
                                )
                            )
                        )
                        .border(
                            1.dp,
                            Brush.linearGradient(
                                listOf(PrimaryGreen.copy(alpha = 0.4f), DarkGreenBorder.copy(alpha = 0.2f))
                            ),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(DarkGreen)
                                        .border(1.dp, PrimaryGreen, RoundedCornerShape(14.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.AdminPanelSettings,
                                        contentDescription = null,
                                        tint = PrimaryGreen,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = currentUser?.nameBn ?: "প্রধান পরিচালক পরিষদ",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite,
                                        fontFamily = SolaimanLipiFontFamily
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(PrimaryGreen.copy(alpha = 0.15f))
                                                .border(0.8.dp, PrimaryGreen.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 7.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = adminRole.displayNameBn,
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GoldAccent,
                                                fontFamily = SolaimanLipiFontFamily
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = currentUser?.designation ?: "ব্যবস্থাপনা কমিটি",
                                            fontSize = 11.sp,
                                            color = TextMuted,
                                            fontFamily = SolaimanLipiFontFamily
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Live Sync Indicator
                        AdminLiveStatusBanner(
                            isOnline = isCloudSynced,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // 2. Real-time Analytics & Device Metrics Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "লাইভ মসজিদ মেট্রিক্স ও এনালিটিক্স",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                        fontFamily = SolaimanLipiFontFamily
                    )
                    Text(
                        text = "সরাসরি সিঙ্ক",
                        fontSize = 11.sp,
                        color = GoldAccent,
                        fontFamily = SolaimanLipiFontFamily
                    )
                }
            }

            // 3. Analytics Metric Cards (2x2 Grid)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AdminMetricCard(
                            title = "কানেক্টেড ডিভাইস",
                            value = "${activeDevicesCount} টি",
                            subtitle = "পুশ নোটিফিকেশন রেডি",
                            icon = Icons.Outlined.Devices,
                            accentColor = PrimaryGreen,
                            modifier = Modifier.weight(1f)
                        )

                        AdminMetricCard(
                            title = "আজকের জামাত উপস্থিতি",
                            value = "৩৫০+ জন",
                            subtitle = "গড় ৫ ওয়াক্ত মুসল্লি",
                            icon = Icons.Outlined.People,
                            accentColor = CyanBlue,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AdminMetricCard(
                            title = "চলতি মাসের অনুদান",
                            value = "৳ ${String.format("%,d", monthlyDonationsTotal)}",
                            subtitle = "মসজিদ উন্নয়ন তহবিল",
                            icon = Icons.Outlined.AccountBalanceWallet,
                            accentColor = GoldAccent,
                            modifier = Modifier.weight(1f)
                        )

                        AdminMetricCard(
                            title = "অমীমাংসিত প্রশ্ন / ফতোয়া",
                            value = "${pendingQuestionsCount} টি নতুন",
                            subtitle = "উত্তরের অপেক্ষায়",
                            icon = Icons.Outlined.QuestionAnswer,
                            accentColor = if (pendingQuestionsCount > 0) PurpleAccent else TextMuted,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 4. Quick Operational Action Strip
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, GoldAccent.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "⚡ ইনস্ট্যান্ট কুইক অ্যাকশন",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent,
                            fontFamily = SolaimanLipiFontFamily
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Instant Test Push Notification Button
                            Button(
                                onClick = {
                                    MasjidFirebaseMessagingService.sendLocalTestPushNotification(context)
                                    Toast.makeText(context, "টেস্ট পুশ নোটিফিকেশন পাঠানো হয়েছে!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                            ) {
                                Icon(Icons.Outlined.NotificationsActive, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("টেস্ট পুশ", color = Color.Black, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                            }

                            // Quick Azan Update
                            OutlinedButton(
                                onClick = onNavigateToPrayers,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkGreenBorder)
                            ) {
                                Icon(Icons.Outlined.AccessTime, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("আজান পরিবর্তন", fontSize = 11.sp, fontFamily = SolaimanLipiFontFamily)
                            }

                            // Quick Emergency Broadcast
                            Button(
                                onClick = onNavigateToEmergency,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RedDigital)
                            ) {
                                Icon(Icons.AutoMirrored.Outlined.VolumeUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("জরুরি এলার্ট", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                            }
                        }
                    }
                }
            }

            // 5. Bento-Grid Modules Header
            item {
                Text(
                    text = "মসজিদ প্রশাসন ও ব্যবস্থাপনা গ্রিড (১২টি মডিউল)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen,
                    fontFamily = SolaimanLipiFontFamily,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // 6. Bento Grid (2 Columns, 6 Rows = 12 Modules)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Row 1: Prayers & Profile
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AdminBentoCard(
                            title = "নামাজের সময়সূচি",
                            subtitle = "আজান ও জামাত সময় পরিবর্তন",
                            icon = Icons.Outlined.AccessTime,
                            accentColor = PrimaryGreen,
                            badgeText = "লাইভ সিঙ্ক",
                            onClick = onNavigateToPrayers,
                            modifier = Modifier.weight(1f)
                        )

                        AdminBentoCard(
                            title = "মসজিদ প্রোফাইল",
                            subtitle = "নাম, ইমাম, মুয়াজ্জিন ও সুবিধা",
                            icon = Icons.Outlined.Mosque,
                            accentColor = CyanBlue,
                            badgeText = "পরিচিতি",
                            onClick = onNavigateToMosqueProfile,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 2: Notices & Push Notifications
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AdminBentoCard(
                            title = "নোটিশ ও বয়ান",
                            subtitle = "নতুন বিজ্ঞপ্তি ও পিন পোস্ট",
                            icon = Icons.Outlined.Campaign,
                            accentColor = GoldAccent,
                            badgeText = "বোর্ড",
                            onClick = onNavigateToNotices,
                            modifier = Modifier.weight(1f)
                        )

                        AdminBentoCard(
                            title = "পুশ নোটিফিকেশন",
                            subtitle = "সকল মুসল্লির ফোনে বার্তা পাঠান",
                            icon = Icons.Outlined.Notifications,
                            accentColor = NeonGreenGlow,
                            badgeText = "FCM ব্রডকাস্ট",
                            onClick = onNavigateToNotifications,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 3: Meals & Emergency
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AdminBentoCard(
                            title = "হুজুরের খানা",
                            subtitle = "মেহমানদারি ও বাড়ির তালিকা",
                            icon = Icons.Outlined.Restaurant,
                            accentColor = GoldAccent,
                            badgeText = "দৈনিক খানা",
                            onClick = onNavigateToMeals,
                            modifier = Modifier.weight(1f)
                        )

                        AdminBentoCard(
                            title = "জরুরি ও জানাজা",
                            subtitle = "রক্তদান ও মৃত্যু সংবাদ এলার্ট",
                            icon = Icons.AutoMirrored.Outlined.VolumeUp,
                            accentColor = RedDigital,
                            badgeText = if (activeEmergencyCount > 0) "জরুরি এলার্ট" else "শান্ত",
                            badgeColor = RedDigital,
                            onClick = onNavigateToEmergency,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 4: Donations & Committee
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AdminBentoCard(
                            title = "দান ও তহবিল",
                            subtitle = "ব্যাংক, বিকাশ ও অনুদান হিসাব",
                            icon = Icons.Outlined.AccountBalanceWallet,
                            accentColor = PrimaryGreen,
                            badgeText = "আয়-ব্যয়",
                            onClick = onNavigateToDonations,
                            modifier = Modifier.weight(1f)
                        )

                        AdminBentoCard(
                            title = "কমিটি সদস্য",
                            subtitle = "পরিচালক পরিষদ ও দায়িত্ব",
                            icon = Icons.Outlined.Group,
                            accentColor = CyanBlue,
                            badgeText = "সদস্যগণ",
                            onClick = onNavigateToCommittee,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 5: Fatwa/Q&A & Events
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AdminBentoCard(
                            title = "ইমামকে প্রশ্ন ও ফতোয়া",
                            subtitle = "মুসল্লিদের প্রশ্নের জবাব প্রদান",
                            icon = Icons.Outlined.QuestionAnswer,
                            accentColor = PurpleAccent,
                            badgeText = if (pendingQuestionsCount > 0) "$pendingQuestionsCount টি বাকি" else "সম্পন্ন",
                            badgeColor = if (pendingQuestionsCount > 0) PurpleAccent else PrimaryGreen,
                            onClick = onNavigateToFatwas,
                            modifier = Modifier.weight(1f)
                        )

                        AdminBentoCard(
                            title = "ইসলামিক ইভেন্ট",
                            subtitle = "ওয়াজ মাহফিল ও সমাবেশ",
                            icon = Icons.Outlined.Event,
                            accentColor = GoldAccent,
                            badgeText = "অনুষ্ঠান",
                            onClick = onNavigateToEvents,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 6: Duas & Activity Logs
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AdminBentoCard(
                            title = "দোয়া ও জিকির ব্যাংক",
                            subtitle = "দৈনন্দিন দোয়ার সংগ্রহশালা",
                            icon = Icons.Outlined.MenuBook,
                            accentColor = CyanBlue,
                            badgeText = "দোয়া",
                            onClick = onNavigateToDuas,
                            modifier = Modifier.weight(1f)
                        )

                        AdminBentoCard(
                            title = "অ্যাডমিন অডিট লগ",
                            subtitle = "নিরাপত্তা ও পরিবর্তনের রেকর্ড",
                            icon = Icons.Outlined.History,
                            accentColor = TextMuted,
                            badgeText = "অডিট লগ",
                            onClick = onNavigateToActivityLogs,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 7: Admin & Role Settings + Mosque Database Isolation
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AdminBentoCard(
                            title = "অ্যাডমিন ও রোল",
                            subtitle = "মুল অ্যাডমিন ও মডারেটর নিয়ন্ত্রণ",
                            icon = Icons.Outlined.ManageAccounts,
                            accentColor = GoldAccent,
                            badgeText = "রোল ও পারমিশন",
                            badgeColor = GoldAccent,
                            onClick = onNavigateToSettings,
                            modifier = Modifier.weight(1f)
                        )

                        AdminBentoCard(
                            title = "মসজিদ ডাটাবেস",
                            subtitle = "মাল্টি-মসজিদ ক্লাউড কনফিগ",
                            icon = Icons.Outlined.Storage,
                            accentColor = PrimaryGreen,
                            badgeText = "আইসোলেশন",
                            onClick = onNavigateToSettings,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("অ্যাডমিন লগআউট", color = TextWhite, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily) },
            text = { Text("আপনি কি অ্যাডমিন সেশন থেকে লগআউট করতে চান?", color = TextMuted, fontFamily = SolaimanLipiFontFamily) },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.signOut()
                        showLogoutDialog = false
                        onExitAdmin()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDigital)
                ) {
                    Text("লগআউট করুন", color = Color.White, fontFamily = SolaimanLipiFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("বাতিল", color = TextMuted, fontFamily = SolaimanLipiFontFamily)
                }
            },
            containerColor = DarkSurfaceElevated,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

