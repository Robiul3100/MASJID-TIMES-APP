package com.robiul.mosquetime.feature.admin.duas

import android.widget.Toast
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.robiul.mosquetime.data.model.DuaCategory
import com.robiul.mosquetime.data.model.DuaItem
import com.robiul.mosquetime.ui.theme.CyanBlue
import com.robiul.mosquetime.ui.theme.DarkBackground
import com.robiul.mosquetime.ui.theme.DarkGreen
import com.robiul.mosquetime.ui.theme.DarkGreenBorder
import com.robiul.mosquetime.ui.theme.DarkSurface
import com.robiul.mosquetime.ui.theme.DarkSurfaceBorder
import com.robiul.mosquetime.ui.theme.GoldAccent
import com.robiul.mosquetime.ui.theme.NeonGreenGlow
import com.robiul.mosquetime.ui.theme.PrimaryGreen
import com.robiul.mosquetime.ui.theme.PurpleAccent
import com.robiul.mosquetime.ui.theme.TextMuted
import com.robiul.mosquetime.ui.theme.TextWhite

@Composable
fun AdminDuaScreen(
    onBackClick: () -> Unit,
    viewModel: AdminDuaViewModel = hiltViewModel(),
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

    var duaToDelete by remember { mutableStateOf<DuaItem?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddDialog() },
                containerColor = PrimaryGreen,
                contentColor = TextWhite,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_add_dua")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "নতুন দোয়া যোগ করুন",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top Bar
            AdminDuaHeader(
                onBackClick = onBackClick,
                totalDuas = uiState.duas.size
            )

            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("admin_dua_search"),
                placeholder = { Text("দোয়ার নাম, অর্থ বা রেফারেন্স দিয়ে খুঁজুন...", color = TextMuted, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "অনুসন্ধান",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "পরিষ্কার করুন",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                singleLine = true
            )

            // Category Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    DuaCategoryChip(
                        title = "সকল দোয়া",
                        isSelected = uiState.selectedCategory == null || uiState.selectedCategory == DuaCategory.ALL,
                        onClick = { viewModel.onCategorySelected(null) }
                    )
                }
                items(DuaCategory.values().filter { it != DuaCategory.ALL }) { category ->
                    DuaCategoryChip(
                        title = category.titleBn,
                        isSelected = uiState.selectedCategory == category,
                        onClick = { viewModel.onCategorySelected(category) }
                    )
                }
            }

            // Duas List
            if (uiState.filteredDuas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "কোনো দোয়া পাওয়া যায়নি",
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "নতুন দোয়া যোগ করতে নিচের প্লাস (+) বাটনে চাপুন",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.filteredDuas, key = { it.id }) { dua ->
                        AdminDuaCard(
                            dua = dua,
                            onEdit = { viewModel.openEditDialog(dua) },
                            onDelete = { duaToDelete = dua }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Add / Edit Dialog
    if (uiState.isAddEditOpen) {
        AdminDuaAddEditDialog(
            dua = uiState.editingDua,
            isSubmitting = uiState.isSubmitting,
            onDismiss = { viewModel.closeDialog() },
            onSave = { id, titleBn, category, arabicText, pronunciationBn, meaningBn, reference, benefit, repetitionCount ->
                viewModel.saveDua(id, titleBn, category, arabicText, pronunciationBn, meaningBn, reference, benefit, repetitionCount)
            }
        )
    }

    // Delete Confirmation Dialog
    duaToDelete?.let { dua ->
        AlertDialog(
            onDismissRequest = { duaToDelete = null },
            containerColor = DarkSurface,
            title = {
                Text("দোয়া মুছে ফেলবেন?", color = TextWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "আপনি কি নিশ্চিত যে '${dua.titleBn}' দোয়াটি স্থায়ীভাবে মুছে ফেলতে চান?",
                    color = TextMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDua(dua)
                        duaToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("মুছে ফেলুন", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { duaToDelete = null },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("বাতিল", color = TextWhite)
                }
            }
        )
    }
}

@Composable
private fun AdminDuaHeader(
    onBackClick: () -> Unit,
    totalDuas: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.testTag("admin_duas_back_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "ফিরে যান",
                tint = TextWhite
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "দোয়া ও ইসলামিক কন্টেন্ট ব্যবস্থাপনা",
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "মোট সংরক্ষিত দোয়া: $totalDuas টি",
                color = GoldAccent,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun DuaCategoryChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) PrimaryGreen else DarkSurface)
            .border(
                width = 1.dp,
                color = if (isSelected) PrimaryGreen else DarkSurfaceBorder,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            color = if (isSelected) TextWhite else TextMuted,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun AdminDuaCard(
    dua: DuaItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_dua_card_${dua.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Category & Repetition Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = dua.category.titleBn,
                        color = NeonGreenGlow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (dua.repetitionCount > 1) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x33FFB300))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${dua.repetitionCount} বার পাঠ্য",
                            color = GoldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = dua.titleBn,
                color = TextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Arabic Text
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkBackground)
                    .padding(12.dp)
            ) {
                Text(
                    text = dua.arabicText,
                    color = GoldAccent,
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (dua.pronunciationBn.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "উচ্চারণ: ${dua.pronunciationBn}",
                    color = CyanBlue,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "অর্থ: ${dua.meaningBn}",
                color = TextWhite,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            if (dua.reference.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "রেফারেন্স: ${dua.reference}",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            if (dua.benefit.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(14.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = dua.benefit,
                        color = GoldAccent.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryGreen),
                    modifier = Modifier.testTag("admin_dua_edit_${dua.id}")
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("সম্পাদনা", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = onDelete,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF5350)),
                    modifier = Modifier.testTag("admin_dua_delete_${dua.id}")
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("মুছুন", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun AdminDuaAddEditDialog(
    dua: DuaItem?,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSave: (
        id: String,
        titleBn: String,
        category: DuaCategory,
        arabicText: String,
        pronunciationBn: String,
        meaningBn: String,
        reference: String,
        benefit: String,
        repetitionCount: Int
    ) -> Unit
) {
    var titleBn by remember { mutableStateOf(dua?.titleBn ?: "") }
    var category by remember { mutableStateOf(dua?.category ?: DuaCategory.DAILY) }
    var arabicText by remember { mutableStateOf(dua?.arabicText ?: "") }
    var pronunciationBn by remember { mutableStateOf(dua?.pronunciationBn ?: "") }
    var meaningBn by remember { mutableStateOf(dua?.meaningBn ?: "") }
    var reference by remember { mutableStateOf(dua?.reference ?: "") }
    var benefit by remember { mutableStateOf(dua?.benefit ?: "") }
    var repetitionCountStr by remember { mutableStateOf((dua?.repetitionCount ?: 1).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = if (dua == null) "নতুন দোয়া যোগ করুন" else "দোয়া সম্পাদনা করুন",
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Title Field
                OutlinedTextField(
                    value = titleBn,
                    onValueChange = { titleBn = it },
                    label = { Text("দোয়ার শিরোনাম *", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_dua_title"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // Category Selection
                Text("দোয়ার ধরন বা ক্যাটাগরি", color = GoldAccent, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(DuaCategory.values().filter { it != DuaCategory.ALL }) { cat ->
                        val isSelected = category == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryGreen else DarkBackground)
                                .border(1.dp, if (isSelected) PrimaryGreen else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                .clickable { category = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat.titleBn,
                                color = if (isSelected) TextWhite else TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Arabic Text Field
                OutlinedTextField(
                    value = arabicText,
                    onValueChange = { arabicText = it },
                    label = { Text("আরবি পাঠ (হরকত সহ) *", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_dua_arabic"),
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // Pronunciation Field
                OutlinedTextField(
                    value = pronunciationBn,
                    onValueChange = { pronunciationBn = it },
                    label = { Text("বাংলা উচ্চারণ", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_dua_pronunciation"),
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // Meaning Field
                OutlinedTextField(
                    value = meaningBn,
                    onValueChange = { meaningBn = it },
                    label = { Text("বাংলা অর্থ ও তাৎপর্য *", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_dua_meaning"),
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // Reference Field
                OutlinedTextField(
                    value = reference,
                    onValueChange = { reference = it },
                    label = { Text("হাদিস / কুরআনের রেফারেন্স", color = TextMuted) },
                    placeholder = { Text("যেমন: সহীহ বুখারী: ৬৩০৬", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_dua_reference"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // Benefit Field
                OutlinedTextField(
                    value = benefit,
                    onValueChange = { benefit = it },
                    label = { Text("ফজিলত ও আমল", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_dua_benefit"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // Repetition Count Field
                OutlinedTextField(
                    value = repetitionCountStr,
                    onValueChange = { repetitionCountStr = it.filter { ch -> ch.isDigit() } },
                    label = { Text("পাঠের সংখ্যা (যেমন: ১, ৩, ৩৩, ১০০)", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_dua_repetition"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val count = repetitionCountStr.toIntOrNull() ?: 1
                    onSave(
                        dua?.id ?: "",
                        titleBn,
                        category,
                        arabicText,
                        pronunciationBn,
                        meaningBn,
                        reference,
                        benefit,
                        count
                    )
                },
                enabled = !isSubmitting && titleBn.isNotBlank() && arabicText.isNotBlank() && meaningBn.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("dialog_dua_save_button")
            ) {
                Text(if (dua == null) "সংরক্ষণ করুন" else "আপডেট করুন", color = TextWhite)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("বাতিল", color = TextWhite)
            }
        }
    )
}
