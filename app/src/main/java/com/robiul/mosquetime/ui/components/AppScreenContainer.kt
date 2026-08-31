package com.robiul.mosquetime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.robiul.mosquetime.ui.theme.AppRadius
import com.robiul.mosquetime.ui.theme.AppSpacing
import com.robiul.mosquetime.ui.theme.DarkBackground
import com.robiul.mosquetime.ui.theme.DarkGreenBorder
import com.robiul.mosquetime.ui.theme.DarkSurface
import com.robiul.mosquetime.ui.theme.DarkSurfaceBorder

/**
 * Standard reusable Screen Container that provides:
 * - Uniform background color (DarkBackground)
 * - Optional vertical scrolling with proper contentPadding
 * - Safe layout contract
 */
@Composable
fun AppScreenContainer(
    modifier: Modifier = Modifier,
    isScrollable: Boolean = true,
    horizontalPadding: Dp = AppSpacing.screenHorizontal,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    topBar: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            topBar?.invoke()

            if (isScrollable) {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = horizontalPadding, vertical = AppSpacing.sm),
                    content = content
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = horizontalPadding, vertical = AppSpacing.sm),
                    content = content
                )
            }
        }
    }
}

/**
 * Standard Islamic Card Component with consistent borders, dark surface background, and corner radius.
 */
@Composable
fun IslamicCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = DarkSurface,
    borderColor: Color = DarkGreenBorder.copy(alpha = 0.6f),
    shape: Shape = RoundedCornerShape(AppRadius.lg),
    contentPadding: Dp = AppSpacing.cardPadding,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
            .padding(contentPadding),
        content = content
    )
}
