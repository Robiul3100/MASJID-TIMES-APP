package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.RedDigital
import com.example.ui.theme.SolaimanLipiFontFamily

/**
 * Fixed Picture Frame Architecture for Bangladeshi Mosque Hero Display:
 * - Step 1: Fixed-ratio wrapper container (1500:1000 = 1.5 aspect ratio).
 * - Step 2: Base Frame Layer (ic_mosque_hero_frame) with 100% width/height and ContentScale.Fit.
 * - Step 3: Pure percentage-based dynamic overlay layer positioned inside the open cutout:
 *           Horizontal: 15% to 85% (Width 70%)
 *           Vertical: 56% to 98% (Height 42%)
 * - Step 4: Fully responsive across all display widths.
 */
@Composable
fun MosqueHeroSection(
    timeString: String,
    dayOfMonth: String,
    monthOfYear: String,
    yearStr: String,
    activeDayIndex: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Step 1: Fixed-Ratio Wrapper Container (1500 x 1000 => 1.5 aspect ratio)
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.5f)
        ) {
            val totalWidth = maxWidth
            val totalHeight = maxHeight

            // Step 2: Base Frame Layer (Exact Arch Frame with Calligraphy, Minarets, Flowers)
            Image(
                painter = painterResource(id = R.drawable.ic_mosque_hero_frame),
                contentDescription = "Mosque Arch Frame",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            // Step 3: Pure Percentage-Based Content Overlay Container
            // Coordinates strictly locked to inner cutout:
            // Left: 15%, Top: 55%, Width: 70%, Height: 43%
            Box(
                modifier = Modifier
                    .offset(
                        x = totalWidth * 0.15f,
                        y = totalHeight * 0.55f
                    )
                    .size(
                        width = totalWidth * 0.70f,
                        height = totalHeight * 0.43f
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    // 1. Digital Clock Display (HH:MM:SS)
                    HeroDigitalLedClock(
                        timeString = timeString,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 2. Date / Month / Year Segment Indicators Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DateLedItem(label = "তারিখ", value = dayOfMonth)
                        DateLedItem(label = "মাস", value = monthOfYear)
                        DateLedItem(label = "বছর", value = yearStr)
                    }

                    // 3. Bengali Weekdays Row with Active Day Pill
                    val bengaliDays = listOf("শনি", "রবি", "সোম", "মঙ্গল", "বুধ", "বৃহস্পতি", "শুক্র")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        bengaliDays.forEachIndexed { index, day ->
                            val isActive = index == activeDayIndex
                            Box(
                                modifier = Modifier
                                    .then(
                                        if (isActive) {
                                            Modifier
                                                .background(
                                                    color = DarkGreen.copy(alpha = 0.85f),
                                                    shape = RoundedCornerShape(4.dp)
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = PrimaryGreen,
                                                    shape = RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        } else {
                                            Modifier.padding(horizontal = 2.dp, vertical = 2.dp)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day,
                                    color = if (isActive) PrimaryGreen else PrimaryGreen.copy(alpha = 0.70f),
                                    fontFamily = SolaimanLipiFontFamily,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Backward compatibility alias
 */
@Composable
fun PrayerHeroSection(
    timeString: String,
    dayOfMonth: String,
    monthOfYear: String,
    yearStr: String,
    activeDayIndex: Int,
    modifier: Modifier = Modifier
) {
    MosqueHeroSection(
        timeString = timeString,
        dayOfMonth = dayOfMonth,
        monthOfYear = monthOfYear,
        yearStr = yearStr,
        activeDayIndex = activeDayIndex,
        modifier = modifier
    )
}

/**
 * Segmented Date/Month/Year cell with Bengali Label and 2-digit 7-segment LED
 */
@Composable
fun DateLedItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            color = PrimaryGreen,
            fontFamily = SolaimanLipiFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(end = 4.dp)
        )
        TableDigitalTime(
            timeString = value,
            activeColor = RedDigital,
            digitWidth = 10.dp,
            digitHeight = 18.dp
        )
    }
}

