package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.Screen

/**
 * Reusable Data Class for Navigation Items
 *
 * @param icon The outline-style ImageVector icon.
 * @param label The localized title text for the item.
 * @param isCenter True if this item represents the floating middle FAB.
 */
data class NavItem(
    val icon: ImageVector,
    val label: String,
    val isCenter: Boolean = false
)

/**
 * 5 Primary Destinations for the Persistent Bottom Navigation Enum
 */
enum class BottomNavItem(
    val title: String,
    val route: String,
    val testTag: String
) {
    SCHEDULE("সময়সূচি", Screen.DailyPrayer.route, "nav_schedule"),
    DUA("দোয়া", Screen.DuaDhikr.route, "nav_dua"),
    HOME("হোম", Screen.Home.route, "nav_home"),
    NOTICE("নোটিশ", Screen.NoticeBoard.route, "nav_notice"),
    SETTINGS("সেটিংস", Screen.Settings.route, "nav_settings");

    companion object {
        fun fromRoute(route: String?): BottomNavItem {
            return when (route) {
                Screen.DailyPrayer.route, Screen.MonthlySchedule.route -> SCHEDULE
                Screen.DuaDhikr.route -> DUA
                Screen.NoticeBoard.route, Screen.Notifications.route -> NOTICE
                Screen.Settings.route -> SETTINGS
                else -> HOME
            }
        }

        fun isPrimaryRoute(route: String?): Boolean {
            return values().any { it.route == route } ||
                    route == Screen.Notifications.route ||
                    route == Screen.HujurKhana.route ||
                    route == Screen.MonthlySchedule.route
        }
    }
}

/**
 * Modern Custom Bottom Navigation Bar with Bezier Notch Cutout and Center Floating Action Button.
 *
 * Features:
 * - 100% Jetpack Compose with custom Canvas + Bezier Path background
 * - Smooth organic U-shape cutout (notch) for the floating center button
 * - Rounded top corners (~26dp) and flat bottom edges
 * - Center Floating Button with Neon Green → Teal/Blue diagonal gradient & soft glow shadow
 * - Press bounce-back scale animation
 * - 4 thin outline side icons with smooth color animation (translucent white → neon green)
 * - Semi-transparent dark glassmorphic container with neon rim stroke
 *
 * @param items List of 5 NavItem instances (Center item must have isCenter = true).
 * @param selectedIndex The index of the currently active item.
 * @param onItemSelected Callback invoked with the selected index when an item is clicked.
 */
