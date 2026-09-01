package com.robiul.mosquetime.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robiul.mosquetime.ui.navigation.Screen
import com.robiul.mosquetime.ui.theme.*
import com.robiul.mosquetime.util.HapticUtils

/**
 * Premium Modern Islamic Navigation Drawer Menu.
 * Features:
 * - Rich Glassmorphic Mosque Header with status badge & crescent motif.
 * - Categorized collapsible/expandable sections for clean navigation:
 *   1. প্রধান বিভাগ (Core Navigation)
 *   2. কুরআন ও দৈনিক আমল (Quran & Daily Worship)
 *   3. মসজিদ ও সমাজ কল্যাণ (Mosque & Welfare Services)
 *   4. সিস্টেম ও অ্যাডমিন (Settings & Admin Access)
 * - Modern Glassmorphic Developer & App Version footer with theme toggle & logout.
 */
@Composable
fun MosqueDrawerContent(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val scrollState = rememberScrollState()

    var isDarkMode by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Section collapse states (default open for primary, expandable for others)
    var isWorshipSectionExpanded by remember { mutableStateOf(true) }
    var isMosqueSectionExpanded by remember { mutableStateOf(true) }
    var isSystemSectionExpanded by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF08120B),
                        Color(0xFF0B170F),
                        Color(0xFF060B08)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(vertical = 10.dp, horizontal = 12.dp)
    ) {
        // -------------------------------------------------------------
        // 1. Premium Glassmorphic Mosque Header Card
        // -------------------------------------------------------------
        DrawerHeaderCard(
            onHeaderClick = {
                HapticUtils.performLongPressHaptic(view)
                onNavigate(Screen.AboutMosque.route)
                onCloseDrawer()
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // -------------------------------------------------------------
        // 2. Scrollable Structured Navigation Menu
        // -------------------------------------------------------------
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // === SECTION 1: প্রধান সেবাসমূহ (Core Essentials) ===
            DrawerSectionHeader(title = "প্রধান সেবাসমূহ")

            DrawerMenuRow(
                icon = Icons.Outlined.Home,
                title = "হোম ড্যাশবোর্ড",
                subtitle = "মূল পাতা ও লাইভ ওয়াক্ত",
                isSelected = currentRoute == Screen.Home.route,
                iconTint = PrimaryGreen,
                onClick = {
                    HapticUtils.performLongPressHaptic(view)
                    onNavigate(Screen.Home.route)
                    onCloseDrawer()
                }
            )

            DrawerMenuRow(
                icon = Icons.Outlined.CalendarMonth,
                title = "নামাজের সময়সূচি ও এলার্ট",
                subtitle = "দৈনিক ও মাসিক ওয়াক্ত",
                isSelected = currentRoute == Screen.DailyPrayer.route || currentRoute == Screen.MonthlySchedule.route,
                iconTint = PrimaryGreen,
                badge = "ওয়াক্ত",
                onClick = {
                    HapticUtils.performLongPressHaptic(view)
                    onNavigate(Screen.DailyPrayer.route)
                    onCloseDrawer()
                }
            )

            DrawerMenuRow(
                icon = Icons.Outlined.Explore,
                title = "কিবলা কম্পাস",
                subtitle = "লাইভ সেন্সর দিকনির্ণয়",
                isSelected = currentRoute == Screen.QiblaCompass.route || currentRoute == Screen.Qibla.route,
                iconTint = CyanBlue,
                badge = "LIVE",
                badgeColor = CyanBlue,
                onClick = {
                    HapticUtils.performLongPressHaptic(view)
                    onNavigate(Screen.QiblaCompass.route)
                    onCloseDrawer()
                }
            )

            DrawerMenuRow(
                icon = Icons.Outlined.Restaurant,
                title = "হুজুরের খানা সূচি",
                subtitle = "১ পরিবার ১ দিন (৩ বেলা)",
                isSelected = currentRoute == Screen.HujurKhana.route,
                iconTint = GoldAccent,
                badge = "১৫ পরিবার",
                badgeColor = GoldAccent,
                onClick = {
                    HapticUtils.performLongPressHaptic(view)
                    onNavigate(Screen.HujurKhana.route)
                    onCloseDrawer()
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 6.dp),
                thickness = 0.6.dp,
                color = DarkGreenBorder.copy(alpha = 0.5f)
            )

            // === SECTION 2: কুরআন ও দৈনিক আমল (Quran & Daily Worship) ===
            DrawerCollapsibleSectionHeader(
                title = "কুরআন ও দৈনিক আমল",
                isExpanded = isWorshipSectionExpanded,
                onToggle = { isWorshipSectionExpanded = !isWorshipSectionExpanded }
            )

            AnimatedVisibility(
                visible = isWorshipSectionExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    DrawerMenuRow(
                        icon = Icons.Outlined.MenuBook,
                        title = "আল-কুরআনুল কারীম",
                        subtitle = "১১৪ সূরা আরবি, বাংলা ও অডিও",
                        isSelected = currentRoute == Screen.Quran.route || currentRoute?.startsWith("quran_surah_detail") == true,
                        iconTint = PrimaryGreen,
                        badge = "তিলাওয়াত",
                        onClick = {
                            HapticUtils.performLongPressHaptic(view)
                            onNavigate(Screen.Quran.route)
                            onCloseDrawer()
                        }
                    )

                    DrawerMenuRow(
                        icon = Icons.Outlined.VolunteerActivism,
                        title = "দোয়া ও মাসনূন আমল",
                        subtitle = "দৈনন্দিন প্রয়োজনীয় দোয়া",
                        isSelected = currentRoute == Screen.DuaDhikr.route,
                        iconTint = GoldAccent,
                        onClick = {
                            HapticUtils.performLongPressHaptic(view)
                            onNavigate(Screen.DuaDhikr.route)
                            onCloseDrawer()
                        }
                    )

                    DrawerMenuRow(
                        icon = Icons.Outlined.TouchApp,
                        title = "স্মার্ট ডিজিটাল তসবিহ",
                        subtitle = "জিকির কাউন্টার ও সাউন্ড",
                        isSelected = currentRoute == Screen.DigitalTasbih.route,
                        iconTint = NeonGreenGlow,
                        onClick = {
                            HapticUtils.performLongPressHaptic(view)
                            onNavigate(Screen.DigitalTasbih.route)
                            onCloseDrawer()
                        }
                    )

                    DrawerMenuRow(
                        icon = Icons.Outlined.EventAvailable,
                        title = "হিজরি ও ইসলামিক ক্যালেন্ডার",
                        subtitle = "বিশেষ দিবস ও নিষিদ্ধ সময়",
                        isSelected = currentRoute == Screen.IslamicCalendar.route,
                        iconTint = CyanBlue,
                        onClick = {
                            HapticUtils.performLongPressHaptic(view)
                            onNavigate(Screen.IslamicCalendar.route)
                            onCloseDrawer()
                        }
                    )

                    DrawerMenuRow(
                        icon = Icons.Outlined.NightsStay,
                        title = "মাহে রমজান ড্যাশবোর্ড",
                        subtitle = "সেহরি ও ইফতারের কাউন্টডাউন",
                        isSelected = currentRoute == Screen.RamadanDashboard.route,
                        iconTint = GoldAccent,
                        badge = "রমজান",
                        badgeColor = GoldAccent,
                        onClick = {
                            HapticUtils.performLongPressHaptic(view)
                            onNavigate(Screen.RamadanDashboard.route)
                            onCloseDrawer()
                        }
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 6.dp),
                thickness = 0.6.dp,
                color = DarkGreenBorder.copy(alpha = 0.5f)
            )

            // === SECTION 3: মসজিদ ও সমাজ সেবা (Mosque & Welfare) ===
            DrawerCollapsibleSectionHeader(
                title = "মসজিদ ও সমাজ কল্যাণ",
                isExpanded = isMosqueSectionExpanded,
                onToggle = { isMosqueSectionExpanded = !isMosqueSectionExpanded }
            )

            AnimatedVisibility(
                visible = isMosqueSectionExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    DrawerMenuRow(
                        icon = Icons.Outlined.Campaign,
                        title = "মসজিদ নোটিশ বোর্ড",
                        subtitle = "গুরুত্বপূর্ণ ঘোষণা ও আপডেট",
                        isSelected = currentRoute == Screen.NoticeBoard.route,
                        iconTint = PrimaryGreen,
                        badge = "নোটিশ",
                        onClick = {
                            HapticUtils.performLongPressHaptic(view)
                            onNavigate(Screen.NoticeBoard.route)
                            onCloseDrawer()
                        }
                    )

                    DrawerMenuRow(
                        icon = Icons.Outlined.NotificationImportant,
                        title = "জানাযা ও জরুরি বার্তা",
                        subtitle = "জরুরি ঘোষণা ও শোক বার্তা",
                        isSelected = currentRoute == Screen.JanazaAlerts.route,
                        iconTint = RedDigital,
                        badge = "জরুরি",
                        badgeColor = RedDigital,
                        onClick = {
                            HapticUtils.performLongPressHaptic(view)
                            onNavigate(Screen.JanazaAlerts.route)
                            onCloseDrawer()
                        }
                    )

                    DrawerMenuRow(
                        icon = Icons.Outlined.QuestionAnswer,
                        title = "ইমাম সাহেবকে জিজ্ঞাসা",
                        subtitle = "ইসলামিক ফতোয়া ও প্রশ্নোত্তর",
                        isSelected = currentRoute == Screen.AskImam.route,
                        iconTint = CyanBlue,
                        onClick = {
                            HapticUtils.performLongPressHaptic(view)
                            onNavigate(Screen.AskImam.route)
                            onCloseDrawer()
                        }
                    )

                    DrawerMenuRow(
                        icon = Icons.Outlined.Calculate,
                        title = "যাকাত ও ফিতরা ক্যালকুলেটর",
                        subtitle = "সঠিক হিসাব ও বণ্টন নিয়ম",
                        isSelected = currentRoute == Screen.ZakatCalculator.route,
                        iconTint = GoldAccent,
                        onClick = {
                            HapticUtils.performLongPressHaptic(view)
                            onNavigate(Screen.ZakatCalculator.route)
                            onCloseDrawer()
                        }
                    )

                    DrawerMenuRow(
                        icon = Icons.Outlined.Payments,
                        title = "অনুদান ও মসজিদ ফান্ড",
                        subtitle = "বিকাশ/নগদ/ব্যাংক ডোনেশন",
                        isSelected = currentRoute == Screen.Donation.route,
                        iconTint = PrimaryGreen,
                        onClick = {
                            HapticUtils.performLongPressHaptic(view)
                            onNavigate(Screen.Donation.route)
                            onCloseDrawer()
                        }
                    )

                    DrawerMenuRow(
                        icon = Icons.Outlined.Celebration,
                        title = "ওয়াজ মাহফিল ও ইভেন্ট",
                        subtitle = "আসন্ন ইসলামিক অনুষ্ঠান",
                        isSelected = currentRoute == Screen.Events.route,
                        iconTint = GoldAccent,
                        onClick = {
                            HapticUtils.performLongPressHaptic(view)
                            onNavigate(Screen.Events.route)
                            onCloseDrawer()
                        }
                    )

                    DrawerMenuRow(
                        icon = Icons.Outlined.Groups,
                        title = "মসজিদ কমিটি ও খাদেম",
                        subtitle = "পরিচালনা পর্ষদের তালিকা",
                        isSelected = currentRoute == Screen.Committee.route,
                        iconTint = TextWhite,
                        onClick = {
                            HapticUtils.performLongPressHaptic(view)
                            onNavigate(Screen.Committee.route)
                            onCloseDrawer()
                        }
                    )

                    DrawerMenuRow(
                        icon = Icons.Outlined.PhotoLibrary,
                        title = "মসজিদ ফটো গ্যালারি",
                        subtitle = "মসজিদের মনরোম দৃশ্যসমূহ",
                        isSelected = currentRoute == Screen.Gallery.route,
                        iconTint = CyanBlue,
                        onClick = {
                            HapticUtils.performLongPressHaptic(view)
                            onNavigate(Screen.Gallery.route)
                            onCloseDrawer()
                        }
                    )

                    DrawerMenuRow(
                        icon = Icons.Outlined.LocationOn,
                        title = "যোগাযোগ ও গুগল ম্যাপ",
                        subtitle = "মসজিদের সঠিক ঠিকানা",
                        isSelected = currentRoute == Screen.Contact.route,
                        iconTint = PrimaryGreen,
                        onClick = {
                            HapticUtils.performLongPressHaptic(view)
                            onNavigate(Screen.Contact.route)
                            onCloseDrawer()
                        }
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 6.dp),
                thickness = 0.6.dp,
                color = DarkGreenBorder.copy(alpha = 0.5f)
            )

            // === SECTION 4: সেটিংস ও অ্যাডমিন (Settings & Admin) ===
            DrawerCollapsibleSectionHeader(
                title = "সিস্টেম ও অ্যাডমিন",
                isExpanded = isSystemSectionExpanded,
                onToggle = { isSystemSectionExpanded = !isSystemSectionExpanded }
            )

            AnimatedVisibility(
                visible = isSystemSectionExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    DrawerMenuRow(
                        icon = Icons.Outlined.Settings,
                        title = "সেটিংস ও জেলা নির্বাচন",
                        subtitle = "অ্যালার্ম, সাউন্ড ও সমন্বয়",
                        isSelected = currentRoute == Screen.Settings.route,
                        iconTint = TextWhite,
                        onClick = {
                            HapticUtils.performLongPressHaptic(view)
                            onNavigate(Screen.Settings.route)
                            onCloseDrawer()
                        }
                    )

                    DrawerMenuRow(
                        icon = Icons.Outlined.HelpOutline,
                        title = "সহায়তা ও প্রশ্নোত্তর (FAQ)",
                        subtitle = "অ্যাপ ব্যবহার নির্দেশিকা",
                        isSelected = currentRoute == Screen.HelpFaq.route,
                        iconTint = CyanBlue,
                        onClick = {
                            HapticUtils.performLongPressHaptic(view)
                            onNavigate(Screen.HelpFaq.route)
                            onCloseDrawer()
                        }
                    )

                    DrawerMenuRow(
                        icon = Icons.Outlined.AdminPanelSettings,
                        title = "অ্যাডমিন প্যানেল",
                        subtitle = "ওয়াক্ত ও মসজিদ ব্যবস্থাপনা",
                        isSelected = currentRoute?.startsWith("admin_") == true,
                        iconTint = GoldAccent,
                        badge = "লক",
                        badgeColor = GoldAccent,
                        onClick = {
                            HapticUtils.performLongPressHaptic(view)
                            onNavigate(Screen.AdminLogin.route)
                            onCloseDrawer()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // -------------------------------------------------------------
        // 3. Modern Glassmorphic Developer & App Version Footer
        // -------------------------------------------------------------
        DeveloperCard(
            onDeveloperClick = {
                onNavigate(Screen.DeveloperProfile.route)
                onCloseDrawer()
            },
            onDarkModeToggle = {
                isDarkMode = !isDarkMode
                val modeName = if (isDarkMode) "ডার্ক মোড সক্রিয়" else "লাইট মোড সক্রিয়"
                Toast.makeText(context, modeName, Toast.LENGTH_SHORT).show()
            },
            onLogoutClick = {
                showLogoutDialog = true
            },
            isDarkMode = isDarkMode
        )
    }

    // -------------------------------------------------------------
    // Logout Confirmation Dialog
    // -------------------------------------------------------------
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = DarkSurfaceElevated,
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = RedDigital,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = "লগআউট নিশ্চিতকরণ",
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SolaimanLipiFontFamily
                )
            },
            text = {
                Text(
                    text = "আপনি কি নিশ্চিতভাবে বর্তমান সেশন থেকে লগআউট করতে চান?",
                    color = TextMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontFamily = SolaimanLipiFontFamily
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        Toast.makeText(context, "সফলভাবে লগআউট সম্পন্ন হয়েছে", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDigital),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("হ্যাঁ, লগআউট", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("বাতিল", color = TextMuted, fontSize = 12.sp, fontFamily = SolaimanLipiFontFamily)
                }
            }
        )
    }
}

/**
 * Top Glassmorphic Mosque Header with Mosque Brand, Live Crescent motif, and Status Pill.
 */
@Composable
private fun DrawerHeaderCard(
    onHeaderClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF143021),
                        Color(0xFF0F2218),
                        Color(0xFF0B1811)
                    )
                )
            )
            .border(
                1.2.dp,
                Brush.horizontalGradient(
                    listOf(
                        PrimaryGreen.copy(alpha = 0.6f),
                        GoldAccent.copy(alpha = 0.3f),
                        PrimaryGreen.copy(alpha = 0.2f)
                    )
                ),
                RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onHeaderClick)
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Glowing Emerald Mosque Emblem
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(EmeraldDeep, Color(0xFF0F3A22))
                            )
                        )
                        .border(1.5.dp, PrimaryGreen, CircleShape)
                        .shadow(6.dp, CircleShape, ambientColor = NeonGreenGlow, spotColor = NeonGreenGlow),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Mosque,
                        contentDescription = "Mosque Logo",
                        tint = NeonGreenGlow,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "বায়তুল আমান জামে মসজিদ",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "ও ইসলামিক রিসার্চ সেন্টার",
                        fontSize = 11.sp,
                        color = GoldAccent,
                        fontWeight = FontWeight.Medium
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = PrimaryGreen.copy(alpha = 0.7f),
                    modifier = Modifier.size(13.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-status Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF09160E))
                    .border(0.6.dp, DarkGreenBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(NeonGreenGlow)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "লাইভ ওয়াক্ত ট্র্যাকার সক্রিয়",
                        color = PrimaryGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "হিজরি ১৪৪৭",
                    color = GoldAccent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Section Title Header
 */
@Composable
private fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        color = GoldAccent,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.3.sp,
        fontFamily = SolaimanLipiFontFamily,
        modifier = Modifier.padding(start = 6.dp, top = 6.dp, bottom = 3.dp)
    )
}

