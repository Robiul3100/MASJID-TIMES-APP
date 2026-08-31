package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DayMealSchedule
import com.example.data.model.MealSchedule
import com.example.data.model.MealStatus
import com.example.data.model.MealType
import com.example.data.model.MealWeeklySummary
import com.example.ui.theme.CyanBlue
import com.example.ui.theme.CyanBlueDim
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.DarkGreenBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GreenDigital
import com.example.ui.theme.GreenDigitalDim
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.RedDigital
import com.example.ui.theme.RedDigitalDim
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

/**
 * Premium Top App Bar for Hujur's Khana screen
 */
@Composable
fun HujurKhanaTopBar(
    onBackClick: () -> Unit,
    isCalendarView: Boolean,
    onToggleViewMode: () -> Unit,
    isSearchVisible: Boolean,
    onToggleSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkBackground)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(DarkGreenBorder.copy(alpha = 0.5f), Color.Transparent)
                ),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Back Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkGreenBorder.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                    .clickable { onBackClick() }
                    .testTag("hujur_khana_back_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextWhite,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Title & Subtitle with Islamic motif
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(DarkGreen)
                            .border(1.dp, PrimaryGreen.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "হুজুরের খানা",
                        color = PrimaryGreen,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "ইমাম সাহেবের দৈনিক খাবারের সময়সূচি",
                    color = TextMuted,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Action Buttons (Search toggle & View switch)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Search toggle
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSearchVisible) DarkGreen else DarkSurface)
                        .border(
                            1.dp,
                            if (isSearchVisible) PrimaryGreen else DarkSurfaceBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onToggleSearch() }
                        .testTag("khana_search_toggle"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSearchVisible) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = "Search",
                        tint = if (isSearchVisible) NeonGreenGlow else TextWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Calendar/List View toggle
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isCalendarView) CyanBlueDim else DarkSurface)
                        .border(
                            1.dp,
                            if (isCalendarView) CyanBlue else DarkSurfaceBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onToggleViewMode() }
                        .testTag("khana_view_mode_toggle"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCalendarView) Icons.AutoMirrored.Filled.ViewList else Icons.Default.DateRange,
                        contentDescription = if (isCalendarView) "List View" else "Calendar View",
                        tint = if (isCalendarView) CyanBlue else TextWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Status Badge Component with distinct colors and icons
 */
