package com.robiul.mosquetime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robiul.mosquetime.ui.theme.DarkGreenBorder
import com.robiul.mosquetime.ui.theme.DarkSurface
import com.robiul.mosquetime.ui.theme.DarkSurfaceBorder
import com.robiul.mosquetime.ui.theme.DarkSurfaceElevated
import com.robiul.mosquetime.ui.theme.EmeraldDeep
import com.robiul.mosquetime.ui.theme.NeonGreenGlow
import com.robiul.mosquetime.ui.theme.PrimaryGreen
import com.robiul.mosquetime.ui.theme.TextMuted
import com.robiul.mosquetime.ui.theme.TextWhite
import com.robiul.mosquetime.util.HapticUtils

/**
 * Modern Developer Card positioned at the bottom of the Navigation Drawer.
 *
 * @param onDeveloperClick Invoked when the user taps on the developer avatar/name section to open Developer Profile.
 * @param onDarkModeToggle Invoked when the user toggles the Dark Mode pill button.
 * @param onLogoutClick Invoked when the user taps the Logout button.
 * @param isDarkMode Current dark mode state.
 */
@Composable
fun DeveloperCard(
    onDeveloperClick: () -> Unit,
    onDarkModeToggle: () -> Unit,
    onLogoutClick: () -> Unit,
    isDarkMode: Boolean = true,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1E2820),
                        Color(0xFF141D16)
                    )
                )
            )
            .border(1.dp, PrimaryGreen.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .testTag("drawer_developer_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // -------------------------------------------------------------
            // Upper Clickable Area: Developer Info (Avatar + Name + Email)
            // -------------------------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = PrimaryGreen.copy(alpha = 0.3f)),
                        onClick = {
                            HapticUtils.performLongPressHaptic(view)
                            onDeveloperClick()
                        }
                    )
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Avatar Placeholder with Gradient Glow Border
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(EmeraldDeep, Color(0xFF0F3822))
                            )
                        )
                        .border(1.5.dp, PrimaryGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Developer Avatar",
                        tint = NeonGreenGlow,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Name and Email
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "HM ROBIUL",
                            color = TextWhite,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(13.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(1.dp))

                    Text(
                        text = "rsf.robiul@gmail.com",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                // Arrow forward indicator
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "View Profile",
                    tint = PrimaryGreen.copy(alpha = 0.7f),
                    modifier = Modifier.size(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // -------------------------------------------------------------
            // Bottom Action Row: 2 Independent Pill-Shaped Buttons
            // -------------------------------------------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Pill Button: Dark Mode Toggle
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isDarkMode) Color(0xFF0D1711) else DarkSurfaceElevated
                        )
                        .border(
                            0.8.dp,
                            if (isDarkMode) PrimaryGreen.copy(alpha = 0.5f) else DarkGreenBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, color = PrimaryGreen.copy(alpha = 0.3f)),
                            onClick = {
                                HapticUtils.performLongPressHaptic(view)
                                onDarkModeToggle()
                            }
                        )
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = "Theme Toggle",
                            tint = if (isDarkMode) PrimaryGreen else Color(0xFFFFD54F),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isDarkMode) "Dark Mode" else "Light Mode",
                            color = TextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Right Pill Button: Logout Action
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF2A1414))
                        .border(0.8.dp, Color(0xFFE53935).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, color = Color(0xFFE53935).copy(alpha = 0.3f)),
                            onClick = {
                                HapticUtils.performLongPressHaptic(view)
                                onLogoutClick()
                            }
                        )
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color(0xFFFF6B6B),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Logout",
                            color = Color(0xFFFF6B6B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
