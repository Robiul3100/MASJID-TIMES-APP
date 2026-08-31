package com.robiul.mosquetime.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robiul.mosquetime.data.model.FitrahCommodity
import com.robiul.mosquetime.data.model.NisabBasis
import com.robiul.mosquetime.data.model.ZakatInputState
import com.robiul.mosquetime.ui.components.CommonHeader
import com.robiul.mosquetime.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ZakatCalculatorScreen(
    onBackClick: () -> Unit,
    onNavigateToDonation: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            CommonHeader(
                title = "জাকাত ও ফিতরা ক্যালকুলেটর",
                subtitle = "শরীয়াহ ও নিসাব ভিত্তিক হিসাব সহায়িকা",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Two Main Tabs: Zakat vs Fitrah
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
                divider = {
                    HorizontalDivider(thickness = 0.5.dp, color = DarkGreenBorder.copy(alpha = 0.5f))
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = if (selectedTabIndex == 0) GoldAccent else TextMuted, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "জাকাত ক্যালকুলেটর",
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
                            Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = if (selectedTabIndex == 1) CyanBlue else TextMuted, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "ফিতরা ক্যালকুলেটর",
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
                ZakatCalculatorTab(onNavigateToDonation = onNavigateToDonation)
            } else {
                FitrahCalculatorTab(onNavigateToDonation = onNavigateToDonation)
            }
        }
    }
}

