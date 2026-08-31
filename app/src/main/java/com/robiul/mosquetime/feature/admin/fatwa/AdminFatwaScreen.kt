package com.robiul.mosquetime.feature.admin.fatwa

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.robiul.mosquetime.data.model.FatwaArticle
import com.robiul.mosquetime.data.model.FatwaCategory
import com.robiul.mosquetime.data.model.UserQuestionSubmission
import com.robiul.mosquetime.ui.theme.CyanBlue
import com.robiul.mosquetime.ui.theme.DarkBackground
import com.robiul.mosquetime.ui.theme.DarkGreen
import com.robiul.mosquetime.ui.theme.DarkGreenBorder
import com.robiul.mosquetime.ui.theme.DarkSurface
import com.robiul.mosquetime.ui.theme.DarkSurfaceBorder
import com.robiul.mosquetime.ui.theme.DarkSurfaceElevated
import com.robiul.mosquetime.ui.theme.GoldAccent
import com.robiul.mosquetime.ui.theme.NeonGreenGlow
import com.robiul.mosquetime.ui.theme.PrimaryGreen
import com.robiul.mosquetime.ui.theme.PurpleAccent
import com.robiul.mosquetime.ui.theme.RedDigital
import com.robiul.mosquetime.ui.theme.TextMuted
import com.robiul.mosquetime.ui.theme.TextWhite

@Composable
fun AdminFatwaScreen(
    onBackClick: () -> Unit,
    viewModel: AdminFatwaViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        floatingActionButton = {
            if (uiState.selectedTab == 1) {
                FloatingActionButton(
                    onClick = { viewModel.openAddFatwaDialog() },
                    containerColor = PrimaryGreen,
                    contentColor = DarkBackground,
                    shape = CircleShape,
                    modifier = Modifier.testTag("add_fatwa_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "ফতোয়া যোগ করুন")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .border(0.5.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(0.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(DarkBackground)
                            .border(1.dp, DarkGreenBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ইমামের ফতোয়া ও প্রশ্ন ব্যাংক",
                            color = GoldAccent,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "মুসুল্লিদের প্রশ্নের উত্তর ও ফতোয়া ব্যবস্থাপনা",
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }

            // Tab Navigation
            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = DarkSurface,
                contentColor = GoldAccent,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                        color = GoldAccent
                    )
                }
            ) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.onTabSelected(0) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.HelpOutline,
                                contentDescription = null,
                                tint = if (uiState.selectedTab == 0) CyanBlue else TextMuted,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "মুসুল্লিদের প্রশ্ন (${uiState.userQuestions.size})",
                                color = if (uiState.selectedTab == 0) TextWhite else TextMuted,
                                fontSize = 12.sp,
                                fontWeight = if (uiState.selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                )

                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.onTabSelected(1) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.QuestionAnswer,
                                contentDescription = null,
                                tint = if (uiState.selectedTab == 1) GoldAccent else TextMuted,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ফতোয়া ব্যাংক (${uiState.fatwaList.size})",
                                color = if (uiState.selectedTab == 1) TextWhite else TextMuted,
                                fontSize = 12.sp,
                                fontWeight = if (uiState.selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                )
            }

            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = {
                    Text(
                        if (uiState.selectedTab == 0) "প্রশ্নকারী বা বিবরণ অনুসন্ধান..." else "ফতোয়া, হাদিস বা মাসআলা অনুসন্ধান...",
                        color = TextMuted,
                        fontSize = 12.5.sp
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            )

            if (uiState.selectedTab == 0) {
                // User Questions Filters
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "ALL" to "সকল প্রশ্ন (${uiState.userQuestions.size})",
                        "PENDING" to "অপেক্ষমান (${uiState.userQuestions.count { it.replyText.isBlank() }})",
                        "ANSWERED" to "উত্তর প্রদত্ত (${uiState.userQuestions.count { it.replyText.isNotBlank() }})"
                    ).forEach { (key, label) ->
                        val isSelected = uiState.questionFilterStatus == key
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) DarkGreen else DarkSurface)
                                .border(1.dp, if (isSelected) PrimaryGreen else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                .clickable { viewModel.onQuestionFilterStatusChanged(key) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) NeonGreenGlow else TextMuted,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // Questions List
                if (uiState.filteredQuestions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "কোনো প্রশ্ন পাওয়া যায়নি",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.filteredQuestions, key = { it.id }) { question ->
                            UserQuestionItemCard(
                                question = question,
                                onReply = { viewModel.openReplyDialog(question) },
                                onDelete = { viewModel.deleteUserQuestion(question) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(60.dp)) }
                    }
                }
            } else {
                // Fatwa Library Category Filter
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(FatwaCategory.values()) { category ->
                        val isSelected = (category == uiState.selectedCategory)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) DarkGreen else DarkSurface)
                                .border(1.dp, if (isSelected) PrimaryGreen else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                .clickable { viewModel.onCategorySelected(category) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = category.titleBn,
                                color = if (isSelected) NeonGreenGlow else TextWhite,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // Fatwa Library List
                if (uiState.filteredFatwas.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "কোনো ফতোয়া পাওয়া যায়নি",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    val expandedMap = remember { mutableStateMapOf<String, Boolean>() }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.filteredFatwas, key = { it.id }) { fatwa ->
                            val isExpanded = expandedMap[fatwa.id] ?: false
                            AdminFatwaItemCard(
                                fatwa = fatwa,
                                isExpanded = isExpanded,
                                onToggleExpand = { expandedMap[fatwa.id] = !isExpanded },
                                onEdit = { viewModel.openEditFatwaDialog(fatwa) },
                                onDelete = { viewModel.deleteFatwa(fatwa) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(70.dp)) }
                    }
                }
            }
        }
    }

    // Reply Dialog
    uiState.replyingQuestion?.let { question ->
        ReplyQuestionDialog(
            question = question,
            onDismiss = { viewModel.closeReplyDialog() },
            onSubmit = { replyText, answeredBy, publishToBank, ref ->
                viewModel.submitReplyToQuestion(question, replyText, answeredBy, publishToBank, ref)
            }
        )
    }

    // Add / Edit Fatwa Dialog
    if (uiState.isAddEditFatwaOpen) {
        AddEditFatwaDialog(
            editingFatwa = uiState.editingFatwa,
            onDismiss = { viewModel.closeFatwaDialog() },
            onSave = { id, q, a, cat, ansBy, ref, date ->
                viewModel.saveFatwa(id, q, a, cat, ansBy, ref, date)
            }
        )
    }
}

