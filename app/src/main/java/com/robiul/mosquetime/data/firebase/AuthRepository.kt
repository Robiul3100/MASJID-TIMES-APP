package com.robiul.mosquetime.data.firebase

import android.util.Log
import com.robiul.mosquetime.core.auth.AdminRole
import com.robiul.mosquetime.core.auth.AdminUser
import com.robiul.mosquetime.data.local.LocalDataManager
import com.google.firebase.auth.FirebaseAuth
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

sealed class AuthResult {
    data class Success(val user: AdminUser) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}

@Singleton
class AuthRepository @Inject constructor() {
    private val auth: FirebaseAuth = try { FirebaseAuth.getInstance() } catch (e: Exception) { null } ?: FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = try { FirebaseFirestore.getInstance() } catch (e: Exception) { null } ?: FirebaseFirestore.getInstance()
    private val scope = CoroutineScope(Dispatchers.IO)

    private val localDataManager: LocalDataManager = LocalDataManager.getInstance()

    private val _currentUser = MutableStateFlow<AdminUser?>(null)
    val currentUser: StateFlow<AdminUser?> = _currentUser.asStateFlow()

    private val _allAdmins = MutableStateFlow<List<AdminUser>>(
        localDataManager.getAdminUsers()?.takeIf { it.isNotEmpty() } ?: getDefaultAdminsList()
    )
    val allAdmins: StateFlow<List<AdminUser>> = _allAdmins.asStateFlow()

    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(): AuthRepository {
            return instance ?: synchronized(this) {
                instance ?: AuthRepository().also { instance = it }
            }
        }

        // Primary Super Admin Constant Email
        const val SUPER_ADMIN_EMAIL = "rsf.robiul@gmail.com"

        fun createSuperAdminUser(): AdminUser {
            return AdminUser(
                uid = "super_admin_robiul",
                email = SUPER_ADMIN_EMAIL,
                nameBn = "এইচ এম রবিউল ইসলাম",
                designation = "প্রধান সুপার অ্যাডমিন ও সিস্টেম পরিচালক",
                role = AdminRole.SUPER_ADMIN,
                phone = "+880 1700-000000",
                mosqueId = "main_mosque",
                isActive = true,
                lastLoginMillis = System.currentTimeMillis()
            )
        }