@Composable
fun CustomBottomNavBar(
    items: List<NavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    barHeight: Dp = 68.dp,
    fabSize: Dp = 58.dp,
    cornerRadius: Dp = 26.dp
) {
    // Theme Colors
    val darkBackground = Color(0xFA0B1115) // Semi-transparent dark charcoal/obsidian
    val darkBorderColor = Color(0xFF162D24)
    val neonAccent = Color(0xFF39FF14) // Vibrant Neon Green
    val cyanAccent = Color(0xFF00E5FF)
    val blueAccent = Color(0xFF2979FF)
    val unselectedColor = Color(0x99FFFFFF) // Soft translucent gray-white

    val centerIndex = items.indexOfFirst { it.isCenter }.takeIf { it >= 0 } ?: (items.size / 2)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // -------------------------------------------------------------
        // 1. Custom Shaped Background with Bezier Notch Cutout
        // -------------------------------------------------------------
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
        ) {
            val width = size.width
            val height = size.height
            val radiusPx = cornerRadius.toPx()
            val centerX = width / 2f

            // Notch geometry parameters
            val fabRadiusPx = (fabSize.toPx() / 2f) + 10.dp.toPx() // Clearance around FAB
            val notchDepthPx = (fabSize.toPx() * 0.52f) // Depth of U-notch
            val controlDistX = fabRadiusPx * 0.72f // Bezier smoothness control distance

            val path = Path().apply {
                // Start after top-left rounded corner
                moveTo(0f, radiusPx)
                // Top-left arc
                quadraticBezierTo(0f, 0f, radiusPx, 0f)

                // Top edge towards center notch
                lineTo(centerX - fabRadiusPx - 16.dp.toPx(), 0f)

                // Smooth organic Bezier U-cutout (Entry curve -> Bottom cradle -> Exit curve)
                cubicTo(
                    x1 = centerX - fabRadiusPx + controlDistX * 0.2f, y1 = 0f,
                    x2 = centerX - controlDistX, y2 = notchDepthPx,
                    x3 = centerX, y3 = notchDepthPx
                )
                cubicTo(
                    x1 = centerX + controlDistX, y1 = notchDepthPx,
                    x2 = centerX + fabRadiusPx - controlDistX * 0.2f, y2 = 0f,
                    x3 = centerX + fabRadiusPx + 16.dp.toPx(), y3 = 0f
                )

                // Top edge to top-right corner
                lineTo(width - radiusPx, 0f)
                // Top-right arc
                quadraticBezierTo(width, 0f, width, radiusPx)

                // Right edge to bottom
                lineTo(width, height)
                // Flat bottom edge
                lineTo(0f, height)
                // Close back to left edge
                close()
            }

            // Draw top ambient shadow / glow
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x3339FF14),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = 16.dp.toPx()
                ),
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw solid dark glassmorphic background
            drawPath(
                path = path,
                color = darkBackground,
                style = Fill
            )

            // Draw top elegant outline rim stroke
            val topRimPath = Path().apply {
                moveTo(0f, radiusPx)
                quadraticBezierTo(0f, 0f, radiusPx, 0f)
                lineTo(centerX - fabRadiusPx - 16.dp.toPx(), 0f)
                cubicTo(
                    centerX - fabRadiusPx + controlDistX * 0.2f, 0f,
                    centerX - controlDistX, notchDepthPx,
                    centerX, notchDepthPx
                )
                cubicTo(
                    centerX + controlDistX, notchDepthPx,
                    centerX + fabRadiusPx - controlDistX * 0.2f, 0f,
                    centerX + fabRadiusPx + 16.dp.toPx(), 0f
                )
                lineTo(width - radiusPx, 0f)
                quadraticBezierTo(width, 0f, width, radiusPx)
            }

            drawPath(
                path = topRimPath,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        darkBorderColor,
                        neonAccent.copy(alpha = 0.5f),
                        darkBorderColor
                    )
                ),
                style = Stroke(width = 1.2.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // -------------------------------------------------------------
        // 2. Side Navigation Items (4 Icons: 2 Left, 2 Right)
        // -------------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Group (Item 0 and 1)
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until centerIndex) {
                    val item = items.getOrNull(i)
                    if (item != null) {
                        SideNavItem(
                            item = item,
                            isSelected = selectedIndex == i,
                            neonColor = neonAccent,
                            unselectedColor = unselectedColor,
                            onClick = { onItemSelected(i) }
                        )
                    }
                }
            }

            // Center Notch Gap (Reservation space for the floating center button)
            Spacer(modifier = Modifier.size(fabSize + 16.dp))

            // Right Group (Item 3 and 4)
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in (centerIndex + 1) until items.size) {
                    val item = items.getOrNull(i)
                    if (item != null) {
                        SideNavItem(
                            item = item,
                            isSelected = selectedIndex == i,
                            neonColor = neonAccent,
                            unselectedColor = unselectedColor,
                            onClick = { onItemSelected(i) }
                        )
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 3. Middle Floating Action Button (Overlapping Notch Cutout)
        // -------------------------------------------------------------
        val centerItem = items.getOrNull(centerIndex)
        if (centerItem != null) {
            val fabInteractionSource = remember { MutableInteractionSource() }
            val isFabPressed by fabInteractionSource.collectIsPressedAsState()

            // Scale Bounce Animation on Press/Release
            val fabScale by animateFloatAsState(
                targetValue = if (isFabPressed) 0.88f else 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "FloatingFabScaleAnimation"
            )

            // Elevated and positioned ~half height above navbar
            Box(
                modifier = Modifier
                    .offset(y = -(barHeight * 0.44f))
                    .scale(fabScale)
                    .size(fabSize)
                    // Soft Neon Green Ambient/Spot Shadow
                    .shadow(
                        elevation = 20.dp,
                        shape = CircleShape,
                        ambientColor = neonAccent,
                        spotColor = neonAccent
                    )
                    // Soft radial glow aura simulation
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    neonAccent.copy(alpha = 0.45f),
                                    Color.Transparent
                                ),
                                radius = size.width * 0.85f
                            )
                        )
                    }
                    .clip(CircleShape)
                    // Diagonal Gradient: Neon Green -> Teal -> Blue
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                neonAccent,
                                cyanAccent,
                                blueAccent
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    )
                    .clickable(
                        interactionSource = fabInteractionSource,
                        indication = ripple(bounded = true, color = Color.White),
                        onClick = { onItemSelected(centerIndex) }
                    )
                    .testTag("floating_center_fab"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = centerItem.icon,
                    contentDescription = centerItem.label,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

/**
 * Reusable Side Navigation Item with Outline Icon, Label, and Smooth Color Animations
 */
@Composable
private fun SideNavItem(
    item: NavItem,
    isSelected: Boolean,
    neonColor: Color,
    unselectedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isSelected) neonColor else unselectedColor,
        animationSpec = tween(durationMillis = 200),
        label = "SideNavItemColor"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 24.dp, color = neonColor.copy(alpha = 0.3f)),
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = animatedColor,
            modifier = Modifier.size(23.dp)
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = item.label,
            color = animatedColor,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            letterSpacing = 0.2.sp
        )
    }
}