/**
 * Collapsible Section Title Header with Arrow
 */
@Composable
private fun DrawerCollapsibleSectionHeader(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onToggle)
            .padding(horizontal = 6.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = GoldAccent,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp,
            fontFamily = SolaimanLipiFontFamily
        )
        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = GoldAccent.copy(alpha = 0.8f),
            modifier = Modifier.size(16.dp)
        )
    }
}

/**
 * Modern High-Contrast Drawer Item Row with Glassmorphic Active State & Subtitle.
 */
@Composable
private fun DrawerMenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    isSelected: Boolean,
    onClick: () -> Unit,
    iconTint: Color = PrimaryGreen,
    badge: String? = null,
    badgeColor: Color = PrimaryGreen
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) EmeraldDeep.copy(alpha = 0.65f) else Color.Transparent,
        animationSpec = tween(150),
        label = "DrawerRowBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryGreen.copy(alpha = 0.5f) else Color.Transparent,
        animationSpec = tween(150),
        label = "DrawerRowBorder"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = PrimaryGreen.copy(alpha = 0.25f)),
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Container Box
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(
                    if (isSelected) PrimaryGreen.copy(alpha = 0.18f) else DarkSurfaceElevated
                )
                .border(
                    0.8.dp,
                    if (isSelected) PrimaryGreen.copy(alpha = 0.4f) else DarkGreenBorder.copy(alpha = 0.4f),
                    RoundedCornerShape(9.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) PrimaryGreen else iconTint,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Title and Subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) PrimaryGreen else TextWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = SolaimanLipiFontFamily
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 9.5.sp,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = SolaimanLipiFontFamily
                )
            }
        }

        // Badge pill
        if (badge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeColor.copy(alpha = 0.16f))
                    .border(0.8.dp, badgeColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badge,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor,
                    fontFamily = SolaimanLipiFontFamily
                )
            }
        }
    }
}

@Composable
fun DrawerMenu(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    MosqueDrawerContent(
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        onCloseDrawer = onCloseDrawer,
        modifier = modifier
    )
}

