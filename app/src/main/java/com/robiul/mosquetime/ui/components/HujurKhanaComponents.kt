package com.robiul.mosquetime.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robiul.mosquetime.data.model.DayMealSchedule
import com.robiul.mosquetime.data.model.MealSchedule
import com.robiul.mosquetime.data.model.MealStatus
import com.robiul.mosquetime.data.model.MealType
import com.robiul.mosquetime.data.model.MealWeeklySummary
import com.robiul.mosquetime.ui.theme.*

/**
 * Minimal & Clean Top Bar for Hujur's Khana screen
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkBackground)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(DarkSurfaceElevated)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextWhite,
                modifier = Modifier.size(18.dp)
            )
        }

        // Title
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
        ) {
            Text(
                text = "হুজুরের খানা সূচি",
                color = PrimaryGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "ইমাম সাহেবের মেহমানদারি তালিকা",
                color = TextMuted,
                fontSize = 10.5.sp
            )
        }

        // Action Buttons
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(
                onClick = onToggleSearch,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isSearchVisible) DarkGreen else DarkSurfaceElevated)
            ) {
                Icon(
                    imageVector = if (isSearchVisible) Icons.Default.Close else Icons.Default.Search,
                    contentDescription = "Search",
                    tint = if (isSearchVisible) NeonGreenGlow else TextWhite,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = onToggleViewMode,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isCalendarView) CyanBlueDim else DarkSurfaceElevated)
            ) {
                Icon(
                    imageVector = if (isCalendarView) Icons.AutoMirrored.Filled.ViewList else Icons.Default.DateRange,
                    contentDescription = "Toggle View",
                    tint = if (isCalendarView) CyanBlue else TextWhite,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Minimal Today's Hero Section with Clean 3-Meal Cards
 */
@Composable
fun TodayKhanaHeroSection(
    todaySchedule: DayMealSchedule?,
    currentMealType: MealType?,
    onMealClick: (MealSchedule) -> Unit,
    modifier: Modifier = Modifier
) {
    if (todaySchedule == null) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF16251C),
                        Color(0xFF0F1812)
                    )
                )
            )
            .border(1.dp, PrimaryGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(NeonGreenGlow)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "আজকের মেহমানদারি",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${todaySchedule.dayNameBn}, ${todaySchedule.dateBn}",
                    color = GoldAccent,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3 Clean Meal Slots
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(todaySchedule.morningMeal, todaySchedule.lunchMeal, todaySchedule.dinnerMeal).forEach { meal ->
                    val isCurrent = currentMealType == meal.mealType
                    MinimalMealRow(
                        meal = meal,
                        isCurrent = isCurrent,
                        onClick = { onMealClick(meal) }
                    )
                }
            }
        }
    }
}

/**
 * Minimalist Single Meal Row
 */
