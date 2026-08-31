package com.example.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.VolunteerActivism
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FitrahCommodity
import com.example.data.model.NisabBasis
import com.example.data.model.ZakatInputState
import com.example.ui.components.CommonHeader
import com.example.ui.theme.CyanBlue
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.DarkGreenBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GreenDigital
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.RedDigital
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ZakatCalculatorScreen(
    onBackClick: () -> Unit,
    onNavigateToDonation: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonHeader(
            title = "জাকাত ও ফিতরা ক্যালকুলেটর",
            subtitle = "শরীয়াহ ও নিসাব ভিত্তিক হিসাব সহায়িকা",
            onBackClick = onBackClick
        )

        // Two Main Tabs: Zakat vs Fitrah
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
            divider = {
                HorizontalDivider(thickness = 0.5.dp, color = DarkSurfaceBorder)
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
                            fontSize = 13.sp
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
                            fontSize = 13.sp
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

@Composable
private fun ZakatCalculatorTab(
    onNavigateToDonation: () -> Unit
) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Result Highlight Card (Always prominent at top)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(DarkGreen.copy(alpha = 0.85f), DarkSurfaceElevated)
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
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (zakatState.isNisabReached) PrimaryGreen else DarkBackground)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (zakatState.isNisabReached) "জাকাত ফরজ হয়েছে" else "নিসাব পূর্ণ হয়নি",
                            color = if (zakatState.isNisabReached) DarkBackground else TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "প্রদেয় জাকাত (২.৫%)",
                    color = TextWhite,
                    fontSize = 13.sp
                )

                Text(
                    text = "৳ ${currencyFormat.format(zakatState.payableZakat.toLong())}",
                    color = NeonGreenGlow,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(thickness = 0.5.dp, color = DarkSurfaceBorder)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("মোট জাকাতযোগ্য সম্পদ", color = TextMuted, fontSize = 11.sp)
                        Text("৳ ${currencyFormat.format(zakatState.netZakatableWealth.toLong())}", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("বর্তমান নিসাব সীমা", color = TextMuted, fontSize = 11.sp)
                        Text("৳ ${currencyFormat.format(zakatState.nisabThresholdValue.toLong())}", color = GoldAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Nisab Basis Selector Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurface)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("নিসাব নির্ধারণ ভিত্তি", color = GoldAccent, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NisabBasis.values().forEach { basis ->
                        val isSelected = (basis == nisabBasis)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) DarkGreen else DarkBackground)
                                .border(1.dp, if (isSelected) PrimaryGreen else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                .clickable { nisabBasis = basis }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (basis == NisabBasis.SILVER) "রূপা (৫২.৫ ভরি)" else "স্বর্ণ (৭.৫ ভরি)",
                                    color = if (isSelected) NeonGreenGlow else TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (basis == NisabBasis.SILVER) "সর্বনিম্ন নিরাপদ" else "উচ্চ সীমা",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 1: স্বর্ণ ও রূপার হিসাব
        CategoryHeader("১. স্বর্ণ ও রূপা")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ZakatInputField(
                label = "স্বর্ণ (ভরি)",
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
                label = "রূপা (ভরি)",
                value = silverWeightText,
                onValueChange = { silverWeightText = it },
                placeholder = "০",
                modifier = Modifier.weight(1f)
            )
            ZakatInputField(
                label = "প্রতি ভরি দর (৳)",
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
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun FitrahCalculatorTab(
    onNavigateToDonation: () -> Unit
) {
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
            .padding(14.dp),
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
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "৳ ${currencyFormat.format(totalFitrah.toLong())}",
                    color = NeonGreenGlow,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$memberCount জন সদস্য × ৳ ${rate.toInt()} (${selectedCommodity.titleBn})",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        // Family Members Counter
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurface)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("পরিবারের মোট সদস্য সংখ্যা", color = TextWhite, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                    Text("প্রাপ্তবয়স্ক, অপ্রাপ্তবয়স্ক ও আশ্রিতজন", color = TextMuted, fontSize = 11.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { if (memberCount > 1) memberCount-- },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DarkBackground)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = RedDigital)
                    }

                    Text(
                        text = "$memberCount",
                        color = NeonGreenGlow,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )

                    IconButton(
                        onClick = { memberCount++ },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DarkBackground)
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
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold
        )

        FitrahCommodity.values().forEach { commodity ->
            val isSelected = (commodity == selectedCommodity)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) DarkGreen.copy(alpha = 0.6f) else DarkSurface)
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) PrimaryGreen else DarkSurfaceBorder,
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
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "(${commodity.arabicName})",
                                color = GoldAccent,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "পরিমাণ: ${commodity.quantityBn} • ${commodity.description}",
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "৳ ${commodity.defaultPriceBdt.toInt()}",
                            color = if (isSelected) NeonGreenGlow else CyanBlue,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "জনপ্রতি",
                            color = TextMuted,
                            fontSize = 10.sp
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
                .clip(RoundedCornerShape(10.dp))
                .background(DarkSurface)
                .border(0.5.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = "ফিতরার জরুরি মাসআলা:",
                    color = GoldAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ঈদের নামাজের পূর্বেই ফিতরা আদায় করা ওয়াজিব। সামর্থ্য অনুযায়ী গম, যব, কিশমিশ, খেজুর বা পনিরের যেকোনো একটির মূল্য দিয়ে ফিতরা আদায় করা যায়। উচ্চবিত্তদের জন্য কিশমিশ, খেজুর বা পনিরের মূল্যে ফিতরা প্রদান অধিক উত্তম।",
                    color = TextMuted,
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

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
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun CategoryHeader(title: String, color: androidx.compose.ui.graphics.Color = GoldAccent) {
    Text(
        text = title,
        color = color,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
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
            fontSize = 11.5.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextMuted, fontSize = 13.sp) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
    }
}
