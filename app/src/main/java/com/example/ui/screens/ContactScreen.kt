package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.MosqueRepository
import com.example.ui.components.CommonHeader
import com.example.ui.components.MosqueCrestIcon
import com.example.ui.theme.CyanBlue
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.DarkGreenBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun ContactScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mosque = MosqueRepository.mosqueInfo
    val scrollState = rememberScrollState()

    val safeLaunchIntent: (Intent, String) -> Unit = { intent, fallbackErrorMsg ->
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, fallbackErrorMsg, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonHeader(
            title = "যোগাযোগ ও অবস্থান",
            subtitle = "মসজিদ কার্যালয় ও দিকনির্দেশনা",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            // Mosque Address Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurface)
                    .border(1.dp, PrimaryGreen.copy(alpha = 0.8f), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "মসজিদের ঠিকানা",
                                color = PrimaryGreen,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Copy Address Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkBackground)
                                .border(1.dp, CyanBlue.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                .clickable {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Mosque Address", "${mosque.nameBn}\n${mosque.address}")
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "ঠিকানা কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(14.dp))
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
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = mosque.address,
                        color = TextWhite.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Open in Google Maps Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(PrimaryGreen)
                            .clickable {
                                val mapUri = Uri.parse("geo:23.8067,90.3686?q=${Uri.encode("${mosque.nameBn} ${mosque.address}")}")
                                val intent = Intent(Intent.ACTION_VIEW, mapUri)
                                safeLaunchIntent(intent, "ম্যাপ অ্যাপ পাওয়া যায়নি")
                            }
                            .padding(vertical = 11.dp)
                            .testTag("open_maps_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Navigation, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("গুগল ম্যাপে লোকেশন দেখুন", color = DarkBackground, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Direct Phone Contacts
            Text("জরুরি যোগাযোগ নম্বরসমূহ", color = GoldAccent, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ContactPhoneRow(
                        title = "মসজিদ অফিস ও পরিচালনা পরিষদ",
                        subtitle = "নিত্যদিনের তথ্য ও অনুদান সংক্রান্ত",
                        phoneNumber = mosque.officePhone,
                        onCall = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${mosque.officePhone}"))
                            safeLaunchIntent(intent, "ডায়াল করা সম্ভব হয়নি")
                        }
                    )

                    HorizontalDivider(thickness = 0.4.dp, color = DarkSurfaceBorder)

                    ContactPhoneRow(
                        title = "খতিব ও প্রধান ইমাম সাহেব",
                        subtitle = mosque.imamName,
                        phoneNumber = mosque.imamPhone,
                        onCall = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${mosque.imamPhone}"))
                            safeLaunchIntent(intent, "ডায়াল করা সম্ভব হয়নি")
                        }
                    )

                    HorizontalDivider(thickness = 0.4.dp, color = DarkSurfaceBorder)

                    ContactPhoneRow(
                        title = "মুয়াজ্জিন সাহেব (আজান ও জামাত তথ্য)",
                        subtitle = mosque.muazzinName,
                        phoneNumber = mosque.muazzinPhone,
                        onCall = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${mosque.muazzinPhone}"))
                            safeLaunchIntent(intent, "ডায়াল করা সম্ভব হয়নি")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Email and Web
            Text("ইমেইল ও অনলাইন মাধ্যম", color = CyanBlue, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:${mosque.officeEmail}")
                                    putExtra(Intent.EXTRA_SUBJECT, "Query regarding Baitul Aman Jame Masjid")
                                }
                                safeLaunchIntent(intent, "ইমেইল অ্যাপ পাওয়া যায়নি")
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("ইমেইল করুন:", color = TextMuted, fontSize = 11.sp)
                            Text(mosque.officeEmail, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    HorizontalDivider(thickness = 0.4.dp, color = DarkSurfaceBorder)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(if (mosque.website.startsWith("http")) mosque.website else "https://${mosque.website}"))
                                safeLaunchIntent(browserIntent, "ব্রাউজার খোলা সম্ভব হয়নি")
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = PurpleAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("অফিসিয়াল ওয়েবসাইট:", color = TextMuted, fontSize = 11.sp)
                            Text(mosque.website, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Share App / Location Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkGreen.copy(alpha = 0.4f))
                    .border(1.dp, PrimaryGreen.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                    .clickable {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "🕌 ${mosque.nameBn}\n📍 ${mosque.address}\n📞 ${mosque.officePhone}\n🌐 ${mosque.website}\n\n- বায়তুল আমান জামে মসজিদ অফিসিয়াল অ্যাপ")
                            type = "text/plain"
                        }
                        safeLaunchIntent(Intent.createChooser(shareIntent, "মসজিদের তথ্য শেয়ার করুন"), "শেয়ার করা সম্ভব হয়নি")
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("মসজিদের তথ্য বন্ধুদের সাথে শেয়ার করুন", color = PrimaryGreen, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ContactPhoneRow(
    title: String,
    subtitle: String,
    phoneNumber: String,
    onCall: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextWhite, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = TextMuted, fontSize = 11.5.sp)
            Text(text = phoneNumber, color = CyanBlue, fontSize = 12.5.sp, fontWeight = FontWeight.Medium)
        }

        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(DarkBackground)
                .border(1.dp, PrimaryGreen.copy(alpha = 0.7f), CircleShape)
                .clickable { onCall() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Call, contentDescription = "Call", tint = PrimaryGreen, modifier = Modifier.size(18.dp))
        }
    }
}
