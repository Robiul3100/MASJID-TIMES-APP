package com.robiul.mosquetime.feature.admin.fatwa

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.robiul.mosquetime.data.firebase.AuthRepository
import com.robiul.mosquetime.data.local.AppDatabase
import com.robiul.mosquetime.data.local.entity.UserQuestionEntity
import com.robiul.mosquetime.data.model.AuditActionCategory
import com.robiul.mosquetime.data.model.FatwaArticle
import com.robiul.mosquetime.data.model.FatwaCategory
import com.robiul.mosquetime.data.model.UserQuestionSubmission
import com.robiul.mosquetime.data.repository.MosqueRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AdminFatwaUiState(
    val selectedTab: Int = 0, // 0 = User Questions, 1 = Fatwa Library
    val userQuestions: List<UserQuestionSubmission> = emptyList(),
    val filteredQuestions: List<UserQuestionSubmission> = emptyList(),
    val fatwaList: List<FatwaArticle> = emptyList(),
    val filteredFatwas: List<FatwaArticle> = emptyList(),
    val selectedCategory: FatwaCategory = FatwaCategory.ALL,
    val questionFilterStatus: String = "ALL", // ALL, PENDING, ANSWERED
    val searchQuery: String = "",
    val replyingQuestion: UserQuestionSubmission? = null,
    val isAddEditFatwaOpen: Boolean = false,
    val editingFatwa: FatwaArticle? = null,
    val isSubmitting: Boolean = false,
    val userMessage: String? = null
)

class AdminFatwaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val _selectedTab = MutableStateFlow(0)
    private val _selectedCategory = MutableStateFlow(FatwaCategory.ALL)
    private val _questionFilterStatus = MutableStateFlow("ALL")
    private val _searchQuery = MutableStateFlow("")
    private val _replyingQuestion = MutableStateFlow<UserQuestionSubmission?>(null)
    private val _isAddEditFatwaOpen = MutableStateFlow(false)
    private val _editingFatwa = MutableStateFlow<FatwaArticle?>(null)
    private val _isSubmitting = MutableStateFlow(false)
    private val _userMessage = MutableStateFlow<String?>(null)

    private val dataFlow = combine(
        _selectedTab,
        db.userQuestionDao().getAllUserQuestionsFlow(),
        MosqueRepository.fatwaListFlow
    ) { tab: Int, entities: List<UserQuestionEntity>, fatwas: List<FatwaArticle> ->
        Triple(tab, entities, fatwas)
    }

    private val filterFlow = combine(
        _selectedCategory,
        _questionFilterStatus,
        _searchQuery
    ) { category: FatwaCategory, status: String, query: String ->
        Triple(category, status, query)
    }

    private val dialogFlow = combine(
        _replyingQuestion,
        _isAddEditFatwaOpen,
        _editingFatwa,
        _isSubmitting,
        _userMessage
    ) { replying, isAddEdit, editing, submitting, message ->
        DialogState(replying, isAddEdit, editing, submitting, message)
    }

    val uiState: StateFlow<AdminFatwaUiState> = combine(
        dataFlow,
        filterFlow,
        dialogFlow
    ) { (tab, entities, fatwas), (category, qStatus, query), dialog ->
        val domainQuestions = entities.map { it.toDomainModel() }

        val filteredQ = domainQuestions.filter { q ->
            val matchesStatus = when (qStatus) {
                "PENDING" -> q.replyText.isBlank()
                "ANSWERED" -> q.replyText.isNotBlank()
                else -> true
            }
            val matchesQuery = query.isBlank() ||
                    q.questionText.contains(query, ignoreCase = true) ||
                    q.senderName.contains(query, ignoreCase = true) ||
                    q.replyText.contains(query, ignoreCase = true)
            matchesStatus && matchesQuery
        }

        val filteredF = fatwas.filter { f ->
            val matchesCat = (category == FatwaCategory.ALL || f.category == category)
            val matchesQuery = query.isBlank() ||
                    f.questionBn.contains(query, ignoreCase = true) ||
                    f.answerBn.contains(query, ignoreCase = true) ||
                    f.referenceBn.contains(query, ignoreCase = true) ||
                    f.answeredBy.contains(query, ignoreCase = true)
            matchesCat && matchesQuery
        }

        AdminFatwaUiState(
            selectedTab = tab,
            userQuestions = domainQuestions,
            filteredQuestions = filteredQ,
            fatwaList = fatwas,
            filteredFatwas = filteredF,
            selectedCategory = category,
            questionFilterStatus = qStatus,
            searchQuery = query,
            replyingQuestion = dialog.replying,
            isAddEditFatwaOpen = dialog.isAddEdit,
            editingFatwa = dialog.editing,
            isSubmitting = dialog.isSubmitting,
            userMessage = dialog.userMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AdminFatwaUiState()
    )

    fun onTabSelected(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun onCategorySelected(category: FatwaCategory) {
        _selectedCategory.value = category
    }

    fun onQuestionFilterStatus(status: String) {
        _questionFilterStatus.value = status
    }

    fun onQuestionFilterStatusChanged(status: String) {
        _questionFilterStatus.value = status
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun openReplyDialog(question: UserQuestionSubmission) {
        _replyingQuestion.value = question
    }

    fun closeReplyDialog() {
        _replyingQuestion.value = null
    }

    fun openAddFatwaDialog() {
        _editingFatwa.value = null
        _isAddEditFatwaOpen.value = true
    }

    fun openEditFatwaDialog(fatwa: FatwaArticle) {
        _editingFatwa.value = fatwa
        _isAddEditFatwaOpen.value = true
    }

    fun closeAddEditFatwaDialog() {
        _isAddEditFatwaOpen.value = false
        _editingFatwa.value = null
    }

    fun closeFatwaDialog() {
        _isAddEditFatwaOpen.value = false
        _editingFatwa.value = null
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    fun submitReplyToQuestion(
        question: UserQuestionSubmission,
        replyText: String,
        answeredBy: String,
        publishToBank: Boolean,
        reference: String
    ) {
        if (replyText.isBlank()) {
            _userMessage.value = "উত্তর প্রদান করতে অনুগ্রহ করে মন্তব্য লিখুন"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isSubmitting.value = true
            try {
                val admin = AuthRepository.getInstance().currentUser.value
                val finalAnswerer = answeredBy.ifBlank {
                    admin?.nameBn ?: "মুফতি ও প্রধান খতিব, বায়তুল আমান জামে মসজিদ"
                }
                val repliedAt = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale("bn", "BD")).format(Date())

                db.userQuestionDao().updateQuestionReply(
                    id = question.id,
                    replyText = replyText.trim(),
                    replyDateBn = repliedAt,
                    repliedBy = finalAnswerer
                )

                if (publishToBank) {
                    val fatwa = FatwaArticle(
                        id = "fatwa_q_${question.id}_${System.currentTimeMillis()}",
                        questionBn = question.questionText.trim(),
                        answerBn = replyText.trim(),
                        category = question.category,
                        answeredBy = finalAnswerer,
                        referenceBn = reference.ifBlank { "কুরআন ও সুন্নাহ ভিত্তিক শরয়ী সমাধান" }.trim(),
                        dateBn = "২০২৫"
                    )
                    MosqueRepository.addOrUpdateFatwa(fatwa)
                }

                val adminName = admin?.nameBn ?: "ইমাম সাহেব"
                val adminRole = admin?.role?.displayNameBn ?: "মুফতি/খতিব"

                MosqueRepository.logAdminAction(
                    adminNameBn = adminName,
                    adminRoleBn = adminRole,
                    category = AuditActionCategory.FATWAS,
                    actionTitleBn = "মুসল্লির প্রশ্নের উত্তর প্রদান",
                    detailsBn = "প্রশ্নোত্তর সম্পন্ন হয়েছে। উত্তরদাতা: $finalAnswerer" + if (publishToBank) " (ফতোয়া ব্যাংকে প্রকাশিত)" else ""
                )

                _userMessage.value = "উত্তর সফলভাবে সংরক্ষিত ও প্রেরিত হয়েছে"
                _replyingQuestion.value = null
            } catch (e: Exception) {
                _userMessage.value = "উত্তর সংরক্ষণ করতে ব্যর্থ: ${e.localizedMessage}"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun submitReply(questionId: String, replyText: String, answeredBy: String) {
        if (replyText.isBlank()) {
            _userMessage.value = "উত্তর প্রদান করতে অনুগ্রহ করে মন্তব্য লিখুন"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isSubmitting.value = true
            try {
                val admin = AuthRepository.getInstance().currentUser.value
                val finalAnswerer = answeredBy.ifBlank {
                    admin?.nameBn ?: "প্রধান খতিব ও মুফতি, বায়তুল আমান জামে মসজিদ"
                }
                val repliedAt = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale("bn", "BD")).format(Date())

                db.userQuestionDao().updateQuestionReply(
                    id = questionId,
                    replyText = replyText.trim(),
                    replyDateBn = repliedAt,
                    repliedBy = finalAnswerer
                )

                val adminName = admin?.nameBn ?: "ইমাম সাহেব"
                val adminRole = admin?.role?.displayNameBn ?: "মুফতি/খতিব"

                MosqueRepository.logAdminAction(
                    adminNameBn = adminName,
                    adminRoleBn = adminRole,
                    category = AuditActionCategory.FATWAS,
                    actionTitleBn = "মুসল্লির প্রশ্নের উত্তর প্রদান",
                    detailsBn = "প্রশ্নোত্তর সম্পন্ন হয়েছে। উত্তরদাতা: $finalAnswerer"
                )

                _userMessage.value = "উত্তর সফলভাবে সংরক্ষিত ও প্রকাশিত হয়েছে"
                _replyingQuestion.value = null
            } catch (e: Exception) {
                _userMessage.value = "উত্তর সংরক্ষণ করতে ব্যর্থ: ${e.localizedMessage}"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun deleteUserQuestion(question: UserQuestionSubmission) {
        deleteUserQuestion(question.id)
    }

    fun deleteUserQuestion(questionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.userQuestionDao().deleteUserQuestion(questionId)
                _userMessage.value = "প্রশ্নটি মুছে ফেলা হয়েছে"
            } catch (e: Exception) {
                _userMessage.value = "মুছে ফেলতে ব্যর্থ: ${e.localizedMessage}"
            }
        }
    }

    fun saveFatwa(
        id: String?,
        questionBn: String,
        answerBn: String,
        category: FatwaCategory,
        answeredBy: String = "মুফতি মাওলানা আব্দুল ওয়াদুদ (খতিব)",
        referenceBn: String = "সহীহ বুখারী, ফতোয়ায়ে শামী",
        dateBn: String = "২০২৫"
    ) {
        if (questionBn.isBlank() || answerBn.isBlank()) {
            _userMessage.value = "প্রশ্ন এবং উত্তরের বিবরণ উভয়ই পূরণ করুন"
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val admin = AuthRepository.getInstance().currentUser.value
                val finalAnswerer = answeredBy.ifBlank {
                    admin?.nameBn ?: "মুফতি ও খতিব, বায়তুল আমান জামে মসজিদ"
                }
                val fatwaId = if (id.isNullOrBlank()) "fatwa_${System.currentTimeMillis()}" else id

                val fatwa = FatwaArticle(
                    id = fatwaId,
                    questionBn = questionBn.trim(),
                    answerBn = answerBn.trim(),
                    category = category,
                    referenceBn = referenceBn.ifBlank { "আল-কুরআন ও সহীহ হাদিস এবং ফাতওয়ায়ে আলমগীরী" }.trim(),
                    answeredBy = finalAnswerer,
                    dateBn = dateBn.ifBlank { "২০২৫" }
                )

                val isNew = id.isNullOrBlank()
                MosqueRepository.addOrUpdateFatwa(fatwa)

                val adminName = admin?.nameBn ?: "ইমাম সাহেব"
                val adminRole = admin?.role?.displayNameBn ?: "মুফতি/খতিব"

                MosqueRepository.logAdminAction(
                    adminNameBn = adminName,
                    adminRoleBn = adminRole,
                    category = AuditActionCategory.FATWAS,
                    actionTitleBn = if (isNew) "নতুন ফতোয়া সংযোজন" else "ফতোয়া সম্পাদন ও আপডেট",
                    detailsBn = "বিষয়: ${fatwa.questionBn.take(40)}... (${fatwa.category.titleBn})"
                )

                _userMessage.value = if (isNew) "ফতোয়া সফলভাবে সংরক্ষিত হয়েছে" else "ফতোয়া আপডেট হয়েছে"
                _isAddEditFatwaOpen.value = false
                _editingFatwa.value = null
            } catch (e: Exception) {
                _userMessage.value = "ফতোয়া সংরক্ষণ করতে ব্যর্থ: ${e.localizedMessage}"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun deleteFatwa(fatwa: FatwaArticle) {
        viewModelScope.launch {
            try {
                val admin = AuthRepository.getInstance().currentUser.value
                val adminName = admin?.nameBn ?: "ইমাম সাহেব"
                val adminRole = admin?.role?.displayNameBn ?: "মুফতি/খতিব"

                MosqueRepository.deleteFatwa(fatwa.id)

                MosqueRepository.logAdminAction(
                    adminNameBn = adminName,
                    adminRoleBn = adminRole,
                    category = AuditActionCategory.FATWAS,
                    actionTitleBn = "ফতোয়া মুছে ফেলা হয়েছে",
                    detailsBn = "মুছে ফেলা ফতোয়া: ${fatwa.questionBn.take(40)}"
                )

                _userMessage.value = "ফতোয়া সফলভাবে মুছে ফেলা হয়েছে"
            } catch (e: Exception) {
                _userMessage.value = "মুছে ফেলতে ব্যর্থ: ${e.localizedMessage}"
            }
        }
    }

    private data class DialogState(
        val replying: UserQuestionSubmission?,
        val isAddEdit: Boolean,
        val editing: FatwaArticle?,
        val isSubmitting: Boolean,
        val userMessage: String?
    )
}
