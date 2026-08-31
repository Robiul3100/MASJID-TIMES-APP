package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CommonHeader
import com.example.ui.theme.CyanBlue
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.DarkGreenBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

data class FaqItem(
    val question: String,
    val answer: String,
    val category: String
)

@Composable
fun HelpFaqScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val faqs = listOf(
        FaqItem(
            question = "নামাজের সময়সূচি কি ইসলামিক ফাউন্ডেশনের সাথে সামঞ্জস্যপূর্ণ?",
            answer = "হ্যাঁ, এই অ্যাপে প্রদর্শিত নামাজের ওয়াক্ত ও আজানের সময়সূচি ইসলামিক ফাউন্ডেশন বাংলাদেশ কর্তৃক প্রণীত বাৎসরিক ক্যালেন্ডার এবং ঢাকা মানদণ্ড অনুযায়ী নির্ধারিত। এছাড়া সেটিংস থেকে বাংলাদেশের যেকোনো জেলার সময় পার্থক্য স্বয়ংক্রিয়ভাবে সমন্বয় করা যায়।",
            category = "নামাজ"
        ),
        FaqItem(
            question = "জুমার খুতবা ও বয়ান কখন শুরু হয়?",
            answer = "প্রতি শুক্রবার দুপুর ১২:৪৫ মিনিটে জুমার প্রথম আজান এবং ০১:০০ টায় সম্মানিত খতিব সাহেবের বয়ান শুরু হয়। পরবর্তীতে ০১:৩০ মিনিটে জুমার ফরজ জামাত অনুষ্ঠিত হয়।",
            category = "নামাজ"
        ),
        FaqItem(
            question = "অনলাইনে অনুদান দেওয়ার পদ্ধতি কী নিরাপদ?",
            answer = "সম্পূর্ণ নিরাপদ। অ্যাপে প্রদত্ত বিকাশ, নগদ, রকেট ও ব্যাংক একাউন্ট নম্বরসমূহ বায়তুল আমান জামে মসজিদ পরিচালনা কমিটির নামে নিবন্ধিত অফিসিয়াল একাউন্ট। অনুদান প্রদানের পর আপনি অ্যাপ থেকে ডিজিটাল রশিদ সংগ্রহ করতে পারেন।",
            category = "অনুদান"
        ),
        FaqItem(
            question = "মহিলাদের কি মসজিদে আলাদা নামাজের ব্যবস্থা রয়েছে?",
            answer = "হ্যাঁ, মসজিদের ৩য় তলায় পর্দানশীন মা-বোনদের জন্য সম্পূর্ণ পৃথক প্রবেশপথ, উন্নত অজুখানা ও আরামদায়ক নামাজের সুব্যবস্থা রয়েছে।",
            category = "মসজিদ"
        ),
        FaqItem(
            question = "শিশুদের নূরানী মক্তবে কীভাবে ভর্তি করাবো?",
            answer = "প্রতিদিন ফজর নামাজের পর অথবা সকাল ৯:০০ টা থেকে দুপুর ১:০০ টার মধ্যে মসজিদ অফিসে সরাসরি যোগাযোগ করে ভর্তি ফরম সংগ্রহ করা যাবে।",
            category = "শিক্ষা"
        ),
        FaqItem(
            question = "অ্যাপ কি অফলাইনে বা ইন্টারনেট ছাড়া কাজ করে?",
            answer = "হ্যাঁ, একবার লোড হওয়ার পর দৈনিক ও মাসিক নামাজের সময়সূচি, কিবলা কম্পাস, দোয়া ও কুরআন অফলাইনে সম্পূর্ণ কার্যকর থাকে।",
            category = "প্রযুক্তি"
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonHeader(
            title = "সাহায্য ও সাধারণ জিজ্ঞাসা",
            subtitle = "সচরাচর জিজ্ঞাসিত প্রশ্ন ও উত্তর (FAQ)",
            onBackClick = onBackClick
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(DarkSurfaceElevated, DarkSurface)
                            )
                        )
                        .border(1.dp, PrimaryGreen.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("প্রয়োজনীয় তথ্যের প্রশ্নোত্তর", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("কোনো প্রশ্ন থাকলে নিচে দেওয়া উত্তরে জেনে নিন", color = TextMuted, fontSize = 11.5.sp)
                        }
                    }
                }
            }

            items(faqs) { faq ->
                FaqAccordionCard(faq = faq)
            }

            item {
                // Support Footer Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface)
                        .border(1.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("অন্য কোনো সহায়তা প্রয়োজন?", color = GoldAccent, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("মসজিদ কার্যালয়ে সরাসরি যোগাযোগ করুন: +৮৮০ ২-৯৮৭৬৫৪৩", color = TextWhite, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("ইমেইল: support@baitulamanmasjid.org", color = CyanBlue, fontSize = 11.5.sp)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun FaqAccordionCard(faq: FaqItem) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(
                1.dp,
                if (isExpanded) PrimaryGreen.copy(alpha = 0.7f) else DarkGreenBorder.copy(alpha = 0.4f),
                RoundedCornerShape(12.dp)
            )
            .clickable { isExpanded = !isExpanded }
            .padding(14.dp)
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
                    modifier = Modifier.weight(1f)
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
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = DarkSurfaceBorder)
                    Text(
                        text = faq.answer,
                        color = TextWhite.copy(alpha = 0.85f),
                        fontSize = 12.5.sp,
                        lineHeight = 19.sp
                    )
                }
            }
        }
    }
}
