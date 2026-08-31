package com.robiul.mosquetime.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.robiul.mosquetime.data.local.entity.UserQuestionEntity
import com.robiul.mosquetime.data.model.FatwaArticle
import com.robiul.mosquetime.data.model.FatwaCategory
import com.robiul.mosquetime.data.model.UserQuestionSubmission
import com.robiul.mosquetime.data.repository.MosqueRepository
import com.robiul.mosquetime.ui.components.CommonHeader
import com.robiul.mosquetime.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun AskImamScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AskImamViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val fatwas by MosqueRepository.fatwaListFlow.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            CommonHeader(
                title = "ইমামকে প্রশ্ন ও ফতোয়া ব্যাংক",
                subtitle = "মাসআলা জিজ্ঞাসা ও শরীয়াহ সমাধান সহায়িকা",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = DarkSurfaceElevated,
                contentColor = PrimaryGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = PrimaryGreen,
                        height = 3.dp
                    )
                },
                divider = { HorizontalDivider(thickness = 0.5.dp, color = DarkGreenBorder.copy(alpha = 0.5f)) }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.QuestionAnswer, contentDescription = null, tint = if (selectedTabIndex == 0) GoldAccent else TextMuted, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "ফতোয়া ও মাসআলা (${fatwas.size})",
                                color = if (selectedTabIndex == 0) TextWhite else TextMuted,
                                fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.5.sp,
                                fontFamily = SolaimanLipiFontFamily
                            )
                        }
                    }
                )

                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, tint = if (selectedTabIndex == 1) CyanBlue else TextMuted, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "প্রশ্ন জিজ্ঞাসা করুন",
                                color = if (selectedTabIndex == 1) TextWhite else TextMuted,
                                fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.5.sp,
                                fontFamily = SolaimanLipiFontFamily
                            )
                        }
                    }
                )
            }

            if (selectedTabIndex == 0) {
                FatwaLibraryTab(fatwas = fatwas)
            } else {
                AskQuestionFormTab(viewModel)
            }
        }
    }
}