@Composable
private fun ZakatCalculatorTab(
    onNavigateToDonation: () -> Unit
) {
    val context = LocalContext.current
    var nisabBasis by remember { mutableStateOf(NisabBasis.SILVER) }
    var goldWeightText by remember { mutableStateOf("") }
    var goldPriceText by remember { mutableStateOf("135000") }
    var silverWeightText by remember { mutableStateOf("") }
    var silverPriceText by remember { mutableStateOf("2100") }
    var cashText by remember { mutableStateOf("") }
    var goodsText by remember { mutableStateOf("") }
    var stockText by remember { mutableStateOf("") }
    var loansText by remember { mutableStateOf("") }
    var otherText by remember { mutableStateOf("") }
    var debtsText by remember { mutableStateOf("") }
    var expensesText by remember { mutableStateOf("") }
    var showMasarifDialog by remember { mutableStateOf(false) }

    val zakatState = remember(
        nisabBasis, goldWeightText, goldPriceText, silverWeightText,
        silverPriceText, cashText, goodsText, stockText, loansText,
        otherText, debtsText, expensesText
    ) {
        ZakatInputState(
            nisabBasis = nisabBasis,
            goldWeightBhori = goldWeightText.toDoubleOrNull() ?: 0.0,
            goldPricePerBhori = goldPriceText.toDoubleOrNull() ?: 135000.0,
            silverWeightBhori = silverWeightText.toDoubleOrNull() ?: 0.0,
            silverPricePerBhori = silverPriceText.toDoubleOrNull() ?: 2100.0,
            cashInHandBank = cashText.toDoubleOrNull() ?: 0.0,
            businessGoodsValue = goodsText.toDoubleOrNull() ?: 0.0,
            stockInvestments = stockText.toDoubleOrNull() ?: 0.0,
            recoverableLoans = loansText.toDoubleOrNull() ?: 0.0,
            otherAssets = otherText.toDoubleOrNull() ?: 0.0,
            debtsDue = debtsText.toDoubleOrNull() ?: 0.0,
            immediateExpenses = expensesText.toDoubleOrNull() ?: 0.0
        )
    }

    val currencyFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

    fun shareZakatSummary() {
        val summaryText = buildString {
            appendLine("🕌 বায়তুল আমান মসজিদ - জাকাত স্টেটমেন্ট")
            appendLine("━━━━━━━━━━━━━━━━━━━")
            appendLine("• মোট জাকাতযোগ্য সম্পদ: ৳ ${currencyFormat.format(zakatState.totalGrossWealth.toLong())}")
            appendLine("• মোট বাদযোগ্য ঋণ/দেনা: ৳ ${currencyFormat.format(zakatState.totalLiabilities.toLong())}")
            appendLine("• নিট জাকাতযোগ্য সম্পদ: ৳ ${currencyFormat.format(zakatState.netZakatableWealth.toLong())}")
            appendLine("• নিসাব সীমা (${if (nisabBasis == NisabBasis.SILVER) "রূপা" else "স্বর্ণ"}): ৳ ${currencyFormat.format(zakatState.nisabThresholdValue.toLong())}")
            appendLine("━━━━━━━━━━━━━━━━━━━")
            if (zakatState.isNisabReached) {
                appendLine("✅ জাকাত ফরজ হয়েছে")
                appendLine("💰 মোট প্রদেয় জাকাত (২.৫%): ৳ ${currencyFormat.format(zakatState.payableZakat.toLong())}")
            } else {
                appendLine("ℹ️ আপনার সম্পদ নিসাব সীমা স্পর্শ করেনি।")
            }
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, summaryText)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "জাকাত হিসাব শেয়ার করুন"))
    }

    fun copyZakatSummary() {
        val summaryText = "জাকাত ফলাফল: নিট সম্পদ ৳${currencyFormat.format(zakatState.netZakatableWealth.toLong())}, প্রদেয় জাকাত (২.৫%): ৳${currencyFormat.format(zakatState.payableZakat.toLong())}"
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Zakat Statement", summaryText)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "জাকাত হিসাব কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Result Highlight Card (Always prominent at top)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(DarkGreen.copy(alpha = 0.9f), DarkSurfaceElevated)
                    )
                )
                .border(1.5.dp, if (zakatState.isNisabReached) PrimaryGreen else DarkGreenBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "জাকাত ফলাফল",
                        color = GoldAccent,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = SolaimanLipiFontFamily
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (zakatState.isNisabReached) PrimaryGreen else DarkBackground)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (zakatState.isNisabReached) "জাকাত ফরজ হয়েছে" else "নিসাব পূর্ণ হয়নি",
                            color = if (zakatState.isNisabReached) DarkBackground else TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "প্রদেয় জাকাত (২.৫%)",
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontFamily = SolaimanLipiFontFamily
                )

                Text(
                    text = "৳ ${currencyFormat.format(zakatState.payableZakat.toLong())}",
                    color = NeonGreenGlow,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(thickness = 0.5.dp, color = DarkGreenBorder.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("মোট জাকাতযোগ্য সম্পদ", color = TextMuted, fontSize = 11.5.sp, fontFamily = SolaimanLipiFontFamily)
                        Text("৳ ${currencyFormat.format(zakatState.netZakatableWealth.toLong())}", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("বর্তমান নিসাব সীমা", color = TextMuted, fontSize = 11.5.sp, fontFamily = SolaimanLipiFontFamily)
                        Text("৳ ${currencyFormat.format(zakatState.nisabThresholdValue.toLong())}", color = GoldAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Copy & Share Statement Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { copyZakatSummary() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, DarkGreenBorder),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("কপি", color = PrimaryGreen, fontSize = 12.sp, fontFamily = SolaimanLipiFontFamily)
                    }

                    Button(
                        onClick = { shareZakatSummary() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, DarkGreenBorder),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("শেয়ার", color = CyanBlue, fontSize = 12.sp, fontFamily = SolaimanLipiFontFamily)
                    }
                }
            }
        }

        // Nisab Basis Selector Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("নিসাব নির্ধারণ ভিত্তি", color = GoldAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NisabBasis.values().forEach { basis ->
                        val isSelected = (basis == nisabBasis)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) DarkGreen else DarkSurface)
                                .border(1.dp, if (isSelected) PrimaryGreen else DarkSurfaceBorder, RoundedCornerShape(10.dp))
                                .clickable { nisabBasis = basis }
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (basis == NisabBasis.SILVER) "রূপা (৫২.৫ ভরি)" else "স্বর্ণ (৭.৫ ভরি)",
                                    color = if (isSelected) NeonGreenGlow else TextWhite,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                                Text(
                                    text = if (basis == NisabBasis.SILVER) "সর্বাধিক গ্রহণযোগ্য" else "উচ্চ সীমা",
                                    color = TextMuted,
                                    fontSize = 10.5.sp,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 1: স্বর্ণ ও রূপার হিসাব
        CategoryHeader("১. স্বর্ণ ও রূপা")

        // Gold Quick Preset Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("স্বর্ণ দর:", color = TextMuted, fontSize = 11.sp, fontFamily = SolaimanLipiFontFamily)
            listOf("22K (১৩৫K)" to "135000", "21K (১২৯K)" to "129000", "18K (১১০K)" to "110000").forEach { (label, price) ->
                val isSelected = goldPriceText == price
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) PrimaryGreen.copy(alpha = 0.2f) else DarkSurfaceElevated)
                        .border(0.8.dp, if (isSelected) PrimaryGreen else DarkSurfaceBorder, RoundedCornerShape(6.dp))
                        .clickable { goldPriceText = price }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(label, color = if (isSelected) PrimaryGreen else TextMuted, fontSize = 10.5.sp, fontFamily = SolaimanLipiFontFamily)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ZakatInputField(
                label = "স্বর্ণের পরিমাণ (ভরি)",
                value = goldWeightText,
                onValueChange = { goldWeightText = it },
                placeholder = "০",
                modifier = Modifier.weight(1f)
            )
            ZakatInputField(
                label = "প্রতি ভরি দর (৳)",
                value = goldPriceText,
                onValueChange = { goldPriceText = it },
                placeholder = "১৩৫০০০",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ZakatInputField(
                label = "রূপার পরিমাণ (ভরি)",
                value = silverWeightText,
                onValueChange = { silverWeightText = it },
                placeholder = "০",
                modifier = Modifier.weight(1f)
            )
            ZakatInputField(
                label = "রূপার প্রতি ভরি দর (৳)",
                value = silverPriceText,
                onValueChange = { silverPriceText = it },
                placeholder = "২১০০",
                modifier = Modifier.weight(1f)
            )
        }

        // Section 2: নগদ অর্থ ও আর্থিক সম্পদ
        CategoryHeader("২. নগদ অর্থ ও বিনিয়োগ")
        ZakatInputField(
            label = "হাতে থাকা নগদ ও ব্যাংক ব্যালেন্স (৳)",
            value = cashText,
            onValueChange = { cashText = it },
            placeholder = "০"
        )

        ZakatInputField(
            label = "দোকান ও ব্যবসার বিক্রয়যোগ্য পণ্যের মূল্য (৳)",
            value = goodsText,
            onValueChange = { goodsText = it },
            placeholder = "০"
        )

        ZakatInputField(
            label = "শেয়ার, প্রাইজবন্ড ও অন্যান্য বিনিয়োগ (৳)",
            value = stockText,
            onValueChange = { stockText = it },
            placeholder = "০"
        )

        ZakatInputField(
            label = "পাওনা টাকা যা আদায়যোগ্য (৳)",
            value = loansText,
            onValueChange = { loansText = it },
            placeholder = "০"
        )

        ZakatInputField(
            label = "অন্যান্য তরল সম্পদ (৳)",
            value = otherText,
            onValueChange = { otherText = it },
            placeholder = "০"
        )

        // Section 3: দায়দেনা ও ঋণ (বিয়োগযোগ্য)
        CategoryHeader("৩. চলতি ঋণ ও দায়দেনা (বাদ যাবে)", color = RedDigital)
        ZakatInputField(
            label = "তাৎক্ষণিক পরিশোধযোগ্য ঋণ ও দেনা (৳)",
            value = debtsText,
            onValueChange = { debtsText = it },
            placeholder = "০"
        )

        ZakatInputField(
            label = "বকেয়া বিল ও পারিবারিক জরুরি খরচ (৳)",
            value = expensesText,
            onValueChange = { expensesText = it },
            placeholder = "০"
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Masarif-e-Zakat Guidance Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .clickable { showMasarifDialog = true }
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("জাকাতের ৮টি খাত (মাসারিফ-উয-যাকাত)", color = GoldAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                    Text("কোরআনের নির্দেশনা অনুযায়ী কাদেরকে জাকাত দেওয়া যাবে", color = TextMuted, fontSize = 11.sp, fontFamily = SolaimanLipiFontFamily)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Direct Donation Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkGreen)
                .border(1.dp, PrimaryGreen, RoundedCornerShape(12.dp))
                .clickable { onNavigateToDonation() }
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "বায়তুল আমান জাকাত ফান্ডে দান করুন",
                    color = DarkBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SolaimanLipiFontFamily
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Masarif-e-Zakat Dialog
    if (showMasarifDialog) {
        AlertDialog(
            onDismissRequest = { showMasarifDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("জাকাত ব্যয়ের ৮টি খাত", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = SolaimanLipiFontFamily)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "পবিত্র কোরআনের সূরা আত-তাওবাহ্ (৯:৬০) অনুযায়ী জাকাত প্রদানের ৮টি নির্ধারিত খাত:",
                        color = CyanBlue,
                        fontSize = 12.5.sp,
                        fontFamily = SolaimanLipiFontFamily
                    )
                    listOf(
                        "১. ফকীর (দরিদ্র ও নিঃস্ব ব্যক্তি)",
                        "২. মিসকীন (যাদের কোনো সম্পদ নেই)",
                        "৩. জাকাত আদায়ে নিয়োজিত কর্মচারী",
                        "৪. নওমুসলিম বা ইসলামের প্রতি আকৃষ্ট করার উদ্দেশ্যে",
                        "৫. দাসমুক্তি বা বন্দিমুক্তি",
                        "৬. ঋণগ্রস্ত ব্যক্তি (ঋণ পরিশোধে অক্ষম)",
                        "৭. ফি-সাবিলিল্লাহ (আল্লাহর রাস্তায় দাওয়াত ও শিক্ষা)",
                        "৮. মুসাফির (সফরে অসহায় হয়ে পড়া ব্যক্তি)"
                    ).forEach { item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurface)
                                .border(0.5.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(item, color = TextWhite, fontSize = 12.5.sp, fontFamily = SolaimanLipiFontFamily)
                        }
                    }
                }
            },
            containerColor = DarkSurfaceElevated,
            confirmButton = {
                TextButton(onClick = { showMasarifDialog = false }) {
                    Text("বন্ধ করুন", color = PrimaryGreen, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                }
            }
        )
    }
}

