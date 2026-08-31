package com.robiul.mosquetime.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robiul.mosquetime.ui.navigation.Screen
import com.robiul.mosquetime.ui.theme.*
import com.robiul.mosquetime.util.HapticUtils

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

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(310.dp)
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(vertical = 12.dp, horizontal = 14.dp)
    ) {
        // -------------------------------------------------------------
        // 1. Drawer Header Card
        // -------------------------------------------------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF162D24), DarkSurface)
                    )
                )
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(EmeraldDeep)
                            .border(1.5.dp, PrimaryGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Mosque,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "চৌধুরী পাটোয়ারী বাড়ি",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                        Text(
                            text = "জামে মসজিদ ও ইসলামিক সেন্টার",
                            fontSize = 10.5.sp,
                            color = GoldAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "স্মার্ট ওয়াক্ত এলার্ট • কিবলা কম্পাস • নোটিশ বোর্ড",
                    fontSize = 9.5.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // -------------------------------------------------------------
        // 2. Scrollable Navigation Menu Items
        // -------------------------------------------------------------
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            DrawerItem(
                icon = Icons.Outlined.Home,
                label = "হোম ড্যাশবোর্ড",
                isSelected = currentRoute == Screen.Home.route,
                onClick = {
                    HapticUtils.performLongPressHaptic(view)
                    onNavigate(Screen.Home.route)
                    onCloseDrawer()
                }
            )

            DrawerItem(
                icon = Icons.Outlined.Explore,
                label = "কিবলা কম্পাস (সেন্সর)",
                badge = "LIVE",
                isSelected = currentRoute == Screen.Qibla.route,
                onClick = {
                    HapticUtils.performLongPressHaptic(view)
                    onNavigate(Screen.Qibla.route)
                    onCloseDrawer()
                }
            )

            DrawerItem(
                icon = Icons.Outlined.CalendarMonth,
                label = "নামাজের সময়সূচি ও এলার্ট",
                isSelected = currentRoute == Screen.DailyPrayer.route,
                onClick = {
                    HapticUtils.performLongPressHaptic(view)
                    onNavigate(Screen.DailyPrayer.route)
                    onCloseDrawer()
                }
            )

            DrawerItem(
                icon = Icons.Outlined.VolunteerActivism,
                label = "দোয়া ও ডিজিটাল তসবিহ",
                isSelected = currentRoute == Screen.DuaDhikr.route,
                onClick = {
                    HapticUtils.performLongPressHaptic(view)
                    onNavigate(Screen.DuaDhikr.route)
                    onCloseDrawer()
                }
            )

            DrawerItem(
                icon = Icons.Outlined.Restaurant,
                label = "হুজুরের খানা সূচি",
                isSelected = currentRoute == Screen.HujurKhana.route,
                onClick = {
                    HapticUtils.performLongPressHaptic(view)
                    onNavigate(Screen.HujurKhana.route)
                    onCloseDrawer()
                }
            )

            DrawerItem(
                icon = Icons.Outlined.Campaign,
                label = "মসজিদ নোটিশ বোর্ড",
                isSelected = currentRoute == Screen.NoticeBoard.route,
                onClick = {
                    HapticUtils.performLongPressHaptic(view)
                    onNavigate(Screen.NoticeBoard.route)
                    onCloseDrawer()
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 6.dp),
                thickness = 0.5.dp,
                color = DarkSurfaceBorder
            )

            DrawerItem(
                icon = Icons.Outlined.Settings,
                label = "সেটিংস ও জেলা নির্বাচন",
                isSelected = currentRoute == Screen.Settings.route,
                onClick = {
                    HapticUtils.performLongPressHaptic(view)
                    onNavigate(Screen.Settings.route)
                    onCloseDrawer()
                }
            )

            DrawerItem(
                icon = Icons.Outlined.AdminPanelSettings,
                label = "অ্যাডমিন প্যানেল",
                isSelected = currentRoute == Screen.AdminLogin.route || currentRoute == Screen.AdminDashboard.route,
                badge = "লক",
                onClick = {
                    HapticUtils.performLongPressHaptic(view)
                    onNavigate(Screen.AdminLogin.route)
                    onCloseDrawer()
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // -------------------------------------------------------------
        // 3. Developer Card at the Bottom of the Drawer
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
                    tint = Color(0xFFFF6B6B),
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = "লগআউট নিশ্চিতকরণ",
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "আপনি কি নিশ্চিতভাবে বর্তমান সেশন থেকে লগআউট করতে চান?",
                    color = TextMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        Toast.makeText(context, "সফলভাবে লগআউট সম্পন্ন হয়েছে", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("হ্যাঁ, লগআউট", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("বাতিল", color = TextMuted, fontSize = 12.sp)
                }
            }
        )
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    badge: String? = null
) {
    val bgColor = if (isSelected) EmeraldDeep.copy(alpha = 0.8f) else Color.Transparent
    val contentColor = if (isSelected) PrimaryGreen else TextWhite
    val borderColor = if (isSelected) DarkGreenBorder else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            fontSize = 12.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = contentColor,
            modifier = Modifier.weight(1f)
        )

        if (badge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(PrimaryGreen.copy(alpha = 0.2f))
                    .border(0.8.dp, PrimaryGreen, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badge,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
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

