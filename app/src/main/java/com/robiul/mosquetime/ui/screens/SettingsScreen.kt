package com.robiul.mosquetime.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robiul.mosquetime.data.model.AppLanguage
import com.robiul.mosquetime.data.model.AppSettings
import com.robiul.mosquetime.data.model.CalculationMethod
import com.robiul.mosquetime.data.model.FontSizeScale
import com.robiul.mosquetime.data.repository.MosqueRepository
import com.robiul.mosquetime.data.repository.UserPreferencesRepository
import com.robiul.mosquetime.ui.components.CommonHeader
import com.robiul.mosquetime.ui.theme.CyanBlue
import com.robiul.mosquetime.ui.theme.DarkBackground
import com.robiul.mosquetime.ui.theme.DarkGreen
import com.robiul.mosquetime.ui.theme.DarkGreenBorder
import com.robiul.mosquetime.ui.theme.DarkSurface
import com.robiul.mosquetime.ui.theme.DarkSurfaceBorder
import com.robiul.mosquetime.ui.theme.DarkSurfaceElevated
import com.robiul.mosquetime.ui.theme.GoldAccent
import com.robiul.mosquetime.ui.theme.NeonGreenGlow
import com.robiul.mosquetime.ui.theme.PrimaryGreen
import com.robiul.mosquetime.ui.theme.PurpleAccent
import com.robiul.mosquetime.ui.theme.RedDigital
import com.robiul.mosquetime.ui.theme.SolaimanLipiFontFamily
import com.robiul.mosquetime.ui.theme.TextMuted
import com.robiul.mosquetime.ui.theme.TextWhite

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onNavigateToAdmin: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by UserPreferencesRepository.settings.collectAsState()
    val scrollState = rememberScrollState()

    var isDistrictMenuOpen by remember { mutableStateOf(false) }
    var isMethodMenuOpen by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    val currentDistrict = MosqueRepository.getDistrictById(settings.selectedDistrictId)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonHeader(
            title = "অ্যাপ সেটিংস",
            subtitle = "কাস্টমাইজেশন ও পছন্দসমূহ",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Location & District Settings Card
            SettingsSectionCard(title = "অবস্থান ও জেলা নির্বাচন (অফলাইন)", icon = Icons.Default.LocationOn, iconTint = PrimaryGreen) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkBackground)
                        .border(1.dp, DarkGreenBorder, RoundedCornerShape(8.dp))
                        .clickable { isDistrictMenuOpen = true }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("নির্বাচিত জেলা (রুম ডাটাবেসে সংরক্ষিত):", color = TextMuted, fontSize = 11.sp)
                            Text(currentDistrict.nameBn, color = NeonGreenGlow, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("পরিবর্তন ▼", color = CyanBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    DropdownMenu(
                        expanded = isDistrictMenuOpen,
                        onDismissRequest = { isDistrictMenuOpen = false },
                        modifier = Modifier.background(DarkSurfaceElevated)
                    ) {
                        MosqueRepository.districts.forEach { dist ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${dist.nameBn} (${if (dist.fajrOffsetMinutes == 0) "ঢাকা মানদণ্ড" else "${if (dist.fajrOffsetMinutes > 0) "+" else ""}${dist.fajrOffsetMinutes} মি."})",
                                        color = if (dist.id == currentDistrict.id) PrimaryGreen else TextWhite
                                    )
                                },
                                onClick = {
                                    UserPreferencesRepository.updateSettings(settings.copy(selectedDistrictId = dist.id))
                                    isDistrictMenuOpen = false
                                    Toast.makeText(context, "${dist.nameBn} জেলা নির্বাচিত ও সেভ হয়েছে", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }

            // Room Database Offline Persistence Status Card
            SettingsSectionCard(title = "রুম ডাটাবেস ও অফলাইন স্টোরেজ", icon = Icons.Default.Storage, iconTint = GoldAccent) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkGreen.copy(alpha = 0.35f))
                            .border(1.dp, DarkGreenBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = NeonGreenGlow,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "১০০% অফলাইন সুবিধা সক্রিয়",
                                color = TextWhite,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "নামাজের সময়সূচি ও অবস্থান ডাটা লোকাল Room DB-তে সংরক্ষিত",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("লোকাল ডাটাবেস স্ট্যাটাস:", color = TextMuted, fontSize = 11.5.sp)
                        Text("সংযুক্ত ও সক্রিয় (SQLite)", color = PrimaryGreen, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("অফলাইন জেলা ডাটা:", color = TextMuted, fontSize = 11.5.sp)
                        Text("৬৪ জেলা ক্যাশড", color = CyanBlue, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Calculation Method Card
            SettingsSectionCard(title = "নামাজ হিসাবের পদ্ধতি", icon = Icons.Default.Timer, iconTint = CyanBlue) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkBackground)
                        .border(1.dp, DarkGreenBorder, RoundedCornerShape(8.dp))
                        .clickable { isMethodMenuOpen = true }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(settings.calculationMethod.title, color = TextWhite, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                            Text(settings.calculationMethod.description, color = GoldAccent, fontSize = 11.sp)
                        }
                        Text("পরিবর্তন ▼", color = CyanBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    DropdownMenu(
                        expanded = isMethodMenuOpen,
                        onDismissRequest = { isMethodMenuOpen = false },
                        modifier = Modifier.background(DarkSurfaceElevated)
                    ) {
                        CalculationMethod.values().forEach { method ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(method.title, color = if (method == settings.calculationMethod) PrimaryGreen else TextWhite, fontWeight = FontWeight.Bold)
                                        Text(method.description, color = TextMuted, fontSize = 11.sp)
                                    }
                                },
                                onClick = {
                                    UserPreferencesRepository.updateSettings(settings.copy(calculationMethod = method))
                                    isMethodMenuOpen = false
                                }
                            )
                        }
                    }
                }
            }

            // Notifications Toggles Card
            SettingsSectionCard(title = "নোটিফিকেশন ও অ্যালার্ট", icon = Icons.Default.Notifications, iconTint = GoldAccent) {
                SettingsSwitchRow(
                    title = "নামাজের আজান ও ওয়াক্ত নোটিফিকেশন",
                    subtitle = "প্রতি ওয়াক্তের পূর্বে সতর্কবার্তা প্রেরণ",
                    checked = settings.isPrayerNotificationEnabled,
                    onCheckedChange = { UserPreferencesRepository.updateSettings(settings.copy(isPrayerNotificationEnabled = it)) }
                )

                HorizontalDivider(thickness = 0.4.dp, color = DarkSurfaceBorder)

                SettingsSwitchRow(
                    title = "জুমার খুতবা ও বিশেষ নোটিশ",
                    subtitle = "জুমার দিন বিশেষ সময়সূচি অ্যালার্ট",
                    checked = settings.isJumahReminderEnabled,
                    onCheckedChange = { UserPreferencesRepository.updateSettings(settings.copy(isJumahReminderEnabled = it)) }
                )

                HorizontalDivider(thickness = 0.4.dp, color = DarkSurfaceBorder)

                SettingsSwitchRow(
                    title = "মসজিদের জরুরি ঘোষণা ও অনুষ্ঠান",
                    subtitle = "ওয়াজ মাহফিল ও নোটিশ নোটিফিকেশন",
                    checked = settings.isNoticeNotificationEnabled,
                    onCheckedChange = { UserPreferencesRepository.updateSettings(settings.copy(isNoticeNotificationEnabled = it)) }
                )
            }

            // Font Size & Accessibility Card
            SettingsSectionCard(title = "ফন্ট সাইজ ও পড়ার সুবিধা", icon = Icons.Default.FormatSize, iconTint = PurpleAccent) {
                Text("লেখা পড়ার আকার নির্বাচন করুন (বয়স্কদের জন্য বড় ফন্ট সহায়ক):", color = TextMuted, fontSize = 11.5.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FontSizeScale.values().forEach { scale ->
                        val isSel = settings.fontSizeScale == scale
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) PrimaryGreen else DarkBackground)
                                .border(1.dp, if (isSel) PrimaryGreen else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    UserPreferencesRepository.updateSettings(settings.copy(fontSizeScale = scale))
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = scale.title.split(" ").first(),
                                color = if (isSel) DarkBackground else TextWhite,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Visual Theme & Effects
            SettingsSectionCard(title = "ভিজ্যুয়াল অ্যাপ থিম ও নিয়ন গ্লো", icon = Icons.Default.Brightness6, iconTint = NeonGreenGlow) {
                SettingsSwitchRow(
                    title = "মসজিদ ডার্ক নিয়ন গ্লো ইফেক্ট",
                    subtitle = "বর্ডার ও সূচকে প্রিমিয়াম সবুজ আলোকচ্ছটা",
                    checked = settings.isNeonGlowActive,
                    onCheckedChange = { UserPreferencesRepository.updateSettings(settings.copy(isNeonGlowActive = it)) }
                )
            }

            // Secure Admin Panel Entry
            SettingsSectionCard(title = "মসজিদ প্রশাসন ও ব্যবস্থাপনা", icon = Icons.Default.AdminPanelSettings, iconTint = GoldAccent) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkBackground)
                        .border(1.dp, GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .clickable { onNavigateToAdmin() }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "মসজিদ অ্যাডমিন প্যানেল",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = SolaimanLipiFontFamily
                        )
                        Text(
                            text = "নামাজের সময়, নোটিশ ও হুজুরের খানা পরিচালনা",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "প্রবেশ",
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Reset Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurface)
                    .border(1.dp, RedDigital.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .clickable { showResetConfirmDialog = true }
                    .padding(vertical = 12.dp)
                    .testTag("reset_settings_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = RedDigital, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ডিফল্ট সেটিংসে ফিরিয়ে নিন", color = RedDigital, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("সেটিংস রিসেট", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = { Text("আপনি কি সকল সেটিংস ডিফল্ট মানে রিসেট করতে চান?", color = TextMuted) },
            confirmButton = {
                Button(
                    onClick = {
                        UserPreferencesRepository.updateSettings(AppSettings())
                        showResetConfirmDialog = false
                        Toast.makeText(context, "সকল সেটিংস রিসেট হয়েছে", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDigital)
                ) {
                    Text("রিসেট করুন", color = androidx.compose.ui.graphics.Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("বাতিল", color = TextMuted)
                }
            },
            containerColor = DarkSurfaceElevated,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(1.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, color = PrimaryGreen, fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = TextMuted, fontSize = 11.sp)
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = DarkBackground,
                checkedTrackColor = PrimaryGreen,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = DarkBackground
            )
        )
    }
}
