package com.robiul.mosquetime.feature.admin.donations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robiul.mosquetime.data.firebase.AuthRepository
import com.robiul.mosquetime.data.model.AuditActionCategory
import com.robiul.mosquetime.data.model.BankAccountInfo
import com.robiul.mosquetime.data.model.DonationRecord
import com.robiul.mosquetime.data.model.MobileAccountInfo
import com.robiul.mosquetime.data.repository.MosqueRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AdminDonationsUiState(
    val selectedTab: Int = 0, // 0 = Records/Tracking, 1 = Mobile Banking, 2 = Bank Accounts
    val donationRecords: List<DonationRecord> = emptyList(),
    val filteredRecords: List<DonationRecord> = emptyList(),
    val mobileAccounts: List<MobileAccountInfo> = emptyList(),
    val bankAccounts: List<BankAccountInfo> = emptyList(),
    val searchQuery: String = "",
    val filterStatus: String = "ALL", // ALL, VERIFIED, PENDING
    val isAddRecordOpen: Boolean = false,
    val isAddMobileOpen: Boolean = false,
    val editingMobile: MobileAccountInfo? = null,
    val isAddBankOpen: Boolean = false,
    val editingBank: BankAccountInfo? = null,
    val isSubmitting: Boolean = false,
    val userMessage: String? = null
)

class AdminDonationsViewModel : ViewModel() {

    private val _selectedTab = MutableStateFlow(0)
    private val _searchQuery = MutableStateFlow("")
    private val _filterStatus = MutableStateFlow("ALL")
    private val _isAddRecordOpen = MutableStateFlow(false)
    private val _isAddMobileOpen = MutableStateFlow(false)
    private val _editingMobile = MutableStateFlow<MobileAccountInfo?>(null)
    private val _isAddBankOpen = MutableStateFlow(false)
    private val _editingBank = MutableStateFlow<BankAccountInfo?>(null)
    private val _isSubmitting = MutableStateFlow(false)
    private val _userMessage = MutableStateFlow<String?>(null)

    private val dataFlow = combine(
        _selectedTab,
        MosqueRepository.donationRecordsFlow,
        MosqueRepository.mobileAccountsFlow,
        MosqueRepository.bankAccountsFlow
    ) { tab, records, mobiles, banks ->
        DataSnapshot(tab, records, mobiles, banks)
    }

    private val filterFlow = combine(
        _searchQuery,
        _filterStatus
    ) { query, status ->
        Pair(query, status)
    }

    private val dialogFlow = combine(
        _isAddRecordOpen,
        _isAddMobileOpen,
        _editingMobile,
        _isAddBankOpen,
        _editingBank
    ) { isRecord, isMobile, editingMobile, isBank, editingBank ->
        DialogSnapshot1(isRecord, isMobile, editingMobile, isBank, editingBank)
    }

    private val stateMetaFlow = combine(
        _isSubmitting,
        _userMessage
    ) { isSubmitting, message ->
        Pair(isSubmitting, message)
    }

