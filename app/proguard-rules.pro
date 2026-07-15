# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

 # With R8 full mode generic signatures are stripped for classes that are not
 # kept. Suspend functions are wrapped in continuations where the type argument
 # is used.
 -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

 # R8 full mode strips generic signatures from return types if not kept.
 -if interface * { @retrofit2.http.* public *** *(...); }
 -keep,allowoptimization,allowshrinking,allowobfuscation class <3>

 # With R8 full mode generic signatures are stripped for classes that are not kept.
 -keep,allowobfuscation,allowshrinking class retrofit2.Response

 # java.lang.management.* is a desktop-JVM-only API (not present on Android).
 # Some dependency (likely pulled in via Ktor server artifacts / a logging
 # library) references it in an optional/reflective code path that never
 # executes on Android, so it's safe to suppress these warnings.
 -dontwarn java.lang.management.ManagementFactory
 -dontwarn java.lang.management.RuntimeMXBean

 # ---------------------------------------------------------------------------
 # Gson / Retrofit network models
 #
 # Root cause of "SyncWorker issues. Failed to load data" appearing only in
 # release builds: R8 was renaming the fields of our Gson-deserialized DTOs
 # (PaginationMeta, UserDto, DraftDto, EditDto, OrganisationDto, ListingDto,
 # PagedSongsResponse, Paystack*). Gson matches JSON keys to fields by name
 # via reflection, so once field names were obfuscated, deserialization
 # silently produced nulls in non-null Kotlin fields, which threw further
 # downstream, sent SyncWorker into an infinite retry loop, and eventually
 # timed out the UI. SongEntity/BookEntity were already safe via @Keep; the
 # DTOs in core/network are now @Keep-annotated too. These rules are the
 # belt-and-suspenders backstop:
 # ---------------------------------------------------------------------------

 # Keep annotations at runtime so @SerializedName (Paystack*) still works,
 # and keep generic signatures so Gson's TypeToken machinery still works.
 -keepattributes Signature
 -keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, AnnotationDefault
 -keepattributes InnerClasses, EnclosingMethod

 # Never obfuscate/shrink field names on any of our network DTOs, regardless
 # of whether we remember to annotate a future one with @Keep.
 -keepclassmembers class com.songlib.core.network.dtos.** {
     <fields>;
 }
 -keep class com.songlib.core.network.dtos.** { *; }

 # Standard Gson rules (from Gson's own R8 recommendations).
 -dontwarn sun.misc.**
 -keep class com.google.gson.stream.** { *; }
 -keep class * extends com.google.gson.TypeAdapter
 -keep class * implements com.google.gson.TypeAdapterFactory
 -keep class * implements com.google.gson.JsonSerializer
 -keep class * implements com.google.gson.JsonDeserializer