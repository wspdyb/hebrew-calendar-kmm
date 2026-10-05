# Keep Kotlin-related classes
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$WhenMappings {
    <fields>;
}

# Keep Compose classes
-keep class androidx.compose.** { *; }
-keep interface androidx.compose.** { *; }

# Keep Material Design 3
-keep class androidx.material3.** { *; }
-keep interface androidx.material3.** { *; }

# Keep Navigation
-keep class androidx.navigation.** { *; }
-keep interface androidx.navigation.** { *; }

# Keep DataStore
-keep class androidx.datastore.** { *; }

# Keep ViewModel and Lifecycle
-keep class androidx.lifecycle.** { *; }
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep Activity and Fragment
-keep class androidx.activity.** { *; }
-keep class androidx.fragment.** { *; }

# Keep serialization
-keepclassmembers class * {
    *** *;
}

# Suppress warnings for missing classes
-dontwarn kotlin.**
-dontwarn androidx.**
-dontwarn com.google.android.**

# Optimization settings
-optimizationpasses 5
-dontusemixedcaseclassnames
-verbose

# Keep line numbers for crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