    val uiState: StateFlow<AdminDonationsUiState> = combine(
        dataFlow,
        filterFlow,
        dialogFlow,
        stateMetaFlow
    ) { data, (query, status), dialog, (isSubmitting, userMessage) ->
        val filtered = data.records.filter { record ->
            val matchesStatus = when (status) {
                "VERIFIED" -> record.status.contains("গৃহীত") || record.status.contains("যাচাইকৃত")
                "PENDING" -> !record.status.contains("গৃহীত") && !record.status.contains("যাচাইকৃত")
                else -> true
            }
            val matchesQuery = query.isBlank() ||
                    record.donorName.contains(query, ignoreCase = true) ||
                    record.donorPhone.contains(query, ignoreCase = true) ||
                    record.transactionId.contains(query, ignoreCase = true) ||
                    record.fundTitle.contains(query, ignoreCase = true) ||
                    record.paymentMethod.contains(query, ignoreCase = true)
            matchesStatus && matchesQuery
        }

        AdminDonationsUiState(
            selectedTab = data.tab,
            donationRecords = data.records,
            filteredRecords = filtered,
            mobileAccounts = data.mobiles,
            bankAccounts = data.banks,
            searchQuery = query,
            filterStatus = status,
            isAddRecordOpen = dialog.isRecord,
            isAddMobileOpen = dialog.isMobile,
            editingMobile = dialog.editingMobile,
            isAddBankOpen = dialog.isBank,
            editingBank = dialog.editingBank,
            isSubmitting = isSubmitting,
            userMessage = userMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AdminDonationsUiState()
    )

    fun onTabSelected(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onFilterStatusChanged(status: String) {
        _filterStatus.value = status
    }

    fun openAddRecordDialog() {
        _isAddRecordOpen.value = true
    }

    fun closeRecordDialog() {
        _isAddRecordOpen.value = false
    }

    fun openAddMobileDialog() {
        _editingMobile.value = null
        _isAddMobileOpen.value = true
    }

    fun openEditMobileDialog(account: MobileAccountInfo) {
        _editingMobile.value = account
        _isAddMobileOpen.value = true
    }

    fun closeMobileDialog() {
        _isAddMobileOpen.value = false
        _editingMobile.value = null
    }

    fun openAddBankDialog() {
        _editingBank.value = null
        _isAddBankOpen.value = true
    }

    fun openEditBankDialog(account: BankAccountInfo) {
        _editingBank.value = account
        _isAddBankOpen.value = true
    }

    fun closeBankDialog() {
        _isAddBankOpen.value = false
        _editingBank.value = null
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    fun updateDonationStatus(recordId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                val admin = AuthRepository.getInstance().currentUser.value
                val adminName = admin?.nameBn ?: "ক্যাশিয়ার"
                val adminRole = admin?.role?.displayNameBn ?: "অ্যাডমিন"

                MosqueRepository.updateDonationStatus(recordId, newStatus)

                MosqueRepository.logAdminAction(
                    adminNameBn = adminName,
                    adminRoleBn = adminRole,
                    category = AuditActionCategory.DONATIONS,
                    actionTitleBn = "অনুদান স্ট্যাটাস আপডেট",
                    detailsBn = "রেকর্ড ID: $recordId, নতুন স্ট্যাটাস: $newStatus"
                )

                _userMessage.value = "অনুদানের স্ট্যাটাস আপডেট হয়েছে"
            } catch (e: Exception) {
                _userMessage.value = "আপডেট করতে ব্যর্থ: ${e.localizedMessage}"
            }
        }
    }

    fun saveDonationRecord(
        fundTitle: String,
        amount: Long,
        paymentMethod: String,
        transactionId: String,
        donorName: String,
        donorPhone: String,
        status: String
    ) {
        if (fundTitle.isBlank() || amount <= 0L) {
            _userMessage.value = "তহবিল এবং সঠিক অনুদানের পরিমাণ লিখুন"
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val admin = AuthRepository.getInstance().currentUser.value
                val adminName = admin?.nameBn ?: "ক্যাশিয়ার"
                val adminRole = admin?.role?.displayNameBn ?: "হিসাব রক্ষক"

                val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale("bn", "BD")).format(Date())

                val newRecord = DonationRecord(
                    id = "dn_${System.currentTimeMillis()}",
                    fundTitle = fundTitle.trim(),
                    amount = amount,
                    paymentMethod = paymentMethod.ifBlank { "ক্যাশ / নগদ গ্রহণ" },
                    transactionId = transactionId.ifBlank { "MANUAL-${System.currentTimeMillis().toString().takeLast(6)}" },
                    donorName = donorName.ifBlank { "নাম প্রকাশে অনিচ্ছুক" },
                    donorPhone = donorPhone.ifBlank { "০১৭XXXXXXXX" },
                    dateString = dateStr,
                    status = status.ifBlank { "যাচাইকৃত ও গৃহীত" }
                )

                MosqueRepository.addDonationRecord(newRecord)

                MosqueRepository.logAdminAction(
                    adminNameBn = adminName,
                    adminRoleBn = adminRole,
                    category = AuditActionCategory.DONATIONS,
                    actionTitleBn = "নতুন অনুদান সংযোজন",
                    detailsBn = "দাতা: ${newRecord.donorName}, পরিমাণ: ৳$amount ($fundTitle)"
                )

                _userMessage.value = "অনুদান রেকর্ড সফলভাবে যুক্ত হয়েছে"
                _isAddRecordOpen.value = false
            } catch (e: Exception) {
                _userMessage.value = "সংরক্ষণ করতে ব্যর্থ: ${e.localizedMessage}"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun deleteDonationRecord(record: DonationRecord) {
        viewModelScope.launch {
            try {
                val admin = AuthRepository.getInstance().currentUser.value
                val adminName = admin?.nameBn ?: "অ্যাডমিন"
                val adminRole = admin?.role?.displayNameBn ?: "হিসাব রক্ষক"

                MosqueRepository.deleteDonationRecord(record.id)

                MosqueRepository.logAdminAction(
                    adminNameBn = adminName,
                    adminRoleBn = adminRole,
                    category = AuditActionCategory.DONATIONS,
                    actionTitleBn = "অনুদান রেকর্ড মুছে ফেলা",
                    detailsBn = "দাতা: ${record.donorName}, পরিমাণ: ৳${record.amount}"
                )

                _userMessage.value = "রেকর্ড সফলভাবে মুছে ফেলা হয়েছে"
            } catch (e: Exception) {
                _userMessage.value = "মুছে ফেলতে ব্যর্থ: ${e.localizedMessage}"
            }
        }
    }

    fun saveMobileAccount(provider: String, number: String, type: String) {
        if (provider.isBlank() || number.isBlank()) {
            _userMessage.value = "প্রোভাইডার এবং নম্বর আবশ্যক"
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val admin = AuthRepository.getInstance().currentUser.value
                val adminName = admin?.nameBn ?: "অ্যাডমিন"
                val adminRole = admin?.role?.displayNameBn ?: "কোষাধ্যক্ষ"

                val isEditing = _editingMobile.value != null

                MosqueRepository.addOrUpdateMobileAccount(
                    MobileAccountInfo(
                        provider = provider.trim(),
                        number = number.trim(),
                        type = type.ifBlank { "মার্চেন্ট পে" }
                    )
                )

                MosqueRepository.logAdminAction(
                    adminNameBn = adminName,
                    adminRoleBn = adminRole,
                    category = AuditActionCategory.DONATIONS,
                    actionTitleBn = if (isEditing) "মোবাইল ব্যাংকিং অ্যাকাউন্ট আপডেট" else "নতুন মোবাইল ব্যাংকিং অ্যাকাউন্ট",
                    detailsBn = "$provider ($number)"
                )

                _userMessage.value = if (isEditing) "অ্যাকাউন্ট সফলভাবে আপডেট হয়েছে" else "নতুন মোবাইল অ্যাকাউন্ট যুক্ত হয়েছে"
                _isAddMobileOpen.value = false
                _editingMobile.value = null
            } catch (e: Exception) {
                _userMessage.value = "সংরক্ষণ করতে ব্যর্থ: ${e.localizedMessage}"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun deleteMobileAccount(account: MobileAccountInfo) {
        viewModelScope.launch {
            try {
                val admin = AuthRepository.getInstance().currentUser.value
                val adminName = admin?.nameBn ?: "অ্যাডমিন"
                val adminRole = admin?.role?.displayNameBn ?: "কোষাধ্যক্ষ"

                MosqueRepository.deleteMobileAccount(account.number)

                MosqueRepository.logAdminAction(
                    adminNameBn = adminName,
                    adminRoleBn = adminRole,
                    category = AuditActionCategory.DONATIONS,
                    actionTitleBn = "মোবাইল অ্যাকাউন্ট মুছে ফেলা",
                    detailsBn = "${account.provider} (${account.number})"
                )

                _userMessage.value = "অ্যাকাউন্ট মুছে ফেলা হয়েছে"
            } catch (e: Exception) {
                _userMessage.value = "মুছে ফেলতে ব্যর্থ: ${e.localizedMessage}"
            }
        }
    }

    fun saveBankAccount(
        bankName: String,
        accountName: String,
        accountNumber: String,
        branchName: String,
        routingNumber: String
    ) {
        if (bankName.isBlank() || accountNumber.isBlank()) {
            _userMessage.value = "ব্যাংকের নাম এবং অ্যাকাউন্ট নম্বর আবশ্যক"
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val admin = AuthRepository.getInstance().currentUser.value
                val adminName = admin?.nameBn ?: "অ্যাডমিন"
                val adminRole = admin?.role?.displayNameBn ?: "কোষাধ্যক্ষ"

                val isEditing = _editingBank.value != null

                MosqueRepository.addOrUpdateBankAccount(
                    BankAccountInfo(
                        bankName = bankName.trim(),
                        accountName = accountName.ifBlank { "BAITUL AMAN JAME MASJID" },
                        accountNumber = accountNumber.trim(),
                        branchName = branchName.ifBlank { "ঢাকা" },
                        routingNumber = routingNumber.trim()
                    )
                )

                MosqueRepository.logAdminAction(
                    adminNameBn = adminName,
                    adminRoleBn = adminRole,
                    category = AuditActionCategory.DONATIONS,
                    actionTitleBn = if (isEditing) "ব্যাংক অ্যাকাউন্ট আপডেট" else "নতুন ব্যাংক অ্যাকাউন্ট সংযোজন",
                    detailsBn = "$bankName ($accountNumber)"
                )

                _userMessage.value = if (isEditing) "ব্যাংক হিসাব আপডেট হয়েছে" else "নতুন ব্যাংক হিসাব যুক্ত হয়েছে"
                _isAddBankOpen.value = false
                _editingBank.value = null
            } catch (e: Exception) {
                _userMessage.value = "সংরক্ষণ করতে ব্যর্থ: ${e.localizedMessage}"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun deleteBankAccount(account: BankAccountInfo) {
        viewModelScope.launch {
            try {
                val admin = AuthRepository.getInstance().currentUser.value
                val adminName = admin?.nameBn ?: "অ্যাডমিন"
                val adminRole = admin?.role?.displayNameBn ?: "কোষাধ্যক্ষ"

                MosqueRepository.deleteBankAccount(account.accountNumber)

                MosqueRepository.logAdminAction(
                    adminNameBn = adminName,
                    adminRoleBn = adminRole,
                    category = AuditActionCategory.DONATIONS,
                    actionTitleBn = "ব্যাংক অ্যাকাউন্ট মুছে ফেলা",
                    detailsBn = "${account.bankName} (${account.accountNumber})"
                )

                _userMessage.value = "ব্যাংক হিসাব মুছে ফেলা হয়েছে"
            } catch (e: Exception) {
                _userMessage.value = "মুছে ফেলতে ব্যর্থ: ${e.localizedMessage}"
            }
        }
    }

    private data class DataSnapshot(
        val tab: Int,
        val records: List<DonationRecord>,
        val mobiles: List<MobileAccountInfo>,
        val banks: List<BankAccountInfo>
    )

    private data class DialogSnapshot1(
        val isRecord: Boolean,
        val isMobile: Boolean,
        val editingMobile: MobileAccountInfo?,
        val isBank: Boolean,
        val editingBank: BankAccountInfo?
    )
}