@Composable
fun MinimalMealRow(
    meal: MealSchedule,
    isCurrent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val (typeIcon, typeColor) = when (meal.mealType) {
        MealType.MORNING -> Pair(Icons.Outlined.LightMode, GoldAccent)
        MealType.LUNCH -> Pair(Icons.Outlined.WbSunny, PrimaryGreen)
        MealType.DINNER -> Pair(Icons.Outlined.Bedtime, CyanBlue)
    }

    val bg = if (isCurrent) EmeraldDeep.copy(alpha = 0.6f) else Color(0xFF121B15)
    val borderCol = if (isCurrent) PrimaryGreen else DarkSurfaceBorder

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.dp, borderCol, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Icon + Type
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1.1f)
        ) {
            Icon(
                imageVector = typeIcon,
                contentDescription = null,
                tint = typeColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = meal.mealType.shortNameBn,
                    color = typeColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = meal.mealType.timeRangeBn.take(11),
                    color = TextMuted,
                    fontSize = 9.5.sp
                )
            }
        }

        // Center Host Name
        Column(
            modifier = Modifier.weight(1.5f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = meal.responsiblePersonName,
                color = TextWhite,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${meal.householdName} • ${meal.area}",
                color = TextMuted,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Right Phone Call Button + Status Dot
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            if (!meal.phoneNumber.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(PrimaryGreen.copy(alpha = 0.15f))
                        .clickable {
                            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${meal.phoneNumber}"))
                            context.startActivity(callIntent)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call",
                        tint = PrimaryGreen,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            MealStatusBadge(status = meal.status, isCompact = true)
        }
    }
}

/**
 * Minimal Status Badge
 */
@Composable
fun MealStatusBadge(
    status: MealStatus,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    val (bgColor, textColor) = when (status) {
        MealStatus.DELIVERED -> Pair(GreenDigitalDim, NeonGreenGlow)
        MealStatus.PENDING -> Pair(Color(0xFF2B2005), GoldAccent)
        MealStatus.UPCOMING -> Pair(CyanBlueDim, CyanBlue)
        MealStatus.MISSED -> Pair(RedDigitalDim, RedDigital)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = status.titleBn,
            color = textColor,
            fontSize = if (isCompact) 9.sp else 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Minimal Tomorrow Reminder Notice
 */
@Composable
fun TomorrowReminderCard(
    tomorrowSchedule: DayMealSchedule?,
    onViewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (tomorrowSchedule == null) return

    val morningHost = tomorrowSchedule.morningMeal.responsiblePersonName.ifEmpty { "নির্ধারিত পরিবার" }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1B2418))
            .border(0.8.dp, GoldAccent.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .clickable(onClick = onViewClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = GoldAccent,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "আগামীকাল (${tomorrowSchedule.dayNameBn}): $morningHost",
                color = TextWhite,
                fontSize = 11.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = "দেখুন →",
            color = GoldAccent,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Minimal Weekly Progress Bar
 */
@Composable
fun MealWeeklySummaryBar(
    summary: MealWeeklySummary,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DarkSurfaceElevated)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "সাপ্তাহিক অগ্রগতি:",
            color = TextMuted,
            fontSize = 11.sp
        )
        Text(
            text = "মোট ২১ বেলার মধ্যে ${summary.deliveredCount} বেলা সম্পন্ন",
            color = PrimaryGreen,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Clean 7-Day Date Ribbon
 */
@Composable
fun WeekDateSelector(
    schedules: List<DayMealSchedule>,
    selectedDateStr: String?,
    onDateSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        schedules.forEach { day ->
            val isSelected = day.dateStr == selectedDateStr
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) PrimaryGreen else DarkSurfaceElevated)
                    .border(
                        0.8.dp,
                        if (isSelected) NeonGreenGlow else DarkSurfaceBorder,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onDateSelect(day.dateStr) }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = day.dayNameBn.take(3),
                        color = if (isSelected) Color(0xFF0D1C12) else TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = day.dateStr.takeLast(2),
                        color = if (isSelected) Color(0xFF0D1C12) else TextWhite,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Minimal Day Schedule Card (For Feed List)
 */
@Composable
fun DayScheduleCard(
    daySchedule: DayMealSchedule,
    onMealClick: (MealSchedule) -> Unit,
    modifier: Modifier = Modifier,
    currentMealType: MealType? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF131D16))
            .border(0.8.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Day Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${daySchedule.dayNameBn} • ${daySchedule.dateBn}",
                    color = PrimaryGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "৩ বেলা নির্ধারিত",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 3 Meals
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(daySchedule.morningMeal, daySchedule.lunchMeal, daySchedule.dinnerMeal).forEach { meal ->
                    MinimalMealRow(
                        meal = meal,
                        isCurrent = currentMealType == meal.mealType && daySchedule.isToday,
                        onClick = { onMealClick(meal) }
                    )
                }
            }
        }
    }
}

/**
 * Clean Search & Filter Bar
 */
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
            .background(DarkSurfaceElevated, RoundedCornerShape(10.dp))
            .padding(8.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            placeholder = { Text("পরিবার বা ব্যক্তির নাম দিয়ে খুঁজুন...", fontSize = 11.5.sp, color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(14.dp))
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(46.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = DarkSurfaceBorder,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
    }
}

/**
 * Minimal Calendar View
 */
@Composable
fun KhanaCalendarMonthView(
    schedules: List<DayMealSchedule>,
    selectedDateStr: String?,
    onDateSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(schedules) { day ->
            val isSelected = day.dateStr == selectedDateStr
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) PrimaryGreen else DarkSurface)
                    .clickable { onDateSelect(day.dateStr) }
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = day.dateStr.takeLast(2),
                        color = if (isSelected) Color.Black else TextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = day.dayNameBn.take(2),
                        color = if (isSelected) Color.Black else TextMuted,
                        fontSize = 8.sp
                    )
                }
            }
        }
    }
}

/**
 * Minimal Household Detail Bottom Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseholdDetailBottomSheet(
    meal: MealSchedule?,
    onDismiss: () -> Unit,
    onUpdateStatus: (MealStatus) -> Unit = {},
    onToggleReminder: () -> Unit = {}
) {
    if (meal == null) return
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceElevated
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${meal.mealType.titleBn} — মেহমানদারি",
                    color = PrimaryGreen,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                MealStatusBadge(status = meal.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "পরিবার: ${meal.householdName}", color = TextWhite, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "মেহমানদারীকারী: ${meal.responsiblePersonName}", color = TextWhite, fontSize = 12.5.sp)
            Text(text = "ঠিকানা / পাড়া: ${meal.area}", color = TextMuted, fontSize = 12.sp)
            Text(text = "সময়সূচি: ${meal.mealType.timeRangeBn}", color = GoldAccent, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(14.dp))

            if (!meal.phoneNumber.isNullOrBlank()) {
                Button(
                    onClick = {
                        val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${meal.phoneNumber}"))
                        context.startActivity(callIntent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ফোন করুন (${meal.phoneNumber})", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
