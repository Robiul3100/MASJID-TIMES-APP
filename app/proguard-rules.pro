# ===================================================================
# MosqueTime App - Production ProGuard / R8 Rules
# ===================================================================

# 1. Preserve Line Numbers & Source File Names for Crash Reports
-keepattributes SourceFile,LineNumberTable,Signature,InnerClasses,EnclosingMethod,*Annotation*

# 2. General Keep Annotations
-keep @androidx.annotation.Keep class * { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}
-keep @com.google.errorprone.annotations.Keep class * { *; }
-keepclassmembers class * {
    @com.google.errorprone.annotations.Keep *;
}

# 3. MosqueTime Data Models (Firestore Serialization & Room Entities)
-keep class com.robiul.mosquetime.data.model.** { *; }
-keepclassmembers class com.robiul.mosquetime.data.model.** { *; }

-keep class com.robiul.mosquetime.core.auth.** { *; }
-keepclassmembers class com.robiul.mosquetime.core.auth.** { *; }

-keep class com.robiul.mosquetime.data.firebase.** { *; }
-keepclassmembers class com.robiul.mosquetime.data.firebase.** { *; }

-keep class com.robiul.mosquetime.model.** { *; }
-keepclassmembers class com.robiul.mosquetime.model.** { *; }

-keep class com.robiul.mosquetime.data.local.** { *; }
-keepclassmembers class com.robiul.mosquetime.data.local.** { *; }

# 4. Firebase Ecosystem (Auth, Firestore, Messaging, Storage)
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-dontwarn com.google.firebase.**
-keep class com.google.firebase.** { *; }
-keepclassmembers class com.google.firebase.** { *; }

# Firebase Firestore JavaBean properties for .toObject() mapping
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
    @com.google.firebase.firestore.PropertyName <methods>;
    @com.google.firebase.firestore.Exclude <fields>;
    @com.google.firebase.firestore.Exclude <methods>;
    @com.google.firebase.firestore.DocumentId <fields>;
    @com.google.firebase.firestore.DocumentId <methods>;
    @com.google.firebase.firestore.ServerTimestamp <fields>;
    @com.google.firebase.firestore.ServerTimestamp <methods>;
}

# 5. Kotlin Coroutines & Flow
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }
-keepclassmembers class kotlinx.coroutines.** { *; }

# 6. Room Database Persistence
-dontwarn androidx.room.paging.**
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.RoomDatabase$Callback
-keep class androidx.room.** { *; }
-keepclassmembers class androidx.room.** { *; }

# 7. Dagger Hilt Dependency Injection
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }
-keepclassmembers class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp { *; }
-keep class * extends androidx.activity.ComponentActivity { *; }
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# 8. Jetpack Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# 9. Play Services Auth
-keep class com.google.android.gms.auth.api.** { *; }
-keep class com.google.android.gms.common.** { *; }

# 10. Broadcast Receivers & Services
-keep class com.robiul.mosquetime.receiver.** { *; }
-keepclassmembers class com.robiul.mosquetime.receiver.** { *; }
-keep class com.robiul.mosquetime.service.** { *; }
-keepclassmembers class com.robiul.mosquetime.service.** { *; }
-keep class com.robiul.mosquetime.widget.** { *; }
-keepclassmembers class com.robiul.mosquetime.widget.** { *; }
