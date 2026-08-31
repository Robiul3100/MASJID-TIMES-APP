package com.robiul.mosquetime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robiul.mosquetime.ui.theme.*
import com.robiul.mosquetime.util.HapticUtils

@Composable
fun MosqueHeader(
    onMenuClick: () -> Unit,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier,
    unreadNotificationCount: Int = 1,
    title: String = "চৌধুরী পাটোয়ারী বাড়ি",
    subtitle: String = "জামে মসজিদ"
) {
    val view = LocalView.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F1A1F),
                        Color(0xFF0B1115)
                    )
                )
            )
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Menu Button with Tactile Long-Press Haptic
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = PrimaryGreen.copy(alpha = 0.2f)),
                        onClick = {
                            HapticUtils.performLongPressHaptic(view)
                            onMenuClick()
                        }
                    )
                    .testTag("header_menu_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = "মেনু",
                    tint = TextWhite,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Mosque Title & Subtitle (Clean, bold, solid elegance)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.3.sp,
                    color = PrimaryGreen,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = GoldAccent,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Notification Bell with Badge & Tactile Long-Press Haptic
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = PrimaryGreen.copy(alpha = 0.2f)),
                        onClick = {
                            HapticUtils.performLongPressHaptic(view)
                            onNotificationClick()
                        }
                    )
                    .testTag("header_notif_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "নোটিফিকেশন",
                    tint = TextWhite,
                    modifier = Modifier.size(22.dp)
                )

                if (unreadNotificationCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-4).dp, y = 4.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(RedDigital)
                    )
                }
            }
        }
    }
}