@Composable
private fun UserQuestionItemCard(
    question: UserQuestionSubmission,
    onReply: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isAnswered = question.replyText.isNotBlank()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceElevated)
            .border(
                1.dp,
                if (isAnswered) DarkGreenBorder else GoldAccent.copy(alpha = 0.5f),
                RoundedCornerShape(12.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isAnswered) DarkGreen else DarkSurface)
                        .border(0.8.dp, if (isAnswered) NeonGreenGlow else GoldAccent, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = question.category.titleBn,
                        color = if (isAnswered) NeonGreenGlow else GoldAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isAnswered) PrimaryGreen.copy(alpha = 0.2f) else RedDigital.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isAnswered) "উত্তর প্রদত্ত" else "অপেক্ষমান",
                        color = if (isAnswered) PrimaryGreen else RedDigital,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sender Information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "প্রশ্নকারী: ${question.senderName}",
                    color = TextWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                if (question.senderPhone.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkBackground)
                            .clickable {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${question.senderPhone}"))
                                context.startActivity(intent)
                            }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(question.senderPhone, color = CyanBlue, fontSize = 10.5.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Question Text
            Text(
                text = question.questionText,
                color = TextWhite,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            // Existing Reply if available
            if (isAnswered) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(thickness = 0.5.dp, color = DarkSurfaceBorder)
                Spacer(modifier = Modifier.height(6.dp))
                Text("প্রদত্ত উত্তর:", color = GoldAccent, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(question.replyText, color = TextWhite, fontSize = 12.sp, lineHeight = 17.sp)
                if (question.repliedBy.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("উত্তরদাতা: ${question.repliedBy} (${question.replyDateBn})", color = CyanBlue, fontSize = 10.5.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedDigital, modifier = Modifier.size(16.dp))
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryGreen)
                        .clickable { onReply() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (isAnswered) Icons.Default.Edit else Icons.Default.Reply,
                            contentDescription = null,
                            tint = DarkBackground,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAnswered) "উত্তর সম্পাদন" else "উত্তর প্রদান করুন",
                            color = DarkBackground,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminFatwaItemCard(
    fatwa: FatwaArticle,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkGreenBorder.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .clickable { onToggleExpand() }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkGreen)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(fatwa.category.titleBn, color = NeonGreenGlow, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = fatwa.questionBn,
                        color = TextWhite,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 18.sp
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = CyanBlue,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (isExpanded) {
                HorizontalDivider(thickness = 0.5.dp, color = DarkSurfaceBorder)
                Spacer(modifier = Modifier.height(8.dp))

                Text("উত্তর:", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(fatwa.answerBn, color = TextWhite, fontSize = 12.5.sp, lineHeight = 18.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    Text("উত্তরদাতা: ${fatwa.answeredBy}", color = TextMuted, fontSize = 10.5.sp)
                    Text("রেফারেন্স: ${fatwa.referenceBn}", color = CyanBlue, fontSize = 10.5.sp)
                    Text("তারিখ / বছর: ${fatwa.dateBn}", color = TextMuted, fontSize = 10.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Fatwa", "প্রশ্ন: ${fatwa.questionBn}\n\nউত্তর: ${fatwa.answerBn}\n\n[রেফারেন্স: ${fatwa.referenceBn}]")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "ফতোয়া কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyanBlue, modifier = Modifier.size(16.dp))
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = GoldAccent, modifier = Modifier.size(16.dp))
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedDigital, modifier = Modifier.size(16.dp))
                    }
                }
            } else {
                Text(
                    text = "${fatwa.answerBn.take(80)}... (বিস্তারিত দেখতে ট্যাপ করুন)",
                    color = TextMuted,
                    fontSize = 11.5.sp
                )
            }
        }
    }
}