@Composable
private fun FatwaLibraryTab(fatwas: List<FatwaArticle>) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FatwaCategory.ALL) }
    val expandedMap = remember { mutableStateMapOf<String, Boolean>() }

    val filteredFatwas = remember(searchQuery, selectedCategory, fatwas) {
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
            placeholder = { Text("মাসআলা বা ফতোয়া খুঁজুন...", color = TextMuted, fontSize = 13.sp, fontFamily = SolaimanLipiFontFamily) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(20.dp))
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(18.dp))
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = DarkGreenBorder.copy(alpha = 0.5f),
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedContainerColor = DarkSurfaceElevated,
                unfocusedContainerColor = DarkSurfaceElevated
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Categories Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(FatwaCategory.values()) { category ->
                val isSelected = (category == selectedCategory)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) DarkGreen else DarkSurfaceElevated)
                        .border(1.dp, if (isSelected) PrimaryGreen else DarkGreenBorder.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = category.titleBn,
                        color = if (isSelected) NeonGreenGlow else TextWhite,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = SolaimanLipiFontFamily
                    )
                }
            }
        }

        // Fatwa List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredFatwas.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkSurfaceElevated)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("কোনো মাসআলা বা ফতোয়া পাওয়া যায়নি", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                            Text("অন্য কোনো শব্দ দিয়ে অনুসন্ধান করুন অথবা ইমাম সাহেবকে সরাসরি প্রশ্ন করুন।", color = TextMuted, fontSize = 12.sp, fontFamily = SolaimanLipiFontFamily)
                        }
                    }
                }
            } else {
                items(filteredFatwas, key = { it.id }) { fatwa ->
                    val isExpanded = expandedMap[fatwa.id] ?: false

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, if (isExpanded) PrimaryGreen.copy(alpha = 0.8f) else DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
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
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(PrimaryGreen.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("প্র", color = PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = fatwa.questionBn,
                                        color = TextWhite,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 20.sp,
                                        fontFamily = SolaimanLipiFontFamily
                                    )
                                }

                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = CyanBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (isExpanded) {
                                HorizontalDivider(thickness = 0.5.dp, color = DarkGreenBorder.copy(alpha = 0.4f))
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "শরয়ী উত্তর ও ফতোয়া:",
                                        color = GoldAccent,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = SolaimanLipiFontFamily
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = fatwa.answerBn,
                                    color = TextWhite.copy(alpha = 0.9f),
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    fontFamily = SolaimanLipiFontFamily
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Reference Box
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DarkSurface)
                                        .border(0.6.dp, DarkGreenBorder.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "উত্তরদাতা: ${fatwa.answeredBy}",
                                                color = GoldAccent,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Medium,
                                                fontFamily = SolaimanLipiFontFamily
                                            )
                                            if (fatwa.referenceBn.isNotBlank()) {
                                                Text(
                                                    text = "রেফারেন্স: ${fatwa.referenceBn}",
                                                    color = CyanBlue,
                                                    fontSize = 11.sp,
                                                    fontFamily = SolaimanLipiFontFamily
                                                )
                                            }
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            // Copy
                                            IconButton(
                                                onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    val clip = ClipData.newPlainText("Fatwa", "প্রশ্ন: ${fatwa.questionBn}\n\nউত্তর: ${fatwa.answerBn}\n\n[উত্তরদাতা: ${fatwa.answeredBy} | রেফারেন্স: ${fatwa.referenceBn}]")
                                                    clipboard.setPrimaryClip(clip)
                                                    Toast.makeText(context, "ফতোয়া কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = GoldAccent, modifier = Modifier.size(16.dp))
                                            }

                                            // Share
                                            IconButton(
                                                onClick = {
                                                    val shareText = "🕌 ফতোয়া ও শরয়ী সমাধান:\n\n❓ প্রশ্ন: ${fatwa.questionBn}\n\n✅ উত্তর: ${fatwa.answerBn}\n\n📚 রেফারেন্স: ${fatwa.referenceBn}\n👤 উত্তরদাতা: ${fatwa.answeredBy}\n\n- বায়তুল আমান জামে মসজিদ"
                                                    val sendIntent = Intent().apply {
                                                        action = Intent.ACTION_SEND
                                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                                        type = "text/plain"
                                                    }
                                                    context.startActivity(Intent.createChooser(sendIntent, "ফতোয়া শেয়ার করুন"))
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.Share, contentDescription = "Share", tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = "${fatwa.answerBn.take(90)}... (বিস্তারিত দেখতে ট্যাপ করুন)",
                                    color = TextMuted,
                                    fontSize = 12.sp,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            }
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
    val context = LocalContext.current
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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Imam Greeting Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(DarkGreen.copy(alpha = 0.6f))
                .border(1.dp, PrimaryGreen.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "মুফতি ও ইমাম সাহেবের নিকট সরাসরি প্রশ্ন করুন",
                    color = GoldAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SolaimanLipiFontFamily
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "যেকোনো শরয়ী জটিলতা, নামাজ, রোজা, জাকাত বা পারিবারিক মাসআলা সম্পর্কে প্রশ্ন পাঠালে ইমাম সাহেব পর্যালোচনা করে দ্রুত জবাব প্রদান করবেন।",
                    color = TextWhite,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    fontFamily = SolaimanLipiFontFamily
                )
            }
        }

        // Form Fields
        OutlinedTextField(
            value = nameText,
            onValueChange = { nameText = it },
            label = { Text("আপনার নাম *", color = TextMuted, fontFamily = SolaimanLipiFontFamily) },
            placeholder = { Text("যেমন: মো. আব্দুল্লাহ", color = TextMuted, fontFamily = SolaimanLipiFontFamily) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = DarkGreenBorder.copy(alpha = 0.5f),
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedContainerColor = DarkSurfaceElevated,
                unfocusedContainerColor = DarkSurfaceElevated
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phoneText,
            onValueChange = { phoneText = it },
            label = { Text("মোবাইল নম্বর *", color = TextMuted, fontFamily = SolaimanLipiFontFamily) },
            placeholder = { Text("যেমন: 017XXXXXXXX", color = TextMuted, fontFamily = FontFamily.Monospace) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = DarkGreenBorder.copy(alpha = 0.5f),
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedContainerColor = DarkSurfaceElevated,
                unfocusedContainerColor = DarkSurfaceElevated
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text("প্রশ্নের বিভাগ নির্বাচন করুন:", color = GoldAccent, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(FatwaCategory.values().filter { it != FatwaCategory.ALL }) { cat ->
                val isSelected = (cat == selectedCategory)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) DarkGreen else DarkSurfaceElevated)
                        .border(1.dp, if (isSelected) PrimaryGreen else DarkGreenBorder.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = cat.titleBn,
                        color = if (isSelected) NeonGreenGlow else TextWhite,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = SolaimanLipiFontFamily
                    )
                }
            }
        }

        OutlinedTextField(
            value = questionText,
            onValueChange = { questionText = it },
            label = { Text("আপনার প্রশ্নটি বিস্তারিত লিখুন *", color = TextMuted, fontFamily = SolaimanLipiFontFamily) },
            placeholder = { Text("মাসআলা বা সমস্যা স্পষ্টভাবে বর্ণনা করুন...", color = TextMuted, fontFamily = SolaimanLipiFontFamily) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = DarkGreenBorder.copy(alpha = 0.5f),
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedContainerColor = DarkSurfaceElevated,
                unfocusedContainerColor = DarkSurfaceElevated
            ),
            shape = RoundedCornerShape(12.dp),
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
                colors = CheckboxDefaults.colors(checkedColor = PrimaryGreen, uncheckedColor = DarkGreenBorder, checkmarkColor = DarkBackground)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("প্রশ্নটি সম্পূর্ণ গোপনীয় ও ব্যক্তিগত রাখতে চাই", color = TextMuted, fontSize = 12.sp, fontFamily = SolaimanLipiFontFamily)
        }

        // Submit Button
        Button(
            onClick = {
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
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("ইমাম সাহেবের কাছে প্রশ্ন জমা দিন", color = DarkBackground, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // History of User Submissions
        if (userQuestions.isNotEmpty()) {
            Text("আমার জমাকৃত প্রশ্নসমূহ (${userQuestions.size})", color = GoldAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)

            userQuestions.forEach { q ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(q.categoryName, color = CyanBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (q.status.contains("উত্তর")) DarkGreen else DarkSurface)
                                    .border(0.6.dp, if (q.status.contains("উত্তর")) PrimaryGreen else TextMuted, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = q.status,
                                    color = if (q.status.contains("উত্তর")) NeonGreenGlow else TextMuted,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(q.questionText, color = TextWhite, fontSize = 12.5.sp, fontFamily = SolaimanLipiFontFamily)

                        if (q.replyText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(thickness = 0.5.dp, color = DarkGreenBorder.copy(alpha = 0.4f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("ইমাম সাহেবের উত্তর:", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(q.replyText, color = TextWhite.copy(alpha = 0.9f), fontSize = 12.5.sp, lineHeight = 18.sp, fontFamily = SolaimanLipiFontFamily)
                            if (q.repliedBy.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("উত্তরদাতা: ${q.repliedBy} ${if (q.replyDateBn.isNotBlank()) "(${q.replyDateBn})" else ""}", color = CyanBlue, fontSize = 11.sp, fontFamily = SolaimanLipiFontFamily)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
