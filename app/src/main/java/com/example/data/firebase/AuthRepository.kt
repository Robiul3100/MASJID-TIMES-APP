package com.example.data.firebase

import android.util.Log
import com.example.core.auth.AdminRole
import com.example.core.auth.AdminUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthResult {
    data class Success(val user: AdminUser) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}

class AuthRepository(
    private val auth: FirebaseAuth = try { FirebaseAuth.getInstance() } catch (e: Exception) { null } ?: FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = try { FirebaseFirestore.getInstance() } catch (e: Exception) { null } ?: FirebaseFirestore.getInstance()
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val _currentUser = MutableStateFlow<AdminUser?>(null)
    val currentUser: StateFlow<AdminUser?> = _currentUser.asStateFlow()

    init {
        checkCurrentAuth()
    }

    private fun checkCurrentAuth() {
        try {
            val fbUser = auth.currentUser
            if (fbUser != null) {
                scope.launch {
                    val profile = fetchAdminProfile(fbUser.uid, fbUser.email ?: "")
                    if (profile != null) {
                        _currentUser.value = profile
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error checking initial auth: ${e.message}")
        }
    }

    suspend fun signIn(email: String, pass: String): AuthResult {
        if (email.isBlank() || pass.isBlank()) {
            return AuthResult.Error("ইমেইল ও পাসওয়ার্ড প্রদান করুন")
        }

        val normalizedEmail = email.trim().lowercase()
        val normalizedPass = pass.trim()

        // 1. Built-in Offline / Demo Admin Credentials for seamless offline testing
        if (normalizedEmail == "admin@mosque.com" || normalizedEmail == "admin@mosque.org") {
            if (normalizedPass == "admin123" || normalizedPass == "123456" || normalizedPass == "admin") {
                val superAdmin = AdminUser(
                    uid = "local_super_admin",
                    email = normalizedEmail,
                    nameBn = "হাজী মো. রফিকুল ইসলাম চৌধুরী",
                    designation = "সভাপতি ও মোতাওয়াল্লী",
                    role = AdminRole.SUPER_ADMIN,
                    phone = "+880 1711-112233",
                    isActive = true
                )
                _currentUser.value = superAdmin
                return AuthResult.Success(superAdmin)
            }
        } else if (normalizedEmail == "imam@mosque.com" || normalizedEmail == "khatib@mosque.com") {
            if (normalizedPass == "imam123" || normalizedPass == "123456") {
                val imamAdmin = AdminUser(
                    uid = "local_imam_admin",
                    email = normalizedEmail,
                    nameBn = "মাওলানা মুহাম্মাদ আব্দুল করিম",
                    designation = "প্রধান খতিব ও পেশ ইমাম",
                    role = AdminRole.IMAM,
                    phone = "+880 1712-345678",
                    isActive = true
                )
                _currentUser.value = imamAdmin
                return AuthResult.Success(imamAdmin)
            }
        } else if (normalizedEmail == "cashier@mosque.com" || normalizedEmail == "treasurer@mosque.com") {
            if (normalizedPass == "cash123" || normalizedPass == "123456") {
                val treasurerAdmin = AdminUser(
                    uid = "local_treasurer_admin",
                    email = normalizedEmail,
                    nameBn = "আলহাজ্ব মো. দেলোয়ার হোসেন",
                    designation = "কোষাধ্যক্ষ ও অর্থ সম্পাদক",
                    role = AdminRole.ADMIN,
                    phone = "+880 1715-556677",
                    isActive = true
                )
                _currentUser.value = treasurerAdmin
                return AuthResult.Success(treasurerAdmin)
            }
        }

        return try {
            val authResult = auth.signInWithEmailAndPassword(email.trim(), pass.trim()).await()
            val firebaseUser = authResult.user ?: return AuthResult.Error("ব্যবহারকারী পাওয়া যায়নি")

            // Fetch admin role from Firestore
            val adminUser = fetchAdminProfile(firebaseUser.uid, firebaseUser.email ?: email)
            if (adminUser != null) {
                if (!adminUser.isActive) {
                    signOut()
                    return AuthResult.Error("আপনার অ্যাডমিন অ্যাকাউন্টটি বর্তমানে নিষ্ক্রিয় রয়েছে।")
                }
                _currentUser.value = adminUser
                AuthResult.Success(adminUser)
            } else {
                // First-time or fallback admin profile
                val fallbackAdmin = AdminUser(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: email,
                    nameBn = "প্রধান অ্যাডমিন",
                    designation = "মসজিদ পরিচালনা পরিষদ",
                    role = AdminRole.SUPER_ADMIN,
                    isActive = true
                )
                saveAdminUser(fallbackAdmin)
                _currentUser.value = fallbackAdmin
                AuthResult.Success(fallbackAdmin)
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Firebase auth failed: ${e.message}")
            // Provide localized error messages for common Firebase exceptions
            val msg = when {
                e.message?.contains("invalid-credential", ignoreCase = true) == true ||
                e.message?.contains("wrong-password", ignoreCase = true) == true ||
                e.message?.contains("user-not-found", ignoreCase = true) == true -> "ইমেইল বা পাসওয়ার্ড ভুল হয়েছে।"
                e.message?.contains("network", ignoreCase = true) == true -> "ইন্টারনেট সংযোগ পরীক্ষা করুন।"
                else -> "প্রবেশ করতে ব্যর্থ হয়েছে: ${e.localizedMessage ?: "অজানা ত্রুটি"}"
            }
            AuthResult.Error(msg)
        }
    }

    private suspend fun fetchAdminProfile(uid: String, email: String): AdminUser? {
        return try {
            val snapshot = firestore.collection(FirestoreCollections.ADMIN_USERS).document(uid).get().await()
            if (snapshot.exists()) {
                val roleStr = snapshot.getString("role") ?: "SUPER_ADMIN"
                val nameBn = snapshot.getString("nameBn") ?: "মসজিদ অ্যাডমিন"
                val designation = snapshot.getString("designation") ?: "কমিটি সদস্য"
                val isActive = snapshot.getBoolean("isActive") ?: true
                val phone = snapshot.getString("phone") ?: ""

                AdminUser(
                    uid = uid,
                    email = email,
                    nameBn = nameBn,
                    designation = designation,
                    role = AdminRole.fromString(roleStr),
                    phone = phone,
                    isActive = isActive
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
                "isActive" to adminUser.isActive,
                "lastLoginMillis" to System.currentTimeMillis()
            )
            firestore.collection(FirestoreCollections.ADMIN_USERS).document(adminUser.uid).set(data).await()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to save admin user: ${e.message}")
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

    companion object {
        @Volatile
        private var INSTANCE: AuthRepository? = null

        fun getInstance(): AuthRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthRepository().also { INSTANCE = it }
            }
        }
    }
}
