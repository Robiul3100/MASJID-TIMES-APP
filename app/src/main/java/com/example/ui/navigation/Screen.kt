package com.example.ui.navigation

sealed class Screen(val route: String, val titleBn: String) {
    // Primary & Public Routes
    object Home : Screen("home", "হোম")
    object DailyPrayer : Screen("daily_prayer", "সময়সূচি")
    object MonthlySchedule : Screen("monthly_schedule", "মাসিক সময়সূচি")
    object Qibla : Screen("qibla", "কিবলা কম্পাস")
    object QiblaCompass : Screen("qibla_compass", "কিবলা কম্পাস")
    object DuaDhikr : Screen("dua_dhikr", "দোয়া ও জিকির")
    object NoticeBoard : Screen("notice_board", "নোটিশ বোর্ড")
    object Notifications : Screen("notifications", "নোটিফিকেশন")
    object HujurKhana : Screen("hujur_khana", "হুজুরের খানা")
    object Settings : Screen("settings", "সেটিংস")
    object IslamicCalendar : Screen("islamic_calendar", "ইসলামিক ক্যালেন্ডার")
    object Quran : Screen("quran", "আল-কোরআন")
    object QuranSurahDetail : Screen("quran_surah_detail/{surahNumber}", "সূরা বিস্তারিত") {
        fun createRoute(surahNumber: Int, surahName: String = ""): String = "quran_surah_detail/$surahNumber"
    }
    object AboutMosque : Screen("about_mosque", "মসজিদ পরিচিতি")
    object Donation : Screen("donation", "অনুদান ও ফান্ড")
    object DigitalTasbih : Screen("digital_tasbih", "ডিজিটাল তসবিহ")
    object ZakatCalculator : Screen("zakat_calculator", "যাকাত ক্যালকুলেটর")
    object RamadanDashboard : Screen("ramadan_dashboard", "রমজান ক্যালেন্ডার")
    object JanazaAlerts : Screen("janaza_alerts", "জানাযা ও জরুরি বার্তা")
    object AskImam : Screen("ask_imam", "ইমামকে জিজ্ঞাসা")
    object Events : Screen("events", "মসজিদের ইভেন্ট ও মাহফিল")
    object Committee : Screen("committee", "মসজিদ কমিটি ও খাদেম")
    object Gallery : Screen("gallery", "মসজিদ গ্যালারি")
    object Contact : Screen("contact", "যোগাযোগ ও মতামত")
    object GlobalSearch : Screen("global_search", "সার্বজনীন অনুসন্ধান")
    object HelpFaq : Screen("help_faq", "সহায়তা ও প্রশ্নোত্তর")

    // Admin Secure Routes
    object AdminLogin : Screen("admin_login", "অ্যাডমিন প্রবেশ")
    object AdminDashboard : Screen("admin_dashboard", "অ্যাডমিন ড্যাশবোর্ড")
    object AdminMosque : Screen("admin_mosque", "মসজিদ প্রোফাইল ব্যবস্থাপনা")
    object AdminPrayer : Screen("admin_prayer", "নামাজের সময়সূচি পরিবর্তন")
    object AdminMeals : Screen("admin_meals", "হুজুরের খানা ব্যবস্থাপনা")
    object AdminNotices : Screen("admin_notices", "নোটিশ তৈরি ও প্রকাশ")
    object AdminNotifications : Screen("admin_notifications", "নোটিফিকেশন প্রেরক")
    object AdminEvents : Screen("admin_events", "ইভেন্ট ও মাহফিল ব্যবস্থাপনা")
    object AdminDuas : Screen("admin_duas", "দোয়া ব্যবস্থাপনা")
    object AdminCommittee : Screen("admin_committee", "কমিটি ও স্টাফ ব্যবস্থাপনা")
    object AdminFatwa : Screen("admin_fatwa", "ফতোয়া ও প্রশ্নোত্তর ব্যবস্থাপনা")
    object AdminDonations : Screen("admin_donations", "অনুদান ও হিসাব ব্যবস্থাপনা")
    object AdminEmergency : Screen("admin_emergency", "জরুরি ঘোষণা")
    object AdminActivity : Screen("admin_activity", "কার্যক্রম লগ")
    object AdminSettings : Screen("admin_settings", "অ্যাডমিন সেটিংস")
}
