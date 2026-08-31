package com.robiul.mosquetime.feature.admin.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.robiul.mosquetime.ui.theme.*

/**
 * High-tech Live Status Pulse Banner showing Firestore Sync status
 */
@Composable
fun AdminLiveStatusBanner(
    isOnline: Boolean = true,
    label: String = if (isOnline) "ক্লাউড সিঙ্ক সক্রিয় • লাইভ ডাটাবেস" else "অফলাইন ক্যাশড মোড",
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val activeColor = if (isOnline) PrimaryGreen else GoldAccent

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurfaceElevated.copy(alpha = 0.8f))
            .border(1.dp, activeColor.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(activeColor.copy(alpha = alpha))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = TextWhite,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = SolaimanLipiFontFamily
        )
    }
}

/**
 * Premium Metric Card for the Admin Analytics section
 */
@Composable
fun AdminMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkSurfaceElevated,
                        DarkSurface
                    )
                )
            )
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(accentColor.copy(alpha = 0.5f), DarkGreenBorder.copy(alpha = 0.2f))
                ),
                RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.12f))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "রিয়েলটাইম",
                        color = accentColor,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = SolaimanLipiFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                color = TextWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = SolaimanLipiFontFamily
            )

            Text(
                text = title,
                color = TextMuted,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = SolaimanLipiFontFamily
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = accentColor,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = SolaimanLipiFontFamily
            )
        }
    }
}

/**
 * Modern Bento-Grid Module Card for Admin Features
 */
@Composable
fun AdminBentoCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    badgeText: String? = null,
    badgeColor: Color = accentColor,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkSurfaceElevated.copy(alpha = 0.95f),
                        DarkSurface.copy(alpha = 0.98f)
                    )
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(
                        accentColor.copy(alpha = 0.4f),
                        DarkGreenBorder.copy(alpha = 0.2f)
                    )
                ),
                RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(accentColor.copy(alpha = 0.22f), accentColor.copy(alpha = 0.08f))
                            )
                        )
                        .border(1.dp, accentColor.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                if (badgeText != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(badgeColor.copy(alpha = 0.16f))
                            .border(0.8.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.5.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = badgeColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column {
                Text(
                    text = title,
                    color = TextWhite,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SolaimanLipiFontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontFamily = SolaimanLipiFontFamily,
                    maxLines = 2,
                    lineHeight = 15.sp,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Ultra-Professional Glassmorphic Pop-up Edit Dialog
 */
@Composable
fun AdminEditDialog(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    iconTint: Color = PrimaryGreen,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    confirmButtonText: String = "সংরক্ষণ করুন",
    confirmButtonEnabled: Boolean = true,
    isConfirmLoading: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DarkSurfaceElevated,
                            DarkSurface
                        )
                    )
                )
                .border(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(
                            iconTint.copy(alpha = 0.5f),
                            DarkGreenBorder.copy(alpha = 0.3f)
                        )
                    ),
                    RoundedCornerShape(24.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(iconTint.copy(alpha = 0.15f))
                                .border(1.dp, iconTint.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconTint,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = title,
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = SolaimanLipiFontFamily
                            )
                            if (subtitle != null) {
                                Text(
                                    text = subtitle,
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(DarkBackground.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "বন্ধ করুন",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 14.dp),
                    thickness = 0.8.dp,
                    color = DarkGreenBorder.copy(alpha = 0.4f)
                )

                // Scrollable Body
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    content()
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = TextMuted
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkGreenBorder)
                    ) {
                        Text(
                            text = "বাতিল",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }

                    Button(
                        onClick = onConfirm,
                        enabled = confirmButtonEnabled && !isConfirmLoading,
                        modifier = Modifier
                            .weight(1.5f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = iconTint,
                            disabledContainerColor = iconTint.copy(alpha = 0.4f)
                        )
                    ) {
                        if (isConfirmLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = confirmButtonText,
                                    color = Color.Black,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
