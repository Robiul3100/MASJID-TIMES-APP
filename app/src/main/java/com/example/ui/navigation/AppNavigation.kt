package com.example.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.feature.admin.activity.AdminActivityLogsScreen
import com.example.feature.admin.auth.AdminAuthViewModel
import com.example.feature.admin.auth.AdminLoginScreen
import com.example.feature.admin.committee.AdminCommitteeScreen
import com.example.feature.admin.dashboard.AdminDashboardScreen
import com.example.feature.admin.donations.AdminDonationsScreen
import com.example.feature.admin.duas.AdminDuaScreen
import com.example.feature.admin.emergency.AdminEmergencyScreen
import com.example.feature.admin.events.AdminEventsScreen
import com.example.feature.admin.fatwa.AdminFatwaScreen
import com.example.feature.admin.meals.AdminMealsScreen
import com.example.feature.admin.notices.AdminNoticeScreen
import com.example.feature.admin.notifications.AdminNotificationScreen
import com.example.feature.admin.prayer.AdminPrayerScheduleScreen
import com.example.feature.admin.profile.AdminMosqueProfileScreen
import com.example.ui.MosqueHomeScreen
import com.example.ui.components.BottomNavItem
import com.example.ui.components.DrawerMenu
import com.example.ui.components.MainBottomNavigation
import com.example.ui.screens.AboutMosqueScreen
import com.example.ui.screens.AskImamScreen
import com.example.ui.screens.CommitteeScreen
import com.example.ui.screens.ContactScreen
import com.example.ui.screens.DailyPrayerScreen
import com.example.ui.screens.DigitalTasbihScreen
import com.example.ui.screens.DonationScreen
import com.example.ui.screens.DuaDhikrScreen
import com.example.ui.screens.EventsScreen
import com.example.ui.screens.GalleryScreen
import com.example.ui.screens.GlobalSearchScreen
import com.example.ui.screens.HelpFaqScreen
import com.example.ui.screens.HujurKhanaScreen
import com.example.ui.screens.IslamicCalendarScreen
import com.example.ui.screens.JanazaAlertsScreen
import com.example.ui.screens.MonthlyScheduleScreen
import com.example.ui.screens.NotificationScreen
import com.example.ui.screens.QiblaCompassScreen
import com.example.ui.screens.QuranScreen
import com.example.ui.screens.QuranSurahDetailScreen
import com.example.ui.screens.RamadanDashboardScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ZakatCalculatorScreen
import com.example.ui.theme.DarkBackground
import kotlinx.coroutines.launch

