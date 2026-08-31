package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DuaItem
import com.example.data.repository.MosqueRepository
import com.example.ui.theme.*
import com.example.util.HapticUtils

@Composable
fun DuaDhikrScreen(
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val view = LocalView.current

    var tasbeehCount by remember { mutableStateOf(0) }
    var tasbeehTarget by remember { mutableStateOf(33) }
    var selectedZikr by remember { mutableStateOf("سُبْحَانَ اللَّهِ (সুবহানাল্লাহ)") }

    val duas by MosqueRepository.duasFlow.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Digital Tasbeeh Section
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ডিজিটাল তসবিহ ও জিকির কাউন্টার",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                    Text(
                        text = selectedZikr,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = GoldAccent,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Large Tactile Circular Tap Button
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .drawBehind {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(PrimaryGreen.copy(alpha = 0.25f), Color.Transparent),
                                        radius = size.width * 0.75f
                                    )
                                )
                            }
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF132A1F), DarkBackground)
                                )
                            )
                            .border(2.5.dp, PrimaryGreen, CircleShape)
                            .clickable {
                                HapticUtils.performLongPressHaptic(view)
                                HapticUtils.performTactilePulse(context, 35)
                                tasbeehCount++
                                if (tasbeehCount % tasbeehTarget == 0) {
                                    HapticUtils.performQiblaLockPulse(context)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$tasbeehCount",
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Black,
                                color = TextWhite
                            )
                            Text(
                                text = "টার্গেট: $tasbeehTarget",
                                fontSize = 11.sp,
                                color = GoldAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tasbeeh Controls (Reset, Target Toggles)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                HapticUtils.performLongPressHaptic(view)
                                tasbeehCount = 0
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                        ) {
                            Icon(Icons.Outlined.Refresh, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("রিসেট", fontSize = 11.sp, color = TextMuted)
                        }

                        // Target Selector 33 / 100
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(33, 99, 100).forEach { target ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (tasbeehTarget == target) PrimaryGreen else DarkSurfaceElevated)
                                        .border(1.dp, if (tasbeehTarget == target) PrimaryGreen else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            HapticUtils.performLongPressHaptic(view)
                                            tasbeehTarget = target
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "$target",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (tasbeehTarget == target) DarkBackground else TextWhite
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Duas Header
        item {
            Text(
                text = "প্রয়োজনীয় দোয়া ও মোনাজাত",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Dua Cards
        items(duas) { dua ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dua.titleBn,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EmeraldDeep)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(dua.category.titleBn, fontSize = 9.sp, color = PrimaryGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = dua.arabicText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "উচ্চারণ: ${dua.pronunciationBn}",
                        fontSize = 12.sp,
                        color = PrimaryGreen.copy(alpha = 0.9f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "অর্থ: ${dua.meaningBn}",
                        fontSize = 12.sp,
                        color = TextMuted,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "সূত্র: ${dua.reference}",
                        fontSize = 10.sp,
                        color = TextSubtle
                    )
                }
            }
        }
    }
}