@Composable
fun MealStatusBadge(
    status: MealStatus,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    val (bgColor, borderColor, textColor, icon) = when (status) {
        MealStatus.DELIVERED -> Quadruple(
            GreenDigitalDim,
            PrimaryGreen.copy(alpha = 0.7f),
            NeonGreenGlow,
            Icons.Default.CheckCircle
        )
        MealStatus.PENDING -> Quadruple(
            Color(0xFF2E2207),
            GoldAccent.copy(alpha = 0.7f),
            GoldAccent,
            Icons.Default.HourglassTop
        )
        MealStatus.UPCOMING -> Quadruple(
            CyanBlueDim,
            CyanBlue.copy(alpha = 0.7f),
            CyanBlue,
            Icons.Default.Schedule
        )
        MealStatus.MISSED -> Quadruple(
            RedDigitalDim,
            RedDigital.copy(alpha = 0.7f),
            RedDigital,
            Icons.Default.Warning
        )
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .padding(
                horizontal = if (isCompact) 6.dp else 8.dp,
                vertical = if (isCompact) 2.dp else 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(if (isCompact) 11.dp else 13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = status.titleBn,
            color = textColor,
            fontSize = if (isCompact) 10.sp else 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

/**
 * Single Meal Card used in Today Section
 */
@Composable
fun TodayMealCard(
    meal: MealSchedule,
    isCurrentPeriod: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val (mealIcon, iconColor, iconBg) = when (meal.mealType) {
        MealType.MORNING -> Triple(Icons.Outlined.LightMode, GoldAccent, Color(0xFF26200A))
        MealType.LUNCH -> Triple(Icons.Outlined.WbSunny, PrimaryGreen, DarkGreen)
        MealType.DINNER -> Triple(Icons.Outlined.Bedtime, PurpleAccent, Color(0xFF23122E))
    }

    val cardBorderColor = if (isCurrentPeriod) {
        PrimaryGreen.copy(alpha = pulseAlpha)
    } else {
        DarkSurfaceBorder
    }

    val cardBg = if (isCurrentPeriod) {
        Brush.verticalGradient(
            colors = listOf(DarkSurfaceElevated, DarkSurface)
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(DarkSurface, DarkBackground)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(cardBg)
            .border(
                width = if (isCurrentPeriod) 1.5.dp else 1.dp,
                color = cardBorderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
            .testTag("today_meal_card_${meal.mealType.name.lowercase()}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Card Header: Meal Type Title, Time Range, and Current Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(iconBg)
                            .border(1.dp, iconColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = mealIcon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = meal.mealType.titleBn,
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isCurrentPeriod) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(PrimaryGreen.copy(alpha = 0.2f))
                                        .border(1.dp, PrimaryGreen, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "বর্তমান",
                                        color = NeonGreenGlow,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                        Text(
                            text = meal.mealType.timeRangeBn,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                // Status Badge
                MealStatusBadge(status = meal.status)
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = DarkSurfaceBorder.copy(alpha = 0.6f), thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Household & Person Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = meal.householdName,
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${meal.responsiblePersonName} • ${meal.area}",
                            color = TextMuted,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Delivered time or Reminder indicator
                Column(horizontalAlignment = Alignment.End) {
                    if (meal.status == MealStatus.DELIVERED && meal.deliveredAt != null) {
                        Text(
                            text = "পৌঁছেছে: ${meal.deliveredAt}",
                            color = NeonGreenGlow,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else if (meal.isReminderActive) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "রিমাইন্ডার অন",
                                color = GoldAccent,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Today Hero Section ("আজকের খানা")
 */
@Composable
fun TodayKhanaHeroSection(
    todaySchedule: DayMealSchedule,
    currentMealType: MealType,
    onMealClick: (MealSchedule) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkSurfaceElevated, DarkSurface)
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(DarkGreenBorder, DarkSurfaceBorder)
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(14.dp)
            .testTag("today_khana_hero_section")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Title, Date, Hijri Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkGreen)
                            .border(1.dp, PrimaryGreen, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "আজকের খানা",
                            color = NeonGreenGlow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${todaySchedule.dateBn} • ${todaySchedule.dayNameBn}",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = todaySchedule.hijriDateBn,
                    color = GoldAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Three Meal Cards
            TodayMealCard(
                meal = todaySchedule.morningMeal,
                isCurrentPeriod = currentMealType == MealType.MORNING,
                onClick = { onMealClick(todaySchedule.morningMeal) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            TodayMealCard(
                meal = todaySchedule.lunchMeal,
                isCurrentPeriod = currentMealType == MealType.LUNCH,
                onClick = { onMealClick(todaySchedule.lunchMeal) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            TodayMealCard(
                meal = todaySchedule.dinnerMeal,
                isCurrentPeriod = currentMealType == MealType.DINNER,
                onClick = { onMealClick(todaySchedule.dinnerMeal) }
            )
        }
    }
}

/**
 * Compact Weekly Summary Statistics Bar
 */
@Composable
fun MealWeeklySummaryBar(
    summary: MealWeeklySummary,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SummaryItem(
            icon = Icons.Default.CheckCircle,
            label = "সম্পন্ন",
            count = summary.deliveredCount,
            tint = NeonGreenGlow,
            bg = GreenDigitalDim
        )

        Box(modifier = Modifier.width(1.dp).height(24.dp).background(DarkSurfaceBorder))

        SummaryItem(
            icon = Icons.Default.HourglassTop,
            label = "অপেক্ষমাণ",
            count = summary.pendingCount,
            tint = GoldAccent,
            bg = Color(0xFF2E2207)
        )

        Box(modifier = Modifier.width(1.dp).height(24.dp).background(DarkSurfaceBorder))

        SummaryItem(
            icon = Icons.Default.Schedule,
            label = "আগামী",
            count = summary.upcomingCount,
            tint = CyanBlue,
            bg = CyanBlueDim
        )

        if (summary.missedCount > 0) {
            Box(modifier = Modifier.width(1.dp).height(24.dp).background(DarkSurfaceBorder))
            SummaryItem(
                icon = Icons.Default.Warning,
                label = "বাকি",
                count = summary.missedCount,
                tint = RedDigital,
                bg = RedDigitalDim
            )
        }
    }
}

@Composable
private fun SummaryItem(
    icon: ImageVector,
    label: String,
    count: Int,
    tint: Color,
    bg: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(13.dp)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = toBanglaDigit(count),
                color = TextWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = TextMuted,
                fontSize = 9.sp
            )
        }
    }
}

/**
 * Advanced Horizontal Week Date Selector
 */
@Composable
fun WeekDateSelector(
    schedules: List<DayMealSchedule>,
    selectedDateStr: String,
    onDateSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        schedules.take(14).forEach { day ->
            val isSelected = day.dateStr == selectedDateStr

            val borderBrush = if (isSelected) {
                Brush.verticalGradient(listOf(NeonGreenGlow, PrimaryGreen))
            } else {
                Brush.verticalGradient(listOf(DarkSurfaceBorder, DarkSurfaceBorder.copy(alpha = 0.4f)))
            }

            val bgColor = if (isSelected) DarkGreen else DarkSurface

            Box(
                modifier = Modifier
                    .width(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor)
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        brush = borderBrush,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onDateSelect(day.dateStr) }
                    .padding(vertical = 8.dp, horizontal = 4.dp)
                    .testTag("date_pill_${day.dateStr}"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Relative badge or short day name
                    Text(
                        text = day.relativeDayLabelBn,
                        color = if (isSelected) NeonGreenGlow else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Day Number
                    Text(
                        text = day.dateBn.split(" ").firstOrNull() ?: "",
                        color = if (isSelected) TextWhite else TextWhite.copy(alpha = 0.8f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Weekday Name
                    Text(
                        text = day.dayNameBn.take(4),
                        color = if (isSelected) PrimaryGreen else TextDark,
                        fontSize = 10.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Status Dot Indicator
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    day.isAllDelivered -> NeonGreenGlow
                                    day.hasPending -> GoldAccent
                                    day.hasMissed -> RedDigital
                                    else -> CyanBlue
                                }
                            )
                    )
                }
            }
        }
    }
}

/**
 * Mobile-Friendly Schedule Card for a Single Day
 */
@Composable
fun DayScheduleCard(
    daySchedule: DayMealSchedule,
    onMealClick: (MealSchedule) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurface)
            .border(
                1.dp,
                if (daySchedule.isToday) DarkGreenBorder else DarkSurfaceBorder,
                RoundedCornerShape(14.dp)
            )
            .padding(12.dp)
            .testTag("day_schedule_card_${daySchedule.dateStr}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Day Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (daySchedule.relativeDayLabelBn in listOf("আজ", "কাল", "পরশু", "গতকাল")) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (daySchedule.isToday) DarkGreen else DarkSurfaceElevated)
                                .border(
                                    1.dp,
                                    if (daySchedule.isToday) PrimaryGreen else DarkSurfaceBorder,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = daySchedule.relativeDayLabelBn,
                                color = if (daySchedule.isToday) NeonGreenGlow else GoldAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = "${daySchedule.dateBn} • ${daySchedule.dayNameBn}",
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = daySchedule.hijriDateBn,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = DarkSurfaceBorder, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // 3 Meal Rows
            MealScheduleRow(
                meal = daySchedule.morningMeal,
                onClick = { onMealClick(daySchedule.morningMeal) }
            )

            Spacer(modifier = Modifier.height(6.dp))

            MealScheduleRow(
                meal = daySchedule.lunchMeal,
                onClick = { onMealClick(daySchedule.lunchMeal) }
            )

            Spacer(modifier = Modifier.height(6.dp))

            MealScheduleRow(
                meal = daySchedule.dinnerMeal,
                onClick = { onMealClick(daySchedule.dinnerMeal) }
            )
        }
    }
}

/**
 * Individual Meal Row inside Day Schedule
 */
@Composable
fun MealScheduleRow(
    meal: MealSchedule,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (mealIcon, iconColor) = when (meal.mealType) {
        MealType.MORNING -> Pair(Icons.Outlined.LightMode, GoldAccent)
        MealType.LUNCH -> Pair(Icons.Outlined.WbSunny, PrimaryGreen)
        MealType.DINNER -> Pair(Icons.Outlined.Bedtime, PurpleAccent)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceElevated)
            .border(0.8.dp, DarkSurfaceBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Meal Icon & Name
        Row(
            modifier = Modifier.weight(1.1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = mealIcon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = meal.mealType.shortNameBn,
                color = TextWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Responsible Household & Person
        Row(
            modifier = Modifier.weight(2.2f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = meal.householdName,
                    color = TextWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = meal.responsiblePersonName,
                    color = TextMuted,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Status Badge
        MealStatusBadge(status = meal.status, isCompact = true)
    }
}

/**
 * Calendar View Component with Islamic aesthetic and status indicators
 */
@Composable
fun KhanaCalendarMonthView(
    schedules: List<DayMealSchedule>,
    selectedDateStr: String,
    onDateSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val daysOfWeek = listOf("শনি", "রবি", "সোম", "মঙ্গল", "বুধ", "বৃহ", "শুক্র")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Month Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "আগস্ট – সেপ্টেম্বর ২০২৬",
                    color = PrimaryGreen,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CalendarLegendDot(color = NeonGreenGlow, label = "সম্পন্ন")
                    CalendarLegendDot(color = GoldAccent, label = "অপেক্ষমাণ")
                    CalendarLegendDot(color = CyanBlue, label = "আগামী")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Weekday Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                daysOfWeek.forEach { dayName ->
                    Text(
                        text = dayName,
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = DarkSurfaceBorder, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Grid of Days
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth().height(230.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(schedules.take(28)) { day ->
                    val isSelected = day.dateStr == selectedDateStr
                    val dayNum = day.dateBn.split(" ").firstOrNull() ?: ""

                    Box(
                        modifier = Modifier
                            .height(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) DarkGreen
                                else if (day.isToday) DarkSurfaceElevated
                                else Color.Transparent
                            )
                            .border(
                                1.dp,
                                if (isSelected) PrimaryGreen
                                else if (day.isToday) DarkGreenBorder
                                else DarkSurfaceBorder.copy(alpha = 0.3f),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onDateSelect(day.dateStr) }
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = dayNum,
                                color = if (isSelected) NeonGreenGlow else if (day.isToday) PrimaryGreen else TextWhite,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected || day.isToday) FontWeight.Bold else FontWeight.Normal
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            // 3 mini status dots for Morning, Lunch, Dinner
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MiniStatusDot(meal = day.morningMeal)
                                MiniStatusDot(meal = day.lunchMeal)
                                MiniStatusDot(meal = day.dinnerMeal)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniStatusDot(meal: MealSchedule) {
    val color = when (meal.status) {
        MealStatus.DELIVERED -> NeonGreenGlow
        MealStatus.PENDING -> GoldAccent
        MealStatus.UPCOMING -> CyanBlue
        MealStatus.MISSED -> RedDigital
    }
    Box(
        modifier = Modifier
            .size(4.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun CalendarLegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = label,
            color = TextMuted,
            fontSize = 9.sp
        )
    }
}

/**
 * Search & Filter Section
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KhanaSearchFilterSection(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    selectedMealType: MealType?,
    onMealTypeSelect: (MealType?) -> Unit,
    selectedStatus: MealStatus?,
    onStatusSelect: (MealStatus?) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurface)
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("khana_search_input"),
            placeholder = {
                Text(
                    text = "কার বাড়িতে খানা? নাম, বাড়ি বা পাড়া খুঁজুন...",
                    color = TextDark,
                    fontSize = 12.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = DarkSurfaceBorder,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedContainerColor = DarkSurfaceElevated,
                unfocusedContainerColor = DarkSurfaceElevated
            ),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips Row
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // All filter
            FilterPill(
                label = "সব খানা",
                isSelected = selectedMealType == null && selectedStatus == null,
                onClick = onClearAll
            )

            // Meal Types
            FilterPill(
                label = "🌅 সকাল",
                isSelected = selectedMealType == MealType.MORNING,
                onClick = { onMealTypeSelect(MealType.MORNING) }
            )
            FilterPill(
                label = "☀️ দুপুর",
                isSelected = selectedMealType == MealType.LUNCH,
                onClick = { onMealTypeSelect(MealType.LUNCH) }
            )
            FilterPill(
                label = "🌙 রাত",
                isSelected = selectedMealType == MealType.DINNER,
                onClick = { onMealTypeSelect(MealType.DINNER) }
            )

            // Statuses
            FilterPill(
                label = "✓ সম্পন্ন",
                isSelected = selectedStatus == MealStatus.DELIVERED,
                onClick = { onStatusSelect(MealStatus.DELIVERED) }
            )
            FilterPill(
                label = "⏳ অপেক্ষমাণ",
                isSelected = selectedStatus == MealStatus.PENDING,
                onClick = { onStatusSelect(MealStatus.PENDING) }
            )
        }
    }
}

@Composable
private fun FilterPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) DarkGreen else DarkSurfaceElevated)
            .border(
                1.dp,
                if (isSelected) PrimaryGreen else DarkSurfaceBorder,
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) NeonGreenGlow else TextMuted,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/**
 * Responsibility Reminder Banner for Tomorrow
 */
@Composable
fun TomorrowReminderCard(
    tomorrowSchedule: DayMealSchedule?,
    onViewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (tomorrowSchedule == null) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(DarkSurfaceElevated, Color(0xFF14241B))
                )
            )
            .border(1.dp, DarkGreenBorder, RoundedCornerShape(14.dp))
            .clickable { onViewClick() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DarkGreen)
                        .border(1.dp, PrimaryGreen.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "আগামীকালের দায়িত্ব",
                            color = PrimaryGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(${tomorrowSchedule.dayNameBn})",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "সকাল: ${tomorrowSchedule.morningMeal.responsiblePersonName}",
                        color = TextWhite,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkGreenBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "তালিকা দেখুন",
                    color = NeonGreenGlow,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Household Detail Bottom Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseholdDetailBottomSheet(
    meal: MealSchedule?,
    onDismiss: () -> Unit,
    onUpdateStatus: (MealStatus) -> Unit,
    onToggleReminder: () -> Unit
) {
    if (meal == null) return

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurfaceElevated,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(TextMuted.copy(alpha = 0.4f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header: Title & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(DarkGreen)
                            .border(1.dp, PrimaryGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "দায়িত্বপ্রাপ্ত পরিবার",
                            color = PrimaryGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${meal.mealType.titleBn} • ${meal.date}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                MealStatusBadge(status = meal.status)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = DarkSurfaceBorder, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Main Info Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    DetailRow(
                        icon = Icons.Default.Home,
                        label = "বাড়ির নাম",
                        value = meal.householdName,
                        isPrimary = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    DetailRow(
                        icon = Icons.Default.Person,
                        label = "দায়িত্বপ্রাপ্ত ব্যক্তি",
                        value = meal.responsiblePersonName
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    DetailRow(
                        icon = Icons.Default.LocationOn,
                        label = "গ্রাম / পাড়া",
                        value = meal.area
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    DetailRow(
                        icon = Icons.Default.Schedule,
                        label = "নির্ধারিত সময়",
                        value = meal.mealType.timeRangeBn
                    )

                    if (meal.notes != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        DetailRow(
                            icon = Icons.Default.Restaurant,
                            label = "মেনু / বিবরণ",
                            value = meal.notes
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Contact button if phone available
            if (!meal.phoneNumber.isNullOrEmpty()) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${meal.phoneNumber.replace("-", "")}")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkGreen,
                        contentColor = NeonGreenGlow
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(listOf(DarkGreenBorder, PrimaryGreen))
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        tint = NeonGreenGlow,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "যোগাযোগ করুন (${meal.phoneNumber})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Reminder Switch Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = if (meal.isReminderActive) GoldAccent else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "খানা পাঠানোর রিমাইন্ডার",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "নির্ধারিত সময়ের ১ ঘণ্টা পূর্বে নোটিফিকেশন",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                Switch(
                    checked = meal.isReminderActive,
                    onCheckedChange = { onToggleReminder() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NeonGreenGlow,
                        checkedTrackColor = DarkGreen,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = DarkSurfaceElevated
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Update Action Section
            Text(
                text = "খাবারের অবস্থা পরিবর্তন করুন:",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Delivered Button
                Button(
                    onClick = { onUpdateStatus(MealStatus.DELIVERED) },
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (meal.status == MealStatus.DELIVERED) PrimaryGreen else GreenDigitalDim,
                        contentColor = if (meal.status == MealStatus.DELIVERED) DarkBackground else NeonGreenGlow
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "দেওয়া হয়েছে", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Pending Button
                Button(
                    onClick = { onUpdateStatus(MealStatus.PENDING) },
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (meal.status == MealStatus.PENDING) GoldAccent else Color(0xFF2E2207),
                        contentColor = if (meal.status == MealStatus.PENDING) DarkBackground else GoldAccent
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "অপেক্ষমাণ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    isPrimary: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isPrimary) PrimaryGreen else TextMuted,
            modifier = Modifier.size(16.dp).padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                color = TextMuted,
                fontSize = 10.sp
            )
            Text(
                text = value,
                color = if (isPrimary) NeonGreenGlow else TextWhite,
                fontSize = if (isPrimary) 14.sp else 12.sp,
                fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

private fun toBanglaDigit(number: Any): String {
    val banglaDigits = mapOf(
        '0' to '০', '1' to '১', '2' to '২', '3' to '৩', '4' to '৪',
        '5' to '৫', '6' to '৬', '7' to '৭', '8' to '৮', '9' to '৯'
    )
    return number.toString().map { banglaDigits[it] ?: it }.joinToString("")
}
