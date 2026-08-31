package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.util.HapticUtils

@Composable
fun MosqueDrawerContent(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(vertical = 16.dp, horizontal = 16.dp)
    ) {
        // Drawer Header Card
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
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(EmeraldDeep)
                            .border(1.5.dp, PrimaryGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Mosque,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "চৌধুরী পাটোয়ারী বাড়ি",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                        Text(
                            text = "জামে মসজিদ ও ইসলামিক সেন্টার",
                            fontSize = 11.sp,
                            color = GoldAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "স্মার্ট ওয়াক্ত এলার্ট • কিবলা কম্পাস • নোটিশ বোর্ড",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Drawer Menu Items
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

        Divider(
            modifier = Modifier.padding(vertical = 12.dp),
            color = DarkSurfaceBorder
        )

        DrawerItem(
            icon = Icons.Outlined.Settings,
            label = "সেটিংস ও অডিও এলার্ট",
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

        Spacer(modifier = Modifier.weight(1f))

        // App Version
        Text(
            text = "সংস্করণ ১.০ • টেকটাইল ফিডব্যাক ও সেন্সর সক্রিয়",
            fontSize = 10.sp,
            color = TextSubtle,
            modifier = Modifier.align(Alignment.CenterHorizontally)
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
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = label,
            fontSize = 13.sp,
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
