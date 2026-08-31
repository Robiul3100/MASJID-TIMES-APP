package com.example.data.repository

import com.example.data.model.AdminAuditLog
import com.example.data.model.AuditActionCategory
import com.example.data.model.BankAccountInfo
import com.example.data.model.CommitteeCategory
import com.example.data.model.CommitteeMember
import com.example.data.model.DhikrItem
import com.example.data.model.District
import com.example.data.model.DonationFundType
import com.example.data.model.DonationRecord
import com.example.data.model.DuaCategory
import com.example.data.model.DuaItem
import com.example.data.model.EmergencyAlert
import com.example.data.model.EventCategory
import com.example.data.model.ExtraPrayerTime
import com.example.data.model.FacilityItem
import com.example.data.model.FatwaArticle
import com.example.data.model.FatwaCategory
import com.example.data.model.GalleryCategory
import com.example.data.model.GalleryItem
import com.example.data.model.JanazaNotice
import com.example.data.model.MobileAccountInfo
import com.example.data.model.MonthlyPrayerDay
import com.example.data.model.MosqueDetails
import com.example.data.model.MosqueEvent
import com.example.data.model.NoticeCategory
import com.example.data.model.NoticeItem
import com.example.data.model.NotificationCategory
import com.example.data.model.AppNotification
import com.example.data.model.PrayerTimeItem
import com.example.data.model.QuranSurah
import com.example.data.model.QuranVerse
import com.example.data.model.RamadanDay
import com.example.data.model.RamadanDua
import com.example.data.model.TasbeehItem
import com.example.ui.components.PrayerType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object MosqueRepository {

    // -------------------------------------------------------------
    // DISTRICTS OF BANGLADESH
    // -------------------------------------------------------------
    val districts = listOf(
        District("dhaka", "ঢাকা", "Dhaka", 0, 0, 0, 0, 0),
        District("chittagong", "চট্টগ্রাম", "Chittagong", -5, -4, -5, -4, -5),
        District("sylhet", "সিলেট", "Sylhet", -6, -6, -6, -6, -6),
        District("rajshahi", "রাজশাহী", "Rajshahi", 6, 6, 6, 6, 6),
        District("khulna", "খুলনা", "Khulna", 4, 3, 4, 3, 4),
        District("barisal", "বরিশাল", "Barisal", 1, 1, 1, 1, 1),
        District("rangpur", "রংপুর", "Rangpur", 4, 4, 4, 3, 4),
        District("mymensingh", "ময়মনসিংহ", "Mymensingh", -1, 0, -1, 0, -1),
        District("cumilla", "কুমিল্লা", "Cumilla", -3, -2, -3, -2, -3),
        District("gazipur", "গাজীপুর", "Gazipur", 0, 0, 0, 0, 0),
        District("narayanganj", "নারায়ণগঞ্জ", "Narayanganj", 0, 0, 0, 0, 0),
        District("bogra", "বগুড়া", "Bogra", 4, 4, 4, 4, 4),
        District("dinajpur", "দিনাজপুর", "Dinajpur", 6, 6, 6, 5, 6),
        District("jessore", "যশোর", "Jessore", 4, 4, 4, 4, 4),
        District("coxsbazar", "কক্সবাজার", "Cox's Bazar", -6, -5, -6, -5, -6)
    )

    fun getDistrictById(id: String): District {
        return districts.find { it.id == id } ?: districts.first()
    }

    // -------------------------------------------------------------
    // MOSQUE DETAILS
    // -------------------------------------------------------------
    private var _currentMosqueInfo: MosqueDetails = MosqueDetails(
        nameBn = "বায়তুল আমান জামে মসজিদ",
        nameEn = "Baitul Aman Jame Masjid",
        establishedYear = "১৯৮৫ খ্রিষ্টাব্দ",
        address = "সেকশন-৬, ব্লক-সি, মিরপুর, ঢাকা-১২১৬",
        district = "ঢাকা",
        capacity = "প্রায় ৩,৫০০ জন মুসল্লি",
        floors = "৪ তলা বিশিষ্ট দৃষ্টিনন্দন ভবন",
        history = "১৯৮৫ সালে ধর্মপ্রাণ এলাকাবাসীর উদ্যোগে ঐতিহ্যবাহী বায়তুল আমান জামে মসজিদের যাত্রা শুরু হয়। পরবর্তীতে আধুনিক ইসলামিক স্থাপত্য শৈলীতে চারতলা বিশিষ্ট সুপরিসর মসজিদ কমপ্লেক্স নির্মাণ করা হয়। বর্তমানে এটি এই অঞ্চলের অন্যতম বৃহৎ ও স্বনামধন্য ইসলামিক কেন্দ্র হিসেবে পরিচিত।",
        description = "মসজিদে নিয়মিত ৫ ওয়াক্ত নামাজ, জুমার নামাজ, খতমে তারাবীহ, নূরানী ও নাজেরা কুরআন শিক্ষা মক্তব, বয়স্কদের দ্বীনি তালিম এবং সাপ্তাহিক তাফসীর মাহফিল পরিচালিত হয়। সম্পূর্ণ শীতাতপ নিয়ন্ত্রিত ও সার্বক্ষণিক জেনারেটর ব্যাকআপ সমৃদ্ধ।",
        imamName = "মাওলানা মুফতি আব্দুল্লাহ আল-মাহমুদ",
        imamTitle = "খতিব ও প্রধান ইমাম",
        imamEducation = "দাওরায়ে হাদিস (দারুল উলুম দেওবন্দ), এম.এ (ইসলামিক স্টাডিজ)",
        imamPhone = "+880 1711-234567",
        muazzinName = "হাফেজ ক্বারী মো. নজরুল ইসলাম",
        muazzinPhone = "+880 1812-345678",
        khademName = "মো. রফিকুল ইসলাম ও মো. আনোয়ার হোসেন",
        officePhone = "+880 2-9876543",
        officeEmail = "info@baitulamanmasjid.org",
        website = "www.baitulamanmasjid.org",
        facilities = listOf(
            FacilityItem("সম্পূর্ণ শীতাতপ নিয়ন্ত্রিত", "নামাজের প্রধান হল ও সকল তলায় আধুনিক সেন্ট্রাল এসি ব্যবস্থা।", "ac"),
            FacilityItem("আধুনিক অজু ও ওজুখানা", "একসাথে ১৫০ জন মুসল্লির অজু ও উন্নত স্যানিটেশন সুবিধা।", "wudu"),
            FacilityItem("মহিলাদের আলাদা নামাজের স্থান", "৩য় তলায় পর্দানশীন মা-বোনদের জন্য পৃথক প্রবেশপথ ও নামাজের ব্যবস্থা।", "women"),
            FacilityItem("ইসলামিক রিসার্চ লাইব্রেরি", "কুরআন, হাদিস, ফিকহ ও ইসলামিক সাহিত্যের সমৃদ্ধ সংগ্রহশালা।", "library"),
            FacilityItem("নূরানী কুরআন শিক্ষা মক্তব", "প্রতিদিন সকাল ৬:০০ থেকে ৭:৩০ পর্যন্ত শিশুদের বিশুদ্ধ কুরআন শিক্ষা।", "maktab"),
            FacilityItem("ফ্রি জানাজা ও এম্বুলেন্স সেবা", "জরুরি প্রয়োজনে ফ্রি লাশবাহী গাড়ি ও জানাজার সুব্যবস্থা।", "ambulance"),
            FacilityItem("সার্বক্ষণিক জেনারেটর", "বিদ্যুৎ বিভ্রাটে তাৎক্ষণিক অটো জেনারেটর ও সোলার ব্যাকআপ।", "power")
        )
    )

    var mosqueInfo: MosqueDetails
        get() = _currentMosqueInfo
        set(value) {
            _currentMosqueInfo = value
        }

    fun updateMosqueInfo(newInfo: MosqueDetails) {
        _currentMosqueInfo = newInfo
    }

    // -------------------------------------------------------------
    // COMMITTEE MEMBERS
    // -------------------------------------------------------------
    private val _initialCommitteeMembers: List<CommitteeMember> = listOf(
        CommitteeMember("1", "আলহাজ্ব মো. রফিকুল ইসলাম চৌধুরী", "সভাপতি", CommitteeCategory.OFFICE_BEARERS, "+880 1711-112233", "বিশিষ্ট সমাজসেবক ও ব্যবসায়ী"),
        CommitteeMember("2", "ইঞ্জিনিয়ার মো. আব্দুল হাই", "সহ-সভাপতি", CommitteeCategory.OFFICE_BEARERS, "+880 1819-223344", "অবসরপ্রাপ্ত প্রধান প্রকৌশলী"),
        CommitteeMember("3", "অধ্যাপক মো. নুরুল হুদা", "সাধারণ সম্পাদক", CommitteeCategory.OFFICE_BEARERS, "+880 1912-334455", "শিক্ষাবিদ ও লেখক"),
        CommitteeMember("4", "মো. তারিকুল ইসলাম স্বপন", "যুগ্ম সাধারণ সম্পাদক", CommitteeCategory.EXECUTIVE, "+880 1611-445566", "ব্যবসায়ী"),
        CommitteeMember("5", "আলহাজ্ব মো. দেলোয়ার হোসেন", "কোষাধ্যক্ষ", CommitteeCategory.OFFICE_BEARERS, "+880 1715-556677", "ব্যাংক কর্মকর্তা (অবসরপ্রাপ্ত)"),
        CommitteeMember("6", "মাওলানা মো. মাহমুদুল হাসান", "প্রচার ও প্রকাশনা সম্পাদক", CommitteeCategory.EXECUTIVE, "+880 1814-667788", "ইসলামিক গবেষক"),
        CommitteeMember("7", "ড. একেএম শামসুল আলম", "প্রধান উপদেষ্টা", CommitteeCategory.ADVISORY, "+880 1713-778899", "সাবেক সচিব"),
        CommitteeMember("8", "বিচারপতি মো. লুৎফর রহমান", "উপদেষ্টা", CommitteeCategory.ADVISORY, "+880 1918-889900", "আইনজীবী ও সাবেক বিচারপতি"),
        CommitteeMember("9", "মো. সাজ্জাদ হোসেন", "সদস্য (উন্নয়ন)", CommitteeCategory.EXECUTIVE, "+880 1716-990011", "স্থাপত্য প্রকৌশলী"),
        CommitteeMember("10", "মো. জহিরুল হক", "সদস্য (সমাজকল্যাণ)", CommitteeCategory.GENERAL_MEMBERS, "+880 1817-123456", "সমাজকর্মী")
    )

    private val _committeeFlow = MutableStateFlow(_initialCommitteeMembers)
    val committeeFlow: StateFlow<List<CommitteeMember>> = _committeeFlow.asStateFlow()

    var committeeMembers: List<CommitteeMember>
        get() = _committeeFlow.value
        set(value) {
            _committeeFlow.value = value
        }

    fun updateCommitteeMembers(newList: List<CommitteeMember>) {
        _committeeFlow.value = newList
    }

    fun addOrUpdateCommitteeMember(member: CommitteeMember) {
        val current = _committeeFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == member.id }
        if (index >= 0) {
            current[index] = member
        } else {
            current.add(member)
        }
        _committeeFlow.value = current
    }

    fun deleteCommitteeMember(memberId: String) {
        _committeeFlow.value = _committeeFlow.value.filter { it.id != memberId }
    }

    // -------------------------------------------------------------
    // NOTICES
    // -------------------------------------------------------------
    private val _initialNotices = listOf(
        NoticeItem(
            id = "n1",
            title = "পবিত্র মাহে রমজান ও খতমে তারাবীহ সংক্রান্ত জরুরি বিজ্ঞপ্তি",
            summary = "আসন্ন মাহে রমজানে খতমে তারাবীহ এবং রোজাদারদের জন্য সার্বিক প্রস্তুতি গ্রহণ সংক্রান্ত নির্দেশনাবলী।",
            fullContent = "আসসালামু আলাইকুম ওয়া রাহমাতুল্লাহ। সকল মুসল্লিয়ানদের সদয় অবগতির জন্য জানানো যাচ্ছে যে, আগামী পবিত্র মাহে রমজানে আমাদের মসজিদে দুইজন প্রখ্যাত হাফেজে কুরআনের ইমামতিতে খতমে তারাবীহ অনুষ্ঠিত হবে। প্রতিদিন এশার আজানের ১৫ মিনিট পর তারাবীহর জামাত শুরু হবে। রোজাদার মুসল্লিদের জন্য নিয়মিত ইফতারের বিশেষ আয়োজন থাকবে। সবার আন্তরিক সহযোগিতা কাম্য।",
            category = NoticeCategory.SPECIAL,
            publishedDate = "২৭ শাবান, ১৪৪৬ / ৮ মে, ২০২৫",
            isPinned = true,
            author = "মসজিদ পরিচালনা কমিটি"
        ),
        NoticeItem(
            id = "n2",
            title = "আগামী শুক্রবার জুমার খুতবা ও বিশেষ আলোচনা",
            summary = "জুমার খুতবায় 'হালাল উপার্জনের গুরুত্ব ও সুদমুক্ত সমাজ গঠন' বিষয়ে বয়ান পেশ করবেন সম্মানিত খতিব সাহেব।",
            fullContent = "সম্মানিত মুসল্লিবৃন্দ, আগামী শুক্রবার জুমার জামাতে বয়ান ও খুতবা পেশ করবেন মসজিদের সম্মানিত খতিব মাওলানা মুফতি আব্দুল্লাহ আল-মাহমুদ সাহেব। বিষয়: 'পবিত্র কুরআন ও সুন্নাহর আলোকে হালাল রিজিক অন্বেষণ ও সমাজে এর বরকত'। সকল মুসল্লিকে দুপুর ১২:৩০ এর মধ্যে মসজিদে উপস্থিত হওয়ার বিনীত অনুরোধ করা হচ্ছে।",
            category = NoticeCategory.JUMAH,
            publishedDate = "৬ মে, ২০২৫",
            isPinned = true,
            author = "খতিব ও ইমাম পরিষদ"
        ),
        NoticeItem(
            id = "n3",
            title = "শিশুদের সকালের নূরানী কুরআন শিক্ষা ক্লাসে ভর্তি চলছে",
            summary = "নতুন সেশনে ৫ থেকে ১২ বছর বয়সী বালক ও বালিকাদের বিশুদ্ধ কুরআন ও দ্বীনিয়াত শিক্ষার আবেদন আহ্বান।",
            fullContent = "আমাদের মসজিদ পরিচালিত 'নূরানী মক্তব'-এ নতুন শিক্ষাবর্ষে শিশু-কিশোরদের ভর্তি কার্যক্রম শুরু হয়েছে। ক্লাসের সময়: প্রতিদিন সকাল ৬:০০ টা থেকে ৭:৩০ টা পর্যন্ত (শুক্রবার বন্ধ)। অভিজ্ঞ ক্বারী সাহেবদের দ্বারা তাজবীদ সহকারে সহিহ কুরআন তিলাওয়াত, জরুরি দোয়া ও মাসায়েল শিক্ষা দেওয়া হবে। আগ্রহীদের মসজিদ অফিসে যোগাযোগ করতে অনুরোধ করা যাচ্ছে।",
            category = NoticeCategory.GENERAL,
            publishedDate = "৪ মে, ২০২৫",
            isPinned = false,
            author = "শিক্ষা ও দাওয়াহ বিভাগ"
        ),
        NoticeItem(
            id = "n4",
            title = "মসজিদের অজুখানা ও স্যানিটেশন সংস্কার কাজের তহবিল সংগ্রহ",
            summary = "৩য় ও ৪র্থ তলার আধুনিক অজুখানা ও নতুন টাইলস বসানোর মহতী উন্নয়ন প্রকল্পে মুক্তহস্তে দান করুন।",
            fullContent = "মসজিদের তৃতীয় তলায় নতুন অজুখানা ও ওজুখানা সম্প্রসারণ কাজ পুরোদমে চলছে। আনুমানিক প্রকল্প ব্যয় প্রায় ৪,৫০,০০০/- (চার লক্ষ পঞ্চাশ হাজার) টাকা। আল্লাহর ঘর মসজিদের উন্নয়নে যে যেভাবে পারেন দান ও সদকায়ে জারিয়া করে আখেরাতের পাথেয় অর্জন করুন। দান সরাসরি অফিস বা অনলাইন ব্যাংকিং/বিকাশের মাধ্যমে প্রেরণ করা যাবে।",
            category = NoticeCategory.URGENT,
            publishedDate = "২ মে, ২০২৫",
            isPinned = false,
            author = "মসজিদ উন্নয়ন উপ-কমিটি"
        ),
        NoticeItem(
            id = "n5",
            title = "সাপ্তাহিক তাফসীরুল কুরআন মাহফিল",
            summary = "প্রতি শনিবার মাগরিবের নামাজের পর সূরা আল-বাকারার ধারাবাহিক তাফসীর অনুষ্ঠিত হবে।",
            fullContent = "মুহতারাম মুসল্লিয়ানে কেরাম, প্রতি শনিবার মাগরিবের নামাজের পর মসজিদে নিয়মিত দারসে কুরআন ও তাফসীর মাহফিল অনুষ্ঠিত হয়। এতে পবিত্র কুরআনের তাফসীর সহ জীবনঘনিষ্ঠ সমসাময়িক বিষয়ের ইসলামিক সমাধান আলোচনা করা হয়। আপনারা সপরিবারে ও বন্ধুদের সাথে নিয়ে উপস্থিত হয়ে দ্বীনি জ্ঞান অর্জন করুন।",
            category = NoticeCategory.EVENT,
            publishedDate = "১ মে, ২০২৫",
            isPinned = false,
            author = "দাওয়াহ কমিটি"
        )
    )

    private val _noticesFlow = MutableStateFlow(_initialNotices)
    val noticesFlow: StateFlow<List<NoticeItem>> = _noticesFlow.asStateFlow()
    val notices: List<NoticeItem> get() = _noticesFlow.value

    fun addOrUpdateNotice(notice: NoticeItem) {
        val current = _noticesFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == notice.id }
        if (index >= 0) {
            current[index] = notice
        } else {
            current.add(0, notice)
        }
        _noticesFlow.value = current
    }

    fun deleteNotice(noticeId: String) {
        _noticesFlow.value = _noticesFlow.value.filter { it.id != noticeId }
    }

    fun togglePinNotice(noticeId: String) {
        _noticesFlow.value = _noticesFlow.value.map {
            if (it.id == noticeId) it.copy(isPinned = !it.isPinned) else it
        }
    }

    // -------------------------------------------------------------
    // NOTIFICATIONS
    // -------------------------------------------------------------
    private val _initialNotifications = listOf(
        AppNotification(
            id = "notif_1",
            title = "আসন্ন মাগরিবের নামাজের সময়",
            message = "মাগরিবের নামাজের আর মাত্র ২৫ মিনিট বাকি আছে। দ্রুত মসজিদের উদ্দেশ্যে রওয়ানা হোন।",
            timestamp = "আজ, ১৮:০৫",
            timeAgo = "২৫ মিনিট আগে",
            category = NotificationCategory.PRAYER,
            isRead = false,
            targetRoute = "daily_prayer"
        ),
        AppNotification(
            id = "notif_2",
            title = "পবিত্র জুমার জামাত ঘোষণা",
            message = "আগামীকাল জুমার নামাজের প্রথম আজান ১২:৪৫ এবং জামাত ০১:৩০ মিনিটে অনুষ্ঠিত হবে।",
            timestamp = "আজ, ১৪:৩০",
            timeAgo = "৪ ঘন্টা আগে",
            category = NotificationCategory.JUMAH,
            isRead = false,
            targetRoute = "notice_board"
        ),
        AppNotification(
            id = "notif_3",
            title = "রমজানের নতুন নোটিশ প্রকাশিত হয়েছে",
            message = "খতমে তারাবীহ ও ইফতার আয়োজন সম্পর্কিত পূর্ণাঙ্গ নির্দেশিকা নোটিশ বোর্ডে প্রকাশ করা হয়েছে।",
            timestamp = "গতকাল, ২০:০০",
            timeAgo = "১ দিন আগে",
            category = NotificationCategory.NOTICE,
            isRead = true,
            targetRoute = "notice_board"
        ),
        AppNotification(
            id = "notif_4",
            title = "সাপ্তাহিক তাফসীর মাহফিল",
            message = "আগামী শনিবার মাগরিব নামাজের পর সূরা বাকারার গুরুত্বপূর্ণ তাফসীর পেশ করা হবে।",
            timestamp = "৪ মে, ২০২৫",
            timeAgo = "৩ দিন আগে",
            category = NotificationCategory.EVENT,
            isRead = true,
            targetRoute = "events"
        )
    )

    private val _notificationsFlow = MutableStateFlow(_initialNotifications)
    val notificationsFlow: StateFlow<List<AppNotification>> = _notificationsFlow.asStateFlow()
    val initialNotifications: List<AppNotification> get() = _notificationsFlow.value

    fun broadcastNotification(notification: AppNotification) {
        val current = _notificationsFlow.value.toMutableList()
        current.add(0, notification)
        _notificationsFlow.value = current
    }

    fun markNotificationAsRead(id: String) {
        _notificationsFlow.value = _notificationsFlow.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun clearAllNotifications() {
        _notificationsFlow.value = emptyList()
    }

    // -------------------------------------------------------------
    // DUAS & DHIKR LIBRARY
    // -------------------------------------------------------------
    private val _initialDuas = listOf(
        DuaItem(
            id = "dua_1",
            titleBn = "মসজিদে প্রবেশের দোয়া",
            category = DuaCategory.MOSQUE,
            arabicText = "اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ",
            pronunciationBn = "আল্লাহুম্মাফ তাহলী আবওয়াবা রাহমাতিক।",
            meaningBn = "হে আল্লাহ! আমার জন্য আপনার রহমতের দরজাসমূহ উন্মুক্ত করে দিন।",
            reference = "সহিহ মুসলিম: ৭১৩",
            benefit = "মসজিদে ডান পা দিয়ে প্রবেশের সময় এ দোয়া পড়লে আল্লাহর রহমত বর্ষিত হয়।",
            repetitionCount = 1
        ),
        DuaItem(
            id = "dua_2",
            titleBn = "মসজিদ থেকে বের হওয়ার দোয়া",
            category = DuaCategory.MOSQUE,
            arabicText = "اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ",
            pronunciationBn = "আল্লাহুম্মা ইন্নি আসআলুকা মিন ফাদলিক।",
            meaningBn = "হে আল্লাহ! আমি আপনার নিকট আপনার অনুগ্রহ ও বরকত প্রার্থনা করছি।",
            reference = "সহিহ মুসলিম: ৭১৩",
            benefit = "মসজিদ থেকে বাম পা দিয়ে বের হওয়ার সময় পড়া সুন্নত।",
            repetitionCount = 1
        ),
        DuaItem(
            id = "dua_3",
            titleBn = "সকাল-সন্ধ্যার শ্রেষ্ঠ ইস্তিগফার (সাইয়্যিদুল ইস্তিগফার)",
            category = DuaCategory.MORNING_EVENING,
            arabicText = "اللَّهُمَّ أَنْتَ رَبِّي لاَ إِلَهَ إِلاَّ أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لاَ يَغْفِرُ الذُّنُوبَ إِلاَّ أَنْتَ",
            pronunciationBn = "আল্লাহুম্মা আনতা রব্বি লা ইলাহা ইল্লা আনতা, খালাক্বতানি ওয়া আনা আবদুকা, ওয়া আনা আলা আহদিকা ওয়া ওয়া'দিকা মাসতাত্বা'তু, আ'উযু বিকা মিন শাররি মা ছানা'তু, আবূউ লাকা বিনি'মাতিকা আলাইয়া, ওয়া আবূউ বিযানবী ফাগফিরলী ফাইন্নাহু লা ইয়াগফিরুয যুনূবা ইল্লা আনতা।",
            meaningBn = "হে আল্লাহ! আপনিই আমার প্রতিপালক। আপনি ছাড়া কোনো সত্য উপাস্য নেই। আপনি আমাকে সৃষ্টি করেছেন এবং আমি আপনার বান্দা। আমি আমার সাধ্যমতো আপনার অঙ্গীকার ও প্রতিশ্রতির উপর প্রতিষ্ঠিত রয়েছি। আমি আমার কৃতকর্মের অনিষ্ট থেকে আপনার আশ্রয় চাই। আমার ওপর আপনার নিয়ামত স্বীকার করছি এবং আমার পাপও স্বীকার করছি। অতএব আমাকে ক্ষমা করে দিন; নিশ্চয় আপনি ছাড়া গুনাহ ক্ষমা করার আর কেউ নেই।",
            reference = "সহিহ বুখারি: ৬৩০৬",
            benefit = "যে ব্যক্তি দিনে বিশ্বাসের সাথে এ দোয়া পাঠ করবে এবং সন্ধ্যায় মারা যাবে, সে জান্নাতি হবে। অনুরূপভাবে রাতে পড়লে।",
            repetitionCount = 1
        ),
        DuaItem(
            id = "dua_4",
            titleBn = "খাবার শুরুর দোয়া",
            category = DuaCategory.FOOD,
            arabicText = "بِسْمِ اللَّهِ وَعَلَى بَرَكَةِ اللَّهِ",
            pronunciationBn = "বিসমিল্লাহি ওয়া 'আলা বারাকাতিল্লাহ।",
            meaningBn = "আল্লাহর নামে এবং আল্লাহর বরকতের উপর ভরসা করে খাওয়া শুরু করছি।",
            reference = "আল-মুসতাদরাক আলাস সহিহাইন: ৭১১১",
            benefit = "খাবারে আল্লাহর বরকত ও কল্যাণ লাভ হয়।",
            repetitionCount = 1
        ),
        DuaItem(
            id = "dua_5",
            titleBn = "খাবার শেষের দোয়া",
            category = DuaCategory.FOOD,
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنَا وَسَقَانَا وَجَعَلَنَا مُسْلِمِينَ",
            pronunciationBn = "আলহামদু লিল্লাহিল্লাযী আত'আমানা ওয়া সাক্বানা ওয়া জা'আলানা মুসলিমীন।",
            meaningBn = "সকল প্রশংসা আল্লাহর জন্য, যিনি আমাদেরকে আহার করালেন, পান করালেন এবং মুসলিমদের অন্তর্ভুক্ত করলেন।",
            reference = "আবু দাউদ: ৩৮৫০, তিরমিজি: ৩৪৫৭",
            benefit = "রিযিকের নিয়ামতের শুকরিয়া আদায় হয়।",
            repetitionCount = 1
        ),
        DuaItem(
            id = "dua_6",
            titleBn = "ঘুমানোর পূর্বের দোয়া",
            category = DuaCategory.SLEEP,
            arabicText = "اللَّهُمَّ بِاسْمِكَ أَمُوتُ وَأَحْيَا",
            pronunciationBn = "আল্লাহুম্মা বিসমিকা আমূতু ওয়া আহ্ইয়া।",
            meaningBn = "হে আল্লাহ! আপনার নাম নিয়ে আমি মৃত্যুবরণ (নিদ্রা গ্রহণ) করছি এবং জীবিত (জাগ্রত) হব।",
            reference = "সহিহ বুখারি: ৬৩২৪",
            benefit = "ঘুমের মধ্যে অনিষ্ট ও দুঃস্বপ্ন থেকে সুরক্ষা মেলে।",
            repetitionCount = 1
        ),
        DuaItem(
            id = "dua_7",
            titleBn = "ঘুম থেকে জাগ্রত হওয়ার দোয়া",
            category = DuaCategory.SLEEP,
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
            pronunciationBn = "আলহামদু লিল্লাহিল্লাযী আহ্ইয়ানা বা'দা মা আমাতানা ওয়া ইলাইহিন নুশূর।",
            meaningBn = "সকল প্রশংসা সেই আল্লাহর জন্য, যিনি আমাদের মৃত্যুর পর পুনরায় জীবিত করলেন এবং তাঁরই সমীপে সকলের পুনরুত্থান হবে।",
            reference = "সহিহ বুখারি: ৬৩১২",
            benefit = "দিনের শুরুতে ঈমানি চেতনা জাগ্রত হয়।",
            repetitionCount = 1
        ),
        DuaItem(
            id = "dua_8",
            titleBn = "ঘর থেকে বের হওয়ার দোয়া",
            category = DuaCategory.TRAVEL,
            arabicText = "بِسْمِ اللَّهِ تَوَكَّلْتُ عَلَى اللَّهِ، لاَ حَوْلَ وَلاَ قُوَّةَ إِلاَّ بِاللَّهِ",
            pronunciationBn = "বিসমিল্লাহি তাওয়াক্কালতু 'আলাল্লাহ, লা হাওলা ওয়ালা কুওয়াতা ইল্লা বিল্লাহ।",
            meaningBn = "আল্লাহর নামে বের হচ্ছি, আল্লাহর উপর ভরসা করলাম। আল্লাহর সাহায্য ছাড়া পাপ থেকে বাঁচার এবং সৎকাজ করার কোনো শক্তি নেই।",
            reference = "আবু দাউদ: ৫০৯৫, তিরমিজি: ৩৪২৬",
            benefit = "ফেরেশতারা হেফাজত করেন এবং শয়তান দূরে সরে যায়।",
            repetitionCount = 1
        ),
        DuaItem(
            id = "dua_9",
            titleBn = "যানবাহনে আরোহণের দোয়া",
            category = DuaCategory.TRAVEL,
            arabicText = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَى رَبِّنَا لَمُنْقَلِبُونَ",
            pronunciationBn = "সুবহানাল্লাযী সাখখারা লানা হাযা ওয়া মা কুন্না লাহূ মুক্বরিনীন, ওয়া ইন্না ইলা রব্বিনা লামুনক্বালিবূন।",
            meaningBn = "পবিত্র সেই মহান সত্তা, যিনি এই বাহনকে আমাদের বশীভূত করে দিয়েছেন, অথচ আমরা একে বশীভূত করতে সক্ষম ছিলাম না। আর নিশ্চয়ই আমরা আমাদের প্রতিপালকের দিকেই প্রত্যাবর্তন করব।",
            reference = "সূরা যুখরুফ: ১৩-১৪, তিরমিজি: ৩৪৪৬",
            benefit = "সফরের সকল বিপদাপদ থেকে আল্লাহর নিরাপত্তায় থাকা যায়।",
            repetitionCount = 1
        ),
        DuaItem(
            id = "dua_10",
            titleBn = "অসুস্থতা ও সকল অনিষ্ট থেকে মুক্তির দোয়া",
            category = DuaCategory.PROTECTION,
            arabicText = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
            pronunciationBn = "আ'উযু বিকালিমা-তিল্লাহিত তা-ম্মা-তি মিন শাররি মা খালাক্ব।",
            meaningBn = "আমি আল্লাহর নিখুঁত বাণীর আশ্রয়ে তাঁর সৃষ্টির যাবতীয় অনিষ্ট থেকে পানাহ চাই।",
            reference = "সহিহ মুসলিম: ২৭০৮",
            benefit = "বিষাক্ত প্রাণী, রোগবালাই ও জিন-ইনসানের অনিষ্ট থেকে হেফাজত পাওয়া যায়।",
            repetitionCount = 3
        ),
        DuaItem(
            id = "dua_11",
            titleBn = "পিতা-মাতার জন্য শ্রেষ্ঠ দোয়া",
            category = DuaCategory.FORGIVENESS,
            arabicText = "رَبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
            pronunciationBn = "রব্বির হামহুমা কামা রব্বায়ানী সাগীরা।",
            meaningBn = "হে আমার প্রতিপালক! তাদের দুজনের (পিতা-মাতার) ওপর দয়া করুন, যেভাবে শৈশবে তারা আমাকে স্নেহভরে লালন-পালন করেছেন।",
            reference = "সূরা বনি ইসরাঈল: ২৪",
            benefit = "পিতা-মাতার প্রতি সন্তানের কৃতজ্ঞতা ও তাদের পারলৌকিক মুক্তির সর্বোত্তম প্রার্থনা।",
            repetitionCount = 1
        ),
        DuaItem(
            id = "dua_12",
            titleBn = "দ্বীনের উপর অবিচল থাকার দোয়া",
            category = DuaCategory.DAILY,
            arabicText = "يَا مُقَلِّبَ الْقُلُوبِ ثَبِّتْ قَلْبِي عَلَى دِينِكَ",
            pronunciationBn = "ইয়া মুক্বাল্লিবাল কুলূব, ছাব্বিত ক্বলবী 'আলা দীনিক।",
            meaningBn = "হে অন্তরসমূহের পরিবর্তনকারী! আমার অন্তরকে আপনার দ্বীনের উপর দৃঢ় ও অবিচল রাখুন।",
            reference = "তিরমিজি: ২১৪০",
            benefit = "রাসূলুল্লাহ (সা.) এই দোয়াটি সর্বাধিক বেশি পাঠ করতেন।",
            repetitionCount = 3
        )
    )

    private val _duasFlow = MutableStateFlow(_initialDuas)
    val duasFlow: StateFlow<List<DuaItem>> = _duasFlow.asStateFlow()
    val duas: List<DuaItem> get() = _duasFlow.value

    fun addOrUpdateDua(dua: DuaItem) {
        val current = _duasFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == dua.id }
        if (index >= 0) {
            current[index] = dua
        } else {
            current.add(0, dua)
        }
        _duasFlow.value = current
    }

    fun deleteDua(duaId: String) {
        _duasFlow.value = _duasFlow.value.filter { it.id != duaId }
    }

    // -------------------------------------------------------------
    // TASBEEH ITEMS
    // -------------------------------------------------------------
    val tasbeehItems = listOf(
        TasbeehItem("t1", "সুবহানাল্লাহ", "سُبْحَانَ اللَّهِ", 33),
        TasbeehItem("t2", "আলহামদুলিল্লাহ", "الْحَمْدُ لِلَّهِ", 33),
        TasbeehItem("t3", "আল্লাহু আকবার", "اللَّهُ أَكْبَرُ", 34),
        TasbeehItem("t4", "লা ইলাহা ইল্লাল্লাহ", "لَا إِلَهَ إِلَّا اللَّهُ", 100),
        TasbeehItem("t5", "আস্তাগফিরুল্লাহ", "أَسْتَغْفِرُ اللَّهَ", 100),
        TasbeehItem("t6", "সাল্লাল্লাহু আলাইহি ওয়া সাল্লাম", "صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ", 100)
    )

    // -------------------------------------------------------------
    // QURAN SURAHS (Authentic Text & Translations)
    // -------------------------------------------------------------
    val quranSurahs = listOf(
        QuranSurah(
            number = 1,
            nameArabic = "الفاتحة",
            nameBengali = "আল-ফাতিহা",
            nameEnglish = "Al-Fatihah",
            meaningBengali = "সূচনা / ভূমিকা",
            totalVerses = 7,
            revelationType = "মক্কী",
            verses = listOf(
                QuranVerse(1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "পরম করুণাময় অসীম দয়ালু আল্লাহর নামে শুরু করছি।", "বিসমিল্লাহির রাহমানির রাহীম"),
                QuranVerse(2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "যাবতীয় প্রশংসা শুধুমাত্র বিশ্বজগতের প্রতিপালক আল্লাহর জন্য।", "আলহামদু লিল্লাহি রাব্বিল আলামীন"),
                QuranVerse(3, "الرَّحْمَٰنِ الرَّحِيمِ", "যিনি পরম করুণাময় ও অসীম দয়ালু।", "আর-রাহমানির রাহীম"),
                QuranVerse(4, "مَالِكِ يَوْمِ الدِّينِ", "যিনি প্রতিফল দিবসের একমাত্র মালিক ও অধিপতি।", "মালিকি ইয়াওমিদ দ্বীন"),
                QuranVerse(5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "আমরা কেবল আপনারই ইবাদত করি এবং শুধুমাত্র আপনারই কাছে সাহায্য প্রার্থনা করি।", "ইয়্যাকা না'বুদু ওয়া ইয়্যাকা নাসতা'ঈন"),
                QuranVerse(6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "আমাদেরকে সরল-সঠিক পথ প্রদর্শন করুন।", "ইহদিনাস সিরাতাল মুসতাক্বীম"),
                QuranVerse(7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "তাদের পথ, যাদেরকে আপনি অনুগ্রহ করেছেন; তাদের পথ নয় যারা অভিশপ্ত হয়েছে এবং যারা পথভ্রষ্ট হয়েছে।", "সিরাতাল্লাযীনা আন'আমতা আলাইহিম, গায়রিল মাগদূবি আলাইহিম ওয়ালাদ-দোয়াল্লীন (আমীন)")
            )
        ),
        QuranSurah(
            number = 112,
            nameArabic = "الإخلاص",
            nameBengali = "আল-ইখলাস",
            nameEnglish = "Al-Ikhlas",
            meaningBengali = "একত্ববাদ / খাঁটি বিশ্বাস",
            totalVerses = 4,
            revelationType = "মক্কী",
            verses = listOf(
                QuranVerse(1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "বলুন, তিনিই আল্লাহ, যিনি একক ও অদ্বিতীয়।", "ক্বুল হুয়াল্লাহু আহাদ"),
                QuranVerse(2, "اللَّهُ الصَّمَدُ", "আল্লাহ অমুখাপেক্ষী, সবাই তাঁর মুখাপেক্ষী।", "আল্লাহুস সামাদ"),
                QuranVerse(3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "তিনি কাউকে জন্ম দেননি এবং তাঁকেও কেউ জন্ম দেয়নি।", "লাম ইয়ালিদ ওয়া লাম ইয়ূলাদ"),
                QuranVerse(4, "وَلَمْ يَكُنْ لَهُ كُفُوًا أَحَدٌ", "এবং তাঁর সমকক্ষ বা তুলনীয় কেউই নেই।", "ওয়া লাম ইয়া কুল্লাহূ কুফুওয়ান আহাদ")
            )
        ),
        QuranSurah(
            number = 113,
            nameArabic = "الفلق",
            nameBengali = "আল-ফালাক",
            nameEnglish = "Al-Falaq",
            meaningBengali = "নিশিভোর / প্রভাত",
            totalVerses = 5,
            revelationType = "মাক্কী",
            verses = listOf(
                QuranVerse(1, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "বলুন, আমি আশ্রয় গ্রহণ করছি প্রভাতের প্রতিপালকের,", "ক্বুল আ'উযু বিরাব্বিল ফালাক্ব"),
                QuranVerse(2, "مِنْ شَرِّ مَا خَلَقَ", "তিনি যা সৃষ্টি করেছেন তার যাবতীয় অনিষ্ট থেকে,", "মিন শাররি মা খালাক্ব"),
                QuranVerse(3, "وَمِنْ شَرِّ غَاسِقٍ إِذَا وَقَبَ", "এবং অন্ধকারের অনিষ্ট থেকে, যখন তা চতুর্দিকে ছড়িয়ে পড়ে,", "ওয়া মিন শাররি গাসিক্বিন ইযা ওয়াক্বাব"),
                QuranVerse(4, "وَمِنْ شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "এবং গ্রন্থিতে ফুৎকার দিয়ে জাদুকারিণীদের অনিষ্ট থেকে,", "ওয়া মিন শাররিন নাফফাসাতি ফিল 'উক্বাদ"),
                QuranVerse(5, "وَمِنْ شَرِّ حَاسِدٍ إِذَا حَسَدَ", "এবং হিংসুকের অনিষ্ট থেকে, যখন সে হিংসা করে।", "ওয়া মিন শাররি হাসিদিন ইযা হাসাদ")
            )
        ),
        QuranSurah(
            number = 114,
            nameArabic = "الناس",
            nameBengali = "আন-নাস",
            nameEnglish = "An-Nas",
            meaningBengali = "মানবজাতি",
            totalVerses = 6,
            revelationType = "মাক্কী",
            verses = listOf(
                QuranVerse(1, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "বলুন, আমি আশ্রয় গ্রহণ করছি মানুষের প্রতিপালকের,", "ক্বুল আ'উযু বিরাব্বিন নাস"),
                QuranVerse(2, "مَلِكِ النَّاسِ", "মানুষের একমাত্র অধিপতির,", "মালিকিন নাস"),
                QuranVerse(3, "إِلَٰهِ النَّاسِ", "মানুষের সত্য উপাস্যের,", "ইলাহিন নাস"),
                QuranVerse(4, "مِنْ شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "আত্মগোপনকারী কুমন্ত্রণাদাতার অনিষ্ট থেকে,", "মিন শাররিল ওয়াসওয়াসিল খান্নাস"),
                QuranVerse(5, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "যে মানুষের অন্তরে কুমন্ত্রণা দেয়,", "আল্লাযী ইউওয়াসউইসু ফী সুদূরিন নাস"),
                QuranVerse(6, "مِنَ الْجِنَّةِ وَالنَّاسِ", "জ্বিনদের মধ্য থেকে অথবা মানুষের মধ্য থেকে।", "মিনাল জিন্নাতি ওয়ান নাস")
            )
        ),
        QuranSurah(
            number = 36,
            nameArabic = "يس",
            nameBengali = "ইয়াসীন",
            nameEnglish = "Ya-Sin",
            meaningBengali = "ইয়াসীন (কুরআনের হৃৎপিণ্ড)",
            totalVerses = 83,
            revelationType = "মাক্কী",
            verses = listOf(
                QuranVerse(1, "يس", "ইয়া-সীন।", "ইয়া-সীন"),
                QuranVerse(2, "وَالْقُرْآنِ الْحَكِيمِ", "প্রজ্ঞাময় কুরআনের শপথ,", "ওয়াল কুরআনিল হাকীম"),
                QuranVerse(3, "إِنَّكَ لَمِنَ الْمُرْسَلِينَ", "নিশ্চয়ই আপনি প্রেরিত রাসূলদের অন্যতম,", "ইন্নাকা লামিনাল মুরসালীন"),
                QuranVerse(4, "عَلَىٰ صِرَاطٍ مُسْتَقِيمٍ", "সরল-সঠিক পথের ওপর প্রতিষ্ঠিত।", "আলা সিরাতিল মুসতাক্বীম"),
                QuranVerse(5, "تَنْزِيلَ الْعَزِيزِ الرَّحِيمِ", "এ কুরআন মহা পরাক্রমশালী, পরম দয়ালু আল্লাহর পক্ষ থেকে অবতীর্ণ,", "তানযীলাল আযীযির রাহীম")
            )
        ),
        QuranSurah(
            number = 67,
            nameArabic = "الملك",
            nameBengali = "আল-মুলক",
            nameEnglish = "Al-Mulk",
            meaningBengali = "সার্বভৌম কর্তৃত্ব / রাজত্ব",
            totalVerses = 30,
            revelationType = "মাক্কী",
            verses = listOf(
                QuranVerse(1, "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", "পরম বরকতময় তিনি, যাঁর হাতে সর্বময় কর্তৃত্ব; আর তিনি সর্ববিষয়ে সর্বশক্তিমান।", "তাবারাকাল্লাযী বিয়াদিহিল মুলকু ওয়া হুয়া 'আলা কুল্লি শাইয়িন ক্বাদীর"),
                QuranVerse(2, "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا", "যিনি সৃষ্টি করেছেন মৃত্যু ও জীবন, তোমাদের পরীক্ষা করার জন্য যে, তোমাদের মধ্যে কে কর্মে শ্রেষ্ঠ?", "আল্লাযী খালাক্বাল মাওতা ওয়াল হায়াতা লিইয়াবলুওয়াকুম আইয়্যুকুম আহসানু 'আমালা"),
                QuranVerse(3, "الَّذِي خَلَقَ سَبْعَ سَمَاوَاتٍ طِبَاقًا", "যিনি সপ্ত আকাশ স্তরে স্তরে সৃষ্টি করেছেন।", "আল্লাযী খালাক্বা সাব'আ সামাওয়া-তিন তিবা-ক্বা")
            )
        )
    )

    // -------------------------------------------------------------
    // EVENTS
    // -------------------------------------------------------------
    private val _initialEvents = listOf(
        MosqueEvent(
            id = "ev_1",
            title = "পবিত্র শবে ক্বদর ও বিশেষ তাহাজ্জুদ ও দোয়ার মাহফিল",
            dateBn = "২৭ রমজান, ১৪৪৬ / ২৫ মে, ২০২৫",
            timeBn = "রাত ১০:৩০ মিনিট থেকে ফজর পর্যন্ত",
            locationBn = "বায়তুল আমান জামে মসজিদ (সকল তলা)",
            description = "হাজার মাসের শ্রেষ্ঠ রাত লাইলাতুল ক্বদরে রাতব্যাপী বিশেষ বয়ান, নফল নামাজ, খতমে কুরআন, যিকির ও উম্মাহর শান্তির জন্য কান্নাকাটিপূর্ণ আখেরি মোনাজাত অনুষ্ঠিত হবে।",
            category = EventCategory.SPECIAL_DUA,
            speaker = "মাওলানা মুফতি আব্দুল্লাহ আল-মাহমুদ ও দেশবরেণ্য ওলামায়ে কেরাম",
            isUpcoming = true
        ),
        MosqueEvent(
            id = "ev_2",
            title = "মাসিক ইসলামিক তরুণ ফোরাম ও ক্যারিয়ার গাইডেন্স",
            dateBn = "১৫ মে, ২০২৫ (বৃহস্পতিবার)",
            timeBn = "বাদ মাগরিব",
            locationBn = "মসজিদ মিলনায়তন (২য় তলা)",
            description = "যুবসমাজকে দ্বীনি মূল্যবোধে উজ্জীবিত করা এবং হালাল ক্যারিয়ার গঠনে দিকনির্দেশনামূলক বিশেষ ইসলামিক মোটিভেশনাল সেমিনার।",
            category = EventCategory.HALQA,
            speaker = "ইঞ্জিনিয়ার ও ইসলামিক গবেষক ড. আরিফুল ইসলাম",
            isUpcoming = true
        ),
        MosqueEvent(
            id = "ev_3",
            title = "বার্ষিক হিফজুল কুরআন প্রতিযোগিতা ও পুরস্কার বিতরণ",
            dateBn = "২০ মে, ২০২৫ (মঙ্গলবার)",
            timeBn = "সকাল ৯:০০ টা থেকে দুপুর ১:০০ টা",
            locationBn = "মসজিদ মূল হল",
            description = "অত্র এলাকার বিভিন্ন মাদরাসা ও মক্তবের শিশু শিক্ষার্থীদের মাঝে সুন্দর কিরাত ও হিফজ প্রতিযোগিতা অনুষ্ঠিত হবে।",
            category = EventCategory.QURAN_CLASS,
            speaker = "আন্তর্জাতিক ক্বারী পরিষদের বিচারকমণ্ডলী",
            isUpcoming = true
        ),
        MosqueEvent(
            id = "ev_4",
            title = "ঐতিহাসিক সীরাতুন্নবী (সা.) ও ইসলামিক প্রদর্শনী",
            dateBn = "১২ রবিউল আউয়াল, ১৪৪৬",
            timeBn = "দিনব্যাপী",
            locationBn = "মসজিদ চত্বর",
            description = "বিশ্বনবী হযরত মুহাম্মদ (সা.) এর পবিত্র জীবনচরিত, মক্কা-মদিনার ঐতিহাসিক মানচিত্র ও দুর্লভ ইসলামিক পাণ্ডুলিপির প্রদর্শনী।",
            category = EventCategory.WAZ,
            speaker = "জাতীয় মসজিদ বায়তুল মোকাররমের খতিব মহোদয়",
            isUpcoming = false
        )
    )

    private val _eventsFlow = MutableStateFlow(_initialEvents)
    val eventsFlow: StateFlow<List<MosqueEvent>> = _eventsFlow.asStateFlow()
    val events: List<MosqueEvent> get() = _eventsFlow.value

    fun addOrUpdateEvent(event: MosqueEvent) {
        val current = _eventsFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == event.id }
        if (index >= 0) {
            current[index] = event
        } else {
            current.add(0, event)
        }
        _eventsFlow.value = current
    }

    fun deleteEvent(eventId: String) {
        _eventsFlow.value = _eventsFlow.value.filter { it.id != eventId }
    }

    // -------------------------------------------------------------
    // AUDIT LOGS & ADMIN ACTIVITY
    // -------------------------------------------------------------
    private val _initialAuditLogs = listOf(
        AdminAuditLog(
            id = "log_1",
            adminNameBn = "মাওলানা আব্দুল্লাহ আল-মাহমুদ",
            adminRoleBn = "প্রধান ইমাম ও খতিব",
            category = AuditActionCategory.PRAYER,
            actionTitleBn = "আসরের জামাত সময় পরিবর্তন",
            detailsBn = "আসরের জামাত বিকাল ৪:৪৫ এর পরিবর্তে ৫:০০ টায় নির্ধারণ করা হয়েছে।",
            timestampBn = "আজ, ১২:৩০ PM"
        ),
        AdminAuditLog(
            id = "log_2",
            adminNameBn = "আলহাজ্ব মো. রফিকুল ইসলাম চৌধুরী",
            adminRoleBn = "সভাপতি",
            category = AuditActionCategory.NOTICES,
            actionTitleBn = "রমজানের তারাবীহ নোটিশ প্রকাশ",
            detailsBn = "পবিত্র মাহে রমজানের তারাবীহ ও ইফতার আয়োজন সংক্রান্ত জরুরি বিজ্ঞপ্তি পিন করা হয়েছে।",
            timestampBn = "গতকাল, ০৮:১৫ PM"
        ),
        AdminAuditLog(
            id = "log_3",
            adminNameBn = "মো. তারিকুল ইসলাম স্বপন",
            adminRoleBn = "যুগ্ম সাধারণ সম্পাদক",
            category = AuditActionCategory.MEALS,
            actionTitleBn = "হুজুরের খানা হোস্ট পরিবর্তন",
            detailsBn = "আগামী শুক্রবারের মেহমানদারি হাজী নুরুল হক সাহেবের বাড়িতে বরাদ্দ নিশ্চিত করা হয়েছে।",
            timestampBn = "২৬ মে, ০৪:২০ PM"
        ),
        AdminAuditLog(
            id = "log_4",
            adminNameBn = "মাওলানা মো. মাহমুদুল হাসান",
            adminRoleBn = "প্রচার ও প্রকাশনা সম্পাদক",
            category = AuditActionCategory.EVENTS,
            actionTitleBn = "লাইলাতুল কদরের মাহফিল শিডিউল যোগ",
            detailsBn = "পবিত্র শবে ক্বদর ও বিশেষ তাহাজ্জুদ মাহফিলের বিস্তারিত সূচি প্রকাশ করা হয়েছে।",
            timestampBn = "২৪ মে, ১০:১৫ AM"
        )
    )

    private val _auditLogsFlow = MutableStateFlow(_initialAuditLogs)
    val auditLogsFlow: StateFlow<List<AdminAuditLog>> = _auditLogsFlow.asStateFlow()
    val auditLogs: List<AdminAuditLog> get() = _auditLogsFlow.value

    fun logAdminAction(
        adminNameBn: String,
        adminRoleBn: String,
        category: AuditActionCategory,
        actionTitleBn: String,
        detailsBn: String
    ) {
        val formatter = SimpleDateFormat("dd MMM, hh:mm a", Locale("bn", "BD"))
        val nowFormatted = formatter.format(Date())
        val newLog = AdminAuditLog(
            id = "log_${System.currentTimeMillis()}",
            adminNameBn = adminNameBn,
            adminRoleBn = adminRoleBn,
            category = category,
            actionTitleBn = actionTitleBn,
            detailsBn = detailsBn,
            timestampBn = nowFormatted
        )
        val current = _auditLogsFlow.value.toMutableList()
        current.add(0, newLog)
        _auditLogsFlow.value = current
    }

    fun clearAuditLogs() {
        _auditLogsFlow.value = emptyList()
    }

    // -------------------------------------------------------------
    // GALLERY
    // -------------------------------------------------------------
    val galleryItems = listOf(
        GalleryItem("g1", "মসজিদের প্রধান সম্মুখভাগ ও রাতের আলোকসজ্জা", "ঐতিহ্যবাহী ইসলামিক আর্চ ও মিনারের আধুনিক স্থাপত্য রূপ।", GalleryCategory.ARCHITECTURE, "", "রমজান ১৪৪৫"),
        GalleryItem("g2", "পবিত্র জুমার নামাজে মুসল্লিদের উপচে পড়া ভিড়", "ভেতরের হল ছাড়িয়ে বাইরেও সারিবদ্ধভাবে মুসল্লিদের জুমার নামাজ আদায়।", GalleryCategory.JUMAH, "", "শুক্রবার, মে ২০২৫"),
        GalleryItem("g3", "রমজানের গণ ইফতার মাহফিল", "প্রতিদিন মসজিদে প্রায় ৫০০ রোজাদারের বিনামূল্যে ইফতার পরিবেশন।", GalleryCategory.RAMADAN, "", "রমজান ১৪৪৫"),
        GalleryItem("g4", "নূরানী মক্তবের শিশু শিক্ষার্থীদের কুরআন তিলাওয়াত", "সকালের বিশুদ্ধ তাজবীদ ক্লাসে শিশুদের পাঠদান।", GalleryCategory.EVENTS, "", "এপ্রিল ২০২৫"),
        GalleryItem("g5", "মসজিদের অভ্যন্তরীণ গম্বুজ ও ক্যালিগ্রাফি আর্ট", "হাতে খোদাইকৃত দৃষ্টিনন্দন ইসলামিক জ্যামিতিক নকশা ও আয়াতুল কুরসী।", GalleryCategory.ARCHITECTURE, "", "মার্চ ২০২৫"),
        GalleryItem("g6", "নতুন অজুখানা নির্মাণ ও আধুনিকায়ন কাজ", "মসজিদ সম্প্রসারণ প্রকল্পের আওতায় আধুনিক ওজুখানা প্রস্তুত।", GalleryCategory.CONSTRUCTION, "", "ফেব্রুয়ারি ২০২৫")
    )

    // -------------------------------------------------------------
    // DONATION ACCOUNTS
    // -------------------------------------------------------------
    private val _initialBankAccounts = listOf(
        BankAccountInfo(
            bankName = "ইসলামী ব্যাংক বাংলাদেশ পিএলসি",
            accountName = "BAITUL AMAN JAME MASJID",
            accountNumber = "২০৫০-১২২০-২০০১-৪৫৬৭",
            branchName = "মিরপুর শাখা, ঢাকা",
            routingNumber = "১২৫২৬২৬৮১"
        ),
        BankAccountInfo(
            bankName = "আল-আরাফাহ ইসলামী ব্যাংক পিএলসি",
            accountName = "BAITUL AMAN MASJID COMMITTEE",
            accountNumber = "০১২১-০২০০-০৩৪৫-৮৯০১",
            branchName = "মিরপুর-১০ শাখা, ঢাকা",
            routingNumber = "০১৫২৬১৩৪২"
        )
    )

    private val _bankAccountsFlow = MutableStateFlow(_initialBankAccounts)
    val bankAccountsFlow: StateFlow<List<BankAccountInfo>> = _bankAccountsFlow.asStateFlow()
    val bankAccounts: List<BankAccountInfo> get() = _bankAccountsFlow.value

    fun addOrUpdateBankAccount(account: BankAccountInfo) {
        val current = _bankAccountsFlow.value.toMutableList()
        val index = current.indexOfFirst { it.accountNumber == account.accountNumber }
        if (index >= 0) {
            current[index] = account
        } else {
            current.add(account)
        }
        _bankAccountsFlow.value = current
    }

    fun deleteBankAccount(accountNumber: String) {
        _bankAccountsFlow.value = _bankAccountsFlow.value.filter { it.accountNumber != accountNumber }
    }

    private val _initialMobileAccounts = listOf(
        MobileAccountInfo("bKash (বিকাশ মার্চেন্ট)", "01711223344", "মার্চেন্ট পেমেন্ট / কাউন্টার নং ১"),
        MobileAccountInfo("Nagad (নগদ ইসলামিক)", "01819223344", "মার্চেন্ট পে"),
        MobileAccountInfo("Rocket (রকেট বিলার)", "01912334455", "বিলার আইডি: ৩৪০৫")
    )

    private val _mobileAccountsFlow = MutableStateFlow(_initialMobileAccounts)
    val mobileAccountsFlow: StateFlow<List<MobileAccountInfo>> = _mobileAccountsFlow.asStateFlow()
    val mobileAccounts: List<MobileAccountInfo> get() = _mobileAccountsFlow.value

    fun addOrUpdateMobileAccount(account: MobileAccountInfo) {
        val current = _mobileAccountsFlow.value.toMutableList()
        val index = current.indexOfFirst { it.number == account.number }
        if (index >= 0) {
            current[index] = account
        } else {
            current.add(account)
        }
        _mobileAccountsFlow.value = current
    }

    fun deleteMobileAccount(number: String) {
        _mobileAccountsFlow.value = _mobileAccountsFlow.value.filter { it.number != number }
    }

    // Community Donation Records Flow for Tracking
    private val _initialDonationRecords = listOf(
        DonationRecord("dn_1", "সাধারণ মসজিদ তহবিল", 2500L, "bKash (বিকাশ)", "TXN89234821", "মো. তারিকুল ইসলাম", "01711000000", "আজ, ০২:১৫ PM", "যাচাইকৃত ও গৃহীত"),
        DonationRecord("dn_2", "নির্মাণ ও সংস্কার তহবিল", 10000L, "ইসলামী ব্যাংক (ব্যাংক ট্রান্সফার)", "IBBL-9902341", "হাজী আলতাফ হোসেন", "01819000000", "গতকাল, ১০:৩০ AM", "যাচাইকৃত ও গৃহীত"),
        DonationRecord("dn_3", "ইমাম-মুয়াজ্জিন কল্যাণ", 1500L, "Nagad (নগদ)", "NGD77218392", "আব্দুর রহমান", "01912000000", "২৮ মে, ০৭:৪৫ PM", "যাচাইকৃত ও গৃহীত"),
        DonationRecord("dn_4", "রমজান ইফতার ও ঈদ তহবিল", 5000L, "নগদ ক্যাশ (মসজিদ অফিস)", "CASH-2025-05", "নাম প্রকাশে অনিচ্ছুক", "01600000000", "২৬ মে, ১২:০০ PM", "যাচাইকৃত ও গৃহীত")
    )

    private val _donationRecordsFlow = MutableStateFlow(_initialDonationRecords)
    val donationRecordsFlow: StateFlow<List<DonationRecord>> = _donationRecordsFlow.asStateFlow()
    val donationRecords: List<DonationRecord> get() = _donationRecordsFlow.value

    fun addDonationRecord(record: DonationRecord) {
        val current = _donationRecordsFlow.value.toMutableList()
        current.add(0, record)
        _donationRecordsFlow.value = current
    }

    fun updateDonationStatus(recordId: String, newStatus: String) {
        _donationRecordsFlow.value = _donationRecordsFlow.value.map {
            if (it.id == recordId) it.copy(status = newStatus) else it
        }
    }

    fun deleteDonationRecord(recordId: String) {
        _donationRecordsFlow.value = _donationRecordsFlow.value.filter { it.id != recordId }
    }

    // -------------------------------------------------------------
    // ISLAMIC IMPORTANT DATES (CALENDAR)
    // -------------------------------------------------------------
    data class IslamicHolyDay(
        val nameBn: String,
        val hijriDateBn: String,
        val gregorianDateBn: String,
        val descriptionBn: String
    )

    val islamicHolyDays = listOf(
        IslamicHolyDay("পবিত্র আশুরা (১০ মহররম)", "১০ মহররম, ১৪৪৬", "১৭ জুলাই, ২০২৪", "ঐতিহাসিক কারবালা ট্র্যাজেডি ও ইসলামের সত্যের বিজয়ের মহিমান্বিত দিন।"),
        IslamicHolyDay("আখেরি চাহার শোম্বা", "২৮ সফর, ১৪৪৬", "৪ সেপ্টেম্বর, ২০২৪", "রাসূলুল্লাহ (সা.) এর শেষ সুস্থতার স্মৃতিবাহী দিন।"),
        IslamicHolyDay("পবিত্র ঈদে মিলাদুন্নবী (সা.)", "১২ রবিউল আউয়াল, ১৪৪৬", "১৬ সেপ্টেম্বর, ২০২৪", "মানবজাতির রহমত বিশ্বনবী মুহাম্মদ (সা.) এর পবিত্র জন্ম ও ওফাত দিবস।"),
        IslamicHolyDay("পবিত্র শবে মেরাজ", "২৭ রজব, ১৪৪৬", "২৮ জানুয়ারি, ২০২৫", "রাসূলুল্লাহ (সা.) এর ঊর্ধ্বাকাশ ভ্রমণ ও পাঁচ ওয়াক্ত নামাজের উপহার প্রাপ্তির রাত।"),
        IslamicHolyDay("পবিত্র শবে বরাত (লাইলাতুল বারাআত)", "১৫ শাবান, ১৪৪৬", "১৫ ফেব্রুয়ারি, ২০২৫", "আল্লাহ তা'আলার পক্ষ থেকে বিশেষ রহমত ও মাগফিরাতের মহা বরকতময় রাত।"),
        IslamicHolyDay("পবিত্র মাহে রমজান শুরু", "১ রমজান, ১৪৪৬", "২ মার্চ, ২০২৫", "সিয়াম সাধনা, কুরআন নাজিল ও আত্মশুদ্ধির পবিত্রতম মাস।"),
        IslamicHolyDay("পবিত্র শবে ক্বদর (লাইলাতুল ক্বদর)", "২৭ রমজান, ১৪৪৬", "২৮ মার্চ, ২০২৫", "হাজার মাস অপেক্ষা শ্রেষ্ঠ বরকতময় রাত, যে রাতে কুরআন অবতীর্ণ হয়।"),
        IslamicHolyDay("পবিত্র ঈদুল ফিতর", "১ শাওয়াল, ১৪৪৬", "৩১ মার্চ, ২০২৫", "রমজানের রোজা সমাপ্তির পরম আনন্দ ও সম্প্রীতির উৎসব।"),
        IslamicHolyDay("পবিত্র হজ ও আরাফার দিন", "৯ জিলহজ, ১৪৪৬", "৫ জুন, ২০২৫", "বিশ্ব মুসলিমের মহাসম্মিলন ও হজের মূল রুকন দিবস।"),
        IslamicHolyDay("পবিত্র ঈদুল আযহা (কোরবানির ঈদ)", "১০ জিলহজ, ১৪৪৬", "৬ জুন, ২০২৫", "হযরত ইব্রাহিম (আ.) এর মহান ত্যাগের স্মরণে আত্মত্যাগের উৎসব।")
    )

    // -------------------------------------------------------------
    // PRAYER TIME CALCULATION ENGINE
    // -------------------------------------------------------------
    private var _customOverrides: com.example.data.firebase.CustomPrayerOverride? = null

    fun setCustomPrayerOverrides(override: com.example.data.firebase.CustomPrayerOverride?) {
        _customOverrides = override
    }

    fun getCustomPrayerOverrides(): com.example.data.firebase.CustomPrayerOverride? = _customOverrides

    fun calculateTodayPrayers(districtId: String = "dhaka"): List<PrayerTimeItem> {
        val dist = getDistrictById(districtId)
        val cal = Calendar.getInstance()
        val currentMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)

        val overrides = _customOverrides
        val isFriday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY

        if (overrides != null && overrides.isCustomScheduleActive) {
            // Helper to check if prayer is passed based on Iqamah/time
            fun parseToMinutes(timeStr: String, isPmDefault: Boolean = false): Int {
                return try {
                    val parts = timeStr.trim().split(":")
                    var h = parts[0].toInt()
                    val m = parts[1].take(2).toInt()
                    if (isPmDefault && h < 12) h += 12
                    h * 60 + m
                } catch (e: Exception) { 0 }
            }

            val fMin = parseToMinutes(overrides.fajrAzan, false)
            val dMin = parseToMinutes(overrides.dhuhrAzan, true)
            val aMin = parseToMinutes(overrides.asrAzan, true)
            val mMin = parseToMinutes(overrides.maghribAzan, true)
            val iMin = parseToMinutes(overrides.ishaAzan, true)

            val isFajrActive = currentMinutes in fMin until (fMin + 90)
            val isDhuhrActive = currentMinutes in dMin until aMin
            val isAsrActive = currentMinutes in aMin until mMin
            val isMaghribActive = currentMinutes in mMin until iMin
            val isIshaActive = currentMinutes >= iMin || currentMinutes < fMin

            return listOf(
                PrayerTimeItem(PrayerType.FAJR, "ফজর", "الفجر", overrides.fajrAzan, overrides.fajrIqamah, isActive = isFajrActive, isPassed = currentMinutes > fMin + 90),
                PrayerTimeItem(PrayerType.DHUHR, if (isFriday) "জুম'আ / যোহর" else "যোহর", if (isFriday) "الجمعة" else "الظهر", if (isFriday) overrides.jumahAzan1 else overrides.dhuhrAzan, if (isFriday) overrides.jumahJamath else overrides.dhuhrIqamah, isJumah = isFriday, isActive = isDhuhrActive, isPassed = currentMinutes >= aMin),
                PrayerTimeItem(PrayerType.ASR, "আসর", "العصر", overrides.asrAzan, overrides.asrIqamah, isActive = isAsrActive, isPassed = currentMinutes >= mMin),
                PrayerTimeItem(PrayerType.MAGHRIB, "মাগরিব", "المغرب", overrides.maghribAzan, overrides.maghribIqamah, isActive = isMaghribActive, isPassed = currentMinutes >= iMin),
                PrayerTimeItem(PrayerType.ISHA, "এশা", "العشاء", overrides.ishaAzan, overrides.ishaIqamah, isActive = isIshaActive, isPassed = currentMinutes < iMin && currentMinutes >= fMin)
            )
        }

        // Baseline timings for Dhaka (in minutes from midnight)
        // Fajr: 04:05 (245m), Dhuhr: 12:00 (720m), Asr: 16:30 (990m), Maghrib: 18:35 (1115m), Isha: 19:55 (1195m)
        val fMin = 245 + dist.fajrOffsetMinutes
        val dMin = 720 + dist.dhuhrOffsetMinutes
        val aMin = 990 + dist.asrOffsetMinutes
        val mMin = 1115 + dist.maghribOffsetMinutes
        val iMin = 1195 + dist.ishaOffsetMinutes

        fun formatTime(totalMins: Int): String {
            val normalized = (totalMins + 1440) % 1440
            var h = normalized / 60
            val m = normalized % 60
            val ampm = if (h >= 12) "PM" else "AM"
            if (h > 12) h -= 12
            if (h == 0) h = 12
            return String.format(Locale.US, "%02d:%02d", h, m)
        }

        // Active prayer logic
        val isFajrActive = currentMinutes in fMin until (fMin + 90)
        val isDhuhrActive = currentMinutes in dMin until aMin
        val isAsrActive = currentMinutes in aMin until mMin
        val isMaghribActive = currentMinutes in mMin until iMin
        val isIshaActive = currentMinutes >= iMin || currentMinutes < fMin

        return listOf(
            PrayerTimeItem(PrayerType.FAJR, "ফজর", "الفجر", formatTime(fMin), formatTime(fMin + 30), isActive = isFajrActive, isPassed = currentMinutes > fMin + 90),
            PrayerTimeItem(PrayerType.DHUHR, if (isFriday) "জুম'আ / যোহর" else "যোহর", if (isFriday) "الجمعة" else "الظهر", formatTime(dMin), if (isFriday) formatTime(dMin + 90) else formatTime(dMin + 75), isJumah = isFriday, isActive = isDhuhrActive, isPassed = currentMinutes >= aMin),
            PrayerTimeItem(PrayerType.ASR, "আসর", "العصر", formatTime(aMin), formatTime(aMin + 30), isActive = isAsrActive, isPassed = currentMinutes >= mMin),
            PrayerTimeItem(PrayerType.MAGHRIB, "মাগরিব", "المغرب", formatTime(mMin), formatTime(mMin + 10), isActive = isMaghribActive, isPassed = currentMinutes >= iMin),
            PrayerTimeItem(PrayerType.ISHA, "এশা", "العشاء", formatTime(iMin), formatTime(iMin + 35), isActive = isIshaActive, isPassed = currentMinutes < iMin && currentMinutes >= fMin)
        )
    }

    fun getExtraPrayerTimes(districtId: String = "dhaka"): List<ExtraPrayerTime> {
        val dist = getDistrictById(districtId)
        val overrides = _customOverrides
        if (overrides != null && overrides.isCustomScheduleActive) {
            return listOf(
                ExtraPrayerTime("তাহাজ্জুদ", overrides.tahajjudTime, "রাতের শেষ তৃতীয়াংশে শ্রেষ্ঠ নফল ইবাদত"),
                ExtraPrayerTime("সেহরি শেষ সময়", overrides.sehriEnd, "সতর্কতামূলক সময় (মসজিদ নির্ধারিত)"),
                ExtraPrayerTime("সূর্যোদয় (ইশরাক)", overrides.ishraqTime, "সূর্যোদয়ের ১৫ মিনিট পর ইশরাকের নামাজ"),
                ExtraPrayerTime("চাশত (সালাতুদ দুহা)", overrides.chashtTime, "দিনের প্রথম ভাগে অত্যন্ত বরকতময় নামাজ"),
                ExtraPrayerTime("সূর্যাস্ত ও ইফতার", overrides.iftarTime, "মাগরিবের আজানের সাথে সাথে ইফতার"),
                ExtraPrayerTime("আউওয়াবিন", "০৬:৫২ PM", "মাগরিবের ফরজ ও সুন্নতের পর ৬ রাকাত নফল")
            )
        }
        return listOf(
            ExtraPrayerTime("তাহাজ্জুদ", "০১:৩০ - ০৩:৪৫ AM", "রাতের শেষ তৃতীয়াংশে শ্রেষ্ঠ নফল ইবাদত"),
            ExtraPrayerTime("সেহরি শেষ সময়", "০৩:৫৮ AM", "সতর্কতামূলক সময় (ঢাকা: +${dist.fajrOffsetMinutes} মি.)"),
            ExtraPrayerTime("সূর্যোদয় (ইশরাক)", "০৫:২২ AM", "সূর্যোদয়ের ১৫ মিনিট পর ইশরাকের নামাজ"),
            ExtraPrayerTime("চাশত (সালাতুদ দুহা)", "০৮:১৫ - ১১:১৫ AM", "দিনের প্রথম ভাগে অত্যন্ত বরকতময় নামাজ"),
            ExtraPrayerTime("সূর্যাস্ত ও ইফতার", "০৬:৩৬ PM", "মাগরিবের আজানের সাথে সাথে ইফতার"),
            ExtraPrayerTime("আউওয়াবিন", "০৬:৫২ PM", "মাগরিবের ফরজ ও সুন্নতের পর ৬ রাকাত নফল")
        )
    }

    fun generateMonthlySchedule(year: Int = 2025, month: Int = 5, districtId: String = "dhaka"): List<MonthlyPrayerDay> {
        val dist = getDistrictById(districtId)
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month - 1)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val todayCal = Calendar.getInstance()
        val todayYear = todayCal.get(Calendar.YEAR)
        val todayMonth = todayCal.get(Calendar.MONTH) + 1
        val todayDay = todayCal.get(Calendar.DAY_OF_MONTH)

        val dayNamesBn = arrayOf("রবি", "সোম", "মঙ্গল", "বুধ", "বৃহস্পতি", "শুক্র", "শনি")

        val list = mutableListOf<MonthlyPrayerDay>()
        for (d in 1..maxDays) {
            cal.set(Calendar.DAY_OF_MONTH, d)
            val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1=Sun .. 7=Sat
            val dayName = dayNamesBn[dayOfWeek - 1]
            val isFri = (dayOfWeek == Calendar.FRIDAY)
            val isToday = (year == todayYear && month == todayMonth && d == todayDay)

            // Dynamic progression across the month
            val dayOffset = (d - 15) / 5
            val fMin = 245 - dayOffset + dist.fajrOffsetMinutes
            val sMin = 320 - dayOffset + dist.fajrOffsetMinutes
            val dMin = 720 + dist.dhuhrOffsetMinutes
            val aMin = 990 + dayOffset + dist.asrOffsetMinutes
            val mMin = 1115 + dayOffset + dist.maghribOffsetMinutes
            val iMin = 1195 + dayOffset + dist.ishaOffsetMinutes

            fun fmt(mins: Int): String {
                val normalized = (mins + 1440) % 1440
                var h = normalized / 60
                val m = normalized % 60
                if (h > 12) h -= 12
                if (h == 0) h = 12
                return String.format(Locale.US, "%02d:%02d", h, m)
            }

            val bengaliDayNum = d + 16 // approximate Boishakh/Joistho day
            val hijriDayNum = d + 5 // approximate Dhul Qadah day

            list.add(
                MonthlyPrayerDay(
                    dayNumber = d,
                    bengaliDate = "$bengaliDayNum বৈশাখ",
                    hijriDate = "$hijriDayNum জিলক্বদ",
                    gregorianDate = "$d মে",
                    dayName = dayName,
                    fajrAzan = fmt(fMin),
                    fajrIqamah = fmt(fMin + 30),
                    sunrise = fmt(sMin),
                    dhuhrAzan = fmt(dMin),
                    dhuhrIqamah = if (isFri) fmt(dMin + 90) else fmt(dMin + 75),
                    asrAzan = fmt(aMin),
                    asrIqamah = fmt(aMin + 30),
                    maghribAzan = fmt(mMin),
                    maghribIqamah = fmt(mMin + 10),
                    ishaAzan = fmt(iMin),
                    ishaIqamah = fmt(iMin + 35),
                    isToday = isToday,
                    isFriday = isFri
                )
            )
        }
        return list
    }

    // -------------------------------------------------------------
    // DIGITAL TASBIH DHIKR PRESETS
    // -------------------------------------------------------------
    val dhikrList = listOf(
        DhikrItem(
            id = "dhikr_1",
            arabicText = "سُبْحَانَ اللَّهِ",
            transliterationBn = "সুবহানাল্লাহ",
            meaningBn = "আল্লাহ পরম পবিত্র ও মহিমান্বিত",
            defaultTarget = 33,
            rewardBn = "জান্নাতে একটি খেজুর গাছ রোপণ করা হয়"
        ),
        DhikrItem(
            id = "dhikr_2",
            arabicText = "الْحَمْدُ لِلَّهِ",
            transliterationBn = "আলহামদুলিল্লাহ",
            meaningBn = "সকল প্রশংসা কেবল মহান আল্লাহর জন্য",
            defaultTarget = 33,
            rewardBn = "মিজানের পাল্লা নেকিতে পরিপূর্ণ করে দেয়"
        ),
        DhikrItem(
            id = "dhikr_3",
            arabicText = "اللَّهُ أَكْبَرُ",
            transliterationBn = "আল্লাহু আকবার",
            meaningBn = "আল্লাহ সর্বশ্রেষ্ঠ ও মহান",
            defaultTarget = 33,
            rewardBn = "আসমান ও জমিনের মধ্যবর্তী স্থান সওয়াবে পূর্ণ হয়"
        ),
        DhikrItem(
            id = "dhikr_4",
            arabicText = "لَا إِلَٰهَ إِلَّا اللَّهُ",
            transliterationBn = "লা ইলাহা ইল্লাল্লাহ",
            meaningBn = "আল্লাহ ছাড়া কোনো সত্য উপাস্য নেই",
            defaultTarget = 100,
            rewardBn = "সর্বশ্রেষ্ঠ যিকির ও ঈমানের মূল ভিত্তি"
        ),
        DhikrItem(
            id = "dhikr_5",
            arabicText = "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
            transliterationBn = "আস্তাগফিরুল্লাহ ওয়া আতূবু ইলাইহি",
            meaningBn = "আমি আল্লাহর কাছে ক্ষমা চাই এবং তাঁর দিকেই প্রত্যাবর্তন করছি",
            defaultTarget = 100,
            rewardBn = "উদ্বেগ-হতাশা দূর করে ও রিযিক বৃদ্ধি করে"
        ),
        DhikrItem(
            id = "dhikr_6",
            arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ ، سُبْحَانَ اللَّهِ الْعَظِيمِ",
            transliterationBn = "সুবহানাল্লাহি ওয়া বিহামদিহী, সুবহানাল্লাহিল আযীম",
            meaningBn = "আল্লাহর প্রশংসাসহ পবিত্রতা ঘোষণা করছি, মহান আল্লাহ পরম পবিত্র",
            defaultTarget = 100,
            rewardBn = "মুখে উচ্চারণ করা সহজ কিন্তু মিজানের পাল্লায় অত্যন্ত ভারী"
        ),
        DhikrItem(
            id = "dhikr_7",
            arabicText = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            transliterationBn = "লা হাওলা ওয়ালা কুওয়াতা ইল্লা বিল্লাহ",
            meaningBn = "আল্লাহর সাহায্য ব্যতীত পাপ থেকে বাঁচার ও নেক কাজ করার কোনো শক্তি নেই",
            defaultTarget = 33,
            rewardBn = "জান্নাতের অন্যতম শ্রেষ্ঠ রত্নভাণ্ডার"
        ),
        DhikrItem(
            id = "dhikr_8",
            arabicText = "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ",
            transliterationBn = "আল্লাহুম্মা সাল্লি আলা মুহাম্মাদিঁও ওয়া আলা আলি মুহাম্মাদ",
            meaningBn = "হে আল্লাহ! মুহাম্মদ (সা.) ও তাঁর বংশধরদের উপর রহমত বর্ষণ করুন",
            defaultTarget = 100,
            rewardBn = "একবার পাঠে ১০টি রহমত, ১০টি গুনাহ মাফ ও ১০টি মর্যাদা বৃদ্ধি"
        ),
        DhikrItem(
            id = "dhikr_9",
            arabicText = "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ",
            transliterationBn = "হাসবুনাল্লাহু ওয়া নি'মাল ওয়াকিল",
            meaningBn = "আল্লাহই আমাদের জন্য যথেষ্ট এবং তিনি উত্তম কর্মবিধায়ক",
            defaultTarget = 33,
            rewardBn = "সকল বিপদ ও দুশ্চিন্তা থেকে মুক্তির অনুপম আশ্রয়"
        ),
        DhikrItem(
            id = "dhikr_10",
            arabicText = "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ",
            transliterationBn = "ইয়া হাইয়্যু ইয়া ক্বাইয়্যূম বিরাহমাতিকা আস্তাগিস",
            meaningBn = "হে চিরঞ্জীব! হে বিশ্ব চরাচরের ধারক! আপনার রহমতের উসিলায় সাহায্য প্রার্থনা করছি",
            defaultTarget = 33,
            rewardBn = "রাসূলুল্লাহ (সা.) কঠিন বিপদে এই যিকির করতেন"
        )
    )

    // -------------------------------------------------------------
    // FATWAS & MAS'ALA DATABASE (ASK THE IMAM)
    // -------------------------------------------------------------
    private val _initialFatwaList = listOf(
        FatwaArticle(
            id = "fatwa_1",
            questionBn = "জামাতে নামাজ পড়ার সময় ইমামের পেছনে সুরা ফাতিহা পড়ার বিধান কী?",
            answerBn = "হানাফী মাযহাব মতে, ইমামের পেছনে মুক্তাদির কোনো কেরাত (সুরা ফাতিহা বা অন্য কোনো সুরা) পড়ার প্রয়োজন নেই। কুরআনুল কারীমে বলা হয়েছে- 'যখন কুরআন তিলাওয়াত করা হয়, তখন তা মনোযোগ দিয়ে শোনো এবং চুপ থাকো।' ইমামের তিলাওয়াতই মুক্তাদির তিলাওয়াত হিসেবে গণ্য হবে। তবে অন্যান্য মাযহাবে নীরবে ফাতিহা পাঠের মত রয়েছে।",
            category = FatwaCategory.SALAT,
            answeredBy = "মুফতি মাওলানা আব্দুল ওয়াদুদ (খতিব ও প্রধান মুফতি)",
            referenceBn = "সহীহ মুসলিম: হাদিস ৩৯৯, বাদায়েউস সানায়ে ১/১১০",
            dateBn = "জানুয়ারি ২০২৫"
        ),
        FatwaArticle(
            id = "fatwa_2",
            questionBn = "ওজু থাকা অবস্থায় রক্ত বের হলে বা ইনজেকশন নিলে কি ওজু নষ্ট হবে?",
            answerBn = "শরীরের কোনো স্থান থেকে রক্ত, পুঁজ বা পানি বের হয়ে যদি গড়িয়ে পড়ার মতো হয়, তবে ওজু ভেঙে যাবে। তবে রক্ত কেবল উপরিভাগে দেখা দিলে এবং গড়িয়ে না পড়লে ওজু ভাঙবে না। সাধারণ মাংশপেশীতে ইনজেকশন নিলে যদি রক্ত বের হয়ে না গড়ায়, তবে ওজু নষ্ট হয় না।",
            category = FatwaCategory.TAHARAT,
            answeredBy = "মাওলানা কারী মিজানুর রহমান (সিনিয়র ইমাম)",
            referenceBn = "ফতোয়ায়ে আলমগীরী ১/১০, রদ্দুল মুহতার ১/১৩৪",
            dateBn = "ফেব্রুয়ারি ২০২৫"
        ),
        FatwaArticle(
            id = "fatwa_3",
            questionBn = "রোজা রেখে ইনহেলার, চোখের ড্রপ বা ইনজেকশন ব্যবহার করা যাবে কি?",
            answerBn = "ইনহেলার ব্যবহার করলে ওষুধের তরল কণা শ্বাসনালীর মাধ্যমে পাকস্থলীতে প্রবেশ করে, তাই রোজা অবস্থায় ইনহেলার ব্যবহার করলে রোজা ভেঙে যাবে এবং পরবর্তীতে কাজা করতে হবে। চোখের ড্রপ দিলে রোজা ভাঙে না। সাধারণ পুষ্টিহীনতা ব্যতীত অ্যান্টিবায়োটিক বা পেনকিলার ইনজেকশনে রোজা নষ্ট হয় না।",
            category = FatwaCategory.SAWM,
            answeredBy = "মুফতি মাওলানা আব্দুল ওয়াদুদ",
            referenceBn = "ইসলামিক ফাউন্ডেশন ফতোয়া বোর্ড, ফাতাওয়ায়ে উসমানী ২/১৭৮",
            dateBn = "মার্চ ২০২৫"
        ),
        FatwaArticle(
            id = "fatwa_4",
            questionBn = "ব্যবসায়িক পণ্যের জাকাত নির্ধারণের সঠিক নিয়ম কী?",
            answerBn = "বছরের শেষে যেদিন জাকাতের বছর পূর্ণ হবে, সেদিন দোকানে বিক্রির উদ্দেশ্যে রক্ষিত সমস্ত পণ্যের পাইকারি বা বর্তমান ক্রয়মূল্য হিসাব করতে হবে। এর সাথে ক্যাশ টাকা ও পাওনা টাকা যোগ করে চলতি দেনা বাদ দিয়ে অবশিষ্ট মূল্যের উপর ২.৫% জাকাত আদায় করতে হবে। দোকানের ডেকোরেশন বা আসবাবপত্রের ওপর জাকাত নেই।",
            category = FatwaCategory.ZAKAT,
            answeredBy = "মুফতি মাওলানা আব্দুল ওয়াদুদ",
            referenceBn = "আল-ফিকহুল ইসলামী ওয়া আদিল্লাতুহু ৩/১৮৮৩",
            dateBn = "এপ্রিল ২০২৫"
        ),
        FatwaArticle(
            id = "fatwa_5",
            questionBn = "মৃত ব্যক্তির পক্ষ থেকে ঈসালে সওয়াব ও দান-সদকার ফজিলত কী?",
            answerBn = "মৃত ব্যক্তির পক্ষ থেকে কুরআন তেলাওয়াত, নফল নামাজ, সাদাকায়ে জারিয়া (মসজিদে দান, নলকূপ স্থাপন, দ্বীনি বই বিতরণ) এবং নেক দোয়া করলে তার সওয়াব মৃত ব্যক্তির আমলনামায় পৌঁছায় এবং আল্লাহ তা'আলা তাদের আজাব মাফ করেন ও মর্যাদা বৃদ্ধি করেন।",
            category = FatwaCategory.MISCELLANEOUS,
            answeredBy = "মাওলানা কারী মিজানুর রহমান",
            referenceBn = "সহীহ মুসলিম: হাদিস ১৬৩১",
            dateBn = "মে ২০২৫"
        ),
        FatwaArticle(
            id = "fatwa_6",
            questionBn = "ডিজিটাল লেনদেন ও মোবাইল ব্যাংকিংয়ের ক্যাশব্যাকের শরয়ী বিধান কী?",
            answerBn = "মোবাইল ব্যাংকিং (বিকাশ, নগদ ইত্যাদি) কোম্পানিগুলো প্রচার ও বিজ্ঞাপনের অংশ হিসেবে যে ডিসকাউন্ট বা ক্যাশব্যাক দেয়, তা শর্তহীন উপহার (হেবা) হিসেবে গ্রহণ করা জায়েজ। তবে সুদের কোনো শর্ত জড়িত থাকলে তা বর্জনীয়।",
            category = FatwaCategory.DAILY_LIFE,
            answeredBy = "মুফতি মাওলানা আব্দুল ওয়াদুদ",
            referenceBn = "বুহুস ফী কাযায়া ফিকহিয়্যাহ মুআসিরাহ",
            dateBn = "জুন ২০২৫"
        )
    )

    private val _fatwaListFlow = MutableStateFlow(_initialFatwaList)
    val fatwaListFlow: StateFlow<List<FatwaArticle>> = _fatwaListFlow.asStateFlow()
    val fatwaList: List<FatwaArticle> get() = _fatwaListFlow.value

    fun addOrUpdateFatwa(fatwa: FatwaArticle) {
        val current = _fatwaListFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == fatwa.id }
        if (index >= 0) {
            current[index] = fatwa
        } else {
            current.add(0, fatwa)
        }
        _fatwaListFlow.value = current
    }

    fun deleteFatwa(id: String) {
        _fatwaListFlow.value = _fatwaListFlow.value.filter { it.id != id }
    }

    // -------------------------------------------------------------
    // JANAZA & EMERGENCY ALERTS
    // -------------------------------------------------------------
    private val _initialJanazaNotices = listOf(
        JanazaNotice(
            id = "janaza_1",
            deceasedNameBn = "মরহুম আলহাজ্ব রফিকুল ইসলাম (৭২)",
            deceasedAge = "৭২ বছর",
            residenceBn = "বাড়ি নং-১২, রোড নং-০৪, ব্লক-সি, সেকশন-৬, মিরপুর",
            demiseTimeBn = "আজ ভোর ৫:৩০ মিনিট",
            janazaTimeBn = "আজ বাদ আসর (বিকাল ৫:১৫ মিনিট)",
            janazaLocationBn = "বায়তুল আমান জামে মসজিদ প্রাঙ্গণ ও সংলগ্ন ময়দান",
            imamNameBn = "মুফতি মাওলানা আব্দুল ওয়াদুদ (খতিব সাহেব)",
            graveyardBn = "মিরপুর শহীদ বুদ্ধিজীবী কবরস্থান",
            contactFamilyPhone = "+8801711223344",
            specialMessageBn = "সকল ধর্মপ্রাণ মুসল্লিদের জানাজায় শরিক হয়ে মরহুমের মাগফিরাতের জন্য দোয়ার অনুরোধ করা হলো।"
        ),
        JanazaNotice(
            id = "janaza_2",
            deceasedNameBn = "মরহুমা ফিরোজা বেগম (৬৫)",
            deceasedAge = "৬৫ বছর",
            residenceBn = "বাড়ি নং-৪৫, রোড নং-০২, সেকশন-৬, মিরপুর",
            demiseTimeBn = "গতকাল রাত ১১:০০ মিনিট",
            janazaTimeBn = "আজ বাদ যোহর (দুপুর ১:৪৫ মিনিট)",
            janazaLocationBn = "বায়তুল আমান জামে মসজিদ ২য় তলা",
            imamNameBn = "মাওলানা কারী মিজানুর রহমান",
            graveyardBn = "কালশী কবরস্থান, মিরপুর",
            contactFamilyPhone = "+8801819887766",
            specialMessageBn = "মরহুমার রুহের মাগফিরাত কামনায় বাদ জানাজা বিশেষ দোয়ার আয়োজন করা হবে।"
        )
    )

    private val _janazaNoticesFlow = MutableStateFlow(_initialJanazaNotices)
    val janazaNoticesFlow: StateFlow<List<JanazaNotice>> = _janazaNoticesFlow.asStateFlow()
    val janazaNotices: List<JanazaNotice> get() = _janazaNoticesFlow.value

    fun addOrUpdateJanaza(janaza: JanazaNotice) {
        val current = _janazaNoticesFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == janaza.id }
        if (index >= 0) {
            current[index] = janaza
        } else {
            current.add(0, janaza)
        }
        _janazaNoticesFlow.value = current
    }

    fun deleteJanaza(janazaId: String) {
        _janazaNoticesFlow.value = _janazaNoticesFlow.value.filter { it.id != janazaId }
    }

    private val _initialEmergencyAlerts = listOf(
        EmergencyAlert(
            id = "em_1",
            titleBn = "জরুরি 'ও নেগেটিভ' (O-) রক্তের প্রয়োজন",
            categoryBn = "রক্তদান আহ্বান",
            descriptionBn = "আমাদের মহল্লার এক অসহায় বোন মিরপুর ন্যাশনাল হার্ট ফাউন্ডেশনে চিকিৎসাধীন আছেন। জরুরি ভিত্তিতে ২ ব্যাগ 'O-' রক্ত প্রয়োজন।",
            urgencyLevel = "HIGH",
            contactPerson = "মো. তারিকুল ইসলাম (মসজিদ স্বেচ্ছাসেবক)",
            contactPhone = "+8801712345678",
            dateBn = "আজকের আবেদন",
            isResolved = false
        ),
        EmergencyAlert(
            id = "em_2",
            titleBn = "অসুস্থ বয়োবৃদ্ধ ব্যক্তির সন্ধান (হারানো বিজ্ঞপ্তি)",
            categoryBn = "হারানো বিজ্ঞপ্তি",
            descriptionBn = "আজ সকালে মসজিদ সংলগ্ন এলাকা থেকে ৭০ বছর বয়সী এক বৃদ্ধ দাদুকে পাওয়া গেছে। তিনি নাম-ঠিকানা ঠিকমতো বলতে পারছেন না। বর্তমানে মসজিদ অফিসে আছেন।",
            urgencyLevel = "HIGH",
            contactPerson = "মসজিদ অফিস কেয়ারটেকার",
            contactPhone = "+8801911998877",
            dateBn = "আজকের বিজ্ঞপ্তি",
            isResolved = false
        )
    )

    private val _emergencyAlertsFlow = MutableStateFlow(_initialEmergencyAlerts)
    val emergencyAlertsFlow: StateFlow<List<EmergencyAlert>> = _emergencyAlertsFlow.asStateFlow()
    val emergencyAlerts: List<EmergencyAlert> get() = _emergencyAlertsFlow.value

    fun addOrUpdateEmergencyAlert(alert: EmergencyAlert) {
        val current = _emergencyAlertsFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == alert.id }
        if (index >= 0) {
            current[index] = alert
        } else {
            current.add(0, alert)
        }
        _emergencyAlertsFlow.value = current
    }

    fun deleteEmergencyAlert(alertId: String) {
        _emergencyAlertsFlow.value = _emergencyAlertsFlow.value.filter { it.id != alertId }
    }

    fun toggleEmergencyAlertResolved(alertId: String) {
        _emergencyAlertsFlow.value = _emergencyAlertsFlow.value.map {
            if (it.id == alertId) it.copy(isResolved = !it.isResolved) else it
        }
    }

    // -------------------------------------------------------------
    // RAMADAN DUAS & CALENDAR GENERATOR
    // -------------------------------------------------------------
    val ramadanDuas = listOf(
        RamadanDua(
            titleBn = "রোজার নিয়ত (সেহরির পর)",
            arabicText = "نَوَيْتُ أَنْ أَصُومَ غَدًا مِنْ شَهْرِ رَمَضَانَ الْمُبَارَكِ فَرْضًا لَكَ يَا اللَّهُ فَتَقَبَّلْ مِنِّي إِنَّكَ أَنْتَ السَّمِيعُ الْعَلِيمُ",
            transliterationBn = "নাওয়াইতু আন আসূমা গাদাম মিন শাহরি রামাদানাল মুবারাকি ফারদাল লাকা ইয়া আল্লাহু ফাতাকাব্বাল মিন্নী, ইন্নাকা আনতাস সামীউল আলীম।",
            meaningBn = "হে আল্লাহ! আমি আগামীকাল পবিত্র রমজান মাসের তোমার নির্ধারিত ফরজ রোজা রাখার নিয়ত করলাম। অতএব তুমি আমার রোজা কবুল করো, নিশ্চয়ই তুমি সর্বশ্রোতা ও সর্বজ্ঞানী।",
            occasionBn = "সেহরি খাওয়ার পর বা সুবহে সাদিকের পূর্বে"
        ),
        RamadanDua(
            titleBn = "ইফতারের দোয়া",
            arabicText = "اللَّهُمَّ إِنِّي لَكَ صُمْتُ وَعَلَىٰ رِزْقِكَ أَفْطَرْتُ",
            transliterationBn = "আল্লাহুম্মা ইন্নি লাকা সুমতু ওয়া আলা রিযক্বিকা আফতারতু।",
            meaningBn = "হে আল্লাহ! আমি আপনারই জন্য রোজা রেখেছি এবং আপনারই দেওয়া রিযিক দ্বারা ইফতার করছি।",
            occasionBn = "ইফতার মুখে দেওয়ার সময়"
        ),
        RamadanDua(
            titleBn = "ইফতারের পর পঠিতব্য দোয়া",
            arabicText = "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الأَجْرُ إِنْ شَاءَ اللَّهُ",
            transliterationBn = "যাহাবায যমায়ু ওয়াবতাল্লাতিল উরূকু ওয়া সাবাতাল আজরু ইনশাআল্লাহ।",
            meaningBn = "পিপাসা নিবারিত হলো, শিরা-উপশিরা সিক্ত হলো এবং ইনশাআল্লাহ পুরস্কার নির্ধারিত হলো।",
            occasionBn = "ইফতার সম্পন্ন করার পর"
        ),
        RamadanDua(
            titleBn = "লাইলাতুল কদরের দোয়া",
            arabicText = "اللَّهُمَّ إِنَّكَ عَفُوٌّ تُحِبُّ الْعَفْوَ فَاعْفُ عَنِّي",
            transliterationBn = "আল্লাহুম্মা ইন্নাকা আফুওয়ুন তুহিব্বুল আফওয়া ফা'ফু আন্নী।",
            meaningBn = "হে আল্লাহ! নিশ্চয়ই আপনি ক্ষমাশীল, ক্ষমা করা পছন্দ করেন। অতএব আমাকে ক্ষমা করে দিন।",
            occasionBn = "রমজানের শেষ দশকের বিজোড় রাতসমূহে"
        ),
        RamadanDua(
            titleBn = "তারাবীহ নামাজের ৪ রাকাত পর দোয়া",
            arabicText = "سُبْحَانَ ذِي الْمُلْكِ وَالْمَلَكُوتِ، سُبْحَانَ ذِي الْعِزَّةِ وَالْعَظَمَةِ وَالْهَيْبَةِ وَالْقُدْرَةِ وَالْكِبْرِيَاءِ وَالْجَبَرُوتِ",
            transliterationBn = "সুবহানা যিল মুলকি ওয়াল মালাকূত, সুবহানা যিল ইয্যাতি ওয়াল আযামাতি ওয়াল হায়বাতি ওয়াল কুদরাতি ওয়াল কিবরিয়ায়ি ওয়াল জাবারূত...",
            meaningBn = "পবিত্র সেই সত্তা যিনি সমগ্র রাজত্ব ও সাম্রাজ্যের মালিক। পবিত্র সেই সত্তা যিনি মহিমান্বিত, পরাক্রমশালী, ভীতিপ্রদ ও চির মর্যাদাবান...",
            occasionBn = "তারাবীহ নামাজের প্রতি চার রাকাত পর বিশ্রামের সময়"
        )
    )

    fun generateRamadanSchedule(selectedDistrictId: String = "dhaka"): List<RamadanDay> {
        val dist = districts.find { it.id == selectedDistrictId } ?: districts.first()
        val list = mutableListOf<RamadanDay>()

        // 30 Days of Ramadan simulation with district offset
        val dayNames = arrayOf("শনি", "রবি", "সোম", "মঙ্গল", "বুধ", "বৃহস্পতি", "শুক্র")
        val baseSehriMins = 275 // 04:35 AM
        val baseIftarMins = 1102 // 06:22 PM

        for (i in 1..30) {
            val dOffset = (i - 1)
            val sMin = baseSehriMins - (dOffset / 3) + dist.fajrOffsetMinutes
            val fMin = sMin + 5
            val iftarMin = baseIftarMins + (dOffset / 3) + dist.maghribOffsetMinutes

            fun fmt(mins: Int): String {
                val normalized = (mins + 1440) % 1440
                var h = normalized / 60
                val m = normalized % 60
                val ampm = if (h >= 12) "PM" else "AM"
                if (h > 12) h -= 12
                if (h == 0) h = 12
                return String.format(Locale.US, "%02d:%02d %s", h, m, ampm)
            }

            val dayName = dayNames[(i + 4) % 7]
            list.add(
                RamadanDay(
                    ramadanDayNumber = i,
                    dateBengali = "${i + 15} ফাল্গুন",
                    dateEnglish = "${i} মার্চ",
                    dayName = dayName,
                    sehriEndTime = fmt(sMin),
                    fajrAzanTime = fmt(fMin),
                    iftarTime = fmt(iftarMin),
                    isToday = (i == 1),
                    isFastCompleted = false
                )
            )
        }
        return list
    }

    // -------------------------------------------------------------
    // QURAN AUDIO RECITERS & CDN ENDPOINTS
    // -------------------------------------------------------------
    data class QariProfile(
        val id: String,
        val nameBn: String,
        val nameAr: String,
        val identifier: String,
        val serverUrlPattern: String // e.g. https://server8.mp3quran.net/afs/%03d.mp3
    )

    val qariList = listOf(
        QariProfile(
            id = "mishary",
            nameBn = "শায়খ মিশারী রাশিদ আল-আফাসী",
            nameAr = "مشاري راشد العفاسي",
            identifier = "ar.alafasy",
            serverUrlPattern = "https://server8.mp3quran.net/afs/%03d.mp3"
        ),
        QariProfile(
            id = "sudais",
            nameBn = "শায়খ আব্দুর রহমান আস-সুদাইস",
            nameAr = "عبد الرحمن السديس",
            identifier = "ar.abdurrahmaansudais",
            serverUrlPattern = "https://server11.mp3quran.net/sds/%03d.mp3"
        ),
        QariProfile(
            id = "muaiqly",
            nameBn = "শায়খ মাহের আল-মুয়াইক্বলী",
            nameAr = "ماهر المعيقلي",
            identifier = "ar.mahermuaiqly",
            serverUrlPattern = "https://server12.mp3quran.net/maher/%03d.mp3"
        )
    )
}

