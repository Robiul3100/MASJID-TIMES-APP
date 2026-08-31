package com.robiul.mosquetime.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robiul.mosquetime.data.model.DonationFundType
import com.robiul.mosquetime.data.model.DonationRecord
import com.robiul.mosquetime.data.repository.MosqueRepository
import com.robiul.mosquetime.data.repository.UserPreferencesRepository
import com.robiul.mosquetime.ui.components.CommonHeader
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
import com.robiul.mosquetime.ui.theme.TextMuted
import com.robiul.mosquetime.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val donationHistory by UserPreferencesRepository.donationHistory.collectAsState()
    val bankAccounts by MosqueRepository.bankAccountsFlow.collectAsState()
    val mobileAccounts by MosqueRepository.mobileAccountsFlow.collectAsState()
    val scrollState = rememberScrollState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = অনুদান একাউন্ট, 1 = অনুদান ট্র্যাকার / জমা, 2 = অনুদান হিস্টোরি
    var selectedFund by remember { mutableStateOf(DonationFundType.GENERAL) }
    var inputAmount by remember { mutableStateOf("1000") }
    var donorName by remember { mutableStateOf("") }
    var donorPhone by remember { mutableStateOf("") }
    var transactionId by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("bKash (বিকাশ)") }
    var previewReceiptRecord by remember { mutableStateOf<DonationRecord?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonHeader(
            title = "মসজিদ ফান্ড ও অনুদান",
            subtitle = "আল্লাহর সন্তুষ্টি ও সদকায়ে জারিয়া",
            onBackClick = onBackClick
        )

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurface,
            contentColor = GoldAccent,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = GoldAccent
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("একাউন্ট সমূহ", color = if (selectedTab == 0) GoldAccent else TextMuted, fontSize = 12.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("অনুদান জমা ও রশিদ", color = if (selectedTab == 1) GoldAccent else TextMuted, fontSize = 12.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("অনুদানের ইতিহাস", color = if (selectedTab == 2) GoldAccent else TextMuted, fontSize = 12.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
            )
        }

        when (selectedTab) {
            0 -> {
                // Bank & Mobile Accounts
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Quranic Inspiration Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(DarkGreen.copy(alpha = 0.5f), DarkSurface)
                                )
                            )
                            .border(1.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "“যারা আল্লাহর রাস্তায় নিজেদের ধন-সম্পদ ব্যয় করে, তাদের উপমা একটি শস্যবীজের মতো, যা থেকে সাতটি শীষ জন্মায় এবং প্রতিটি শীষে থাকে একশত দানা।”",
                                color = GoldAccent,
                                fontSize = 12.5.sp,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "— সূরা আল-বাকারা: ২৬১",
                                color = CyanBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Mobile Banking Accounts Card
                    Text("মোবাইল ব্যাংকিং (বিকাশ / নগদ / রকেট)", color = PrimaryGreen, fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
                    mobileAccounts.forEach { mobile ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurface)
                                .border(1.dp, DarkGreenBorder.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(DarkBackground)
                                            .border(1.dp, PrimaryGreen, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(mobile.provider, color = TextWhite, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                                        Text(mobile.number, color = NeonGreenGlow, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                        Text(mobile.type, color = TextMuted, fontSize = 10.5.sp)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(DarkBackground)
                                        .border(1.dp, CyanBlue.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                        .clickable {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Account Number", mobile.number)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "${mobile.provider} নম্বর কপি হয়েছে", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                 ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("কপি", color = CyanBlue, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Bank Accounts Card
                    Text("অফিসিয়াল ব্যাংক হিসাব বিবরণী", color = GoldAccent, fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
                    bankAccounts.forEach { bank ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurface)
                                .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(bank.bankName, color = GoldAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(DarkBackground)
                                            .border(1.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                            .clickable {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText("Bank Account", "${bank.bankName}\nহিসাব: ${bank.accountName}\nহিসাব নং: ${bank.accountNumber}\nশাখা: ${bank.branchName}\nরাউটিং: ${bank.routingNumber}")
                                                clipboard.setPrimaryClip(clip)
                                                Toast.makeText(context, "ব্যাংক একাউন্ট তথ্য কপি হয়েছে", Toast.LENGTH_SHORT).show()
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("সব কপি", color = GoldAccent, fontSize = 11.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text("হিসাবের নাম: ${bank.accountName}", color = TextWhite, fontSize = 12.5.sp)
                                Text("হিসাব নম্বর: ${bank.accountNumber}", color = NeonGreenGlow, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                Text("শাখা: ${bank.branchName} • রাউটিং: ${bank.routingNumber}", color = TextMuted, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            1 -> {
                // Donation Submission Form & Simulated Receipt
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("অনুদানের খাত নির্বাচন করুন", color = PrimaryGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    // Fund Types
                    DonationFundType.values().forEach { fund ->
                        val isSel = selectedFund == fund
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) DarkGreen.copy(alpha = 0.5f) else DarkSurface)
                                .border(1.dp, if (isSel) PrimaryGreen else DarkSurfaceBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedFund = fund }
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(fund.titleBn, color = if (isSel) NeonGreenGlow else TextWhite, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                                Text(fund.subtitleBn, color = TextMuted, fontSize = 11.sp)
                            }
                        }
                    }

                    Text("অনুদানের পরিমাণ (টাকা)", color = GoldAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    // Preset Amount Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("500", "1000", "2000", "5000").forEach { preset ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (inputAmount == preset) PrimaryGreen else DarkSurface)
                                    .border(1.dp, if (inputAmount == preset) PrimaryGreen else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                    .clickable { inputAmount = preset }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("৳$preset", color = if (inputAmount == preset) DarkBackground else TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = inputAmount,
                        onValueChange = { inputAmount = it },
                        label = { Text("অন্য পরিমাণ লিখুন (টাকা)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = DarkGreenBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = donorName,
                        onValueChange = { donorName = it },
                        label = { Text("আপনার নাম (ঐচ্ছিক / বেনামী)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = DarkGreenBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = donorPhone,
                        onValueChange = { donorPhone = it },
                        label = { Text("মোবাইল নম্বর") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = DarkGreenBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = transactionId,
                        onValueChange = { transactionId = it },
                        label = { Text("ট্রানজেকশন আইডি (TrxID)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanBlue,
                            unfocusedBorderColor = DarkGreenBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Submit Donation Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(GoldAccent)
                            .clickable {
                                val amt = inputAmount.toLongOrNull() ?: 1000L
                                val dateFmt = SimpleDateFormat("dd MMM, yyyy", Locale("bn", "BD")).format(Date())
                                val newRecord = DonationRecord(
                                    id = "don_${System.currentTimeMillis() % 100000}",
                                    fundTitle = selectedFund.titleBn,
                                    amount = amt,
                                    paymentMethod = selectedMethod,
                                    transactionId = if (transactionId.isNotBlank()) transactionId else "TXN${System.currentTimeMillis() % 1000000}",
                                    donorName = if (donorName.isNotBlank()) donorName else "বেনামী দাতা",
                                    donorPhone = if (donorPhone.isNotBlank()) donorPhone else "017XXXXXXXX",
                                    dateString = dateFmt,
                                    status = "গৃহীত (যাচাই প্রক্রিয়ায়)"
                                )
                                UserPreferencesRepository.addDonationRecord(newRecord)
                                MosqueRepository.addDonationRecord(newRecord)
                                previewReceiptRecord = newRecord
                                Toast.makeText(context, "অনুদান তথ্য সফলভাবে নথিভুক্ত হয়েছে!", Toast.LENGTH_LONG).show()
                            }
                            .padding(vertical = 12.dp)
                            .testTag("submit_donation_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("অনুদান নিশ্চিত করুন ও ডিজিটাল রশিদ দেখুন", color = DarkBackground, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            2 -> {
                // Donation History
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("আপনার পূর্ববর্তী অনুদানের তালিকা", color = PrimaryGreen, fontSize = 14.5.sp, fontWeight = FontWeight.Bold)

                    donationHistory.forEach { record ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurface)
                                .border(1.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .clickable { previewReceiptRecord = record }
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(record.fundTitle, color = TextWhite, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                                    Text("তারিখ: ${record.dateString} • TrxID: ${record.transactionId}", color = TextMuted, fontSize = 11.sp)
                                    Text("পেমেন্ট: ${record.paymentMethod}", color = CyanBlue, fontSize = 11.5.sp)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("৳${record.amount}", color = GoldAccent, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(DarkGreen)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("রশিদ দেখুন", color = NeonGreenGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Donation Receipt Dialog
    previewReceiptRecord?.let { receipt ->
        BasicAlertDialog(
            onDismissRequest = { previewReceiptRecord = null },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, GoldAccent, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("অফিসিয়াল দান রশিদ (Receipt)", color = GoldAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { previewReceiptRecord = null }, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextWhite, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(DarkGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonGreenGlow, modifier = Modifier.size(28.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("বায়তুল আমান জামে মসজিদ", color = PrimaryGreen, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text("মিরপুর-৬, ঢাকা-১২১৬", color = TextMuted, fontSize = 11.sp)

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 0.6.dp, color = DarkSurfaceBorder)

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ReceiptRow("রশিদ নং:", receipt.id)
                    ReceiptRow("দাতার নাম:", receipt.donorName)
                    ReceiptRow("অনুদানের খাত:", receipt.fundTitle)
                    ReceiptRow("অনুদানের পরিমাণ:", "৳${receipt.amount}/- (টাকা)", valueColor = GoldAccent)
                    ReceiptRow("পদ্ধতি:", receipt.paymentMethod)
                    ReceiptRow("TrxID:", receipt.transactionId)
                    ReceiptRow("তারিখ:", receipt.dateString)
                    ReceiptRow("স্ট্যাটাস:", receipt.status, valueColor = NeonGreenGlow)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 0.6.dp, color = DarkSurfaceBorder)

                Text(
                    text = "“আল্লাহ আপনার এই নেক দানকে কবুল করে আখেরাতের অসীম নাজাত ও বরকতের উসিলা বানান। আমীন।”",
                    color = CyanBlue,
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryGreen)
                        .clickable { previewReceiptRecord = null }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("রশিদ সম্পন্ন (বন্ধ করুন)", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    valueColor: Color = TextWhite
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextMuted, fontSize = 12.sp)
        Text(value, color = valueColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
