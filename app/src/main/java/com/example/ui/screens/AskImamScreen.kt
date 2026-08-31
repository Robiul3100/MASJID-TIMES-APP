package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppDatabase
import com.example.data.local.entity.UserQuestionEntity
import com.example.data.model.FatwaArticle
import com.example.data.model.FatwaCategory
import com.example.data.model.UserQuestionSubmission
import com.example.data.repository.MosqueRepository
import com.example.ui.components.CommonHeader
import com.example.ui.theme.CyanBlue
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.DarkGreenBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.RedDigital
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AskImamScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AskImamViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonHeader(
            title = "ইমামকে প্রশ্ন ও ফতোয়া ব্যাংক",
            subtitle = "মাসআলা জিজ্ঞাসা ও শরীয়াহ সমাধান সহায়িকা",
            onBackClick = onBackClick
        )

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = DarkSurface,
            contentColor = PrimaryGreen,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = PrimaryGreen,
                    height = 2.5.dp
                )
            },
            divider = { HorizontalDivider(thickness = 0.5.dp, color = DarkSurfaceBorder) }
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.QuestionAnswer, contentDescription = null, tint = if (selectedTabIndex == 0) GoldAccent else TextMuted, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ফতোয়া ও মাসআলা", color = if (selectedTabIndex == 0) TextWhite else TextMuted, fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
                    }
                }
            )

            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HelpOutline, contentDescription = null, tint = if (selectedTabIndex == 1) CyanBlue else TextMuted, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("প্রশ্ন জিজ্ঞাসা করুন", color = if (selectedTabIndex == 1) TextWhite else TextMuted, fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
                    }
                }
            )
        }

        val fatwas by MosqueRepository.fatwaListFlow.collectAsState()

        if (selectedTabIndex == 0) {
            FatwaLibraryTab(fatwas = fatwas)
        } else {
            AskQuestionFormTab(viewModel)
        }
    }
}