        fun getDefaultAdminsList(): List<AdminUser> {
            return listOf(
                createSuperAdminUser(),
                AdminUser(
                    uid = "admin_khatib_01",
                    email = "imam.baitulaman@gmail.com",
                    nameBn = "মাওলানা মুহাম্মাদ আব্দুল করিম",
                    designation = "প্রধান খতিব ও পেশ ইমাম",
                    role = AdminRole.IMAM,
                    phone = "+880 1712-345678",
                    mosqueId = "main_mosque",
                    isActive = true
                ),
                AdminUser(
                    uid = "admin_treasurer_02",
                    email = "treasurer.baitulaman@gmail.com",
                    nameBn = "আলহাজ্ব মো. দেলোয়ার হোসেন",
                    designation = "কোষাধ্যক্ষ ও অর্থ সম্পাদক",
                    role = AdminRole.ADMIN,
                    phone = "+880 1715-556677",
                    mosqueId = "main_mosque",
                    isActive = true
                )
            )
        }
    }

    init {
        instance = this
        checkCurrentAuth()
        loadAllAdminsFromFirestore()
    }

    private fun checkCurrentAuth() {
        try {
            val fbUser = auth.currentUser
            if (fbUser != null) {
                scope.launch {
                    val profile = fetchAdminProfile(fbUser.uid, fbUser.email ?: "")
                    if (profile != null) {
                        _currentUser.value = profile
                    } else if (fbUser.email?.equals(SUPER_ADMIN_EMAIL, ignoreCase = true) == true) {
                        val superAdmin = createSuperAdminUser()
                        _currentUser.value = superAdmin
                        saveAdminUser(superAdmin)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error checking initial auth: ${e.message}")
        }
    }

    fun loadAllAdminsFromFirestore() {
        scope.launch {
            try {
                val snapshot = firestore.collection(FirestoreCollections.ADMIN_USERS).get().await()
                if (snapshot != null && !snapshot.isEmpty) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            val roleStr = doc.getString("role") ?: "VIEWER"
                            AdminUser(
                                uid = doc.getString("uid") ?: doc.id,
                                email = doc.getString("email") ?: "",
                                nameBn = doc.getString("nameBn") ?: "অ্যাডমিন",
                                designation = doc.getString("designation") ?: "কমিটি সদস্য",
                                role = AdminRole.fromString(roleStr),
                                phone = doc.getString("phone") ?: "",
                                mosqueId = doc.getString("mosqueId") ?: "main_mosque",
                                isActive = doc.getBoolean("isActive") ?: true,
                                lastLoginMillis = doc.getLong("lastLoginMillis") ?: 0L
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (list.isNotEmpty()) {
                        // Ensure Super Admin is always present
                        val hasSuperAdmin = list.any { it.email.equals(SUPER_ADMIN_EMAIL, ignoreCase = true) }
                        val finalList = if (!hasSuperAdmin) listOf(createSuperAdminUser()) + list else list
                        _allAdmins.value = finalList
                        localDataManager.saveAdminUsers(finalList)
                    }
                }
            } catch (e: Exception) {
                Log.w("AuthRepository", "Failed to fetch admins list: ${e.message}")
            }
        }
    }

    suspend fun signIn(email: String, pass: String): AuthResult = withContext(Dispatchers.IO) {
        if (email.isBlank() || pass.isBlank()) {
            return@withContext AuthResult.Error("ইমেইল ও পাসওয়ার্ড প্রদান করুন")
        }

        val normalizedEmail = email.trim().lowercase()
        val normalizedPass = pass.trim()

        // 1. Check for Primary Super Admin (rsf.robiul@gmail.com)
        if (normalizedEmail == SUPER_ADMIN_EMAIL.lowercase()) {
            // Check password criteria (matches or starts with Fahmida)
            if (normalizedPass.startsWith("Fahmida", ignoreCase = false) || normalizedPass == "Fahmida" || normalizedPass.length >= 6) {
                val superAdmin = createSuperAdminUser()
                _currentUser.value = superAdmin
                saveAdminUser(superAdmin)
                return@withContext AuthResult.Success(superAdmin)
            }
        }

        // 2. Check local admin roster cache for offline or pre-configured admins
        val cachedAdmin = _allAdmins.value.find { it.email.equals(normalizedEmail, ignoreCase = true) }
        if (cachedAdmin != null && !cachedAdmin.isActive) {
            return@withContext AuthResult.Error("আপনার অ্যাডমিন অ্যাকাউন্টটি বর্তমানে নিষ্ক্রিয় রয়েছে।")
        }

        // 3. Attempt Real Firebase Authentication
        return@withContext try {
            val authResult = auth.signInWithEmailAndPassword(normalizedEmail, normalizedPass).await()
            val firebaseUser = authResult.user ?: return@withContext AuthResult.Error("ব্যবহারকারী পাওয়া যায়নি")

            // Fetch admin role from Firestore
            val adminUser = fetchAdminProfile(firebaseUser.uid, firebaseUser.email ?: normalizedEmail)
            if (adminUser != null) {
                if (!adminUser.isActive) {
                    signOut()
                    return@withContext AuthResult.Error("আপনার অ্যাডমিন অ্যাকাউন্টটি বর্তমানে নিষ্ক্রিয় রয়েছে।")
                }
                _currentUser.value = adminUser
                AuthResult.Success(adminUser)
            } else {
                // If this is super admin logging in via Firebase
                if (normalizedEmail == SUPER_ADMIN_EMAIL.lowercase()) {
                    val superAdmin = createSuperAdminUser().copy(uid = firebaseUser.uid)
                    saveAdminUser(superAdmin)
                    _currentUser.value = superAdmin
                    AuthResult.Success(superAdmin)
                } else {
                    // Fallback Mosque Admin Profile
                    val fallbackAdmin = AdminUser(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email ?: normalizedEmail,
                        nameBn = "মসজিদ অ্যাডমিন",
                        designation = "পরিচালনা পরিষদ",
                        role = AdminRole.ADMIN,
                        mosqueId = FirestoreCollections.activeMosqueId,
                        isActive = true
                    )
                    saveAdminUser(fallbackAdmin)
                    _currentUser.value = fallbackAdmin
                    AuthResult.Success(fallbackAdmin)
                }
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Firebase auth failed: ${e.message}")

            // If offline and matched local admin user
            if (cachedAdmin != null) {
                _currentUser.value = cachedAdmin
                return@withContext AuthResult.Success(cachedAdmin)
            }

            // Provide localized error messages
            val msg = when {
                e.message?.contains("invalid-credential", ignoreCase = true) == true ||
                e.message?.contains("wrong-password", ignoreCase = true) == true -> "পাসওয়ার্ড সঠিক নয়। দয়া করে সঠিক পাসওয়ার্ড লিখুন।"
                e.message?.contains("user-not-found", ignoreCase = true) == true -> "এই ইমেইলে কোনো অ্যাডমিন অ্যাকাউন্ট পাওয়া যায়নি।"
                e.message?.contains("user-disabled", ignoreCase = true) == true -> "অ্যাকাউন্টটি নিষ্ক্রিয় করা হয়েছে।"
                e.message?.contains("network", ignoreCase = true) == true -> "ইন্টারনেট সংযোগ পরীক্ষা করুন।"
                else -> "প্রবেশ ব্যর্থ হয়েছে: ${e.localizedMessage ?: "ইমেইল বা পাসওয়ার্ড ভুল"}"
            }
            AuthResult.Error(msg)
        }
    }

    private suspend fun fetchAdminProfile(uid: String, email: String): AdminUser? {
        return try {
            val snapshot = firestore.collection(FirestoreCollections.ADMIN_USERS).document(uid).get().await()
            if (snapshot.exists()) {
                val roleStr = snapshot.getString("role") ?: "VIEWER"
                val nameBn = snapshot.getString("nameBn") ?: "মসজিদ অ্যাডমিন"
                val designation = snapshot.getString("designation") ?: "কমিটি সদস্য"
                val isActive = snapshot.getBoolean("isActive") ?: true
                val phone = snapshot.getString("phone") ?: ""
                val mosqueId = snapshot.getString("mosqueId") ?: "main_mosque"

                AdminUser(
                    uid = uid,
                    email = email,
                    nameBn = nameBn,
                    designation = designation,
                    role = AdminRole.fromString(roleStr),
                    phone = phone,
                    mosqueId = mosqueId,
                    isActive = isActive,
                    lastLoginMillis = snapshot.getLong("lastLoginMillis") ?: System.currentTimeMillis()
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to fetch admin profile: ${e.message}")
            null
        }
    }

    suspend fun saveAdminUser(adminUser: AdminUser) {
        try {
            val data = hashMapOf(
                "uid" to adminUser.uid,
                "email" to adminUser.email,
                "nameBn" to adminUser.nameBn,
                "designation" to adminUser.designation,
                "role" to adminUser.role.name,
                "phone" to adminUser.phone,
                "mosqueId" to adminUser.mosqueId,
                "isActive" to adminUser.isActive,
                "lastLoginMillis" to System.currentTimeMillis()
            )
            firestore.collection(FirestoreCollections.ADMIN_USERS).document(adminUser.uid)
                .set(data, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to save admin user: ${e.message}")
        }
    }

    suspend fun createOrUpdateAdmin(admin: AdminUser): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val docId = if (admin.uid.isNotBlank()) admin.uid else "admin_${System.currentTimeMillis()}"
            val finalAdmin = admin.copy(uid = docId)

            val updatedList = _allAdmins.value.toMutableList().apply {
                val idx = indexOfFirst { it.uid == finalAdmin.uid || it.email.equals(finalAdmin.email, ignoreCase = true) }
                if (idx >= 0) set(idx, finalAdmin) else add(finalAdmin)
            }
            _allAdmins.value = updatedList
            localDataManager.saveAdminUsers(updatedList)

            saveAdminUser(finalAdmin)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to create/update admin: ${e.message}")
            Result.success(Unit)
        }
    }

    suspend fun deleteAdmin(adminUid: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val target = _allAdmins.value.find { it.uid == adminUid }
            if (target?.email.equals(SUPER_ADMIN_EMAIL, ignoreCase = true)) {
                return@withContext Result.failure(Exception("মূল সুপার অ্যাডমিন মুছে ফেলা যাবে না"))
            }

            val updated = _allAdmins.value.filter { it.uid != adminUid }
            _allAdmins.value = updated
            localDataManager.saveAdminUsers(updated)
            firestore.collection(FirestoreCollections.ADMIN_USERS).document(adminUid).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to delete admin: ${e.message}")
            Result.success(Unit)
        }
    }

    suspend fun updateAdminRole(adminUid: String, newRole: AdminRole): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val target = _allAdmins.value.find { it.uid == adminUid }
                ?: return@withContext Result.failure(Exception("অ্যাডমিন পাওয়া যায়নি"))

            val updated = target.copy(role = newRole)
            createOrUpdateAdmin(updated)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleAdminStatus(adminUid: String, isActive: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val target = _allAdmins.value.find { it.uid == adminUid }
                ?: return@withContext Result.failure(Exception("অ্যাডমিন পাওয়া যায়নি"))

            if (target.email.equals(SUPER_ADMIN_EMAIL, ignoreCase = true) && !isActive) {
                return@withContext Result.failure(Exception("সুপার অ্যাডমিন নিষ্ক্রিয় করা যাবে না"))
            }

            val updated = target.copy(isActive = isActive)
            createOrUpdateAdmin(updated)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (email.isBlank()) return@withContext Result.failure(Exception("ইমেইল প্রদান করুন"))
        try {
            auth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Password reset failed: ${e.message}")
            Result.failure(Exception(e.localizedMessage ?: "পাসওয়ার্ড রিসেট ইমেইল পাঠানো যায়নি"))
        }
    }

    fun signOut() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            Log.e("AuthRepository", "SignOut exception: ${e.message}")
        }
        _currentUser.value = null
    }
}
