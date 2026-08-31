package com.robiul.mosquetime.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robiul.mosquetime.data.repository.MosqueRepository
import com.robiul.mosquetime.ui.components.AppEmptyStateView
import com.robiul.mosquetime.ui.components.CommonHeader
import com.robiul.mosquetime.ui.theme.*
import com.robiul.mosquetime.util.HapticUtils

enum class FaqCategory(val titleBn: String) {
    ALL("সকল প্রশ্ন"),
    PRAYER("নামাজ ও ওয়াক্ত"),
    HUJUR_KHANA("হুজুরের খানা"),
    DONATION("অনুদান ও ফান্ড"),
    FACILITIES("মসজিদ সুবিধা"),
    EDUCATION("শিক্ষা ও মক্তব"),
    TECH("অ্যাপ ও অফলাইন")
}

data class FaqItem(
    val id: String,
    val question: String,
    val answer: String,
    val category: FaqCategory
)

@Composable
fun HelpFaqScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val mosque = MosqueRepository.mosqueInfo

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FaqCategory.ALL) }

    val allFaqs = remember {
        listOf(
            FaqItem(
                id = "faq1",
                question = "নামাজের সময়সূচি কি ইসলামিক ফাউন্ডেশনের সাথে সামঞ্জস্যপূর্ণ?",
                answer = "হ্যাঁ, এই অ্যাপে প্রদর্শিত সকল ওয়াক্ত ও আজানের সময়সূচি ইসলামিক ফাউন্ডেশন বাংলাদেশ কর্তৃক প্রণীত বাৎসরিক ক্যালেন্ডার এবং ঢাকা মানদণ্ড অনুযায়ী নির্ধারিত। এছাড়া সেটিংস থেকে বাংলাদেশের যেকোনো জেলার জন্য নির্দিষ্ট মিনিট অফসেট স্বয়ংক্রিয়ভাবে সমন্বয় করা যায়।",
                category = FaqCategory.PRAYER
            ),
            FaqItem(
                id = "faq2",
                question = "জুমার খুতবা ও বয়ান কখন শুরু হয়?",
                answer = "প্রতি শুক্রবার দুপুর ১২:৪৫ মিনিটে জুমার প্রথম আজান এবং ০১:০০ টায় সম্মানিত খতিব সাহেবের বয়ান শুরু হয়। পরবর্তীতে ০১:৩০ মিনিটে জুমার ফরজ জামাত অনুষ্ঠিত হয়।",
                category = FaqCategory.PRAYER
            ),
            FaqItem(
                id = "faq3",
                question = "হুজুরের খানা (ইমাম সাহেবের মেহমানদারি) কীভাবে পরিচালিত হয়?",
                answer = "মসজিদ পাড়ার বাসিন্দারা স্বেচ্ছায় পালাক্রমে প্রতিদিন সকাল, দুপুর ও রাতের খাবার পরিবেশন করে থাকেন। অ্যাপের 'হুজুরের খানা' মেন্যু থেকে আপনার বাড়ি কোন তারিখে নির্ধারিত তা দেখতে পারবেন এবং ক্যালেন্ডার রিমাইন্ডার সেট করতে পারবেন। নতুন বাড়ি অন্তর্ভুক্তির জন্য মসজিদ কার্যালয়ে যোগাযোগ করুন।",
                category = FaqCategory.HUJUR_KHANA
            ),
            FaqItem(
                id = "faq4",
                question = "অনলাইনে ডিজিটাল অনুদান দেওয়ার পদ্ধতি কী নিরাপদ?",
                answer = "সম্পূর্ণ নিরাপদ ও নির্ভরযোগ্য। অ্যাপে প্রদত্ত বিকাশ, নগদ, রকেট ও ব্যাংক একাউন্ট নম্বরসমূহ বায়তুল আমান জামে মসজিদ পরিচালনা কমিটির নামে নিবন্ধিত অফিসিয়াল একাউন্ট। অনুদান প্রদানের পর ডিজিটাল মানিরশিদ সংগ্রহ করে নিজের রেকর্ডে রাখা যায়।",
                category = FaqCategory.DONATION
            ),
            FaqItem(
                id = "faq5",
                question = "মহিলাদের কি মসজিদে আলাদা নামাজের ব্যবস্থা রয়েছে?",
                answer = "হ্যাঁ, আলহামদুলিল্লাহ! মসজিদের ৩য় তলায় পর্দানশীন মা-বোনদের জন্য সম্পূর্ণ পৃথক প্রবেশপথ, উন্নত অজুখানা, পরিচ্ছন্ন ওয়াশরুম ও আরামদায়ক সাউন্ড সিস্টেমসহ নামাজের সুব্যবস্থা রয়েছে।",
                category = FaqCategory.FACILITIES
            ),
            FaqItem(
                id = "faq6",
                question = "জরুরি মুহূর্তে লাশবাহী ফ্রিজিং অ্যাম্বুলেন্স সেবা কীভাবে পাবো?",
                answer = "মসজিদ কমিটির নিজস্ব সার্বক্ষণিক ফ্রিজিং লাশবাহী অ্যাম্বুলেন্স রয়েছে। যেকোনো জরুরি প্রয়োজনে অ্যাপের 'মসজিদ পরিচিতি' অথবা সরাসরি অফিস হটলাইনে (+৮৮০ ২-৯৮৭৬৫৪৩) যোগাযোগ করলেই সেবা পৌঁছে দেওয়া হবে।",
                category = FaqCategory.FACILITIES
            ),
            FaqItem(
                id = "faq7",
                question = "শিশুদের নূরানী মক্তবে কীভাবে ভর্তি করাবো?",
                answer = "প্রতিদিন ফজর নামাজের পর অথবা সকাল ৯:০০ টা থেকে দুপুর ১:০০ টার মধ্যে মসজিদ অফিসে সরাসরি যোগাযোগ করে ভর্তি ফরম সংগ্রহ করা যাবে। প্রতিদিন সকালে বিশুদ্ধ কুরআন তিলাওয়াত ও মাসয়ালা-মাসায়েল শিক্ষা দেওয়া হয়।",
                category = FaqCategory.EDUCATION
            ),
            FaqItem(
                id = "faq8",
                question = "অ্যাপ কি অফলাইনে বা ইন্টারনেট ছাড়া কাজ করে?",
                answer = "হ্যাঁ! একবার অ্যাপ ইনস্টল ও লোড হওয়ার পর দৈনিক ৫ ওয়াক্ত নামাজের সময়, কিবলা কম্পাস, ডিজিটাল তাসবীহ, পবিত্র কুরআনের ১১৪টি সূরা এবং সকল দোয়া ইন্টারনেট সংযোগ ছাড়াই শতভাগ নির্ভুলভাবে ব্যবহার করা যায়।",
                category = FaqCategory.TECH
            ),
            FaqItem(
                id = "faq9",
                question = "কিবলা কম্পাস কি সব ফোনে সঠিকভাবে কাজ করে?",
                answer = "কিবলা কম্পাস ব্যবহারের পূর্বে ফোনটিকে ইংরেজি ৪/৮ (Infinity) আকারে ঘুরিয়ে ম্যাগনেটিক সেন্সর ক্যালিব্রেট করে নিন এবং ধাতব বস্তু থেকে ফোন দূরে রাখুন। এতে সুনির্দিষ্টভাবে কাবার দিক নির্দেশিত হবে।",
                category = FaqCategory.TECH
            )
        )
    }

    val filteredFaqs = remember(searchQuery, selectedCategory, allFaqs) {
        allFaqs.filter { faq ->
            val matchesCategory = selectedCategory == FaqCategory.ALL || faq.category == selectedCategory
            val matchesQuery = searchQuery.isBlank() ||
                    faq.question.contains(searchQuery, ignoreCase = true) ||
                    faq.answer.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("help_faq_screen"),
        containerColor = DarkBackground,
        topBar = {
            CommonHeader(
                title = "সহায়তা ও প্রশ্নোত্তর",
                subtitle = "সচরাচর জিজ্ঞাসিত প্রশ্ন ও সমাধান (FAQ)",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("প্রশ্ন বা উত্তর খুঁজুন...", color = TextMuted, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = DarkBackground,
                        unfocusedContainerColor = DarkBackground,
                        cursorColor = PrimaryGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("faq_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Filter Pills with Badges
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(FaqCategory.entries.toTypedArray()) { cat ->
                        val isSelected = selectedCategory == cat
                        val count = if (cat == FaqCategory.ALL) allFaqs.size
                        else allFaqs.count { it.category == cat }

                        val bg = if (isSelected) EmeraldDeep else DarkSurfaceElevated
                        val border = if (isSelected) PrimaryGreen else DarkSurfaceBorder
                        val textColor = if (isSelected) PrimaryGreen else TextMuted

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(bg)
                                .border(1.dp, border, RoundedCornerShape(16.dp))
                                .clickable {
                                    HapticUtils.performLongPressHaptic(view)
                                    selectedCategory = cat
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = cat.titleBn,
                                    color = textColor,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (isSelected) PrimaryGreen else DarkSurfaceBorder)
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "$count",
                                        color = if (isSelected) DarkBackground else TextWhite,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // FAQ Items List
            if (filteredFaqs.isEmpty()) {
                AppEmptyStateView(
                    icon = Icons.Outlined.SearchOff,
                    title = "কোনো প্রশ্নোত্তর পাওয়া যায়নি",
                    subtitle = if (searchQuery.isNotBlank()) "'$searchQuery' সম্পর্কিত কোনো প্রশ্ন পাওয়া যায়নি।" else "এই ক্যাটাগরিতে বর্তমানে কোনো প্রশ্ন অন্তর্ভুক্ত নেই।",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
                ) {
                    item {
                        // Quick Knowledge Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF1B3828), DarkSurfaceElevated)
                                    )
                                )
                                .border(1.dp, PrimaryGreen.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("প্রয়োজনীয় তথ্যের প্রশ্নোত্তর", color = TextWhite, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                                    Text("যে প্রশ্নের উত্তর জানতে চান তার উপর স্পর্শ করুন", color = TextMuted, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    items(filteredFaqs, key = { it.id }) { faq ->
                        FaqAccordionCard(
                            faq = faq,
                            onCopy = {
                                copyFaqToClipboard(context, faq)
                            },
                            onShare = {
                                shareFaq(context, faq)
                            }
                        )
                    }

                    item {
                        // Support Helpdesk Footer Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(DarkSurface)
                                .border(1.dp, DarkGreenBorder.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "আপনার প্রশ্নের উত্তর পাননি?",
                                    color = GoldAccent,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "মসজিদ অফিস ও পরিচালনা পরিষদকে সরাসরি প্রশ্ন করতে পারেন:",
                                    color = TextMuted,
                                    fontSize = 11.5.sp
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            HapticUtils.performLongPressHaptic(view)
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${mosque.officePhone}"))
                                            try {
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "কল করা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("হটলাইনে কল", color = DarkBackground, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            HapticUtils.performLongPressHaptic(view)
                                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                                data = Uri.parse("mailto:${mosque.officeEmail}")
                                                putExtra(Intent.EXTRA_SUBJECT, "মসজিদ সংক্রান্ত জিজ্ঞাসা")
                                            }
                                            try {
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "ইমেইল অ্যাপ পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Outlined.Email, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("ইমেইল করুন", color = DarkBackground, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FaqAccordionCard(
    faq: FaqItem,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    val view = LocalView.current
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurface)
            .border(
                1.dp,
                if (isExpanded) PrimaryGreen.copy(alpha = 0.8f) else DarkGreenBorder.copy(alpha = 0.4f),
                RoundedCornerShape(14.dp)
            )
            .clickable {
                HapticUtils.performLongPressHaptic(view)
                isExpanded = !isExpanded
            }
            .padding(14.dp)
            .testTag("faq_card_${faq.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    color = if (isExpanded) NeonGreenGlow else TextWhite,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    lineHeight = 19.sp
                )

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (isExpanded) PrimaryGreen else TextMuted,
                    modifier = Modifier.size(22.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        thickness = 0.5.dp,
                        color = DarkSurfaceBorder
                    )
                    Text(
                        text = faq.answer,
                        color = TextWhite.copy(alpha = 0.9f),
                        fontSize = 12.5.sp,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onCopy, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(onClick = onShare, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.Outlined.Share, contentDescription = "Share", tint = CyanBlue, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun copyFaqToClipboard(context: Context, faq: FaqItem) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val text = "❓ প্রশ্ন: ${faq.question}\n\n💡 উত্তর: ${faq.answer}\n\n— বায়তুল আমান জামে মসজিদ"
    val clip = ClipData.newPlainText("FAQ Item", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "উত্তর কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
}

private fun shareFaq(context: Context, faq: FaqItem) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "🕌 *মসজিদ সংক্রান্ত সাধারণ জিজ্ঞাসা (FAQ)*\n\n❓ *প্রশ্ন:* ${faq.question}\n\n💡 *উত্তর:* ${faq.answer}\n\n— চৌধুরী পাটোয়ারী বাড়ি জামে মসজিদ ও ইসলামিক সেন্টার"
        )
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "প্রশ্নোত্তর শেয়ার করুন")
    context.startActivity(shareIntent)
}
