package com.robiul.mosquetime.feature.admin.donations

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.robiul.mosquetime.data.model.BankAccountInfo
import com.robiul.mosquetime.data.model.DonationRecord
import com.robiul.mosquetime.data.model.MobileAccountInfo
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
fun AdminDonationsScreen(
    onBackClick: () -> Unit,
    viewModel: AdminDonationsViewModel = hiltViewModel(),
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
            FloatingActionButton(
                onClick = {
                    when (uiState.selectedTab) {
                        0 -> viewModel.openAddRecordDialog()
                        1 -> viewModel.openAddMobileDialog()
                        2 -> viewModel.openAddBankDialog()
                    }
                },
                containerColor = PrimaryGreen,
                contentColor = DarkBackground,
                shape = CircleShape,
                modifier = Modifier.testTag("add_donation_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "নতুন যোগ করুন")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top App Bar
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
                            text = "অনুদান ও ব্যাংক হিসাব ব্যবস্থাপনা",
                            color = GoldAccent,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "তহবিল রেকর্ডস ও ব্যাংক একাউন্ট কনফিগারেশন",
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }

            // Summary Info Banner
            val totalVerifiedAmount = uiState.donationRecords
                .filter { it.status.contains("গৃহীত") || it.status.contains("যাচাইকৃত") }
                .sumOf { it.amount }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkGreen)
                    .border(1.dp, PrimaryGreen.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("মোট সংগৃহীত ও যাচাইকৃত অনুদান", color = TextMuted, fontSize = 11.sp)
                        Text(
                            text = "৳ $totalVerifiedAmount",
                            color = NeonGreenGlow,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkBackground)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${uiState.donationRecords.size} টি এন্ট্রি",
                            color = GoldAccent,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Tab Row
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
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = if (uiState.selectedTab == 0) GoldAccent else TextMuted, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("রেকর্ডস (${uiState.donationRecords.size})", color = if (uiState.selectedTab == 0) TextWhite else TextMuted, fontSize = 11.5.sp, fontWeight = if (uiState.selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )

                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.onTabSelected(1) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = if (uiState.selectedTab == 1) CyanBlue else TextMuted, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("মোবাইল (${uiState.mobileAccounts.size})", color = if (uiState.selectedTab == 1) TextWhite else TextMuted, fontSize = 11.5.sp, fontWeight = if (uiState.selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )

                Tab(
                    selected = uiState.selectedTab == 2,
                    onClick = { viewModel.onTabSelected(2) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = if (uiState.selectedTab == 2) PrimaryGreen else TextMuted, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ব্যাংক হিসাব (${uiState.bankAccounts.size})", color = if (uiState.selectedTab == 2) TextWhite else TextMuted, fontSize = 11.5.sp, fontWeight = if (uiState.selectedTab == 2) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )
            }

            // Tab 0: Donation Records Tab
            if (uiState.selectedTab == 0) {
                // Search Box
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = { Text("দাতা, ফোন নম্বর বা ট্রানজেকশন ID অনুসন্ধান...", color = TextMuted, fontSize = 12.sp) },
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
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                )

                // Status Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "ALL" to "সকল (${uiState.donationRecords.size})",
                        "VERIFIED" to "যাচাইকৃত (${uiState.donationRecords.count { it.status.contains("গৃহীত") || it.status.contains("যাচাইকৃত") }})",
                        "PENDING" to "অপেক্ষমান (${uiState.donationRecords.count { !it.status.contains("গৃহীত") && !it.status.contains("যাচাইকৃত") }})"
                    ).forEach { (key, label) ->
                        val isSelected = uiState.filterStatus == key
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) DarkGreen else DarkSurface)
                                .border(1.dp, if (isSelected) PrimaryGreen else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                .clickable { viewModel.onFilterStatusChanged(key) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
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

                // Records List
                if (uiState.filteredRecords.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("কোনো অনুদান রেকর্ড পাওয়া যায়নি", color = TextMuted, fontSize = 14.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.filteredRecords, key = { it.id }) { record ->
                            DonationRecordItemCard(
                                record = record,
                                onUpdateStatus = { newStatus -> viewModel.updateDonationStatus(record.id, newStatus) },
                                onDelete = { viewModel.deleteDonationRecord(record) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(70.dp)) }
                    }
                }
            } else if (uiState.selectedTab == 1) {
                // Tab 1: Mobile Banking Accounts Tab
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.mobileAccounts, key = { it.number }) { account ->
                        MobileAccountAdminCard(
                            account = account,
                            onEdit = { viewModel.openEditMobileDialog(account) },
                            onDelete = { viewModel.deleteMobileAccount(account) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(70.dp)) }
                }
            } else {
                // Tab 2: Bank Accounts Tab
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.bankAccounts, key = { it.accountNumber }) { account ->
                        BankAccountAdminCard(
                            account = account,
                            onEdit = { viewModel.openEditBankDialog(account) },
                            onDelete = { viewModel.deleteBankAccount(account) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(70.dp)) }
                }
            }
        }
    }

    // Add Record Dialog
    if (uiState.isAddRecordOpen) {
        AddDonationRecordDialog(
            onDismiss = { viewModel.closeRecordDialog() },
            onSave = { fund, amt, method, trx, name, phone, status ->
                viewModel.saveDonationRecord(fund, amt, method, trx, name, phone, status)
            }
        )
    }

    // Add / Edit Mobile Account Dialog
    if (uiState.isAddMobileOpen) {
        AddEditMobileDialog(
            editingAccount = uiState.editingMobile,
            onDismiss = { viewModel.closeMobileDialog() },
            onSave = { provider, number, type ->
                viewModel.saveMobileAccount(provider, number, type)
            }
        )
    }

    // Add / Edit Bank Account Dialog
    if (uiState.isAddBankOpen) {
        AddEditBankDialog(
            editingAccount = uiState.editingBank,
            onDismiss = { viewModel.closeBankDialog() },
            onSave = { bName, accName, accNum, branch, route ->
                viewModel.saveBankAccount(bName, accName, accNum, branch, route)
            }
        )
    }
}

