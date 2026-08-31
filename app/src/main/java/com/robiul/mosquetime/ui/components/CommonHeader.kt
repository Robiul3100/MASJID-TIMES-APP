package com.robiul.mosquetime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robiul.mosquetime.ui.theme.AppRadius
import com.robiul.mosquetime.ui.theme.AppSpacing
import com.robiul.mosquetime.ui.theme.AppTypography
import com.robiul.mosquetime.ui.theme.DarkBackground
import com.robiul.mosquetime.ui.theme.DarkGreenBorder
import com.robiul.mosquetime.ui.theme.DarkSurface
import com.robiul.mosquetime.ui.theme.PrimaryGreen
import com.robiul.mosquetime.ui.theme.RedDigital
import com.robiul.mosquetime.ui.theme.TextMuted
import com.robiul.mosquetime.ui.theme.TextWhite

/**
 * Standard Header for Secondary & Nested Screens with safe-area status bar handling.
 */
@Composable
fun CommonHeader(
    title: String,
    subtitle: String? = null,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    actionIcon: ImageVector? = null,
    actionDescription: String? = null,
    actionBadgeCount: Int = 0,
    onActionClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DarkBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.screenHorizontal, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Back Button
            Box(
                modifier = Modifier
                    .size(AppSpacing.minTouchTarget)
                    .clip(RoundedCornerShape(AppRadius.md))
                    .background(DarkSurface)
                    .border(1.dp, DarkGreenBorder.copy(alpha = 0.6f), RoundedCornerShape(AppRadius.md))
                    .clickable { onBackClick() }
                    .testTag("header_back_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "পূর্ববর্তী পৃষ্ঠায় ফিরুন",
                    tint = TextWhite,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Title and Subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = AppSpacing.sm)
            ) {
                Text(
                    text = title,
                    style = AppTypography.screenTitle.copy(
                        fontSize = 17.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = AppTypography.screenSubtitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Optional Action Button or balanced spacing box
            if (actionIcon != null && onActionClick != null) {
                Box(
                    modifier = Modifier
                        .size(AppSpacing.minTouchTarget)
                        .clip(RoundedCornerShape(AppRadius.md))
                    .background(DarkSurface)
                    .border(1.dp, DarkGreenBorder.copy(alpha = 0.6f), RoundedCornerShape(AppRadius.md))
                    .clickable { onActionClick() }
                    .testTag("header_action_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = actionDescription ?: "Action",
                        tint = TextWhite,
                        modifier = Modifier.size(20.dp)
                    )

                    if (actionBadgeCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 4.dp, end = 4.dp)
                                .size(14.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(RedDigital),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (actionBadgeCount > 9) "9+" else actionBadgeCount.toString(),
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.size(AppSpacing.minTouchTarget))
            }
        }
    }
}
