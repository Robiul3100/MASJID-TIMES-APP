package com.robiul.mosquetime.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robiul.mosquetime.data.model.FacilityItem
import com.robiul.mosquetime.data.repository.MosqueRepository
import com.robiul.mosquetime.ui.components.CommonHeader
import com.robiul.mosquetime.ui.components.MosqueCrestIcon
import com.robiul.mosquetime.ui.theme.*
import com.robiul.mosquetime.util.HapticUtils

enum class MosqueAboutTab(val titleBn: String, val icon: ImageVector) {
    OVERVIEW("ইতিহাস ও পরিচিতি", Icons.Outlined.Info),
    FACILITIES("সুযোগ-সুবিধা", Icons.Outlined.Apartment),
    SCHOLARS("ইমাম ও খাদেম", Icons.Outlined.PeopleOutline),
    LOCATION("অবস্থান ও ম্যাপস", Icons.Outlined.LocationOn)
}

@Composable
fun AboutMosqueScreen(
    onBackClick: () -> Unit,
    onNavigateToCommittee: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mosque by com.robiul.mosquetime.data.firebase.MosqueAdminRepository.getInstance().mosqueDetails.collectAsState()
    val context = LocalContext.current
    val view = LocalView.current
    val scrollState = rememberScrollState()

    var selectedTab by remember { mutableStateOf(MosqueAboutTab.OVERVIEW) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("about_mosque_screen"),
        containerColor = DarkBackground,
        topBar = {
            CommonHeader(
                title = "মসজিদ পরিচিতি ও ইতিহাস",
                subtitle = "${mosque.nameBn} কমপ্লেক্স",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            // Hero Mosque Identity Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1C3A29), DarkSurfaceElevated)
                        )
                    )
                    .border(1.2.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(DarkGreen.copy(alpha = 0.5f))
                            .border(1.5.dp, GoldAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        MosqueCrestIcon(modifier = Modifier.size(46.dp, 34.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = mosque.nameBn,
                        color = TextWhite,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = mosque.nameEn,
                        color = GoldAccent,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = mosque.address,
                            color = TextMuted,
                            fontSize = 11.5.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Mosque Quick Stats 3-Grid Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkBackground.copy(alpha = 0.85f))
                            .border(0.8.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("প্রতিষ্ঠা সাল", color = TextMuted, fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(mosque.establishedYear, color = GoldAccent, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(modifier = Modifier.height(26.dp).width(1.dp).background(DarkSurfaceBorder))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("ধারণক্ষমতা", color = TextMuted, fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(mosque.capacity, color = CyanBlue, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(modifier = Modifier.height(26.dp).width(1.dp).background(DarkSurfaceBorder))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("ভবন বিন্যাস", color = TextMuted, fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(mosque.floors, color = NeonGreenGlow, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Tabs Segment Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MosqueAboutTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    val bg = if (isSelected) EmeraldDeep else DarkSurfaceElevated
                    val border = if (isSelected) PrimaryGreen else DarkSurfaceBorder
                    val tint = if (isSelected) PrimaryGreen else TextMuted

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(bg)
                            .border(1.dp, border, RoundedCornerShape(10.dp))
                            .clickable {
                                HapticUtils.performLongPressHaptic(view)
                                selectedTab = tab
                            }
                            .padding(vertical = 8.dp, horizontal = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = tab.icon, contentDescription = tab.titleBn, tint = tint, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = tab.titleBn,
                                color = if (isSelected) TextWhite else TextMuted,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Content Rendering
            when (selectedTab) {
                MosqueAboutTab.OVERVIEW -> {
                    OverviewTabContent(mosque = mosque, onNavigateToCommittee = onNavigateToCommittee)
                }
                MosqueAboutTab.FACILITIES -> {
                    FacilitiesTabContent(facilities = mosque.facilities)
                }
                MosqueAboutTab.SCHOLARS -> {
                    ScholarsTabContent(
                        mosque = mosque,
                        onCall = { phone -> makePhoneCall(context, phone) },
                        onSms = { phone -> sendSms(context, phone) },
                        onCopy = { phone, label -> copyToClipboard(context, phone, "$label কপি করা হয়েছে") }
                    )
                }
                MosqueAboutTab.LOCATION -> {
                    LocationTabContent(
                        mosque = mosque,
                        onOpenMaps = { openGoogleMaps(context, mosque.address, mosque.nameBn) },
                        onShareLocation = { shareMosqueLocation(context, mosque.nameBn, mosque.address, mosque.website) },
                        onCallOffice = { makePhoneCall(context, mosque.officePhone) },
                        onEmailOffice = { sendEmail(context, mosque.officeEmail) },
                        onVisitWebsite = { openWebsite(context, mosque.website) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Tab 1: Overview & History Content
 */
@Composable
private fun OverviewTabContent(
    mosque: com.robiul.mosquetime.data.model.MosqueDetails,
    onNavigateToCommittee: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Quranic Ayat Inspiration Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "إِنَّمَا يَعْمُرُ مَسَاجِدَ اللَّهِ مَنْ آمَنَ بِاللَّهِ وَالْيَوْمِ الْآخِرِ",
                    color = GoldAccent,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "\"নিঃসন্দেহে তারাই আল্লাহর মসজিদসমূহের আবাদ করে, যারা আল্লাহ ও শেষ দিবসের প্রতি ঈমান আনে...\" — (সূরা তাওবা: ১৮)",
                    color = TextWhite.copy(alpha = 0.9f),
                    fontSize = 11.5.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 17.sp
                )
            }
        }

        // History Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurface)
                .border(1.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.HistoryEdu, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ঐতিহাসিক পটভূমি ও সূচনা",
                        color = PrimaryGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = mosque.history,
                    color = TextWhite.copy(alpha = 0.9f),
                    fontSize = 12.5.sp,
                    lineHeight = 20.sp
                )
            }
        }

        // Description Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurface)
                .border(1.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Mosque, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "কার্যক্রম ও ধর্মীয় শিক্ষা",
                        color = GoldAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = mosque.description,
                    color = TextWhite.copy(alpha = 0.9f),
                    fontSize = 12.5.sp,
                    lineHeight = 20.sp
                )
            }
        }

        // Link to Committee Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, PrimaryGreen.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                .clickable { onNavigateToCommittee() }
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.People, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("মসজিদ পরিচালনা পরিষদ দেখুন", color = TextWhite, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                        Text("সভাপতি, সাধারণ সম্পাদক ও কার্যনির্বাহী কমিটি", color = TextMuted, fontSize = 11.sp)
                    }
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Go", tint = PrimaryGreen, modifier = Modifier.size(16.dp))
            }
        }
    }
}

/**
 * Tab 2: Facilities Grid Content
 */
@Composable
private fun FacilitiesTabContent(facilities: List<FacilityItem>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(DarkSurfaceElevated)
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = NeonGreenGlow, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "মসজিদ কমপ্লেক্সে মুসল্লিদের জন্য সকল আধুনিক সুযোগ-সুবিধা সার্বক্ষণিক প্রস্তুত রাখা হয়।",
                    fontSize = 11.5.sp,
                    color = TextWhite
                )
            }
        }

        facilities.forEach { facility ->
            FacilityCard(facility = facility)
        }
    }
}

@Composable
private fun FacilityCard(facility: FacilityItem) {
    val (icon, tint) = when (facility.iconType) {
        "ac" -> Icons.Default.AcUnit to CyanBlue
        "wudu" -> Icons.Default.WaterDrop to PrimaryGreen
        "women" -> Icons.Default.Woman to PurpleAccent
        "library" -> Icons.Default.LocalLibrary to GoldAccent
        "maktab" -> Icons.AutoMirrored.Filled.MenuBook to NeonGreenGlow
        "ambulance" -> Icons.Default.Emergency to Color(0xFFFF5252)
        "power" -> Icons.Default.ElectricBolt to GoldAccent
        else -> Icons.Default.Info to PrimaryGreen
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(1.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.15f))
                    .border(1.dp, tint.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = facility.title,
                    color = TextWhite,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = facility.description,
                    color = TextMuted,
                    fontSize = 11.5.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

/**
 * Tab 3: Scholars & Staff Directory
 */
@Composable
private fun ScholarsTabContent(
    mosque: com.robiul.mosquetime.data.model.MosqueDetails,
    onCall: (String) -> Unit,
    onSms: (String) -> Unit,
    onCopy: (String, String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Imam & Khatib Profile Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(DarkSurface)
                .border(1.2.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(GoldAccent.copy(alpha = 0.15f))
                            .border(1.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = mosque.imamTitle,
                            color = GoldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = { onCall(mosque.imamPhone) }, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.Default.Call, contentDescription = "Call", tint = NeonGreenGlow, modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = { onSms(mosque.imamPhone) }, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.Outlined.Email, contentDescription = "SMS", tint = CyanBlue, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(GoldAccent.copy(alpha = 0.15f))
                            .border(1.5.dp, GoldAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(28.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = mosque.imamName,
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = mosque.imamEducation,
                            color = TextMuted,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = DarkSurfaceBorder)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onCopy(mosque.imamPhone, "ইমাম সাহেবের নম্বর") }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "মোবাইল: ${mosque.imamPhone}", color = CyanBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy", tint = TextMuted, modifier = Modifier.size(14.dp))
                }
            }
        }

        // Muazzin Profile Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(DarkSurface)
                .border(1.dp, CyanBlue.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyanBlue.copy(alpha = 0.15f))
                            .border(1.dp, CyanBlue.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "সম্মানিত মুয়াজ্জিন",
                            color = CyanBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = { onCall(mosque.muazzinPhone) }, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.Default.Call, contentDescription = "Call", tint = NeonGreenGlow, modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = { onSms(mosque.muazzinPhone) }, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.Outlined.Email, contentDescription = "SMS", tint = CyanBlue, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(CyanBlue.copy(alpha = 0.15f))
                            .border(1.dp, CyanBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(24.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = mosque.muazzinName,
                            color = TextWhite,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "আজান ও নামাজের সময় পরিচালনা",
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = DarkSurfaceBorder)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onCopy(mosque.muazzinPhone, "মুয়াজ্জিন সাহেবের নম্বর") }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "মোবাইল: ${mosque.muazzinPhone}", color = CyanBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy", tint = TextMuted, modifier = Modifier.size(14.dp))
                }
            }
        }

        // Khadem Council Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(DarkSurface)
                .border(1.dp, PrimaryGreen.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.CleaningServices, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "খাদেম ও পরিচ্ছন্নতা পরিষদ",
                        color = PrimaryGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = mosque.khademName,
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "মসজিদ প্রাঙ্গণ, ওজুখানা ও সার্বিক স্যানিটেশন সেবায় নিয়োজিত।",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}

/**
 * Tab 4: Location & Google Maps Navigation Content
 */
@Composable
private fun LocationTabContent(
    mosque: com.robiul.mosquetime.data.model.MosqueDetails,
    onOpenMaps: () -> Unit,
    onShareLocation: () -> Unit,
    onCallOffice: () -> Unit,
    onEmailOffice: () -> Unit,
    onVisitWebsite: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Location Card with Google Maps Navigation Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1B3528), DarkSurface)
                    )
                )
                .border(1.2.dp, PrimaryGreen.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "মসজিদের ভৌগোলিক অবস্থান ও দিকনির্দেশনা",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = mosque.nameBn,
                    color = GoldAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${mosque.address}, জেলা: ${mosque.district}",
                    color = TextWhite,
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // GPS Google Maps Button & Share Location Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenMaps,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Directions, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("গুগল ম্যাপস", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                    }

                    Button(
                        onClick = onShareLocation,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Outlined.Share, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("অবস্থান শেয়ার", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                    }
                }
            }
        }

        // Office Contact Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(DarkSurface)
                .border(1.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "মসজিদ অফিস ও যোগাযোগ মাধ্যম",
                    color = GoldAccent,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold
                )

                ContactOptionRow(
                    label = "অফিস ল্যান্ডফোন / হটলাইন",
                    value = mosque.officePhone,
                    icon = Icons.Outlined.Phone,
                    onClick = onCallOffice
                )

                ContactOptionRow(
                    label = "অফিসিয়াল ইমেইল",
                    value = mosque.officeEmail,
                    icon = Icons.Outlined.Email,
                    onClick = onEmailOffice
                )

                ContactOptionRow(
                    label = "অফিসিয়াল ওয়েবসাইট",
                    value = mosque.website,
                    icon = Icons.Outlined.Language,
                    onClick = onVisitWebsite
                )
            }
        }
    }
}

