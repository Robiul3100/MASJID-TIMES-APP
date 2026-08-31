package com.example.feature.admin.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.auth.AdminRole
import com.example.core.auth.AdminUser
import com.example.core.auth.PermissionManager
import com.example.feature.admin.auth.AdminAuthViewModel
import com.example.ui.theme.*

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
    onExitAdmin: () -> Unit,
    modifier: Modifier = Modifier,
    authViewModel: AdminAuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val adminRole = currentUser?.role ?: AdminRole.SUPER_ADMIN
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
                            text = currentUser?.nameBn ?: "প্রধান পরিচালক পরিষদ",
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
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
        ) {
            // Admin Profile & Status Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkGreenBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(DarkGreen)
                                .border(1.dp, PrimaryGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AdminPanelSettings,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentUser?.nameBn ?: "চৌধুরী পাটোয়ারী বাড়ি জামে মসজিদ",
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
                                        .background(DarkGreen)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = adminRole.displayNameBn,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent,
                                        fontFamily = SolaimanLipiFontFamily
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "সক্রিয় সেশন",
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            }
                        }
                    }
                }
            }

            // Quick Operational Actions Header
            item {
                Text(
                    text = "প্রধান ব্যবস্থাপনা মডিউলসমূহ",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen,
                    fontFamily = SolaimanLipiFontFamily,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            // 1. Prayer Times Management
            item {
                AdminModuleCard(
                    title = "নামাজের সময়সূচি পরিবর্তন ও আপডেট",
                    subtitle = "৫ ওয়াক্ত আজান ও জামাত সময়, জুমুআ ও বিশেষ পরিবর্তন",
                    icon = Icons.Outlined.AccessTime,
                    badgeText = "লাইভ সিন্ক",
                    badgeColor = PrimaryGreen,
                    isEnabled = PermissionManager.canManagePrayerTimes(adminRole),
                    onClick = onNavigateToPrayers
                )
            }

            // 2. Hujurer Khana (Meal Management)
            item {
                AdminModuleCard(
                    title = "হুজুরের খানা ও মেহমানদারি ব্যবস্থাপনা",
                    subtitle = "তারিখ অনুযায়ী বাড়ি নির্ধারণ, স্ট্যাটাস ও নোট আপডেট",
                    icon = Icons.Outlined.Restaurant,
                    badgeText = "দৈনিক দায়িত্ব",
                    badgeColor = GoldAccent,
                    isEnabled = PermissionManager.canManageMealSchedules(adminRole),
                    onClick = onNavigateToMeals
                )
            }

            // 3. Mosque Profile Management
            item {
                AdminModuleCard(
                    title = "মসজিদ পরিচিতি ও কমিটি তথ্য",
                    subtitle = "মসজিদের নাম, ঠিকানা, খতিব, ইমাম, মুয়াজ্জিন ও ইতিহাস",
                    icon = Icons.Outlined.Mosque,
                    badgeText = "প্রোফাইল",
                    badgeColor = CyanBlue,
                    isEnabled = PermissionManager.canManageMosqueProfile(adminRole),
                    onClick = onNavigateToMosqueProfile
                )
            }

            // 4. Notice Board Management
            item {
                AdminModuleCard(
                    title = "নোটিশ বোর্ড ও সাধারণ ঘোষণা",
                    subtitle = "নতুন নোটিশ তৈরি, পিন করা ও প্রকাশনা নিয়ন্ত্রণ",
                    icon = Icons.Outlined.Campaign,
                    badgeText = "ঘোষণা",
                    badgeColor = PrimaryGreen,
                    isEnabled = PermissionManager.canManageNotices(adminRole),
                    onClick = onNavigateToNotices
                )
            }

            // 5. Emergency Announcement
            item {
                AdminModuleCard(
                    title = "জরুরি বার্তা ও জানাযা নোটিফিকেশন",
                    subtitle = "হোম স্ক্রিনে লাল সতর্কতা ব্যানার ও পুশ নোটিফিকেশন",
                    icon = Icons.AutoMirrored.Outlined.VolumeUp,
                    badgeText = "জরুরি",
                    badgeColor = RedDigital,
                    isEnabled = PermissionManager.canSendEmergencyAnnouncement(adminRole),
                    onClick = onNavigateToEmergency
                )
            }

            // 6. Push Notifications
            item {
                AdminModuleCard(
                    title = "সরাসরি পুশ নোটিফিকেশন প্রেরক",
                    subtitle = "সকল মুসল্লির ফোনে তাৎক্ষণিক বার্তা পাঠানো",
                    icon = Icons.Outlined.NotificationsActive,
                    badgeText = "FCM বার্তা",
                    badgeColor = PurpleAccent,
                    isEnabled = PermissionManager.canSendNotifications(adminRole),
                    onClick = onNavigateToNotifications
                )
            }

            // 7. Events & Programs Management
            item {
                AdminModuleCard(
                    title = "মাহফিল ও ইসলামিক ইভেন্ট",
                    subtitle = "ওয়াজ মাহফিল, কুরআন ক্লাস ও বিশেষ কর্মসূচি",
                    icon = Icons.Outlined.Event,
                    badgeText = "ইভেন্ট",
                    badgeColor = CyanBlue,
                    isEnabled = PermissionManager.canManageEvents(adminRole),
                    onClick = onNavigateToEvents
                )
            }

            // 8. Committee Management
            item {
                AdminModuleCard(
                    title = "মসজিদ কমিটি ও পরিষদ ব্যবস্থাপনা",
                    subtitle = "কর্মকর্তা, সদস্য তালিকা, পদবী ও ফোন নম্বর ব্যবস্থাপনা",
                    icon = Icons.Outlined.Group,
                    badgeText = "কমিটি",
                    badgeColor = GoldAccent,
                    isEnabled = PermissionManager.canManageCommittee(adminRole),
                    onClick = onNavigateToCommittee
                )
            }

            // 9. Duas & Content Management
            item {
                AdminModuleCard(
                    title = "দোয়া ও ইসলামিক কন্টেন্ট ব্যবস্থাপনা",
                    subtitle = "দৈনন্দিন দোয়া, আরবি পাঠ, বাংলা অর্থ ও ফজিলত আপডেট",
                    icon = Icons.Outlined.MenuBook,
                    badgeText = "দোয়া",
                    badgeColor = PrimaryGreen,
                    isEnabled = PermissionManager.canManageDuas(adminRole),
                    onClick = onNavigateToDuas
                )
            }

            // 10. Ask Imam & Fatwa Management
            item {
                AdminModuleCard(
                    title = "ইমামের ফতোয়া ও প্রশ্ন ব্যাংক",
                    subtitle = "মুসুল্লিদের প্রশ্নের উত্তর প্রদান ও ফতোয়া লাইব্রেরি ব্যবস্থাপনা",
                    icon = Icons.Outlined.QuestionAnswer,
                    badgeText = "প্রশ্নোত্তর",
                    badgeColor = GoldAccent,
                    isEnabled = PermissionManager.canManageFatwas(adminRole),
                    onClick = onNavigateToFatwas
                )
            }

            // 11. Donations & Fund Accounts Management
            item {
                AdminModuleCard(
                    title = "অনুদান ও ব্যাংক হিসাব ব্যবস্থাপনা",
                    subtitle = "অনুদানের রেকর্ড যাচাই, বিকাশ/নগদ ও ব্যাংক একাউন্ট কনফিগারেশন",
                    icon = Icons.Outlined.AccountBalance,
                    badgeText = "তহবিল",
                    badgeColor = CyanBlue,
                    isEnabled = PermissionManager.canManageDonations(adminRole),
                    onClick = onNavigateToDonations
                )
            }

            // 12. Admin Activity & Audit Logs
            item {
                AdminModuleCard(
                    title = "অ্যাডমিন কার্যক্রম অডিট লগ",
                    subtitle = "প্রশাসকদের সকল কার্যক্রম, সময়সূচি পরিবর্তন ও ইতিহাস ট্র্যাকার",
                    icon = Icons.Outlined.History,
                    badgeText = "অডিট লগ",
                    badgeColor = PurpleAccent,
                    isEnabled = PermissionManager.canViewActivityLogs(adminRole),
                    onClick = onNavigateToActivityLogs
                )
            }

            // Return to Public App Quick Action
            item {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onExitAdmin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Home,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "সাধারণ পাবলিক অ্যাপে ফিরে যান",
                        color = PrimaryGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = SolaimanLipiFontFamily
                    )
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "অ্যাডমিন লগআউট",
                    fontWeight = FontWeight.Bold,
                    fontFamily = SolaimanLipiFontFamily,
                    color = TextWhite
                )
            },
            text = {
                Text(
                    text = "আপনি কি নিশ্চিতভাবে অ্যাডমিন প্যানেল থেকে লগআউট করতে চান?",
                    fontFamily = SolaimanLipiFontFamily,
                    color = TextMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.signOut()
                        onExitAdmin()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDigital)
                ) {
                    Text("লগআউট", color = Color.White, fontFamily = SolaimanLipiFontFamily)
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

@Composable
private fun AdminModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeText: String,
    badgeColor: Color,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isEnabled, onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) DarkSurface else DarkSurface.copy(alpha = 0.5f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isEnabled) DarkGreenBorder else DarkSurfaceBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isEnabled) badgeColor else TextMuted,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isEnabled) TextWhite else TextMuted,
                        fontFamily = SolaimanLipiFontFamily
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(badgeColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 16.sp,
                    fontFamily = SolaimanLipiFontFamily
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = if (isEnabled) badgeColor else TextMuted.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