/**
 * Centralized Application Shell and Navigation Host.
 * Implements: ONE APP SHELL, ONE SCAFFOLD, ONE PERSISTENT BOTTOM NAVIGATION.
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    // Primary routes that display the Persistent Bottom Navigation Bar
    val isPrimaryRoute = BottomNavItem.isPrimaryRoute(currentRoute)

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = currentRoute == Screen.Home.route,
        drawerContent = {
            DrawerMenu(
                currentRoute = currentRoute,
                onNavigate = { targetRoute ->
                    if (currentRoute != targetRoute) {
                        navController.navigate(targetRoute) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .background(DarkBackground),
            containerColor = DarkBackground,
            bottomBar = {
                if (isPrimaryRoute) {
                    MainBottomNavigation(
                        currentRoute = currentRoute,
                        onNavigate = { targetRoute ->
                            if (currentRoute != targetRoute) {
                                navController.navigate(targetRoute) {
                                    popUpTo(Screen.Home.route) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(DarkBackground),
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }
            ) {
                // 1. Home Screen (Master Screen)
                composable(Screen.Home.route) {
                    MosqueHomeScreen(
                        onOpenDrawer = {
                            scope.launch { drawerState.open() }
                        },
                        onNavigateToNotification = {
                            navController.navigate(Screen.Notifications.route)
                        },
                        onNavigateToDailyPrayer = {
                            navController.navigate(Screen.DailyPrayer.route)
                        },
                        onNavigateToMonthlySchedule = {
                            navController.navigate(Screen.MonthlySchedule.route)
                        },
                        onNavigateToIslamicCalendar = {
                            navController.navigate(Screen.IslamicCalendar.route)
                        },
                        onNavigateToQibla = {
                            navController.navigate(Screen.QiblaCompass.route)
                        },
                        onNavigateToQuran = {
                            navController.navigate(Screen.Quran.route)
                        },
                        onNavigateToDua = {
                            navController.navigate(Screen.DuaDhikr.route)
                        },
                        onNavigateToAboutMosque = {
                            navController.navigate(Screen.AboutMosque.route)
                        },
                        onNavigateToDonation = {
                            navController.navigate(Screen.Donation.route)
                        },
                        onNavigateToSettings = {
                            navController.navigate(Screen.Settings.route)
                        },
                        onNavigateToNoticeBoard = {
                            navController.navigate(Screen.NoticeBoard.route)
                        },
                        onNavigateToDigitalTasbih = {
                            navController.navigate(Screen.DigitalTasbih.route)
                        },
                        onNavigateToZakat = {
                            navController.navigate(Screen.ZakatCalculator.route)
                        },
                        onNavigateToRamadan = {
                            navController.navigate(Screen.RamadanDashboard.route)
                        },
                        onNavigateToJanaza = {
                            navController.navigate(Screen.JanazaAlerts.route)
                        },
                        onNavigateToAskImam = {
                            navController.navigate(Screen.AskImam.route)
                        }
                    )
                }

                // 2. Daily Prayer Times (Primary Tab)
                composable(Screen.DailyPrayer.route) {
                    DailyPrayerScreen(
                        onBackClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        },
                        onNavigateToMonthly = {
                            navController.navigate(Screen.MonthlySchedule.route)
                        },
                        onNavigateToDistrictSettings = {
                            navController.navigate(Screen.Settings.route)
                        }
                    )
                }

                // 3. Monthly Prayer Timetable Schedule
                composable(Screen.MonthlySchedule.route) {
                    MonthlyScheduleScreen(
                        onBackClick = { navController.popBackStack() },
                        onNavigateToDistrictSettings = {
                            navController.navigate(Screen.Settings.route)
                        }
                    )
                }

                // 4. Dua & Dhikr (Primary Tab)
                composable(Screen.DuaDhikr.route) {
                    DuaDhikrScreen(
                        onBackClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    )
                }

                // 5. Quran Surah List
                composable(Screen.Quran.route) {
                    QuranScreen(
                        onBackClick = { navController.popBackStack() },
                        onSurahClick = { surahNumber ->
                            navController.navigate(Screen.QuranSurahDetail.createRoute(surahNumber))
                        }
                    )
                }

                // 6. Quran Surah Recitation Detail
                composable(
                    route = Screen.QuranSurahDetail.route,
                    arguments = listOf(navArgument("surahNumber") { type = NavType.IntType })
                ) { backStackEntry ->
                    val surahNumber = backStackEntry.arguments?.getInt("surahNumber") ?: 1
                    QuranSurahDetailScreen(
                        surahNumber = surahNumber,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // 7. Notice Board (Maps to Unified Notification & Notice Center on Notice tab)
                composable(Screen.NoticeBoard.route) {
                    NotificationScreen(
                        onBackClick = { navController.popBackStack() },
                        initialTab = 1,
                        onNavigateToRoute = { routeKey ->
                            when (routeKey) {
                                "prayer" -> navController.navigate(Screen.DailyPrayer.route)
                                "event" -> navController.navigate(Screen.Events.route)
                                "donation" -> navController.navigate(Screen.Donation.route)
                                else -> {}
                            }
                        }
                    )
                }

                // 8. About Mosque
                composable(Screen.AboutMosque.route) {
                    AboutMosqueScreen(
                        onBackClick = { navController.popBackStack() },
                        onNavigateToCommittee = {
                            navController.navigate(Screen.Committee.route)
                        }
                    )
                }

                // 9. Mosque Committee
                composable(Screen.Committee.route) {
                    CommitteeScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // 10. Photo Gallery
                composable(Screen.Gallery.route) {
                    GalleryScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // 11. Contact & Location
                composable(Screen.Contact.route) {
                    ContactScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // 12. Donation & Fund
                composable(Screen.Donation.route) {
                    DonationScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // 13. Hujur's Khana - Imam's Meal Schedule (Primary Tab)
                composable(Screen.HujurKhana.route) {
                    HujurKhanaScreen(
                        onBackClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    )
                }

                // 14. Qibla Compass
                composable(Screen.QiblaCompass.route) {
                    QiblaCompassScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // 15. Islamic Calendar
                composable(Screen.IslamicCalendar.route) {
                    IslamicCalendarScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // 16. Events & Waz Mahfil
                composable(Screen.Events.route) {
                    EventsScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // 17. Global Search
                composable(Screen.GlobalSearch.route) {
                    GlobalSearchScreen(
                        onBackClick = { navController.popBackStack() },
                        onNavigateToRoute = { route ->
                            navController.navigate(route)
                        }
                    )
                }

                // 18. Help & FAQ
                composable(Screen.HelpFaq.route) {
                    HelpFaqScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // 19. Settings (Primary Tab)
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        onBackClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        },
                        onNavigateToAdmin = {
                            navController.navigate(Screen.AdminLogin.route)
                        }
                    )
                }

                // 20. Notifications Center (Unified)
                composable(Screen.Notifications.route) {
                    NotificationScreen(
                        onBackClick = { navController.popBackStack() },
                        initialTab = 0,
                        onNavigateToRoute = { routeKey ->
                            when (routeKey) {
                                "prayer" -> navController.navigate(Screen.DailyPrayer.route)
                                "event" -> navController.navigate(Screen.Events.route)
                                "donation" -> navController.navigate(Screen.Donation.route)
                                else -> {}
                            }
                        }
                    )
                }

                // 21. Smart Digital Tasbih Counter
                composable(Screen.DigitalTasbih.route) {
                    DigitalTasbihScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // 22. Zakat & Fitrah Calculator
                composable(Screen.ZakatCalculator.route) {
                    ZakatCalculatorScreen(
                        onBackClick = { navController.popBackStack() },
                        onNavigateToDonation = {
                            navController.navigate(Screen.Donation.route)
                        }
                    )
                }

                // 23. Ramadan & Iftar/Sehri Dashboard
                composable(Screen.RamadanDashboard.route) {
                    RamadanDashboardScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // 24. Janaza & Emergency Welfare Board
                composable(Screen.JanazaAlerts.route) {
                    JanazaAlertsScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // 25. Ask the Imam (Fatwa & Q&A)
                composable(Screen.AskImam.route) {
                    AskImamScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // ==================== ADMIN SECURE GRAPH ====================
                // 26. Admin Login Screen
                composable(Screen.AdminLogin.route) {
                    AdminLoginScreen(
                        onLoginSuccess = {
                            navController.navigate(Screen.AdminDashboard.route) {
                                popUpTo(Screen.AdminLogin.route) { inclusive = true }
                            }
                        },
                        onBackToPublic = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    )
                }

                // 27. Admin Dashboard Screen
                composable(Screen.AdminDashboard.route) {
                    AdminDashboardScreen(
                        onNavigateToMosqueProfile = { navController.navigate(Screen.AdminMosque.route) },
                        onNavigateToPrayers = { navController.navigate(Screen.AdminPrayer.route) },
                        onNavigateToMeals = { navController.navigate(Screen.AdminMeals.route) },
                        onNavigateToNotices = { navController.navigate(Screen.AdminNotices.route) },
                        onNavigateToNotifications = { navController.navigate(Screen.AdminNotifications.route) },
                        onNavigateToEvents = { navController.navigate(Screen.AdminEvents.route) },
                        onNavigateToEmergency = { navController.navigate(Screen.AdminEmergency.route) },
                        onNavigateToDuas = { navController.navigate(Screen.AdminDuas.route) },
                        onNavigateToCommittee = { navController.navigate(Screen.AdminCommittee.route) },
                        onNavigateToFatwas = { navController.navigate(Screen.AdminFatwa.route) },
                        onNavigateToDonations = { navController.navigate(Screen.AdminDonations.route) },
                        onNavigateToActivityLogs = { navController.navigate(Screen.AdminActivity.route) },
                        onExitAdmin = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    )
                }

                // Sub-admin modules
                composable(Screen.AdminMosque.route) {
                    val authViewModel: AdminAuthViewModel = viewModel()
                    val currentAdmin by authViewModel.currentUser.collectAsStateWithLifecycle()
                    AdminMosqueProfileScreen(
                        currentAdmin = currentAdmin,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.AdminPrayer.route) {
                    val authViewModel: AdminAuthViewModel = viewModel()
                    val currentAdmin by authViewModel.currentUser.collectAsStateWithLifecycle()
                    AdminPrayerScheduleScreen(
                        currentAdmin = currentAdmin,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.AdminMeals.route) {
                    val authViewModel: AdminAuthViewModel = viewModel()
                    val currentAdmin by authViewModel.currentUser.collectAsStateWithLifecycle()
                    AdminMealsScreen(
                        currentAdmin = currentAdmin,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.AdminNotices.route) {
                    val authViewModel: AdminAuthViewModel = viewModel()
                    val currentAdmin by authViewModel.currentUser.collectAsStateWithLifecycle()
                    AdminNoticeScreen(
                        currentAdmin = currentAdmin,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.AdminNotifications.route) {
                    val authViewModel: AdminAuthViewModel = viewModel()
                    val currentAdmin by authViewModel.currentUser.collectAsStateWithLifecycle()
                    AdminNotificationScreen(
                        currentAdmin = currentAdmin,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.AdminEvents.route) {
                    AdminEventsScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(Screen.AdminCommittee.route) {
                    AdminCommitteeScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(Screen.AdminFatwa.route) {
                    AdminFatwaScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(Screen.AdminDonations.route) {
                    AdminDonationsScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(Screen.AdminDuas.route) {
                    AdminDuaScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(Screen.AdminActivity.route) {
                    AdminActivityLogsScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(Screen.AdminEmergency.route) {
                    val authViewModel: AdminAuthViewModel = viewModel()
                    val currentAdmin by authViewModel.currentUser.collectAsStateWithLifecycle()
                    AdminEmergencyScreen(
                        currentAdmin = currentAdmin,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