@Composable
private fun ReplyQuestionDialog(
    question: UserQuestionSubmission,
    onDismiss: () -> Unit,
    onSubmit: (replyText: String, answeredBy: String, publishToBank: Boolean, reference: String) -> Unit
) {
    var replyText by remember { mutableStateOf(question.replyText) }
    var answeredBy by remember { mutableStateOf(question.repliedBy.ifBlank { "মুফতি মাওলানা আব্দুল ওয়াদুদ" }) }
    var publishToBank by remember { mutableStateOf(false) }
    var reference by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text("প্রশ্নের উত্তর প্রদান", color = GoldAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkBackground)
                        .padding(10.dp)
                ) {
                    Column {
                        Text("প্রশ্নকারী: ${question.senderName} (${question.category.titleBn})", color = CyanBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(question.questionText, color = TextWhite, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    label = { Text("ইমাম সাহেবের উত্তর *", color = TextMuted) },
                    placeholder = { Text("শরয়ী বিধান ও সমাধান লিখুন...", color = TextMuted) },
                    minLines = 4,
                    maxLines = 7,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = answeredBy,
                    onValueChange = { answeredBy = it },
                    label = { Text("উত্তরদাতার নাম ও পদবী", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { publishToBank = !publishToBank }
                ) {
                    Switch(
                        checked = publishToBank,
                        onCheckedChange = { publishToBank = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DarkBackground,
                            checkedTrackColor = PrimaryGreen
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("সাধারণ ফতোয়া ব্যাংকে প্রকাশ করুন", color = TextWhite, fontSize = 12.sp)
                }

                if (publishToBank) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reference,
                        onValueChange = { reference = it },
                        label = { Text("হাদিস বা কিতাবের রেফারেন্স", color = TextMuted) },
                        placeholder = { Text("যেমন: সহীহ মুসলিম, ফতোয়ায়ে শামী", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(replyText, answeredBy, publishToBank, reference) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = DarkBackground)
            ) {
                Text("উত্তর সংরক্ষণ করুন", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("বাতিল", color = TextMuted)
            }
        }
    )
}

@Composable
private fun AddEditFatwaDialog(
    editingFatwa: FatwaArticle?,
    onDismiss: () -> Unit,
    onSave: (id: String?, q: String, a: String, cat: FatwaCategory, ansBy: String, ref: String, date: String) -> Unit
) {
    var questionBn by remember { mutableStateOf(editingFatwa?.questionBn ?: "") }
    var answerBn by remember { mutableStateOf(editingFatwa?.answerBn ?: "") }
    var selectedCategory by remember { mutableStateOf(editingFatwa?.category ?: FatwaCategory.SALAT) }
    var answeredBy by remember { mutableStateOf(editingFatwa?.answeredBy ?: "মুফতি মাওলানা আব্দুল ওয়াদুদ (খতিব)") }
    var referenceBn by remember { mutableStateOf(editingFatwa?.referenceBn ?: "সহীহ বুখারী, ফতোয়ায়ে শামী") }
    var dateBn by remember { mutableStateOf(editingFatwa?.dateBn ?: "২০২৫") }
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = if (editingFatwa == null) "নতুন ফতোয়া সংযোজন" else "ফতোয়া সম্পাদন",
                color = GoldAccent,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                Text("ক্যাটাগরি নির্বাচন করুন:", color = TextMuted, fontSize = 11.5.sp)
                Spacer(modifier = Modifier.height(4.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(FatwaCategory.values().filter { it != FatwaCategory.ALL }) { cat ->
                        val isSelected = cat == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) DarkGreen else DarkBackground)
                                .border(1.dp, if (isSelected) PrimaryGreen else DarkSurfaceBorder, RoundedCornerShape(6.dp))
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = cat.titleBn,
                                color = if (isSelected) NeonGreenGlow else TextWhite,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = questionBn,
                    onValueChange = { questionBn = it },
                    label = { Text("প্রশ্ন / মাসআলার শিরোনাম *", color = TextMuted) },
                    placeholder = { Text("যেমন: জামাতে নামাজ পড়ার সময়...", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = answerBn,
                    onValueChange = { answerBn = it },
                    label = { Text("ফতোয়া ও বিস্তারিত উত্তর *", color = TextMuted) },
                    placeholder = { Text("কুরআন ও হাদিসের আলোকে বিস্তারিত সমাধান...", color = TextMuted) },
                    minLines = 4,
                    maxLines = 7,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = answeredBy,
                    onValueChange = { answeredBy = it },
                    label = { Text("উত্তরদাতার নাম ও পদবী", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = referenceBn,
                    onValueChange = { referenceBn = it },
                    label = { Text("রেফারেন্স ও কিতাব", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = dateBn,
                    onValueChange = { dateBn = it },
                    label = { Text("তারিখ / মাস", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        editingFatwa?.id,
                        questionBn,
                        answerBn,
                        selectedCategory,
                        answeredBy,
                        referenceBn,
                        dateBn
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = DarkBackground)
            ) {
                Text("সংরক্ষণ করুন", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("বাতিল", color = TextMuted)
            }
        }
    )
}
