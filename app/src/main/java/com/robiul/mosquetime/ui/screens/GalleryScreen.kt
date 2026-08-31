package com.robiul.mosquetime.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.robiul.mosquetime.data.model.GalleryCategory
import com.robiul.mosquetime.data.model.GalleryItem
import com.robiul.mosquetime.data.repository.MosqueRepository
import com.robiul.mosquetime.ui.components.AppEmptyStateView
import com.robiul.mosquetime.ui.components.CommonHeader
import com.robiul.mosquetime.ui.components.MosqueCrestIcon
import com.robiul.mosquetime.ui.theme.*
import com.robiul.mosquetime.util.HapticUtils

@Composable
fun GalleryScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current

    var selectedCategory by remember { mutableStateOf(GalleryCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var activeViewerIndex by remember { mutableStateOf<Int?>(null) }

    val allGalleryItems = MosqueRepository.galleryItems

    val filteredItems = remember(selectedCategory, searchQuery, allGalleryItems) {
        allGalleryItems.filter { item ->
            val matchCategory = selectedCategory == GalleryCategory.ALL || item.category == selectedCategory
            val matchQuery = searchQuery.isBlank() ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.description.contains(searchQuery, ignoreCase = true) ||
                    item.date.contains(searchQuery, ignoreCase = true)
            matchCategory && matchQuery
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("gallery_screen"),
        containerColor = DarkBackground,
        topBar = {
            CommonHeader(
                title = "ফটো ও মিডিয়া গ্যালারি",
                subtitle = "মসজিদের স্থাপত্য, জামাত ও আয়োজনের স্থিরচিত্র",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search and Category Section
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
                        .testTag("gallery_search_input"),
                    placeholder = {
                        Text(
                            text = "ছবির নাম, বিবরণ বা উপলক্ষ খুঁজুন...",
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

                // Category Filter Chips with Count Badges
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(GalleryCategory.entries.toTypedArray()) { cat ->
                        val isSelected = selectedCategory == cat
                        val count = if (cat == GalleryCategory.ALL) {
                            allGalleryItems.size
                        } else {
                            allGalleryItems.count { it.category == cat }
                        }

                        val bg = if (isSelected) EmeraldDeep else DarkSurfaceElevated
                        val border = if (isSelected) PrimaryGreen else DarkSurfaceBorder
                        val textColor = if (isSelected) PrimaryGreen else TextMuted

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(bg)
                                .border(1.dp, border, RoundedCornerShape(16.dp))
                                .clickable {
                                    HapticUtils.performLongPressHaptic(view)
                                    selectedCategory = cat
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = cat.titleBn,
                                    color = textColor,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (isSelected) PrimaryGreen else DarkSurfaceBorder)
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "$count",
                                        color = if (isSelected) DarkBackground else TextWhite,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Gallery Grid Content
            if (filteredItems.isEmpty()) {
                AppEmptyStateView(
                    icon = Icons.Outlined.PhotoLibrary,
                    title = "কোনো ছবি পাওয়া যায়নি",
                    subtitle = if (searchQuery.isNotEmpty()) "'$searchQuery'-এর সাথে মিলে এমন কোনো ছবি নেই" else "এই ক্যাটাগরিতে বর্তমানে কোনো স্থিরচিত্র নেই",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
                ) {
                    itemsIndexed(filteredItems, key = { _, item -> item.id }) { index, item ->
                        GalleryGridCardItem(
                            item = item,
                            onClick = {
                                HapticUtils.performLongPressHaptic(view)
                                activeViewerIndex = index
                            }
                        )
                    }
                }
            }
        }
    }

    // Full-Screen Image Viewer Lightbox Modal Dialog
    activeViewerIndex?.let { index ->
        val currentItem = filteredItems.getOrNull(index) ?: return@let

        GalleryLightboxDialog(
            item = currentItem,
            currentIndex = index,
            totalCount = filteredItems.size,
            hasPrevious = index > 0,
            hasNext = index < filteredItems.size - 1,
            onDismiss = { activeViewerIndex = null },
            onPrevious = {
                HapticUtils.performLongPressHaptic(view)
                activeViewerIndex = index - 1
            },
            onNext = {
                HapticUtils.performLongPressHaptic(view)
                activeViewerIndex = index + 1
            },
            onShare = {
                shareGalleryPhoto(context, currentItem)
            },
            onCopyCaption = {
                copyCaptionToClipboard(context, currentItem.title, currentItem.description)
            }
        )
    }
}

/**
 * Modern 2-Column Gallery Grid Card
 */
@Composable
private fun GalleryGridCardItem(
    item: GalleryItem,
    onClick: () -> Unit
) {
    val categoryColor = when (item.category) {
        GalleryCategory.ARCHITECTURE -> GoldAccent
        GalleryCategory.JUMAH -> CyanBlue
        GalleryCategory.RAMADAN -> NeonGreenGlow
        GalleryCategory.EVENTS -> PurpleAccent
        GalleryCategory.CONSTRUCTION -> PrimaryGreen
        else -> TextWhite
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurface)
            .border(1.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(10.dp)
            .testTag("gallery_item_${item.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Photo Representation Canvas with Islamic Aesthetics
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1B3D2B), DarkSurfaceElevated)
                        )
                    )
                    .border(0.8.dp, DarkSurfaceBorder, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    MosqueCrestIcon(modifier = Modifier.size(44.dp, 32.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkBackground.copy(alpha = 0.7f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.category.titleBn,
                            color = categoryColor,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                color = TextWhite,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.date,
                    color = CyanBlue,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(Icons.Outlined.Visibility, contentDescription = "View", tint = TextMuted, modifier = Modifier.size(13.dp))
            }
        }
    }
}

/**
 * Full Screen Lightbox / Image Viewer Dialog
 */
@Composable
private fun GalleryLightboxDialog(
    item: GalleryItem,
    currentIndex: Int,
    totalCount: Int,
    hasPrevious: Boolean,
    hasNext: Boolean,
    onDismiss: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onShare: () -> Unit,
    onCopyCaption: () -> Unit
) {
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(18.dp))
                .background(DarkSurfaceElevated)
                .border(1.2.dp, PrimaryGreen.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                // Top Action Bar (Index count and Close)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(EmeraldDeep)
                            .border(0.8.dp, PrimaryGreen, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${currentIndex + 1} / $totalCount",
                            color = PrimaryGreen,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = onCopyCaption, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy Caption", tint = TextMuted, modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = onShare, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.Outlined.Share, contentDescription = "Share", tint = GoldAccent, modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = onDismiss, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextWhite, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // High Fidelity Islamic Canvas Representation Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1B3D2B), DarkBackground)
                            )
                        )
                        .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        MosqueCrestIcon(modifier = Modifier.size(68.dp, 48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = item.title,
                            color = NeonGreenGlow,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "বায়তুল আমান জামে মসজিদ কমপ্লেক্স",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = item.title,
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 21.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "তারিখ/সময়কাল: ${item.date}",
                        color = GoldAccent,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = item.description,
                    color = TextWhite.copy(alpha = 0.9f),
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = DarkSurfaceBorder)
                Spacer(modifier = Modifier.height(12.dp))

                // Navigation Controls (Previous and Next Buttons)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onPrevious,
                        enabled = hasPrevious,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = if (hasPrevious) PrimaryGreen else TextMuted),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (hasPrevious) PrimaryGreen else DarkSurfaceBorder),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("পূর্ববর্তী", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = onNext,
                        enabled = hasNext,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen,
                            disabledContainerColor = DarkSurface
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("পরবর্তী", color = if (hasNext) DarkBackground else TextMuted, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = if (hasNext) DarkBackground else TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

private fun shareGalleryPhoto(context: Context, item: GalleryItem) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "📸 *মসজিদ ফটো গ্যালারি*\n\n🖼️ *শিরোনাম:* ${item.title}\n📅 *সময়কাল:* ${item.date}\n📌 *ক্যাটাগরি:* ${item.category.titleBn}\n\n📖 *বিবরণ:* ${item.description}\n\n— চৌধুরী পাটোয়ারী বাড়ি জামে মসজিদ ও ইসলামিক সেন্টার"
        )
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "স্থিরচিত্রের বিবরণ শেয়ার করুন")
    context.startActivity(shareIntent)
}

private fun copyCaptionToClipboard(context: Context, title: String, description: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val text = "$title\n$description\n— বায়তুল আমান জামে মসজিদ"
    val clip = ClipData.newPlainText("Gallery Caption", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "ক্যাপশন কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
}