@Composable
private fun FatwaLibraryTab(fatwas: List<FatwaArticle>) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FatwaCategory.ALL) }
    val expandedMap = remember { mutableStateMapOf<String, Boolean>() }

    val filteredFatwas = remember(searchQuery, selectedCategory) {
        fatwas.filter { article ->
            val matchCat = (selectedCategory == FatwaCategory.ALL || article.category == selectedCategory)
            val matchQuery = searchQuery.isBlank() ||
                    article.questionBn.contains(searchQuery, ignoreCase = true) ||
                    article.answerBn.contains(searchQuery, ignoreCase = true) ||
                    article.referenceBn.contains(searchQuery, ignoreCase = true)
            matchCat && matchQuery
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("মাসআলা বা ফতোয়া খুঁজুন...", color = TextMuted, fontSize = 13.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(18.dp))
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
                .padding(horizontal = 14.dp, vertical = 6.dp)
        )

        // Categories Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(FatwaCategory.values()) { category ->
                val isSelected = (category == selectedCategory)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) DarkGreen else DarkSurface)
                        .border(1.dp, if (isSelected) PrimaryGreen else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                        .clickable { selectedCategory = category }
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

        // Fatwa List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredFatwas, key = { it.id }) { fatwa ->
                val isExpanded = expandedMap[fatwa.id] ?: false

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, DarkGreenBorder.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .clickable { expandedMap[fatwa.id] = !isExpanded }
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
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryGreen.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("প্র", color = PrimaryGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = fatwa.questionBn,
                                    color = TextWhite,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 19.sp
                                )
                            }

                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = CyanBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Answer snippet or full view
                        Spacer(modifier = Modifier.height(8.dp))

                        if (isExpanded) {
                            HorizontalDivider(thickness = 0.5.dp, color = DarkSurfaceBorder)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "উত্তর:",
                                color = GoldAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = fatwa.answerBn,
                                color = TextWhite,
                                fontSize = 12.5.sp,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "উত্তরদাতা: ${fatwa.answeredBy}",
                                        color = TextMuted,
                                        fontSize = 10.5.sp
                                    )
                                    Text(
                                        text = "রেফারেন্স: ${fatwa.referenceBn}",
                                        color = CyanBlue,
                                        fontSize = 10.5.sp
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Fatwa", "প্রশ্ন: ${fatwa.questionBn}\n\nউত্তর: ${fatwa.answerBn}\n\n[রেফারেন্স: ${fatwa.referenceBn}]")
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "ফতোয়া কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = GoldAccent, modifier = Modifier.size(16.dp))
                                }
                            }
                        } else {
                            Text(
                                text = "${fatwa.answerBn.take(90)}... (বিস্তারিত দেখতে ট্যাপ করুন)",
                                color = TextMuted,
                                fontSize = 11.5.sp
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun AskQuestionFormTab(
    viewModel: AskImamViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userQuestions by viewModel.userQuestions.collectAsState()

    var nameText by remember { mutableStateOf("") }
    var phoneText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FatwaCategory.SALAT) }
    var questionText by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Imam Greeting Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkGreen.copy(alpha = 0.5f))
                .border(1.dp, PrimaryGreen.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = "মুফতি ও ইমাম সাহেবের নিকট সরাসরি প্রশ্ন করুন",
                    color = GoldAccent,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "যেকোনো শরয়ী জটিলতা, নামাজ, রোজা, জাকাত বা পারিবারিক মাসআলা সম্পর্কে প্রশ্ন পাঠালে ইমাম সাহেব পর্যালোচনা করে দ্রুত জবাব প্রদান করবেন।",
                    color = TextWhite,
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp
                )
            }
        }

        // Form Fields
        OutlinedTextField(
            value = nameText,
            onValueChange = { nameText = it },
            label = { Text("আপনার নাম *", color = TextMuted) },
            placeholder = { Text("যেমন: মো. আব্দুল্লাহ", color = TextMuted) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = DarkSurfaceBorder,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface
            ),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phoneText,
            onValueChange = { phoneText = it },
            label = { Text("মোবাইল নম্বর *", color = TextMuted) },
            placeholder = { Text("যেমন: 017XXXXXXXX", color = TextMuted) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = DarkSurfaceBorder,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface
            ),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text("প্রশ্নের বিভাগ নির্বাচন করুন:", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)

        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(FatwaCategory.values().filter { it != FatwaCategory.ALL }) { cat ->
                val isSelected = (cat == selectedCategory)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) DarkGreen else DarkSurface)
                        .border(1.dp, if (isSelected) PrimaryGreen else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = cat.titleBn,
                        color = if (isSelected) NeonGreenGlow else TextWhite,
                        fontSize = 11.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        OutlinedTextField(
            value = questionText,
            onValueChange = { questionText = it },
            label = { Text("আপনার প্রশ্নটি বিস্তারিত লিখুন *", color = TextMuted) },
            placeholder = { Text("মাসআলা বা সমস্যা স্পষ্টভাবে বর্ণনা করুন...", color = TextMuted) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = DarkSurfaceBorder,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface
            ),
            shape = RoundedCornerShape(10.dp),
            minLines = 4,
            maxLines = 6,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { isPrivate = !isPrivate }
        ) {
            Checkbox(
                checked = isPrivate,
                onCheckedChange = { isPrivate = it },
                colors = CheckboxDefaults.colors(checkedColor = PrimaryGreen, uncheckedColor = DarkSurfaceBorder, checkmarkColor = DarkBackground)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("প্রশ্নটি সম্পূর্ণ গোপনীয় ও ব্যক্তিগত রাখতে চাই", color = TextMuted, fontSize = 12.sp)
        }

        // Submit Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(PrimaryGreen)
                .clickable {
                    if (nameText.isBlank() || questionText.isBlank()) {
                        Toast.makeText(context, "অনুগ্রহ করে নাম এবং প্রশ্ন লিখুন", Toast.LENGTH_SHORT).show()
                    } else {
                        coroutineScope.launch(Dispatchers.IO) {
                            val newQ = UserQuestionSubmission(
                                id = UUID.randomUUID().toString(),
                                senderName = nameText,
                                senderPhone = phoneText,
                                category = selectedCategory,
                                questionText = questionText,
                                isPrivate = isPrivate
                            )
                            viewModel.submitQuestion(UserQuestionEntity.fromDomainModel(newQ))
                        }
                        Toast.makeText(context, "প্রশ্নটি সফলভাবে ইমাম সাহেবের কাছে পাঠানো হয়েছে", Toast.LENGTH_LONG).show()
                        questionText = ""
                    }
                }
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Send, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("ইমাম সাহেবের কাছে প্রশ্ন জমা দিন", color = DarkBackground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // History of User Submissions
        if (userQuestions.isNotEmpty()) {
            Text("আমার জমাকৃত প্রশ্নসমূহ (${userQuestions.size})", color = GoldAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)

            userQuestions.forEach { q ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurface)
                        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(q.categoryName, color = CyanBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(DarkGreen)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(q.status, color = NeonGreenGlow, fontSize = 9.5.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(q.questionText, color = TextWhite, fontSize = 12.sp)

                        if (q.replyText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(thickness = 0.5.dp, color = DarkSurfaceBorder)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("ইমাম সাহেবের উত্তর:", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(q.replyText, color = TextWhite, fontSize = 11.5.sp, lineHeight = 16.sp)
                            if (q.repliedBy.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("উত্তরদাতা: ${q.repliedBy} ${if (q.replyDateBn.isNotBlank()) "(${q.replyDateBn})" else ""}", color = CyanBlue, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
