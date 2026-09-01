package com.robiul.mosquetime.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robiul.mosquetime.data.repository.MosqueRepository
import com.robiul.mosquetime.ui.components.CommonHeader
import com.robiul.mosquetime.ui.components.MosqueCrestIcon
import com.robiul.mosquetime.ui.theme.*
import com.robiul.mosquetime.util.HapticUtils

enum class FeedbackCategory(val titleBn: String) {
    GENERAL("সাধারণ পরামর্শ"),
    DEVELOPMENT("মসজিদ উন্নয়ন"),
    DONATION("অনুদান ও ফান্ড"),
    COMPLAINT("অভিযোগ বা সংশোধন"),
    DUA("বিশেষ দোয়ার আবেদন")
}

@Composable
fun ContactScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val mosque by com.robiul.mosquetime.data.firebase.MosqueAdminRepository.getInstance().mosqueDetails.collectAsState()
    val scrollState = rememberScrollState()

    // Feedback Form State
    var senderName by remember { mutableStateOf("") }
    var senderPhone by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FeedbackCategory.GENERAL) }
    var feedbackMessage by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("contact_screen"),
        containerColor = DarkBackground,
        topBar = {
            CommonHeader(
                title = "যোগাযোগ ও মতামত",
                subtitle = "মসজিদ কার্যালয়, ইমাম পরিষদ ও ডিজিটাল বার্তা বক্স",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Mosque Address & GPS Navigation Hero Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1B3828), DarkSurfaceElevated)
                        )
                    )
                    .border(1.2.dp, PrimaryGreen.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "মসজিদ কমপ্লেক্সের ঠিকানা",
                                color = PrimaryGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Copy Address Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkBackground)
                                .border(0.8.dp, CyanBlue.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                .clickable {
                                    HapticUtils.performLongPressHaptic(view)
                                    copyToClipboard(context, "${mosque.nameBn}\n${mosque.address}", "ঠিকানা কপি করা হয়েছে")
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.ContentCopy, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("কপি", color = CyanBlue, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = mosque.nameBn,
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = mosque.address,
                        color = TextWhite.copy(alpha = 0.9f),
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Open in Google Maps & Share Location Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                HapticUtils.performLongPressHaptic(view)
                                openGoogleMaps(context, mosque.address, mosque.nameBn)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("গুগল ম্যাপস", color = DarkBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                HapticUtils.performLongPressHaptic(view)
                                shareMosqueLocation(context, mosque.nameBn, mosque.address, mosque.website)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Outlined.Share, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("অবস্থান শেয়ার", color = DarkBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Direct Phone Directory Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "গুরুত্বপূর্ণ যোগাযোগ ডিরেক্টরি",
                    color = GoldAccent,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold
                )

                // Imam Contact Card
                PhoneDirectoryCard(
                    title = "খতিব ও প্রধান ইমাম",
                    name = mosque.imamName,
                    phone = mosque.imamPhone,
                    badgeColor = GoldAccent,
                    onCall = { makePhoneCall(context, mosque.imamPhone) },
                    onSms = { sendSms(context, mosque.imamPhone) },
                    onCopy = { copyToClipboard(context, mosque.imamPhone, "ইমাম সাহেবের নম্বর কপি করা হয়েছে") }
                )

                // Muazzin Contact Card
                PhoneDirectoryCard(
                    title = "সম্মানিত মুয়াজ্জিন",
                    name = mosque.muazzinName,
                    phone = mosque.muazzinPhone,
                    badgeColor = CyanBlue,
                    onCall = { makePhoneCall(context, mosque.muazzinPhone) },
                    onSms = { sendSms(context, mosque.muazzinPhone) },
                    onCopy = { copyToClipboard(context, mosque.muazzinPhone, "মুয়াজ্জিন সাহেবের নম্বর কপি করা হয়েছে") }
                )

                // Mosque Office Hotline Card
                PhoneDirectoryCard(
                    title = "মসজিদ অফিস ও পরিচালনা পরিষদ",
                    name = "নিত্যদিনের তথ্য, হুজুরের খানা ও অনুদান সংক্রান্ত",
                    phone = mosque.officePhone,
                    badgeColor = PrimaryGreen,
                    onCall = { makePhoneCall(context, mosque.officePhone) },
                    onSms = { sendSms(context, mosque.officePhone) },
                    onCopy = { copyToClipboard(context, mosque.officePhone, "অফিস নম্বর কপি করা হয়েছে") }
                )
            }

            // Digital Feedback & Suggestion Box Form
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.RateReview, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ডিজিটাল মতামত ও পরামর্শ বাক্স",
                            color = GoldAccent,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "মসজিদের উন্নয়ন, কোনো পরামর্শ বা বিশেষ দোয়ার আবেদনের বার্তা সরাসরি মসজিদ কমিটির নিকট প্রেরণ করুন।",
                        color = TextMuted,
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp
                    )

                    // Sender Name Input
                    OutlinedTextField(
                        value = senderName,
                        onValueChange = { senderName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("আপনার নাম লিখুন", color = TextMuted, fontSize = 12.sp) },
                        label = { Text("নাম", color = TextMuted, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedContainerColor = DarkBackground,
                            unfocusedContainerColor = DarkBackground,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            cursorColor = PrimaryGreen
                        ),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Sender Phone Input
                    OutlinedTextField(
                        value = senderPhone,
                        onValueChange = { senderPhone = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("আপনার মোবাইল নম্বর (ঐচ্ছিক)", color = TextMuted, fontSize = 12.sp) },
                        label = { Text("মোবাইল নম্বর", color = TextMuted, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedContainerColor = DarkBackground,
                            unfocusedContainerColor = DarkBackground,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            cursorColor = PrimaryGreen
                        ),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Feedback Category Selector Pills
                    Text(
                        text = "বার্তার বিষয় নির্বাচন করুন:",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FeedbackCategory.entries.take(3).forEach { category ->
                            val isSelected = selectedCategory == category
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) EmeraldDeep else DarkBackground)
                                    .border(1.dp, if (isSelected) PrimaryGreen else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                    .clickable {
                                        HapticUtils.performLongPressHaptic(view)
                                        selectedCategory = category
                                    }
                                    .padding(vertical = 6.dp, horizontal = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = category.titleBn,
                                    color = if (isSelected) PrimaryGreen else TextMuted,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Message Text Area
                    OutlinedTextField(
                        value = feedbackMessage,
                        onValueChange = { feedbackMessage = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        placeholder = { Text("আপনার মূল্যবান মতামত বা বার্তা এখানে লিখুন...", color = TextMuted, fontSize = 12.sp) },
                        label = { Text("মতামত / পরামর্শ", color = TextMuted, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedContainerColor = DarkBackground,
                            unfocusedContainerColor = DarkBackground,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            cursorColor = PrimaryGreen
                        ),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 4
                    )

                    // Submit Action Buttons (WhatsApp / Email)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (feedbackMessage.isBlank()) {
                                    Toast.makeText(context, "দয়া করে আপনার বার্তা লিখুন", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                HapticUtils.performLongPressHaptic(view)
                                sendFeedbackViaEmail(
                                    context = context,
                                    mosqueEmail = mosque.officeEmail,
                                    name = senderName,
                                    phone = senderPhone,
                                    category = selectedCategory.titleBn,
                                    message = feedbackMessage
                                )
                                isSubmitted = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Outlined.Email, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ইমেইলে পাঠান", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                if (feedbackMessage.isBlank()) {
                                    Toast.makeText(context, "দয়া করে আপনার বার্তা লিখুন", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                HapticUtils.performLongPressHaptic(view)
                                shareFeedbackToApp(
                                    context = context,
                                    name = senderName,
                                    phone = senderPhone,
                                    category = selectedCategory.titleBn,
                                    message = feedbackMessage
                                )
                                isSubmitted = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Outlined.Share, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("বার্তা শেয়ার", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    if (isSubmitted) {
                        Text(
                            text = "✓ জাযাকাল্লাহু খাইরান! আপনার বার্তা প্রস্তুত করা হয়েছে।",
                            color = NeonGreenGlow,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Email and Web Channels Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "অনলাইন ও ডিজিটাল মাধ্যম",
                        color = CyanBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkBackground)
                            .clickable {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:${mosque.officeEmail}")
                                    putExtra(Intent.EXTRA_SUBJECT, "মসজিদ সংক্রান্ত যোগাযোগ")
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "ইমেইল অ্যাপ পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("অফিসিয়াল ইমেইল:", color = TextMuted, fontSize = 10.5.sp)
                            Text(mosque.officeEmail, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkBackground)
                            .clickable {
                                val url = if (!mosque.website.startsWith("http")) "https://${mosque.website}" else mosque.website
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                try {
                                    context.startActivity(browserIntent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "ব্রাউজার খোলা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = PurpleAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("অফিসিয়াল ওয়েবসাইট:", color = TextMuted, fontSize = 10.5.sp)
                            Text(mosque.website, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Phone Directory Card Item
 */
@Composable
private fun PhoneDirectoryCard(
    title: String,
    name: String,
    phone: String,
    badgeColor: Color,
    onCall: () -> Unit,
    onSms: () -> Unit,
    onCopy: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(1.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(12.dp)
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
                        .background(badgeColor.copy(alpha = 0.15f))
                        .border(0.8.dp, badgeColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 2.5.dp)
                ) {
                    Text(text = title, color = badgeColor, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onCall, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Call, contentDescription = "Call", tint = NeonGreenGlow, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onSms, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Outlined.Email, contentDescription = "SMS", tint = CyanBlue, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = name,
                color = TextWhite,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onCopy() }
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.PhoneAndroid, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = phone, color = CyanBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy", tint = TextMuted, modifier = Modifier.size(12.dp))
            }
        }
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
            "🕌 *${mosqueName}*\n📍 *ঠিকানা:* ${address}\n🌐 *ওয়েবসাইট:* https://${website}\n🗺️ *গুগল ম্যাপস:* ${mapLink}\n\n— চৌধুরী পাটোয়ারী বাড়ি জামে মসজিদ ও ইসলামিক সেন্টার"
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

private fun sendFeedbackViaEmail(
    context: Context,
    mosqueEmail: String,
    name: String,
    phone: String,
    category: String,
    message: String
) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$mosqueEmail")
            putExtra(Intent.EXTRA_SUBJECT, "মসজিদ মতামত ও পরামর্শ: $category")
            putExtra(
                Intent.EXTRA_TEXT,
                "প্রেরকের নাম: $name\nমোবাইল নম্বর: $phone\nক্যাটাগরি: $category\n\nমতামত / বার্তা:\n$message"
            )
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "ইমেইল অ্যাপ পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
    }
}

private fun shareFeedbackToApp(
    context: Context,
    name: String,
    phone: String,
    category: String,
    message: String
) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "🕌 *মসজিদে প্রেরিত বার্তা*\n\n👤 *নাম:* $name\n📱 *মোবাইল:* $phone\n📌 *ক্যাটাগরি:* $category\n\n💬 *বার্তা:* $message\n\n— বায়তুল আমান জামে মসজিদ ডিজিটাল মতামত বক্স"
        )
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "বার্তা শেয়ার করুন")
    context.startActivity(shareIntent)
}

private fun copyToClipboard(context: Context, text: String, toastMessage: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Contact Info", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
}
