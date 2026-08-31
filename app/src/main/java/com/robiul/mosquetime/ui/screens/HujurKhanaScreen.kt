package com.robiul.mosquetime.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.robiul.mosquetime.data.model.MealStatus
import com.robiul.mosquetime.ui.components.DayScheduleCard
import com.robiul.mosquetime.ui.components.HouseholdDetailBottomSheet
import com.robiul.mosquetime.ui.components.HujurKhanaTopBar
import com.robiul.mosquetime.ui.components.KhanaCalendarMonthView
import com.robiul.mosquetime.ui.components.KhanaSearchFilterSection
import com.robiul.mosquetime.ui.components.MealWeeklySummaryBar
import com.robiul.mosquetime.ui.components.TodayKhanaHeroSection
import com.robiul.mosquetime.ui.components.TomorrowReminderCard
import com.robiul.mosquetime.ui.components.WeekDateSelector
import com.robiul.mosquetime.ui.theme.DarkBackground
import com.robiul.mosquetime.ui.theme.DarkGreen
import com.robiul.mosquetime.ui.theme.NeonGreenGlow
import com.robiul.mosquetime.ui.theme.PrimaryGreen
import com.robiul.mosquetime.ui.theme.TextMuted
import com.robiul.mosquetime.ui.theme.TextWhite
import kotlinx.coroutines.launch

@Composable
fun HujurKhanaScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HujurKhanaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isCalendarView by viewModel.isCalendarView.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedMealTypeFilter by viewModel.selectedMealTypeFilter.collectAsState()
    val selectedStatusFilter by viewModel.selectedStatusFilter.collectAsState()
    val selectedDateStr by viewModel.selectedDateStr.collectAsState()
    val selectedMealDetail by viewModel.selectedMealDetail.collectAsState()
    val currentMealType by viewModel.currentMealType.collectAsState()
    val filteredSchedules by viewModel.filteredSchedules.collectAsState()

    var isSearchVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBackground)
            ) {
                // Top Bar
                HujurKhanaTopBar(
                    onBackClick = onBackClick,
                    isCalendarView = isCalendarView,
                    onToggleViewMode = { viewModel.toggleCalendarView() },
                    isSearchVisible = isSearchVisible,
                    onToggleSearch = {
                        isSearchVisible = !isSearchVisible
                        if (!isSearchVisible) {
                            viewModel.clearFilters()
                        }
                    }
                )

                // Search & Filter Dropdown (Expandable)
                AnimatedVisibility(
                    visible = isSearchVisible,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    KhanaSearchFilterSection(
                        searchQuery = searchQuery,
                        onQueryChange = { viewModel.onSearchQueryChange(it) },
                        selectedMealType = selectedMealTypeFilter,
                        onMealTypeSelect = { viewModel.onMealTypeFilterSelect(it) },
                        selectedStatus = selectedStatusFilter,
                        onStatusSelect = { viewModel.onStatusFilterSelect(it) },
                        onClearAll = { viewModel.clearFilters() },
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                // Screen Content based on state
                when (val state = uiState) {
                    is HujurKhanaUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "খানার তথ্য লোড হচ্ছে...",
                                color = PrimaryGreen,
                                fontSize = 14.sp
                            )
                        }
                    }

                    is HujurKhanaUiState.Empty -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Outlined.EventBusy,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "কোনো খানা তথ্য পাওয়া যায়নি",
                                    color = TextWhite,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    is HujurKhanaUiState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.message,
                                color = TextMuted,
                                fontSize = 14.sp
                            )
                        }
                    }

                    is HujurKhanaUiState.Success -> {
                        if (isCalendarView) {
                            // Monthly Calendar Grid View
                            KhanaCalendarMonthView(
                                schedules = state.allSchedules,
                                selectedDateStr = selectedDateStr,
                                onDateSelect = { dateStr ->
                                    viewModel.selectDate(dateStr)
                                    viewModel.toggleCalendarView()
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp)
                            )
                        } else {
                            // Standard Rich List Feed View
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("khana_schedules_list"),
                                contentPadding = PaddingValues(bottom = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // 1. Today's Hero Section with Active Meal Tracker
                                item {
                                    TodayKhanaHeroSection(
                                        todaySchedule = state.todaySchedule,
                                        currentMealType = currentMealType,
                                        onMealClick = { meal ->
                                            viewModel.showMealDetail(meal)
                                        },
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    )
                                }

                                // 2. Tomorrow's Meal Reminder Card (Notice style)
                                item {
                                    TomorrowReminderCard(
                                        tomorrowSchedule = state.tomorrowSchedule,
                                        onViewClick = {
                                            state.tomorrowSchedule?.let { viewModel.selectDate(it.dateStr) }
                                        },
                                        modifier = Modifier.padding(horizontal = 14.dp)
                                    )
                                }

                                // 3. Weekly Meal Rotation Summary Progress Bar
                                item {
                                    MealWeeklySummaryBar(
                                        summary = state.weeklySummary,
                                        modifier = Modifier.padding(horizontal = 14.dp)
                                    )
                                }

                                // 4. Interactive 7-Day Date Ribbon Selector
                                item {
                                    WeekDateSelector(
                                        schedules = state.allSchedules,
                                        selectedDateStr = selectedDateStr,
                                        onDateSelect = { dateStr ->
                                            viewModel.selectDate(dateStr)
                                        },
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }

                                // 5. List Section Header
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (searchQuery.isNotBlank() || selectedMealTypeFilter != null || selectedStatusFilter != null) {
                                                "🔍 অনুসন্ধানের ফলাফল (${filteredSchedules.size} দিন)"
                                            } else {
                                                "📅 সম্পূর্ণ খানা বণ্টন তালিকা"
                                            },
                                            color = TextWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )

                                        Text(
                                            text = "রোটেশন সূচি",
                                            color = PrimaryGreen,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                // 6. Filtered Day Schedule Cards
                                if (filteredSchedules.isEmpty()) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(24.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "কোনো খানা তথ্য পাওয়া যায়নি",
                                                color = TextMuted,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                } else {
                                    items(filteredSchedules, key = { it.dateStr }) { daySchedule ->
                                        DayScheduleCard(
                                            daySchedule = daySchedule,
                                            onMealClick = { meal ->
                                                viewModel.showMealDetail(meal)
                                            },
                                            modifier = Modifier.padding(horizontal = 14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Snackbar Host
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )

            // Household Detail Bottom Sheet Modal
            selectedMealDetail?.let { meal ->
                HouseholdDetailBottomSheet(
                    meal = meal,
                    onDismiss = { viewModel.closeMealDetail() },
                    onUpdateStatus = { newStatus ->
                        viewModel.updateMealStatus(meal.id, newStatus)
                        viewModel.closeMealDetail()
                        scope.launch {
                            val statusName = when (newStatus) {
                                MealStatus.DELIVERED -> "দেওয়া হয়েছে"
                                MealStatus.PENDING -> "অপেক্ষমাণ"
                                MealStatus.UPCOMING -> "আগামী"
                                MealStatus.MISSED -> "দেওয়া হয়নি"
                            }
                            snackbarHostState.showSnackbar("${meal.mealType.titleBn} খানা '$statusName' হিসেবে চিহ্নিত করা হয়েছে")
                        }
                    },
                    onToggleReminder = {
                        viewModel.toggleReminder(meal.id)
                    }
                )
            }
        }
    }
}
