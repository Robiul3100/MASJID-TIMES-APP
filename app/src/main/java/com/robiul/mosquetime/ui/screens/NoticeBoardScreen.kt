package com.robiul.mosquetime.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.robiul.mosquetime.data.model.NoticeCategory
import com.robiul.mosquetime.data.model.NoticeItem
import com.robiul.mosquetime.data.repository.MosqueRepository
import com.robiul.mosquetime.ui.components.AppEmptyStateView
import com.robiul.mosquetime.ui.components.CommonHeader
import com.robiul.mosquetime.ui.theme.*
import com.robiul.mosquetime.util.HapticUtils

@Composable
fun NoticeBoardScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current

    val allNotices by MosqueRepository.noticesFlow.collectAsState()
    var selectedCategory by remember { mutableStateOf(NoticeCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var activeNoticeForDialog by remember { mutableStateOf<NoticeItem?>(null) }

    val filteredNotices = remember(allNotices, selectedCategory, searchQuery) {
        allNotices.filter { notice ->
            val matchCategory = selectedCategory == NoticeCategory.ALL || notice.category == selectedCategory
            val matchQuery = searchQuery.isBlank() ||
                    notice.title.contains(searchQuery, ignoreCase = true) ||
                    notice.summary.contains(searchQuery, ignoreCase = true) ||
                    notice.fullContent.contains(searchQuery, ignoreCase = true) ||
                    notice.author.contains(searchQuery, ignoreCase = true)
            matchCategory && matchQuery
        }
    }

    val pinnedNotices = remember(filteredNotices) {
        filteredNotices.filter { it.isPinned }
    }

    val regularNotices = remember(filteredNotices) {
        filteredNotices.filter { !it.isPinned }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("notice_board_screen"),
        containerColor = DarkBackground,
        topBar = {
            CommonHeader(
                title = "মসজিদ নোটিশ বোর্ড ও ঘোষণা",
                subtitle = "জরুরি বিজ্ঞপ্তি, জুমার বয়ান ও উন্নয়ন সংবাদ",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar & Filter Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("notice_search_input"),
                    placeholder = {
                        Text(
                            text = "বিজ্ঞপ্তি ও বিষয় অনুসন্ধান করুন...",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedContainerColor = DarkBackground,
                        unfocusedContainerColor = DarkBackground,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = PrimaryGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Filter Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(NoticeCategory.entries.toTypedArray()) { category ->
                        val isSelected = selectedCategory == category
                        val categoryBg = if (isSelected) EmeraldDeep else DarkSurfaceElevated
                        val categoryBorder = if (isSelected) PrimaryGreen else DarkSurfaceBorder
                        val categoryTextColor = if (isSelected) PrimaryGreen else TextMuted

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(categoryBg)
                                .border(1.dp, categoryBorder, RoundedCornerShape(20.dp))
                                .clickable {
                                    HapticUtils.performLongPressHaptic(view)
                                    selectedCategory = category
                                }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = category.titleBn,
                                color = categoryTextColor,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Notices Content List
            if (filteredNotices.isEmpty()) {
                AppEmptyStateView(
                    icon = Icons.Outlined.Campaign,
                    title = "কোনো বিজ্ঞপ্তি পাওয়া যায়নি",
                    subtitle = if (searchQuery.isNotEmpty()) "'$searchQuery'-এর সাথে মিলে এমন কোনো নোটিশ নেই" else "এই ক্যাটাগরিতে বর্তমানে কোনো নোটিশ নেই",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    // Pinned Notices Section
                    if (pinnedNotices.isNotEmpty()) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PushPin,
                                    contentDescription = "Pinned",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "জরুরি ও পিন করা বিজ্ঞপ্তি",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent
                                )
                            }
                        }

                        items(pinnedNotices, key = { "pinned_${it.id}" }) { notice ->
                            NoticeItemCard(
                                notice = notice,
                                onClick = {
                                    HapticUtils.performLongPressHaptic(view)
                                    activeNoticeForDialog = notice
                                },
                                onCopy = {
                                    copyNoticeToClipboard(context, notice)
                                },
                                onShare = {
                                    shareNotice(context, notice)
                                }
                            )
                        }

                        if (regularNotices.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Campaign,
                                        contentDescription = null,
                                        tint = PrimaryGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "সকল সাম্প্রতিক নোটিশ ও সাধারণ ঘোষণা",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                }
                            }
                        }
                    }

                    // Regular Notices Section
                    items(regularNotices, key = { it.id }) { notice ->
                        NoticeItemCard(
                            notice = notice,
                            onClick = {
                                HapticUtils.performLongPressHaptic(view)
                                activeNoticeForDialog = notice
                            },
                            onCopy = {
                                copyNoticeToClipboard(context, notice)
                            },
                            onShare = {
                                shareNotice(context, notice)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    // Notice Detail Full Reader Dialog
    activeNoticeForDialog?.let { notice ->
        NoticeDetailDialog(
            notice = notice,
            onDismiss = { activeNoticeForDialog = null },
            onShare = { shareNotice(context, notice) },
            onCopy = { copyNoticeToClipboard(context, notice) }
        )
    }
}

/**
 * Notice Item Card UI
 */
@Composable
private fun NoticeItemCard(
    notice: NoticeItem,
    onClick: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = when (notice.category) {
        NoticeCategory.URGENT -> RedDigital
        NoticeCategory.SPECIAL -> GoldAccent
        NoticeCategory.JUMAH -> PrimaryGreen
        NoticeCategory.EVENT -> PurpleAccent
        NoticeCategory.GENERAL -> CyanBlue
        NoticeCategory.ALL -> TextMuted
    }

    val cardBorderColor = if (notice.isPinned) GoldAccent.copy(alpha = 0.55f) else DarkSurfaceBorder
    val cardBackground = if (notice.isPinned) {
        Brush.verticalGradient(
            listOf(DarkSurfaceElevated, Color(0xFF1E261E))
        )
    } else {
        Brush.verticalGradient(
            listOf(DarkSurfaceElevated, DarkSurface)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(cardBackground)
            .border(1.dp, cardBorderColor, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            // Header Row: Category Badge + Date + Pin Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(categoryColor.copy(alpha = 0.15f))
                            .border(0.8.dp, categoryColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = notice.category.titleBn,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = categoryColor
                        )
                    }

                    if (notice.isPinned) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GoldAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.PushPin,
                                    contentDescription = "Pinned",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "পিন করা",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent
                                )
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Event,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = notice.publishedDate,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Notice Title
            Text(
                text = notice.title,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Summary Content
            Text(
                text = notice.summary,
                fontSize = 12.5.sp,
                color = TextMuted,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Divider & Actions Footer
            HorizontalDivider(color = DarkSurfaceBorder.copy(alpha = 0.6f))

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Author tag
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalance,
                        contentDescription = null,
                        tint = PrimaryGreen.copy(alpha = 0.8f),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = notice.author,
                        fontSize = 11.sp,
                        color = PrimaryGreen.copy(alpha = 0.9f)
                    )
                }

                // Action Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Copy",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = "সম্পূর্ণ পড়ুন ›",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

/**
 * Notice Detail Modal Reader Dialog
 */
@Composable
private fun NoticeDetailDialog(
    notice: NoticeItem,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onCopy: () -> Unit
) {
    val scrollState = rememberScrollState()

    val categoryColor = when (notice.category) {
        NoticeCategory.URGENT -> RedDigital
        NoticeCategory.SPECIAL -> GoldAccent
        NoticeCategory.JUMAH -> PrimaryGreen
        NoticeCategory.EVENT -> PurpleAccent
        NoticeCategory.GENERAL -> CyanBlue
        NoticeCategory.ALL -> TextMuted
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(18.dp))
                .background(DarkSurfaceElevated)
                .border(1.2.dp, PrimaryGreen.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                // Header Row: Category Badge + Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(categoryColor.copy(alpha = 0.15f))
                            .border(0.8.dp, categoryColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = notice.category.titleBn,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = categoryColor
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title
                Text(
                    text = notice.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Date and Mosque Authority Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Event,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = notice.publishedDate,
                            fontSize = 11.5.sp,
                            color = GoldAccent
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.VerifiedUser,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = notice.author,
                            fontSize = 11.sp,
                            color = PrimaryGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = DarkSurfaceBorder)
                Spacer(modifier = Modifier.height(14.dp))

                // Full Content
                Text(
                    text = notice.fullContent.ifBlank { notice.summary },
                    fontSize = 13.5.sp,
                    color = TextWhite,
                    lineHeight = 21.sp,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = DarkSurfaceBorder)
                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onCopy,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextWhite
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Copy",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("কপি করুন", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onShare,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = DarkBackground,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("শেয়ার করুন", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

private fun copyNoticeToClipboard(context: Context, notice: NoticeItem) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(
        "Notice",
        "📢 ${notice.title}\n\n${notice.fullContent.ifBlank { notice.summary }}\n\n📅 তারিখ: ${notice.publishedDate}\n— চৌধুরী পাটোয়ারী বাড়ি জামে মসজিদ"
    )
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "বিজ্ঞপ্তি ক্লিপবোর্ডে কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
}

private fun shareNotice(context: Context, notice: NoticeItem) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "📢 *${notice.title}*\n\n${notice.fullContent.ifBlank { notice.summary }}\n\n📅 তারিখ: ${notice.publishedDate}\n🏛️ চৌধুরী পাটোয়ারী বাড়ি জামে মসজিদ"
        )
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "বিজ্ঞপ্তি শেয়ার করুন")
    context.startActivity(shareIntent)
}

