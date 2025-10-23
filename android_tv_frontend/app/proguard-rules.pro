# Keep Retrofit interfaces
-keep interface com.example.tv.data.api.ApiService

# Keep DTOs and their members (used by Moshi reflection)
-keep class com.example.tv.data.api.dto.** { *; }

# Keep domain models used in UI mapping
-keep class com.example.tv.data.api.ContentItem { *; }

# Keep annotation and signature metadata for Moshi/Kotlin reflection
-keepattributes Signature
-keepattributes *Annotation*

# Don't warn for common okhttp/okio/annotations used by retrofit/moshi
-dontwarn okio.**
-dontwarn javax.annotation.**

# Keep Kotlin metadata (helps reflection-based adapters)
-keep class kotlin.Metadata { *; }
