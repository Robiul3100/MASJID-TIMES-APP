package com.robiul.mosquetime.data.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

data class MosqueConfig(
    val id: String = "main_mosque",
    val nameBn: String = "বায়তুল আমান জামে মসজিদ",
    val nameEn: String = "Baitul Aman Jame Masjid",
    val district: String = "ঢাকা",
    val address: String = "রোড #০৪, সেক্টর #০৩, উত্তরা, ঢাকা-১২৩০",
    val establishedYear: String = "১৯৮৫",
    val isActive: Boolean = true
)

@Singleton
class MosqueConfigManager @Inject constructor() {

    private val firestore: FirebaseFirestore = try { FirebaseFirestore.getInstance() } catch (e: Exception) { null } ?: FirebaseFirestore.getInstance()
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _configuredMosques = MutableStateFlow<List<MosqueConfig>>(getDefaultMosques())
    val configuredMosques: StateFlow<List<MosqueConfig>> = _configuredMosques.asStateFlow()

    private val _activeMosque = MutableStateFlow<MosqueConfig>(getDefaultMosques().first())
    val activeMosque: StateFlow<MosqueConfig> = _activeMosque.asStateFlow()

    companion object {
        @Volatile
        private var instance: MosqueConfigManager? = null

        fun getInstance(): MosqueConfigManager {
            return instance ?: synchronized(this) {
                instance ?: MosqueConfigManager().also { instance = it }
            }
        }

        fun getDefaultMosques(): List<MosqueConfig> {
            return listOf(
                MosqueConfig(
                    id = "main_mosque",
                    nameBn = "বায়তুল আমান জামে মসজিদ ও ইসলামিক রিসার্চ সেন্টার",
                    nameEn = "Baitul Aman Jame Mosque & Islamic Research Center",
                    district = "ঢাকা",
                    address = "রোড #০৪, সেক্টর #০৩, উত্তরা, ঢাকা-১২৩০",
                    establishedYear = "১৯৮৫",
                    isActive = true
                ),
                MosqueConfig(
                    id = "baitul_mukarram",
                    nameBn = "বায়তুল মোকাররম জাতীয় মসজিদ",
                    nameEn = "Baitul Mukarram National Mosque",
                    district = "ঢাকা",
                    address = "পল্টন, ঢাকা-১০০০",
                    establishedYear = "১৯৬৮",
                    isActive = true
                ),
                MosqueConfig(
                    id = "uttara_sector_7",
                    nameBn = "উত্তরা কেন্দ্রীয় জামে মসজিদ",
                    nameEn = "Uttara Central Jame Mosque",
                    district = "ঢাকা",
                    address = "সেক্টর #০৭, উত্তরা, ঢাকা",
                    establishedYear = "১৯৯২",
                    isActive = true
                )
            )
        }
    }

    init {
        instance = this
        FirestoreCollections.activeMosqueId = _activeMosque.value.id
        loadMosqueConfigsFromFirestore()
    }

    fun loadMosqueConfigsFromFirestore() {
        scope.launch {
            try {
                val snapshot = firestore.collection(FirestoreCollections.MOSQUE_CONFIGS).get().await()
                if (snapshot != null && !snapshot.isEmpty) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            MosqueConfig(
                                id = doc.getString("id") ?: doc.id,
                                nameBn = doc.getString("nameBn") ?: "",
                                nameEn = doc.getString("nameEn") ?: "",
                                district = doc.getString("district") ?: "ঢাকা",
                                address = doc.getString("address") ?: "",
                                establishedYear = doc.getString("establishedYear") ?: "",
                                isActive = doc.getBoolean("isActive") ?: true
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (list.isNotEmpty()) {
                        _configuredMosques.value = list
                        // Match or fallback active mosque
                        val currentActive = list.find { it.id == FirestoreCollections.activeMosqueId } ?: list.first()
                        _activeMosque.value = currentActive
                    }
                }
            } catch (e: Exception) {
                Log.w("MosqueConfigManager", "Firestore mosque configs load error: ${e.message}")
            }
        }
    }

    suspend fun switchActiveMosque(mosqueId: String): Result<MosqueConfig> = withContext(Dispatchers.IO) {
        val target = _configuredMosques.value.find { it.id == mosqueId }
            ?: return@withContext Result.failure(Exception("মসজিদ আইডি পাওয়া যায়নি"))

        _activeMosque.value = target
        FirestoreCollections.activeMosqueId = target.id

        // Trigger repo reload for new mosque database
        MosqueAdminRepository.getInstance().loadMosqueProfileFromFirestore()
        MosqueAdminRepository.getInstance().loadPrayerScheduleFromFirestore()

        Result.success(target)
    }

    suspend fun registerOrUpdateMosque(config: MosqueConfig): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val updatedList = _configuredMosques.value.toMutableList().apply {
                val idx = indexOfFirst { it.id == config.id }
                if (idx >= 0) {
                    set(idx, config)
                } else {
                    add(config)
                }
            }
            _configuredMosques.value = updatedList

            val data = hashMapOf(
                "id" to config.id,
                "nameBn" to config.nameBn,
                "nameEn" to config.nameEn,
                "district" to config.district,
                "address" to config.address,
                "establishedYear" to config.establishedYear,
                "isActive" to config.isActive,
                "updatedAt" to System.currentTimeMillis()
            )

            firestore.collection(FirestoreCollections.MOSQUE_CONFIGS).document(config.id)
                .set(data, SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MosqueConfigManager", "Failed to save mosque config: ${e.message}")
            Result.success(Unit) // Offline successful
        }
    }
}
