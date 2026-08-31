package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanBlue
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.DarkGreenBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.PrimaryGreen

data class PrayerScheduleItem(
    val type: PrayerType,
    val bengaliName: String,
    val arabicName: String,
    val time: String,
    val isJumah: Boolean = false,
    val isActive: Boolean = false
)

/**
 * Premium Dark Prayer Timetable Card:
 * - Header: "নামাজের সময় সূচী" in Cyan with accents
 * - 8 High-fidelity Prayer rows
 * - Glassy dark surface with neon green & cyan illuminated border
 */
@Composable
fun PrayerScheduleCard(
    scheduleItems: List<PrayerScheduleItem>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurface.copy(alpha = 0.85f))
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        NeonGreenGlow.copy(alpha = 0.8f),
                        PrimaryGreen.copy(alpha = 0.5f),
                        DarkGreenBorder.copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(vertical = 10.dp, horizontal = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: "নামাজের সময় সূচী"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "••• ",
                    color = CyanBlue.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "নামাজের সময় সূচী",
                    color = CyanBlue,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = " •••",
                    color = CyanBlue.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                thickness = 0.6.dp,
                color = DarkSurfaceBorder.copy(alpha = 0.6f)
            )

            // Prayer Rows
            scheduleItems.forEachIndexed { index, item ->
                PrayerRow(
                    type = item.type,
                    bengaliName = item.bengaliName,
                    arabicName = item.arabicName,
                    timeString = item.time,
                    isJumah = item.isJumah,
                    isActive = item.isActive
                )

                if (index < scheduleItems.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        thickness = 0.4.dp,
                        color = DarkSurfaceBorder.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}
