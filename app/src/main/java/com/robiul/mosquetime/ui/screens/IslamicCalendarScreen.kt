package com.robiul.mosquetime.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.robiul.mosquetime.ui.components.AppEmptyStateView
import com.robiul.mosquetime.ui.components.CommonHeader
import com.robiul.mosquetime.ui.theme.*
import com.robiul.mosquetime.util.HapticUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class HolyDayCategory(val titleBn: String) {
    ALL("সকল দিবস"),
    FASTING("রোজা ও সিয়াম"),
    FESTIVAL("ঈদ ও উৎসব"),
    BLESSED_NIGHT("বরকতময় রাত"),
    HISTORIC("ঐতিহাসিক দিন")
}

data class RichHolyDay(
    val id: String,
    val nameBn: String,
    val hijriDateBn: String,
    val gregorianDateBn: String,
    val category: HolyDayCategory,
    val descriptionBn: String,
    val amalsBn: String,
    val isMajor: Boolean = false
)

data class HijriMonthInfo(
    val number: Int,
    val nameBn: String,
    val nameAr: String,
    val significanceBn: String,
    val isSacred: Boolean = false
)

@Composable
fun IslamicCalendarScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current

    var selectedTab by remember { mutableIntStateOf(0) } // 0: দিবসসমূহ, 1: ১২টি হিজরি মাস, 2: তারিখ রূপান্তর
    var moonOffset by remember { mutableIntStateOf(0) } // -1, 0, +1
    var selectedCategory by remember { mutableStateOf(HolyDayCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var activeHolyDayForDialog by remember { mutableStateOf<RichHolyDay?>(null) }

    // Date Converter State
    var converterDaysOffset by remember { mutableIntStateOf(0) }

    // Master Holy Days Data
    val holyDays = remember {
        listOf(
            RichHolyDay(
                id = "hd_1",
                nameBn = "পবিত্র আশুরা (১০ই মহররম)",
                hijriDateBn = "১০ মহররম, ১৪৪৬",
                gregorianDateBn = "১৭ জুলাই, ২০২৪",
                category = HolyDayCategory.FASTING,
                descriptionBn = "ঐতিহাসিক কারবালা ট্র্যাজেডি ও ইসলামের সত্যের বিজয়ের মহিমান্বিত দিন। হযরত মুসা (আ.) ও বনি ইসরাইলের ফেরাউন থেকে নাজাত পাওয়ার স্মারক।",
                amalsBn = "• ৯ ও ১০ অথবা ১০ ও ১১ই মহররম নফল রোজা রাখা অত্যন্ত সওয়াবের। হাদিস শরিফে এসেছে, আশুরার রোজা পূর্ববর্তী এক বছরের গুনাহের কাফফারা স্বরূপ।\n• পরিবার-পরিজনের জন্য উত্তম খাবারের ব্যবস্থা করা বরকতের কারণ।\n• বেশি বেশি ইস্তিগফার ও তাওবা করা।",
                isMajor = true
            ),
            RichHolyDay(
                id = "hd_2",
                nameBn = "আখেরী চাহার শোম্বা",
                hijriDateBn = "২৮ সফর, ১৪৪৬",
                gregorianDateBn = "৪ সেপ্টেম্বর, ২০২৪",
                category = HolyDayCategory.HISTORIC,
                descriptionBn = "সফর মাসের শেষ বুধবার। রাসুলুল্লাহ (সা.) কঠিন অসুস্থতার পর শেষবারের মতো সাময়িক সুস্থতা লাভ করে গোসল করেছিলেন এবং সাহাবিদের নিয়ে নামাজ আদায় করেছিলেন।",
                amalsBn = "• শুকরিয়াস্বরূপ নফল নামাজ ও দরূদ পাঠ।\n• গরিব-দুঃখীদের মধ্যে সাদকা প্রদান ও কল্যাণমূলক কাজ।",
                isMajor = false
            ),
            RichHolyDay(
                id = "hd_3",
                nameBn = "পবিত্র ঈদে মিলাদুন্নবী (সা.)",
                hijriDateBn = "১২ রবিউল আউয়াল, ১৪৪৬",
                gregorianDateBn = "১৬ সেপ্টেম্বর, ২০২৪",
                category = HolyDayCategory.HISTORIC,
                descriptionBn = "মানবজাতির রহমত বিশ্বনবী হযরত মুহাম্মদ মুস্তফা (সা.) এর পবিত্র জন্ম ও ওফাতের মহিমান্বিত স্মৃতিবাহী দিন।",
                amalsBn = "• নবীজির প্রতি বেশি বেশি দরূদ ও সালাম প্রেরণ করা (যেমন দরূদে ইব্রাহিম)।\n• সীরাতুন্নবী (সা.) পাঠ ও তাঁর সুন্নাত নিজের জীবনে বাস্তবায়নের অঙ্গীকার করা।\n• নফল রোজা রাখা (রাসুলুল্লাহ সা. প্রতি সোমবার তাঁর জন্মদিন হিসেবে রোজা রাখতেন)।",
                isMajor = true
            ),
            RichHolyDay(
                id = "hd_4",
                nameBn = "পবিত্র ফাতেহা-ই-ইয়াজদাহম",
                hijriDateBn = "১১ রবিউস সানি, ১৪৪৬",
                gregorianDateBn = "১৬ অক্টোবর, ২০২৪",
                category = HolyDayCategory.HISTORIC,
                descriptionBn = "বড়পীর হযরত গাউসুল আজম আব্দুল কাদের জিলানী (রহ.)-এর ওফাত দিবস স্মরণে দোয়া ও মাহফিল।",
                amalsBn = "• কুরআন তিলাওয়াত ও ঈসালে সওয়াব।\n• আল্লাহর ওলিদের জীবন ও তাকওয়া থেকে শিক্ষা গ্রহণ।",
                isMajor = false
            ),
            RichHolyDay(
                id = "hd_5",
                nameBn = "পবিত্র শবে মেরাজ (লাইলাতুল মেরাজ)",
                hijriDateBn = "২৭ রজব, ১৪৪৬",
                gregorianDateBn = "২৮ জানুয়ারি, ২০২৫",
                category = HolyDayCategory.BLESSED_NIGHT,
                descriptionBn = "রাসূলুল্লাহ (সা.) এর সশরীরে মসজিদে হারাম থেকে মসজিদে আকসা এবং সেখান থেকে সাত আসমান পেরিয়ে রব্বুল আলামীনের সান্নিধ্যে ঐতিহাসিক ঊর্ধ্বাকাশ ভ্রমণ ও পাঁচ ওয়াক্ত নামাজের উপহার প্রাপ্তির রাত।",
                amalsBn = "• দিবাগত রাতে নফল নামাজ, তাহাজ্জুদ ও কুরআন তিলাওয়াত।\n• পরদিন (২৭ রজব) নফল রোজা রাখা।\n• নামাজের গুরুত্ব অনুধাবন ও জামাতে নামাজ আদায়ের দৃঢ় সংকল্প।",
                isMajor = true
            ),
            RichHolyDay(
                id = "hd_6",
                nameBn = "পবিত্র শবে বরাত (লাইলাতুল বারাআত)",
                hijriDateBn = "১৫ শাবান, ১৪৪৬",
                gregorianDateBn = "১৫ ফেব্রুয়ারি, ২০২৫",
                category = HolyDayCategory.BLESSED_NIGHT,
                descriptionBn = "আল্লাহ তা'আলার পক্ষ থেকে বিশেষ রহমত ও গোনাহ মাগফিরাতের মহা বরকতময় রাত। এ রাতে আগামী এক বছরের রিজিক, হায়াত ও তাকদীরের বিভিন্ন ফায়সালা করা হয়।",
                amalsBn = "• রাতের বেলা নফল নামাজ (তাহাজ্জুদ, সালাতুত তাসবিহ), জিকির ও গভীর অনুশোচনায় তাওবা-ইস্তিগফার।\n• কবর জিয়ারত ও মৃত ব্যক্তিদের মাগফিরাতের জন্য দোয়া।\n• ১৫ই শাবান নফল রোজা রাখা।",
                isMajor = true
            ),
            RichHolyDay(
                id = "hd_7",
                nameBn = "পবিত্র মাহে রমজান শুরু (১ম রোজা)",
                hijriDateBn = "১ রমজান, ১৪৪৬",
                gregorianDateBn = "২ মার্চ, ২০২৫",
                category = HolyDayCategory.FASTING,
                descriptionBn = "রহমত, মাগফিরাত ও নাজাতের মাস। সিয়াম সাধনা ও কুরআন নাজিলের পবিত্রতম মাসের শুভ সূচনা।",
                amalsBn = "• একনিষ্ঠভাবে ফরজ রোজা পালন ও জামাতে খতমে তারাবীহ নামাজ আদায়।\n• শেষ রাতে সেহরি খাওয়া ও যথাসময়ে ইফতার করা।\n• প্রতিদিন অন্তত ১ পারা কুরআন তেলাওয়াত সম্পন্ন করার পরিকল্পনা।",
                isMajor = true
            ),
            RichHolyDay(
                id = "hd_8",
                nameBn = "ঐতিহাসিক বদর দিবস (১৭ই রমজান)",
                hijriDateBn = "১৭ রমজান, ১৪৪৬",
                gregorianDateBn = "১৮ মার্চ, ২০২৫",
                category = HolyDayCategory.HISTORIC,
                descriptionBn = "হিজরি ২য় সনের ১৭ই রমজান ইসলামের প্রথম সম্মুখ যুদ্ধ সংঘটিত হয়েছিল, যেখানে ৩১৩ জন নিরস্ত্র সাহাবি ১০০০ সশস্ত্র কাফের বাহিনীর বিরুদ্ধে চূড়ান্ত বিজয় অর্জন করেন।",
                amalsBn = "• বদরের শহীদ সাহাবিদের স্মরণে দোয়া।\n• সত্য ও ন্যায়ের পথে অবিচল থাকার শপথ গ্রহণ।",
                isMajor = false
            ),
            RichHolyDay(
                id = "hd_9",
                nameBn = "পবিত্র শবে ক্বদর (লাইলাতুল ক্বদর)",
                hijriDateBn = "২৭ রমজান, ১৪৪৬",
                gregorianDateBn = "২৮ মার্চ, ২০২৫",
                category = HolyDayCategory.BLESSED_NIGHT,
                descriptionBn = "হাজার মাস অপেক্ষা শ্রেষ্ঠ বরকতময় মহিমান্বিত রাত। যে রাতে লাওহে মাহফুজ থেকে প্রথম আসমানে পবিত্র কুরআন অবতীর্ণ হয়েছিল।",
                amalsBn = "• সারা রাত জেগে নফল ইবাদত, তাহাজ্জুদ ও সালাতুত তাসবিহ।\n• বিশেষ দোয়া: 'আল্লাহুম্মা ইন্নাকা আফুউন তুহিব্বুল আফওয়া ফা'ফু আন্নী' (হে আল্লাহ! নিশ্চয় আপনি ক্ষমাশীল, ক্ষমা ভালোবাসেন; অতএব আমাকে ক্ষমা করুন)।\n• রমজানের শেষ দশকের সকল বেজোড় রাতে (২১, ২৩, ২৫, ২৭, ২৯) কদর অনুসন্ধান।",
                isMajor = true
            ),
            RichHolyDay(
                id = "hd_10",
                nameBn = "জুমাতুল বিদা (রমজানের শেষ জুমা)",
                hijriDateBn = "২৯ রমজান, ১৪৪৬",
                gregorianDateBn = "৩০ মার্চ, ২০২৫",
                category = HolyDayCategory.BLESSED_NIGHT,
                descriptionBn = "পবিত্র মাহে রমজানের বিদায়ী জুমার দিন। মুসল্লিদের জন্য অত্যন্ত আবেগঘন ও তওবা-ইস্তিগফারের দিন।",
                amalsBn = "• মসজিদে আগে আগে উপস্থিত হয়ে জুমার বিশেষ খুতবা শ্রবণ।\n• রমজানের ভুলত্রুটির জন্য আল্লাহর দরবারে কান্নাকাটি করে ক্ষমা প্রার্থনা।\n• ফিতরা আদায় সম্পন্ন করা।",
                isMajor = true
            ),
            RichHolyDay(
                id = "hd_11",
                nameBn = "পবিত্র ঈদুল ফিতর (রমজানের ঈদ)",
                hijriDateBn = "১ শাওয়াল, ১৪৪৬",
                gregorianDateBn = "৩১ মার্চ, ২০২৫",
                category = HolyDayCategory.FESTIVAL,
                descriptionBn = "এক মাস সিয়াম সাধনা সফলভাবে সম্পন্ন করার পর মহান রবের পক্ষ থেকে বান্দার জন্য আনন্দ ও ক্ষমার মহোৎসব।",
                amalsBn = "• ঈদের জামাতে যাওয়ার আগে সদকাতুল ফিতর আদায় করা।\n• গোসল করে উত্তম পোশাক পরিধান ও সুগন্ধি ব্যবহার করে তাকবির বলতে বলতে ঈদগাহে যাওয়া।\n• আত্মীয়-স্বজন ও প্রতিবেশীদের খোঁজখবর নেওয়া ও শুভেচ্ছা বিনিময়।",
                isMajor = true
            ),
            RichHolyDay(
                id = "hd_12",
                nameBn = "শাওয়াল মাসের ৬ রোজা",
                hijriDateBn = "২-৩০ শাওয়াল, ১৪৪৬",
                gregorianDateBn = "এপ্রিল, ২০২৫",
                category = HolyDayCategory.FASTING,
                descriptionBn = "রমজানের পর শাওয়াল মাসে যেকোনো ৬টি রোজা রাখলে সারা বছর রোজা রাখার সমান সওয়াব পাওয়া যায় (সহিহ মুসলিম)।",
                amalsBn = "• শাওয়াল মাসের মধ্যে যেকোনো ৬ দিন রোজা রাখা (একটানা বা বিরতি দিয়ে)।",
                isMajor = false
            ),
            RichHolyDay(
                id = "hd_13",
                nameBn = "পবিত্র হজ ও আরাফার দিন (৯ই জিলহজ)",
                hijriDateBn = "৯ জিলহজ, ১৪৪৬",
                gregorianDateBn = "৫ জুন, ২০২৫",
                category = HolyDayCategory.FASTING,
                descriptionBn = "বিশ্ব মুসলিমের মহাসম্মিলন ও হজের মূল রুকন দিবস। আরাফাতের ময়দানে হাজীদের অবস্থানের দিন।",
                amalsBn = "• যারা হজে যাননি তাদের জন্য ৯ই জিলহজ আরাফার রোজা রাখা মোস্তাহাব। হাদিস অনুযায়ী এতে বিগত এক বছর ও আগামী এক বছরের গুনাহ মাফ হয়।\n• ৯ই জিলহজ ফজর থেকে ১৩ই জিলহজ আসর পর্যন্ত প্রতি ফরজ নামাজের পর তাকবীরে তাশরিক পড়া ওয়াজিব।",
                isMajor = true
            ),
            RichHolyDay(
                id = "hd_14",
                nameBn = "পবিত্র ঈদুল আযহা (কোরবানির ঈদ)",
                hijriDateBn = "১০ জিলহজ, ১৪৪৬",
                gregorianDateBn = "৬ জুন, ২০২৫",
                category = HolyDayCategory.FESTIVAL,
                descriptionBn = "হযরত ইব্রাহিম (আ.) ও হযরত ইসমাইল (আ.)-এর অনুপম ত্যাগের স্মরণে পশু কোরবানির মাধ্যমে আল্লাহর নৈকট্য অর্জনের উৎসব।",
                amalsBn = "• ঈদের নামাজ আদায় শেষে সামর্থ্যবানদের জন্য পশু কোরবানি করা।\n• কোরবানির গোশত তিন ভাগে ভাগ করে আত্মীয় ও গরিব-মিসকিনদের হক আদায় করা।\n• তাকবীরে তাশরিক অব্যাহত রাখা।",
                isMajor = true
            ),
            RichHolyDay(
                id = "hd_15",
                nameBn = "আইয়ামে তাশরিকের দিনসমূহ",
                hijriDateBn = "১১-১৩ জিলহজ, ১৪৪৬",
                gregorianDateBn = "৭-৯ জুন, ২০২৫",
                category = HolyDayCategory.HISTORIC,
                descriptionBn = "কোরবানির ঈদের পরবর্তী তিন দিন। এ দিনগুলোতে রোজা রাখা হারাম এবং তাকবীরে তাশরিক পাঠ জারি রাখা ওয়াজিব।",
                amalsBn = "• প্রতি ফরজ নামাজের পর উচ্চস্বরে একবার 'আল্লাহু আকবার আল্লাহু আকবার লা ইলাহা ইল্লাল্লাহু ওয়াল্লাহু আকবার আল্লাহু আকবার ওয়া লিল্লাহিল হামদ' পাঠ করা।",
                isMajor = false
            )
        )
    }

    // 12 Hijri Months Guide Data
    val hijriMonths = remember {
        listOf(
            HijriMonthInfo(1, "মহররম", "المحرَّم", "সম্মানিত ৪টি মাসের অন্যতম। আশুরা ও ঐতিহাসিক তাত্পর্যপূর্ণ মাস।", isSacred = true),
            HijriMonthInfo(2, "সফর", "صَفَر", "ইসলামের ২য় মাস। কোনো মাসকে অশুভ মনে করা কুসংস্কার।", isSacred = false),
            HijriMonthInfo(3, "রবিউল আউয়াল", "ربيع الأوّل", "বিশ্বনবী হযরত মুহাম্মদ (সা.)-এর পবিত্র বেলাদত ও ওফাতের মহিমান্বিত মাস।", isSacred = false),
            HijriMonthInfo(4, "রবিউস সানি", "ربيع الآخر", "ইসলামি জ্ঞান ও হেদায়েতের মাস। গাউসুল আজম জিলানী (রহ.)-এর স্মৃতিধন্য।", isSacred = false),
            HijriMonthInfo(5, "জমাদিউল আউয়াল", "جمادى الأولى", "হিজরি ক্যালেন্ডারের ৫ম মাস। ইবাদত ও নফল আমলের মাস।", isSacred = false),
            HijriMonthInfo(6, "জমাদিউস সানি", "جمادى الآخرة", "ইসলামের ৬ষ্ঠ মাস। আত্মশুদ্ধি ও দ্বীনি শিক্ষার মাস।", isSacred = false),
            HijriMonthInfo(7, "রজব", "رَجَب", "সম্মানিত মাসের অন্যতম। লাইলাতুল মেরাজ ও রমজানের পূর্বপ্রস্তুতি।", isSacred = true),
            HijriMonthInfo(8, "শাবান", "شَعْبَان", "বরকতময় মাস। শবে বরাত ও রাসুলুল্লাহ (সা.)-এর অধিক নফল রোজার মাস।", isSacred = false),
            HijriMonthInfo(9, "রমজান", "رَمَضَان", "সর্বশ্রেষ্ঠ মাস। ফরজ সিয়াম, কুরআন নাজিল ও লাইলাতুল কদরের মহাসৌভাগ্য।", isSacred = false),
            HijriMonthInfo(10, "শাওয়াল", "شَوَّال", "ঈদুল ফিতর ও শাওয়ালের ৬টি বরকতময় নফল রোজার মাস।", isSacred = false),
            HijriMonthInfo(11, "জিলকদ", "ذو القَعْدَة", "সম্মানিত ৪টি মাসের অন্যতম। হজের প্রস্তুতি ও ঝগড়া-বিবাদ বর্জনের মাস।", isSacred = true),
            HijriMonthInfo(12, "জিলহজ", "ذو الحِجَّة", "সম্মানিত মাস। হজের রুকন, আরাফাত দিবস, ঈদুল আযহা ও কোরবানির মাস।", isSacred = true)
        )
    }

    // Filtered Holy Days
    val filteredHolyDays = remember(holyDays, selectedCategory, searchQuery) {
        holyDays.filter { day ->
            val matchCategory = selectedCategory == HolyDayCategory.ALL || day.category == selectedCategory
            val matchQuery = searchQuery.isBlank() ||
                    day.nameBn.contains(searchQuery, ignoreCase = true) ||
                    day.hijriDateBn.contains(searchQuery, ignoreCase = true) ||
                    day.descriptionBn.contains(searchQuery, ignoreCase = true) ||
                    day.amalsBn.contains(searchQuery, ignoreCase = true)
            matchCategory && matchQuery
        }
    }

    // Dynamic Live Date Strings
    val currentCal = remember { Calendar.getInstance() }
    val formattedEnglishToday = remember {
        SimpleDateFormat("d MMMM, yyyy (EEEE)", Locale.forLanguageTag("bn-BD")).format(currentCal.time)
    }
    val hijriOffsetString = when (moonOffset) {
        1 -> " (+১ দিন চাঁদ অগ্রবর্তী)"
        -1 -> " (-১ দিন চাঁদ বিলম্বিত)"
        else -> " (ডিফল্ট হিসাব)"
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("islamic_calendar_screen"),
        containerColor = DarkBackground,
        topBar = {
            CommonHeader(
                title = "ইসলামিক ক্যালেন্ডার ও দিবস",
                subtitle = "হিজরি ১৪৪৬ সন • চান্দ্র মাস ও আমল নির্দেশিকা",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen Tabs Row (3 Tabs)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TabPill(
                    title = "ইসলামিক দিবসসমূহ",
                    icon = Icons.AutoMirrored.Outlined.EventNote,
                    isSelected = selectedTab == 0,
                    onClick = {
                        HapticUtils.performLongPressHaptic(view)
                        selectedTab = 0
                    },
                    modifier = Modifier.weight(1f)
                )
                TabPill(
                    title = "১২টি হিজরি মাস",
                    icon = Icons.Outlined.CalendarMonth,
                    isSelected = selectedTab == 1,
                    onClick = {
                        HapticUtils.performLongPressHaptic(view)
                        selectedTab = 1
                    },
                    modifier = Modifier.weight(1f)
                )
                TabPill(
                    title = "তারিখ রূপান্তর",
                    icon = Icons.Outlined.SwapHoriz,
                    isSelected = selectedTab == 2,
                    onClick = {
                        HapticUtils.performLongPressHaptic(view)
                        selectedTab = 2
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Tab 0: ইসলামিক দিবস ও হিজরি সন
            if (selectedTab == 0) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
                ) {
                    // Current Hijri Hero Banner Card
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF1B2E24), DarkSurface)
                                    )
                                )
                                .border(1.2.dp, PrimaryGreen.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Column {
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
                                                .background(PrimaryGreen.copy(alpha = 0.2f))
                                                .border(1.dp, PrimaryGreen, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.NightlightRound,
                                                contentDescription = null,
                                                tint = GoldAccent,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "পবিত্র হিজরি ১৪৪৬ সন",
                                                color = PrimaryGreen,
                                                fontSize = 15.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "বাংলা ১৪৩১-১৪৩২ বঙ্গাব্দ",
                                                color = GoldAccent,
                                                fontSize = 11.5.sp
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(EmeraldDeep)
                                            .border(1.dp, DarkGreenBorder, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "হিজরি চান্দ্রবর্ষ",
                                            color = NeonGreenGlow,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = DarkSurfaceBorder)
                                Spacer(modifier = Modifier.height(10.dp))

                                // Moon Sighting Adjustment Controller
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "চাঁদ দেখার সমন্বয় (Moon Offset):",
                                            color = TextWhite,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = hijriOffsetString,
                                            color = TextMuted,
                                            fontSize = 10.sp
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        MoonOffsetChip(
                                            label = "-১ দিন",
                                            isSelected = moonOffset == -1,
                                            onClick = {
                                                HapticUtils.performLongPressHaptic(view)
                                                moonOffset = -1
                                            }
                                        )
                                        MoonOffsetChip(
                                            label = "০ দিন",
                                            isSelected = moonOffset == 0,
                                            onClick = {
                                                HapticUtils.performLongPressHaptic(view)
                                                moonOffset = 0
                                            }
                                        )
                                        MoonOffsetChip(
                                            label = "+১ দিন",
                                            isSelected = moonOffset == 1,
                                            onClick = {
                                                HapticUtils.performLongPressHaptic(view)
                                                moonOffset = 1
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Search & Filter Header
                    item {
                        Column {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("holyday_search_input"),
                                placeholder = {
                                    Text(
                                        text = "ইসলামিক দিবস বা ফজিলত খুঁজুন...",
                                        color = TextMuted,
                                        fontSize = 12.5.sp
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Search,
                                        contentDescription = "Search",
                                        tint = PrimaryGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Clear",
                                                tint = TextMuted,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryGreen,
                                    unfocusedBorderColor = DarkSurfaceBorder,
                                    focusedContainerColor = DarkSurface,
                                    unfocusedContainerColor = DarkSurface,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    cursorColor = PrimaryGreen
                                ),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Category Chips
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(HolyDayCategory.entries.toTypedArray()) { category ->
                                    val isSelected = selectedCategory == category
                                    val chipBg = if (isSelected) EmeraldDeep else DarkSurface
                                    val chipBorder = if (isSelected) PrimaryGreen else DarkSurfaceBorder
                                    val chipTextColor = if (isSelected) PrimaryGreen else TextMuted

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(chipBg)
                                            .border(1.dp, chipBorder, RoundedCornerShape(18.dp))
                                            .clickable {
                                                HapticUtils.performLongPressHaptic(view)
                                                selectedCategory = category
                                            }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = category.titleBn,
                                            color = chipTextColor,
                                            fontSize = 11.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Holy Days Cards List
                    if (filteredHolyDays.isEmpty()) {
                        item {
                            AppEmptyStateView(
                                icon = Icons.Outlined.EventBusy,
                                title = "কোনো দিবস পাওয়া যায়নি",
                                subtitle = "অন্য কোনো শব্দ দিয়ে অনুসন্ধান করুন",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp)
                            )
                        }
                    } else {
                        items(filteredHolyDays, key = { it.id }) { holyDay ->
                            HolyDayCard(
                                day = holyDay,
                                onClick = {
                                    HapticUtils.performLongPressHaptic(view)
                                    activeHolyDayForDialog = holyDay
                                },
                                onShare = {
                                    shareHolyDay(context, holyDay)
                                }
                            )
                        }
                    }
                }
            }

            // Tab 1: ১২টি হিজরি মাসের পূর্ণাঙ্গ পরিচিতি
            if (selectedTab == 1) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceElevated)
                                .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Info, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "পবিত্র কুরআনে আল্লাহ তাআলা বলেন: 'নিশ্চয় আল্লাহর বিধানে মাসের সংখ্যা বারোটি, যেদিন থেকে তিনি আকাশমন্ডলী ও পৃথিবী সৃষ্টি করেছেন।' — (সূরা তাওবা: ৩৬)",
                                    color = TextWhite,
                                    fontSize = 11.5.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }

                    items(hijriMonths, key = { it.number }) { month ->
                        HijriMonthCard(month = month)
                    }
                }
            }

            // Tab 2: তারিখ রূপান্তর (Interactive Date Converter)
            if (selectedTab == 2) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, PrimaryGreen.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.SwapHoriz, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ইংরেজি ⇄ হিজরি তারিখ ক্যালকুলেটর",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "যেকোনো দিনের হিজরি ও বাংলা তারিখের সমতুল্য হিসাব দেখুন।",
                                fontSize = 11.5.sp,
                                color = TextMuted
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Interactive Date Stepper
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkBackground)
                                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        HapticUtils.performLongPressHaptic(view)
                                        converterDaysOffset -= 1
                                    }
                                ) {
                                    Icon(Icons.Default.ChevronLeft, contentDescription = "Prev Day", tint = PrimaryGreen)
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    val calcCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, converterDaysOffset) }
                                    val calcDateStr = SimpleDateFormat("d MMMM, yyyy (EEEE)", Locale.forLanguageTag("bn-BD")).format(calcCal.time)
                                    Text(
                                        text = calcDateStr,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = if (converterDaysOffset == 0) "আজকের দিন" else if (converterDaysOffset > 0) "+$converterDaysOffset দিন পর" else "$converterDaysOffset দিন পূর্বে",
                                        fontSize = 10.5.sp,
                                        color = GoldAccent
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        HapticUtils.performLongPressHaptic(view)
                                        converterDaysOffset += 1
                                    }
                                ) {
                                    Icon(Icons.Default.ChevronRight, contentDescription = "Next Day", tint = PrimaryGreen)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Conversion Results Card
                            val calcCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, converterDaysOffset) }
                            val approxHijriDay = ((calcCal.get(Calendar.DAY_OF_MONTH) + 8 + moonOffset) % 29) + 1
                            val approxHijriMonth = "রবিউল আউয়াল"

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF162B20))
                                    .border(1.dp, DarkGreenBorder, RoundedCornerShape(12.dp))
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "অনুরূপ হিজরি ক্যালেন্ডার তারিখ:",
                                        fontSize = 11.5.sp,
                                        color = TextMuted
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$approxHijriDay $approxHijriMonth, ১৪৪৬ হিজরি",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonGreenGlow
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "অনুরূপ বাংলা পঞ্জিকা: ${calcCal.get(Calendar.DAY_OF_MONTH)} ভাদ্র, ১৪৩১ বঙ্গাব্দ",
                                        fontSize = 12.sp,
                                        color = GoldAccent
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Reset to Today Button
                            if (converterDaysOffset != 0) {
                                OutlinedButton(
                                    onClick = {
                                        HapticUtils.performLongPressHaptic(view)
                                        converterDaysOffset = 0
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryGreen),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("আজকের তারিখে ফিরে যান", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Holy Day Detail & Amal Modal Dialog
    activeHolyDayForDialog?.let { holyDay ->
        HolyDayDetailDialog(
            day = holyDay,
            onDismiss = { activeHolyDayForDialog = null },
            onShare = { shareHolyDay(context, holyDay) }
        )
    }
}

/**
 * Holy Day Card Item
 */
@Composable
private fun HolyDayCard(
    day: RichHolyDay,
    onClick: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (day.isMajor) GoldAccent.copy(alpha = 0.6f) else DarkGreenBorder.copy(alpha = 0.5f)
    val cardBackground = if (day.isMajor) {
        Brush.verticalGradient(listOf(DarkSurfaceElevated, Color(0xFF1E281E)))
    } else {
        Brush.verticalGradient(listOf(DarkSurfaceElevated, DarkSurface))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(cardBackground)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Leading Badge
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (day.isMajor) GoldAccent.copy(alpha = 0.15f) else DarkGreen.copy(alpha = 0.5f))
                    .border(1.dp, if (day.isMajor) GoldAccent else PrimaryGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (day.isMajor) Icons.Default.Star else Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = if (day.isMajor) GoldAccent else PrimaryGreen,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = day.nameBn,
                        color = TextWhite,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkBackground)
                            .border(0.8.dp, DarkSurfaceBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = day.category.titleBn,
                            fontSize = 9.5.sp,
                            color = if (day.isMajor) GoldAccent else PrimaryGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = day.hijriDateBn,
                        color = NeonGreenGlow,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${day.gregorianDateBn})",
                        color = CyanBlue,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = day.descriptionBn,
                    color = TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = DarkSurfaceBorder.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ফজিলত ও আমল দেখুন ›",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )

                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = TextMuted,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 12 Hijri Month Card Item
 */
@Composable
private fun HijriMonthCard(
    month: HijriMonthInfo,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(1.dp, if (month.isSacred) GoldAccent.copy(alpha = 0.5f) else DarkSurfaceBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (month.isSacred) GoldAccent.copy(alpha = 0.15f) else EmeraldDeep)
                    .border(1.dp, if (month.isSacred) GoldAccent else PrimaryGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${month.number}",
                    color = if (month.isSacred) GoldAccent else PrimaryGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = month.nameBn,
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(${month.nameAr})",
                            color = GoldAccent,
                            fontSize = 13.sp
                        )
                    }

                    if (month.isSacred) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GoldAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "পবিত্র ৪ মাসের ১টি",
                                color = GoldAccent,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = month.significanceBn,
                    color = TextMuted,
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

/**
 * Holy Day Detail & Amal Modal Dialog
 */
@Composable
private fun HolyDayDetailDialog(
    day: RichHolyDay,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(18.dp))
                .background(DarkSurfaceElevated)
                .border(1.2.dp, if (day.isMajor) GoldAccent else PrimaryGreen, RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PrimaryGreen.copy(alpha = 0.15f))
                            .border(0.8.dp, PrimaryGreen, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = day.category.titleBn,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = day.nameBn,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = day.hijriDateBn,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonGreenGlow
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${day.gregorianDateBn})",
                        fontSize = 11.5.sp,
                        color = CyanBlue
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = DarkSurfaceBorder)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "তাৎপর্য ও ঐতিহাসিক পটভূমি:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = day.descriptionBn,
                    fontSize = 12.5.sp,
                    color = TextWhite,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "বিশেষ আমল ও ফজিলত:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = day.amalsBn,
                    fontSize = 12.5.sp,
                    color = TextWhite,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = DarkSurfaceBorder)
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onShare,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Outlined.Share, contentDescription = "Share", tint = DarkBackground, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("দিবসের তথ্য ও আমল শেয়ার করুন", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun TabPill(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isSelected) EmeraldDeep else DarkSurfaceElevated
    val border = if (isSelected) PrimaryGreen else DarkSurfaceBorder
    val tint = if (isSelected) PrimaryGreen else TextMuted

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = title, tint = tint, modifier = Modifier.size(17.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                color = if (isSelected) TextWhite else TextMuted,
                fontSize = 10.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun MoonOffsetChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) PrimaryGreen else DarkSurface
    val textCol = if (isSelected) DarkBackground else TextMuted
    val borderCol = if (isSelected) PrimaryGreen else DarkSurfaceBorder

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(0.8.dp, borderCol, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textCol
        )
    }
}

private fun shareHolyDay(context: Context, day: RichHolyDay) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "🌙 *${day.nameBn}*\n📅 হিজরি: ${day.hijriDateBn}\n📅 খ্রিস্টাব্দ: ${day.gregorianDateBn}\n\n📌 *তাৎপর্য:* ${day.descriptionBn}\n\n🤲 *আমলসমূহ:* \n${day.amalsBn}\n\n— চৌধুরী পাটোয়ারী বাড়ি জামে মসজিদ ও ইসলামিক সেন্টার"
        )
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "ইসলামিক দিবসের আমল শেয়ার করুন")
    context.startActivity(shareIntent)
}
