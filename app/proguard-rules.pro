# Mind Bend Proguard & R8 Deobfuscation Rules

# Preserve Jetpack Compose & Kotlin metadata
-keepclassmembers class * extends androidx.compose.ui.node.LayoutNode { *; }
-keep class androidx.compose.ui.** { *; }
-keep class com.mindbend.optical.game.** { *; }

# Preserve Firebase & Google Play Services
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep line numbers for ANR / crash symbolication stack traces
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable,Signature,*Annotation*