@Composable
private fun FitrahCalculatorTab(
    onNavigateToDonation: () -> Unit
) {
    val context = LocalContext.current
    var memberCount by remember { mutableIntStateOf(1) }
    var selectedCommodity by remember { mutableStateOf(FitrahCommodity.WHEAT_FLOUR) }
    var customRateText by remember { mutableStateOf(FitrahCommodity.WHEAT_FLOUR.defaultPriceBdt.toInt().toString()) }

    val rate = customRateText.toDoubleOrNull() ?: selectedCommodity.defaultPriceBdt
    val totalFitrah = memberCount * rate
    val currencyFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Result Summary Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(DarkSurfaceElevated, DarkSurface)
                    )
                )
                .border(1.5.dp, CyanBlue.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "মোট প্রদেয় ফিতরা",
                    color = CyanBlue,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SolaimanLipiFontFamily
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "৳ ${currencyFormat.format(totalFitrah.toLong())}",
                    color = NeonGreenGlow,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$memberCount জন সদস্য × ৳ ${rate.toInt()} (${selectedCommodity.titleBn})",
                    color = TextMuted,
                    fontSize = 12.5.sp,
                    fontFamily = SolaimanLipiFontFamily
                )
            }
        }

        // Family Members Counter
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("পরিবারের মোট সদস্য সংখ্যা", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                    Text("প্রাপ্তবয়স্ক, অপ্রাপ্তবয়স্ক ও আশ্রিতজন", color = TextMuted, fontSize = 11.5.sp, fontFamily = SolaimanLipiFontFamily)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { if (memberCount > 1) memberCount-- },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DarkSurface)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = RedDigital)
                    }

                    Text(
                        text = "$memberCount",
                        color = NeonGreenGlow,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )

                    IconButton(
                        onClick = { memberCount++ },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DarkSurface)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = PrimaryGreen)
                    }
                }
            }
        }

        // Commodity Selection List
        Text(
            text = "শরীয়াহ অনুমোদিত ফিতরার উপকরণ নির্বাচন করুন",
            color = GoldAccent,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = SolaimanLipiFontFamily
        )

        FitrahCommodity.values().forEach { commodity ->
            val isSelected = (commodity == selectedCommodity)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) DarkGreen.copy(alpha = 0.6f) else DarkSurfaceElevated)
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) PrimaryGreen else DarkGreenBorder.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        selectedCommodity = commodity
                        customRateText = commodity.defaultPriceBdt.toInt().toString()
                    }
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = commodity.titleBn,
                                color = TextWhite,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = SolaimanLipiFontFamily
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "(${commodity.arabicName})",
                                color = GoldAccent,
                                fontSize = 12.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "পরিমাণ: ${commodity.quantityBn} • ${commodity.description}",
                            color = TextMuted,
                            fontSize = 11.5.sp,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "৳ ${commodity.defaultPriceBdt.toInt()}",
                            color = if (isSelected) NeonGreenGlow else CyanBlue,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "জনপ্রতি",
                            color = TextMuted,
                            fontSize = 10.5.sp,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }
                }
            }
        }

        // Custom rate editable field
        ZakatInputField(
            label = "নির্বাচিত পণ্যের বর্তমান বাজারমূল্য (জনপ্রতি ৳)",
            value = customRateText,
            onValueChange = { customRateText = it },
            placeholder = selectedCommodity.defaultPriceBdt.toInt().toString()
        )

        // Islamic Rules note
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurfaceElevated)
                .border(0.8.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = "ফিতরার জরুরি মাসআলা:",
                    color = GoldAccent,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SolaimanLipiFontFamily
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ঈদের নামাজের পূর্বেই ফিতরা আদায় করা ওয়াজিব। সামর্থ্য অনুযায়ী গম, যব, কিশমিশ, খেজুর বা পনিরের যেকোনো একটির মূল্য দিয়ে ফিতরা আদায় করা যায়। উচ্চবিত্তদের জন্য কিশমিশ, খেজুর বা পনিরের মূল্যে ফিতরা প্রদান অধিক সওয়াবের।",
                    color = TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    fontFamily = SolaimanLipiFontFamily
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Direct Donation Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(PrimaryGreen)
                .clickable { onNavigateToDonation() }
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "মসজিদের ফিতরা তহবিলে প্রদান করুন",
                color = DarkBackground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = SolaimanLipiFontFamily
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun CategoryHeader(title: String, color: androidx.compose.ui.graphics.Color = GoldAccent) {
    Text(
        text = title,
        color = color,
        fontSize = 13.5.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = SolaimanLipiFontFamily,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
    )
}

@Composable
private fun ZakatInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 12.sp,
            fontFamily = SolaimanLipiFontFamily,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextMuted, fontSize = 13.sp) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = DarkGreenBorder.copy(alpha = 0.5f),
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedContainerColor = DarkSurfaceElevated,
                unfocusedContainerColor = DarkSurfaceElevated
            ),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
