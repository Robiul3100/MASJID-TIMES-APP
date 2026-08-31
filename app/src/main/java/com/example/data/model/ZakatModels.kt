package com.example.data.model

enum class NisabBasis(val titleBn: String, val thresholdUnit: String, val defaultWeight: Double) {
    SILVER("রূপার নিসাব (সর্বনিম্ন ও অধিক নিরাপদ)", "৫২.৫ ভরি (৬১২.৩৬ গ্রাম)", 52.5),
    GOLD("স্বর্ণের নিসাব", "৭.৫ ভরি (৮৭.৪৮ গ্রাম)", 7.5)
}

data class ZakatInputState(
    val nisabBasis: NisabBasis = NisabBasis.SILVER,
    val goldWeightBhori: Double = 0.0,
    val goldPricePerBhori: Double = 135000.0,
    val silverWeightBhori: Double = 0.0,
    val silverPricePerBhori: Double = 2100.0,
    val cashInHandBank: Double = 0.0,
    val businessGoodsValue: Double = 0.0,
    val stockInvestments: Double = 0.0,
    val recoverableLoans: Double = 0.0,
    val otherAssets: Double = 0.0,
    val debtsDue: Double = 0.0,
    val immediateExpenses: Double = 0.0
) {
    val totalGoldValue: Double get() = goldWeightBhori * goldPricePerBhori
    val totalSilverValue: Double get() = silverWeightBhori * silverPricePerBhori
    val totalGrossWealth: Double get() = totalGoldValue + totalSilverValue + cashInHandBank + businessGoodsValue + stockInvestments + recoverableLoans + otherAssets
    val totalLiabilities: Double get() = debtsDue + immediateExpenses
    val netZakatableWealth: Double get() = (totalGrossWealth - totalLiabilities).coerceAtLeast(0.0)

    val nisabThresholdValue: Double get() = when (nisabBasis) {
        NisabBasis.SILVER -> 52.5 * silverPricePerBhori
        NisabBasis.GOLD -> 7.5 * goldPricePerBhori
    }

    val isNisabReached: Boolean get() = netZakatableWealth >= nisabThresholdValue
    val payableZakat: Double get() = if (isNisabReached) netZakatableWealth * 0.025 else 0.0
}

enum class FitrahCommodity(
    val titleBn: String,
    val arabicName: String,
    val quantityBn: String,
    val defaultPriceBdt: Double,
    val description: String
) {
    WHEAT_FLOUR("উন্নত আটা / গম", "القمح", "১ কেজি ৬৫০ গ্রাম", 115.0, "সর্বনিম্ন নির্ধারিত ফিতরা"),
    BARLEY("যব", "الشعير", "৩ কেজি ৩০০ গ্রাম", 400.0, "সুন্নাহ অনুমোদিত মাঝারি স্তর"),
    RAISINS("কিশমিশ", "الزبيب", "৩ কেজি ৩০০ গ্রাম", 1650.0, "উচ্চমান স্তর"),
    DATES("খেজুর", "التمر", "৩ কেজি ৩০০ গ্রাম", 2000.0, "উচ্চমান স্তর"),
    CHEESE("পনির", "الأقط", "৩ কেজি ৩০০ গ্রাম", 2800.0, "সর্বোচ্চ স্তর")
}

data class FitrahCalculation(
    val memberCount: Int = 1,
    val selectedCommodity: FitrahCommodity = FitrahCommodity.WHEAT_FLOUR,
    val customPricePerPerson: Double = FitrahCommodity.WHEAT_FLOUR.defaultPriceBdt
) {
    val totalFitrahPayable: Double get() = memberCount * customPricePerPerson
}