/**
 * Reusable Centralized Persistent Bottom Navigation Component for the Application Shell.
 */
@Composable
fun MainBottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 5 Navigation Items mapped according to user specifications
    val navItems = remember {
        listOf(
            NavItem(
                icon = Icons.Outlined.CalendarMonth,
                label = "সময়সূচি",
                isCenter = false
            ),
            NavItem(
                icon = Icons.Outlined.VolunteerActivism,
                label = "দোয়া",
                isCenter = false
            ),
            NavItem(
                icon = Icons.Outlined.Home,
                label = "হোম",
                isCenter = true
            ),
            NavItem(
                icon = Icons.Outlined.Notifications,
                label = "নোটিশ",
                isCenter = false
            ),
            NavItem(
                icon = Icons.Outlined.Settings,
                label = "সেটিংস",
                isCenter = false
            )
        )
    }

    val selectedEnum = BottomNavItem.fromRoute(currentRoute)
    val selectedIndex = when (selectedEnum) {
        BottomNavItem.SCHEDULE -> 0
        BottomNavItem.DUA -> 1
        BottomNavItem.HOME -> 2
        BottomNavItem.NOTICE -> 3
        BottomNavItem.SETTINGS -> 4
    }

    CustomBottomNavBar(
        items = navItems,
        selectedIndex = selectedIndex,
        onItemSelected = { index: Int ->
            val targetEnum = when (index) {
                0 -> BottomNavItem.SCHEDULE
                1 -> BottomNavItem.DUA
                2 -> BottomNavItem.HOME
                3 -> BottomNavItem.NOTICE
                4 -> BottomNavItem.SETTINGS
                else -> BottomNavItem.HOME
            }
            if (currentRoute != targetEnum.route) {
                onNavigate(targetEnum.route)
            }
        },
        modifier = modifier
    )
}

/**
 * Backwards-compatibility alias for screens that previously imported BottomNavigationBar
 */
@Composable
fun BottomNavigationBar(
    selectedItem: BottomNavItem = BottomNavItem.HOME,
    onItemSelected: (BottomNavItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    MainBottomNavigation(
        currentRoute = selectedItem.route,
        onNavigate = { route: String ->
            val item = BottomNavItem.values().find { it.route == route } ?: BottomNavItem.HOME
            onItemSelected(item)
        },
        modifier = modifier
    )
}

// -------------------------------------------------------------
// Android Studio Preview
// -------------------------------------------------------------
@Preview(showBackground = true, backgroundColor = 0xFF0D0D0D)
@Composable
fun CustomBottomNavBarPreview() {
    val items = listOf(
        NavItem(icon = Icons.Outlined.CalendarMonth, label = "সময়সূচি"),
        NavItem(icon = Icons.Outlined.VolunteerActivism, label = "দোয়া"),
        NavItem(icon = Icons.Outlined.Home, label = "হোম", isCenter = true),
        NavItem(icon = Icons.Outlined.Notifications, label = "নোটিশ"),
        NavItem(icon = Icons.Outlined.Settings, label = "সেটিংস")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D)),
        contentAlignment = Alignment.BottomCenter
    ) {
        CustomBottomNavBar(
            items = items,
            selectedIndex = 2,
            onItemSelected = { _: Int -> }
        )
    }
}
