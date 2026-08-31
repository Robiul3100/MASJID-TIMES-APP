package com.robiul.mosquetime.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
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
import com.robiul.mosquetime.ui.theme.*
import com.robiul.mosquetime.util.HapticUtils

/**
 * Social link data model
 */
data class SocialLink(
    val icon: ImageVector,
    val label: String,
    val url: String,
    val brandColor: Color
)

/**
 * Tutorial Item data model
 */
data class TutorialItem(
    val id: String,
    val title: String,
    val category: String,
    val iconRes: ImageVector,
    val steps: List<String>,
    val timeAgo: String = "টিউটোরিয়াল • হালনাগাদ করা হয়েছে"
)

/**
 * Full Developer Profile Screen with Bio, Social Media Channels, and Facebook Post-Style Tutorial Cards.
 */
@Composable
fun DeveloperProfileScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current

    // Social Media Links
    val socialLinks = remember {
        listOf(
            SocialLink(
                icon = Icons.Default.Public,
                label = "Facebook",
                url = "https://www.facebook.com/RSF.ROBIUL",
                brandColor = Color(0xFF1877F2)
            ),
            SocialLink(
                icon = Icons.Default.Phone,
                label = "WhatsApp",
                url = "https://wa.me/+8801840036984",
                brandColor = Color(0xFF25D366)
            ),
            SocialLink(
                icon = Icons.Default.Email,
                label = "Gmail",
                url = "mailto:rsf.robiul@gmail.com",
                brandColor = Color(0xFFEA4335)
            ),
            SocialLink(
                icon = Icons.AutoMirrored.Filled.Send,
                label = "Telegram",
                url = "https://t.me/RSF_ROBIUL",
                brandColor = Color(0xFF0088CC)
            )
        )
    }

    // App Tutorials List
    val tutorials = remember {
        listOf(
            TutorialItem(
                id = "tut_home",
                title = "হোমস্ক্রিন ও লাইভ ওয়াক্ত ব্যবহার",
                category = "মূল ইন্টারফেস",
                iconRes = Icons.Outlined.Home,
                steps = listOf(
                    "অ্যাপ ওপেন করলেই হোমস্ক্রিনে বর্তমান চলমান নামাজের ওয়াক্ত ও পরবর্তী আজানের কাউন্টডাউন দেখতে পাবেন।",
                    "হিজরি ও বাংলা তারিখ স্বয়ংক্রিয়ভাবে ক্যালকুলেট হয়ে ব্যানারের উপরে প্রদর্শিত হয়।",
                    "হোমস্ক্রিনের কুইক গ্রিড থেকে সরাসরি এক ট্যাপে কিবলা, দোয়া, তসবিহ বা খানা সূচিতে যেতে পারবেন।"
                )
            ),
            TutorialItem(
                id = "tut_prayer",
                title = "নামাজের সময়সূচি ও অ্যালার্ম নোটিফিকেশন",
                category = "নামাজ ও ওয়াক্ত",
                iconRes = Icons.Outlined.CalendarMonth,
                steps = listOf(
                    "নিচের নেভিগেশন বার থেকে 'সময়সূচি' ট্যাবে চাপ দিন।",
                    "ফজর, যোহর, আসর, মাগরিব, ইশা এবং তাহাজ্জুদ ও ইশরাক নামাজের শুরু ও শেষ সময় দেখতে পাবেন।",
                    "প্রতিটি ওয়াক্তের ডানপাশের অ্যালার্ম আইকন স্পর্শ করে আজান নোটিফিকেশন চালু বা বন্ধ করতে পারেন।"
                )
            ),
            TutorialItem(
                id = "tut_monthly",
                title = "মাসিক নামাজের সময়সূচি চার্ট দেখা",
                category = "ক্যালেন্ডার",
                iconRes = Icons.Outlined.DateRange,
                steps = listOf(
                    "সাইড মেন্যু বা সময়সূচি স্ক্রিন থেকে 'মাসিক সময়সূচি' নির্বাচন করুন।",
                    "সম্পূর্ণ ৩০ দিনের নামাজের স্থায়ী ক্যালেন্ডার তারিখ অনুযায়ী তালিকাভুক্ত পাবেন।",
                    "ইন্টারনেট সংযোগ ছাড়াও পুরো মাসের ডেটা অফলাইনে ব্রাউজ করা যায়।"
                )
            ),
            TutorialItem(
                id = "tut_hijri",
                title = "আরবি ও হিজরি ক্যালেন্ডার",
                category = "ইসলামিক সন",
                iconRes = Icons.Outlined.Event,
                steps = listOf(
                    "সাইড ড্রয়ার থেকে 'ইসলামিক ক্যালেন্ডার' স্ক্রিনে যান।",
                    "আশুরা, শবে ক্বদর, ঈদুল ফিতরসহ সারা বছরের গুরুত্বপূর্ণ ইসলামিক দিবসের তালিকা দেখুন।",
                    "চাঁদ দেখা অনুযায়ী প্রয়োজনে [+১ দিন বা -১ দিন] অফসেট সমন্বয় করতে পারেন।"
                )
            ),
            TutorialItem(
                id = "tut_qibla",
                title = "কিবলার দিক ও কাবার সঠিক দিক বের করা",
                category = "সেন্সর ও কম্পাস",
                iconRes = Icons.Outlined.Explore,
                steps = listOf(
                    "সাইড মেন্যু থেকে 'কিবলা কম্পাস' স্ক্রিনটি চালু করুন।",
                    "আপনার ফোনটিকে অনুভূমিকভাবে (ফ্ল্যাট) হাতের তালুতে রাখুন এবং ইংরেজি '8' আকারে বাতাসে ঘোরান।",
                    "কম্পাসের সবুজ তীর চিহ্ন সরাসরি মক্কার কাবা শরীফের দিক নির্দেশ করবে।"
                )
            ),
            TutorialItem(
                id = "tut_khana",
                title = "হুজুরের খানা সূচি ও মেহমানদারি",
                category = "পাড়া ও মসজিদ",
                iconRes = Icons.Outlined.Restaurant,
                steps = listOf(
                    "বটম নেভিগেশন বার থেকে 'খানা সূচি' ট্যাবে চাপ দিন।",
                    "আজ ও আগামী দিনগুলোতে কোন পরিবারে ইমাম সাহেবের খানা নির্ধারিত রয়েছে তা দেখতে পাবেন।",
                    "সংশ্লিষ্ট পরিবারের সাথে সরাসরি ১-ট্যাপে ফোন কলে যোগাযোগ করা যায়।"
                )
            ),
            TutorialItem(
                id = "tut_tasbih",
                title = "ডিজিটাল তাসবীহ ও যিকির ট্র্যাকার",
                category = "আমল ও যিকির",
                iconRes = Icons.Outlined.TouchApp,
                steps = listOf(
                    "ড্রয়ার মেন্যু থেকে 'ডিজিটাল তসবিহ' স্ক্রিনে প্রবেশ করুন।",
                    "পর্দায় বড় ট্যাপিং বোতামে চাপ দিয়ে সুবহানাল্লাহ, আলহামদুলিল্লাহ পাঠ গণনা করুন।",
                    "প্রতি ক্লিকে ভাইব্রেশন ও সাউন্ড ফিডব্যাক পাওয়া যাবে এবং রিসেট করা যাবে।"
                )
            ),
            TutorialItem(
                id = "tut_donation",
                title = "মসজিদে ডিজিটাল অনুদান ও ফান্ড",
                category = "অনুদান ও সেবা",
                iconRes = Icons.Outlined.VolunteerActivism,
                steps = listOf(
                    "সাইড মেন্যু থেকে 'অনুদান ও ফান্ড' সেকশনে যান।",
                    "মসজিদের অফিশিয়াল বিকাশ ও নগদ মার্চেন্ট নম্বরে অনুদান পাঠান।",
                    "অনুদানের পর ডিজিটাল মানিরশিদ সংগ্রহ করে নিজের কাছে সংরক্ষণ করুন।"
                )
            ),
            TutorialItem(
                id = "tut_settings",
                title = "জেলা নির্বাচন ও সেটিংস সমন্বয়",
                category = "অ্যাপ সেটিংস",
                iconRes = Icons.Outlined.Settings,
                steps = listOf(
                    "নিচের নেভিগেশন থেকে 'সেটিংস' অপশনে চাপ দিন।",
                    "আপনার নিজ জেলা (যেমন চট্টগ্রাম, সিলেট, রাজশাহী ইত্যাদি) নির্বাচন করুন।",
                    "অ্যাপ স্বয়ংক্রিয়ভাবে আপনার জেলার সঠিক ওয়াক্তের মিনিট সমন্বয় করে নেবে।"
                )
            )
        )
    }

    var expandedTutorialId by remember { mutableStateOf<String?>("tut_home") }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("developer_profile_screen"),
        containerColor = DarkBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(DarkBackground)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        HapticUtils.performLongPressHaptic(view)
                        onBackClick()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "ডেভেলপার পরিচিতি ও টিউটোরিয়াল",
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "অ্যাপ ব্যবহার নির্দেশিকা ও যোগাযোগ",
                        color = PrimaryGreen,
                        fontSize = 11.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
        ) {
            // -------------------------------------------------------------
            // 1. HEADER SECTION: Developer Profile & Bio
            // -------------------------------------------------------------
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF1B2C21),
                                    Color(0xFF121D16)
                                )
                            )
                        )
                        .border(1.2.dp, PrimaryGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Big Circular Avatar with Glow Border
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .shadow(16.dp, CircleShape, ambientColor = PrimaryGreen, spotColor = PrimaryGreen)
                                .drawBehind {
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            listOf(PrimaryGreen.copy(alpha = 0.4f), Color.Transparent),
                                            radius = size.width * 0.85f
                                        )
                                    )
                                }
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(EmeraldDeep, Color(0xFF0F3E26))
                                    )
                                )
                                .border(2.5.dp, PrimaryGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Developer Photo",
                                tint = NeonGreenGlow,
                                modifier = Modifier.size(52.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Developer Name
                        Text(
                            text = "RSF ROBIUL",
                            color = TextWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        // Tagline & Bio
                        Text(
                            text = "App Developer | Bangladesh",
                            color = GoldAccent,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "ইসলামিক প্রযুক্তি ও আধুনিক মোবাইল অ্যাপ্লিকেশন তৈরির মাধ্যমে মুসলিম উম্মাহর সেবায় নিবেদিত।",
                            color = TextMuted,
                            fontSize = 11.5.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 17.sp,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }

            // -------------------------------------------------------------
            // 2. SOCIAL MEDIA SECTION: 4 Brand Circular Action Buttons
            // -------------------------------------------------------------
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "যোগাযোগ ও সোশ্যাল প্রোফাইল",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        socialLinks.forEach { link ->
                            SocialMediaButton(
                                link = link,
                                onClick = {
                                    HapticUtils.performLongPressHaptic(view)
                                    openUrlIntent(context, link.url)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // 3. APP TUTORIAL SECTION: Facebook Post Style Cards
            // -------------------------------------------------------------
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "অ্যাপ ব্যবহারের নিয়মাবলী ও টিউটোরিয়াল",
                            color = TextWhite,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "সহজ বাংলায় প্রতিটি ফিচারের বিস্তারিত গাইড",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Facebook Post Style Tutorial Cards
            items(tutorials, key = { it.id }) { tutorial ->
                val isExpanded = expandedTutorialId == tutorial.id

                TutorialCard(
                    item = tutorial,
                    isExpanded = isExpanded,
                    onToggle = {
                        HapticUtils.performLongPressHaptic(view)
                        expandedTutorialId = if (isExpanded) null else tutorial.id
                    }
                )
            }
        }
    }
}

/**
 * Circular Social Media Button with Brand Colors
 */
@Composable
private fun SocialMediaButton(
    link: SocialLink,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(Color(0xFF141D17))
                .border(1.2.dp, link.brandColor.copy(alpha = 0.7f), CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, color = link.brandColor),
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = link.icon,
                contentDescription = link.label,
                tint = link.brandColor,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = link.label,
            color = TextWhite,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Facebook Post-Style Tutorial Card with Expandable Content & Social Action Row
 */
@Composable
fun TutorialCard(
    item: TutorialItem,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isHelpful by remember { mutableStateOf<Boolean?>(null) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF162219)) // Facebook post card style slightly lighter dark surface
            .border(
                1.dp,
                if (isExpanded) PrimaryGreen.copy(alpha = 0.65f) else DarkGreenBorder.copy(alpha = 0.4f),
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onToggle)
            .padding(14.dp)
            .testTag("tutorial_card_${item.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Post Header (Avatar + Title + Time / Subtitle)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Post Avatar / Badge Icon
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(EmeraldDeep)
                        .border(1.dp, PrimaryGreen.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.iconRes,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        color = if (isExpanded) NeonGreenGlow else TextWhite,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${item.category} • ${item.timeAgo}",
                        color = TextMuted,
                        fontSize = 10.5.sp
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (isExpanded) PrimaryGreen else TextMuted,
                    modifier = Modifier.size(22.dp)
                )
            }

            // 2. Expandable Step-by-Step Instructions Content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    item.steps.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Step Number Badge (১, ২, ৩...)
                            val bengaliNumber = toBengaliDigits(index + 1)
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryGreen.copy(alpha = 0.18f))
                                    .border(0.8.dp, PrimaryGreen, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = bengaliNumber,
                                    color = PrimaryGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = step,
                                color = TextWhite.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Facebook Post-Style Thin Divider
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = DarkSurfaceBorder
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // 3. Post Reaction / Action Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Helpful feedback button
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    isHelpful = true
                                    Toast.makeText(context, "ধন্যবাদ আপনার মতামতের জন্য!", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = "Helpful",
                                tint = if (isHelpful == true) PrimaryGreen else TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isHelpful == true) "সহায়ক হয়েছে ✓" else "সহায়ক ছিল?",
                                color = if (isHelpful == true) PrimaryGreen else TextMuted,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Share button
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "📖 *${item.title}*\n\n${item.steps.joinToString("\n\n")}\n\n— মসজিদ টাইমস অ্যাপ গাইড"
                                        )
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "টিউটোরিয়াল শেয়ার করুন"))
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = CyanBlue,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "শেয়ার করুন",
                                color = CyanBlue,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Helper to open Web, WhatsApp, Mail or Telegram URLs
 */
private fun openUrlIntent(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "লিংকটি ওপেন করা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Bengali digits converter
 */
private fun toBengaliDigits(number: Int): String {
    val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    return number.toString().map { if (it.isDigit()) bnDigits[it - '0'] else it }.joinToString("")
}