@Composable
private fun DonationRecordItemCard(
    record: DonationRecord,
    onUpdateStatus: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isVerified = record.status.contains("গৃহীত") || record.status.contains("যাচাইকৃত")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceElevated)
            .border(
                1.dp,
                if (isVerified) DarkGreenBorder else GoldAccent.copy(alpha = 0.4f),
                RoundedCornerShape(12.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(record.fundTitle, color = CyanBlue, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                    Text("দাতা: ${record.donorName}", color = TextWhite, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
                }

                Text(
                    text = "৳ ${record.amount}",
                    color = GoldAccent,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("মাধ্যম: ${record.paymentMethod}", color = TextMuted, fontSize = 11.sp)
                Text("তারিখ: ${record.dateString}", color = TextMuted, fontSize = 10.5.sp)
            }

            if (record.transactionId.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("TrxID: ${record.transactionId}", color = NeonGreenGlow, fontSize = 11.sp, fontFamily = FontFamily.Monospace)

                    if (record.donorPhone.isNotBlank() && record.donorPhone != "০১৭XXXXXXXX") {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkBackground)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${record.donorPhone}"))
                                    context.startActivity(intent)
                                }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(record.donorPhone, color = CyanBlue, fontSize = 10.5.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(thickness = 0.5.dp, color = DarkSurfaceBorder)
            Spacer(modifier = Modifier.height(8.dp))

            // Status Badge & Action Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isVerified) DarkGreen else DarkSurface)
                        .border(0.8.dp, if (isVerified) NeonGreenGlow else GoldAccent, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = record.status,
                        color = if (isVerified) NeonGreenGlow else GoldAccent,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isVerified) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PrimaryGreen)
                                .clickable { onUpdateStatus("যাচাইকৃত ও গৃহীত") }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("যাচাই ও গ্রহণ", color = DarkBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedDigital, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MobileAccountAdminCard(
    account: MobileAccountInfo,
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
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(DarkBackground)
                        .border(1.dp, PrimaryGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(22.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(account.provider, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(account.number, color = NeonGreenGlow, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Text(account.type, color = TextMuted, fontSize = 11.sp)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Mobile Account", account.number)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "${account.provider} নম্বর কপি হয়েছে", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyanBlue, modifier = Modifier.size(16.dp))
                }

                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = GoldAccent, modifier = Modifier.size(16.dp))
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedDigital, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun BankAccountAdminCard(
    account: BankAccountInfo,
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
            .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(account.bankName, color = GoldAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Bank Account", "${account.bankName}\nহিসাব নাম: ${account.accountName}\nহিসাব নম্বর: ${account.accountNumber}\nশাখা: ${account.branchName}")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "ব্যাংক হিসাব কপি হয়েছে", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyanBlue, modifier = Modifier.size(16.dp))
                    }

                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = GoldAccent, modifier = Modifier.size(16.dp))
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedDigital, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text("হিসাবের নাম: ${account.accountName}", color = TextWhite, fontSize = 12.sp)
            Text("হিসাব নং: ${account.accountNumber}", color = NeonGreenGlow, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Text("শাখা: ${account.branchName} | রাউটিং নং: ${account.routingNumber}", color = TextMuted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun AddDonationRecordDialog(
    onDismiss: () -> Unit,
    onSave: (fund: String, amt: Long, method: String, trx: String, name: String, phone: String, status: String) -> Unit
) {
    var fundName by remember { mutableStateOf("সাধারণ মসজিদ তহবিল") }
    var amountText by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("নগদ ক্যাশ (মসজিদ অফিস)") }
    var transactionId by remember { mutableStateOf("") }
    var donorName by remember { mutableStateOf("") }
    var donorPhone by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("যাচাইকৃত ও গৃহীত") }
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text("নতুন অনুদান এন্ট্রি যোগ করুন", color = GoldAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                OutlinedTextField(
                    value = fundName,
                    onValueChange = { fundName = it },
                    label = { Text("তহবিলের নাম *", color = TextMuted) },
                    placeholder = { Text("যেমন: নির্মাণ ও সংস্কার তহবিল", color = TextMuted) },
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
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("অনুদানের পরিমাণ (টাকা) *", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    value = paymentMethod,
                    onValueChange = { paymentMethod = it },
                    label = { Text("পরিশোধ মাধ্যম", color = TextMuted) },
                    placeholder = { Text("যেমন: bKash, নগদ ক্যাশ, ব্যাংক", color = TextMuted) },
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
                    value = transactionId,
                    onValueChange = { transactionId = it },
                    label = { Text("ট্রানজেকশন ID / মানি রিসিট নং", color = TextMuted) },
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
                    value = donorName,
                    onValueChange = { donorName = it },
                    label = { Text("দানকারীর নাম", color = TextMuted) },
                    placeholder = { Text("যেমন: হাজী আলতাফ হোসেন", color = TextMuted) },
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
                    value = donorPhone,
                    onValueChange = { donorPhone = it },
                    label = { Text("দানকারীর মোবাইল নম্বর", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                    val amt = amountText.toLongOrNull() ?: 0L
                    onSave(fundName, amt, paymentMethod, transactionId, donorName, donorPhone, status)
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

@Composable
private fun AddEditMobileDialog(
    editingAccount: MobileAccountInfo?,
    onDismiss: () -> Unit,
    onSave: (provider: String, number: String, type: String) -> Unit
) {
    var provider by remember { mutableStateOf(editingAccount?.provider ?: "") }
    var number by remember { mutableStateOf(editingAccount?.number ?: "") }
    var type by remember { mutableStateOf(editingAccount?.type ?: "মার্চেন্ট পে / কাউন্টার নং ১") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                if (editingAccount == null) "নতুন মোবাইল অ্যাকাউন্ট" else "মোবাইল অ্যাকাউন্ট সম্পাদন",
                color = GoldAccent,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = provider,
                    onValueChange = { provider = it },
                    label = { Text("প্রোভাইডার নাম *", color = TextMuted) },
                    placeholder = { Text("যেমন: bKash (বিকাশ মার্চেন্ট)", color = TextMuted) },
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
                    value = number,
                    onValueChange = { number = it },
                    label = { Text("হিসাব নম্বর *", color = TextMuted) },
                    placeholder = { Text("যেমন: 01711223344", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("হিসাবের ধরন ও বিবরণ", color = TextMuted) },
                    placeholder = { Text("যেমন: মার্চেন্ট পে / পার্সোনাল", color = TextMuted) },
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
                onClick = { onSave(provider, number, type) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = DarkBackground)
            ) {
                Text("সংরক্ষণ", fontWeight = FontWeight.Bold)
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
private fun AddEditBankDialog(
    editingAccount: BankAccountInfo?,
    onDismiss: () -> Unit,
    onSave: (bName: String, accName: String, accNum: String, branch: String, route: String) -> Unit
) {
    var bankName by remember { mutableStateOf(editingAccount?.bankName ?: "") }
    var accountName by remember { mutableStateOf(editingAccount?.accountName ?: "BAITUL AMAN JAME MASJID") }
    var accountNumber by remember { mutableStateOf(editingAccount?.accountNumber ?: "") }
    var branchName by remember { mutableStateOf(editingAccount?.branchName ?: "মিরপুর শাখা, ঢাকা") }
    var routingNumber by remember { mutableStateOf(editingAccount?.routingNumber ?: "") }
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                if (editingAccount == null) "নতুন ব্যাংক হিসাব সংযোজন" else "ব্যাংক হিসাব সম্পাদন",
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
                OutlinedTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = { Text("ব্যাংকের নাম *", color = TextMuted) },
                    placeholder = { Text("যেমন: ইসলামী ব্যাংক বাংলাদেশ পিএলসি", color = TextMuted) },
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
                    value = accountName,
                    onValueChange = { accountName = it },
                    label = { Text("হিসাবের শিরোনাম (Account Title) *", color = TextMuted) },
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
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    label = { Text("হিসাব নম্বর (Account Number) *", color = TextMuted) },
                    placeholder = { Text("যেমন: ২০৫০-১২২০-২০০১-৪৫৬৭", color = TextMuted) },
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
                    value = branchName,
                    onValueChange = { branchName = it },
                    label = { Text("শাখা (Branch)", color = TextMuted) },
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
                    value = routingNumber,
                    onValueChange = { routingNumber = it },
                    label = { Text("রাউটিং নম্বর (Routing Number)", color = TextMuted) },
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
                onClick = { onSave(bankName, accountName, accountNumber, branchName, routingNumber) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = DarkBackground)
            ) {
                Text("সংরক্ষণ", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("বাতিল", color = TextMuted)
            }
        }
    )
}