@Composable
private fun ContactOptionRow(
    label: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DarkBackground)
            .clickable { onClick() }
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = label, color = TextMuted, fontSize = 10.5.sp)
                Text(text = value, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
    }
}

private fun openGoogleMaps(context: Context, address: String, mosqueName: String) {
    try {
        val query = Uri.encode("$mosqueName, $address")
        val uri = Uri.parse("geo:0,0?q=$query")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=$query"))
            context.startActivity(webIntent)
        }
    } catch (e: Exception) {
        Toast.makeText(context, "ম্যাপস খোলা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
    }
}

private fun shareMosqueLocation(context: Context, mosqueName: String, address: String, website: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        val mapLink = "https://maps.google.com/?q=${Uri.encode("$mosqueName, $address")}"
        putExtra(
            Intent.EXTRA_TEXT,
            "🕌 *${mosqueName}*\n📍 *ঠিকানা:* ${address}\n🌐 *ওয়েবসাইট:* https://${website}\n🗺️ *গুগল ম্যাপস লোকেশন:* ${mapLink}\n\n— চৌধুরী পাটোয়ারী বাড়ি জামে মসজিদ ও ইসলামিক সেন্টার"
        )
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "মসজিদের অবস্থান শেয়ার করুন")
    context.startActivity(shareIntent)
}

private fun makePhoneCall(context: Context, phone: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "কল করা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
    }
}

private fun sendSms(context: Context, phone: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phone")).apply {
            putExtra("sms_body", "আসসালামু আলাইকুম...")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "মেসেজ পাঠানো সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
    }
}

private fun sendEmail(context: Context, email: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email")).apply {
            putExtra(Intent.EXTRA_SUBJECT, "মসজিদ সংক্রান্ত যোগাযোগ")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "ইমেইল অ্যাপ পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
    }
}

private fun openWebsite(context: Context, website: String) {
    try {
        val url = if (!website.startsWith("http://") && !website.startsWith("https://")) "https://$website" else website
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "ওয়েবসাইট খোলা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
    }
}

private fun copyToClipboard(context: Context, text: String, toastMessage: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Mosque Contact", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
}
