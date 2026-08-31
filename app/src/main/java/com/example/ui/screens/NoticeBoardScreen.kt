package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NoticeItem
import com.example.ui.theme.*

@Composable
fun NoticeBoardScreen(
    modifier: Modifier = Modifier
) {
    val notices = remember {
        listOf(
            NoticeItem(
                id = "1",
                title = "পবিত্র জুমার বয়ান ও জামাতের সময়সূচি",
                description = "আগামী শুক্রবার চৌধুরী পাটোয়ারী বাড়ি জামে মসজিদে বাদ জুমা বিশেষ তাফসীর মাহফিল অনুষ্ঠিত হবে। প্রধান আলোচক হিসেবে উপস্থিত থাকবেন মসজিদের খতিব সাহেব।",
                dateBn = "১৫ ভাদ্র, ১৪৩১",
                category = "জুমা নোটিশ",
                isPinned = true
            ),
            NoticeItem(
                id = "2",
                title = "মসজিদ উন্নয়ন ও সংস্কার ফান্ডে অনুদান আহ্বান",
                description = "মসজিদের ২য় তলার টাইলস ও সাউন্ড সিস্টেম আধুনিকীকরণের কাজ চলমান। আগ্রহী মুসল্লিবৃন্দ মসজিদ ফান্ডে সরাসরি বা কমিটির সাথে যোগাযোগ করে অনুদান দিতে পারেন।",
                dateBn = "১২ ভাদ্র, ১৪৩১",
                category = "উন্নয়ন নোটিশ",
                isPinned = false
            ),
            NoticeItem(
                id = "3",
                title = "সাপ্তাহিক তা'লীম ও শিশুদের কোরআন শিক্ষা",
                description = "প্রতিদিন বাদ ফজর এবং বাদ আসর মসজিদে শিশুদের জন্য বিশুদ্ধ কোরআন তিলাওয়াত ও কায়দা শিক্ষার নতুন ব্যাচ শুরু হয়েছে।",
                dateBn = "১০ ভাদ্র, ১৪৩১",
                category = "শিক্ষা",
                isPinned = false
            )
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Campaign,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "মসজিদের নোটিশ বোর্ড ও ঘোষণা",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
            }
        }

        items(notices) { notice ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (notice.isPinned) GoldAccent.copy(alpha = 0.5f) else DarkSurfaceBorder
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (notice.isPinned) {
                                Icon(
                                    imageVector = Icons.Outlined.PushPin,
                                    contentDescription = "Pinned",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = notice.category,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (notice.isPinned) GoldAccent else PrimaryGreen
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Event,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = notice.dateBn,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = notice.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = notice.description,
                        fontSize = 12.sp,
                        color = TextMuted,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
