-injars  build/libs/apc-shadow.jar
-outjars build/libs/apc.jar
-libraryjars  <java.home>/jmods/java.base.jmod(!**.jar;!module-info.class)

#-dontshrink
#-dontobfuscate
#-dontoptimize
#-addconfigurationdebugging
-ignorewarnings
-repackageclasses
-optimizationpasses 1
-optimizeaggressively
-flattenpackagehierarchy
#-adaptclassstrings

-keep public class de.rub.mobsec.MainKt { public static void main(java.lang.String[]); }
-keep,allowoptimization  class de.rub.mobsec.AndroidUnlockPattern
-keepclassmembers,allowoptimization class de.rub.mobsec.AndroidUnlockPattern { public *; }
-keep,allowoptimization  class de.rub.mobsec.StrengthMeter
-keep,allowoptimization  class * implements de.rub.mobsec.StrengthMeter { public *; }

##############
### kotlin ###
##############
-keepclassmembers,allowoptimization public class kotlin.reflect.jvm.internal.impl.** { public <methods>; }

#-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata { <methods>; }

-dontnote kotlin.internal.PlatformImplementationsKt
-dontwarn kotlin.reflect.jvm.internal.**
-dontwarn java.lang.ClassValue

##############
### Clikt ###
##############
-keepnames,allowshrinking,allowoptimization class com.github.ajalt.clikt.parameters.options.OptionWithValuesKt__OptionWithValuesKt
-keepattributes RuntimeVisible*Annotations,AnnotationDefault
-dontwarn org.graalvm.**
-dontwarn com.oracle.svm.core.annotate.Delete
