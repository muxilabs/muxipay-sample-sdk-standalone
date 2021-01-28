# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\bruco\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

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


-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }


-keep class javax.xml.** { *; }
-keep interface javax.xml.** { *; }
-dontwarn javax.xml.**

-keep class java.awt.** { *; }
-keep interface java.awt.** { *; }
-dontwarn java.awt.**

-keep class javax.security.** { *; }
-keep interface javax.security.** { *; }
-dontwarn javax.security.**

-keep class com.sun.mail.* { *; }
-keep interface com.sun.mail.** { *; }
-dontwarn com.sun.mail.**

-keep class java.beans.** { *; }
-keep interface java.beans.** { *; }


-keep class org.simpleframework.xml.* { *; }
-keep interface org.simpleframework.xml.** { *; }


-keep class com.posweblib.** { *; }
-keep interface com.posweblib.** { *; }

-keep class br.com.appi.novastecnologias.androidb.** { *; }
-keep interface br.com.appi.novastecnologias.android.** { *; }

-keep class br.com.appi.novastecnologias.android.java.beans.** { *; }
-keep interface br.com.appi.novastecnologias.android.java.beans.** { *; }

-keep class com.muxi.pwhal.common.coordinator.** { *; }
-keep class com.muxi.pwhal.common.coordinator.PPCoordinator{*;}

-keep class javax.security.** { *; }
-keep interface javax.security.** { *; }

-keep class javamail.** {*;}
-keep class javax.mail.** {*;}
-keep class javax.activation.** {*;}
-keep class com.sun.mail.dsn.** {*;}
-keep class com.sun.mail.handlers.** {*;}
-keep class com.sun.mail.smtp.** {*;}
-keep class com.sun.mail.util.** {*;}
-keep class mailcap.** {*;}
-keep class mimetypes.** {*;}
-keep class myjava.awt.datatransfer.** {*;}
-keep class org.apache.harmony.awt.** {*;}
-keep class org.apache.harmony.misc.** {*;}
-keep interface br.com.appi.android.porting.posweb.nativeinit.** {*;}
-keep class br.com.appi.android.porting.posweb.nativeinit.** {*;}
-keep class muxi.pwservice.br.com.appi.android.porting.posweb.nativeinit.** {*;}
-keep class Java.br.com.appi.android.porting.posweb.nativeinit.** {*;}

-keep class br.com.appi.android.porting.posweb.components.c2java.initializer.InitializerScreenCoordinator{*;}
-keep class br.com.appi.android.porting.posweb.components.c2java.initializer.ScreenInitializerNative{*;}
-keep class br.com.appi.android.porting.posweb.components.c2java.dfc.DFCCoordinator{*;}
-keep class br.com.appi.android.porting.posweb.components.c2java.dfc.DFCNative{*;}
-keep class br.com.appi.android.porting.posweb.components.c2java.infrastructure.InfrastructureCoordinator{*;}
-keep class br.com.appi.android.porting.posweb.components.c2java.infrastructure.InfrastructureNative{*;}
-keep class br.com.appi.android.porting.posweb.components.c2java.constructor.ScreenCoordinator{*;}
-keep class br.com.appi.android.porting.posweb.components.c2java.constructor.ScreenConstructorNative{*;}
-keep class br.com.appi.android.porting.posweb.components.c2java.runtime.ScreenRunTimeCoordinator{*;}
-keep class br.com.appi.android.porting.posweb.components.c2java.runtime.ScreenRuntimeNative{*;}
-keep class br.com.appi.android.porting.posweb.components.c2java.runtime.ScreenRuntimeNative{*;}
-keep class br.com.appi.android.porting.posweb.components.java2c.Java2CBrowserActionsIMP{*;}
-keep class br.com.appi.android.porting.posweb.components.java2c.Java2CBrowserActionsNative{*;}
-keep class br.com.appi.android.porting.posweb.components.java2c.PWTimerEventsCoordinator{*;}
-keep class br.com.appi.android.porting.posweb.components.java2c.PWTimerEventsNative{*;}

-keep class br.com.appi.android.porting.posweb.components.java2c.PinPadCoordinator{*;}
-keep class br.com.appi.android.porting.posweb.native_init.PWCoordinator{*;}
-keep class com.muxi.pwhal.common.coordinator.BluetoothCoordinator{*;}
-keep class android.pwhal.common.defaultimp.bluetooth{*;}
-keep class br.com.appi.android.porting.posweb.bluetooth.BluetoothDeviceDiscover{*;}

-keep class br.com.appi.android.porting.posweb.components.java2c.PinPadCoordinatorNative{*;}
-keep class br.com.appi.android.porting.posweb.components.java2c.Java2CUiEventsNative{*;}

-keep class com.muxi.pwhal.common.model.PinPadResult{*;}
-keep class com.muxi.pwhal.common.util.FixedRecyclerView{*;}
-keep class com.muxi.pwhal.common.defaultimp.printer.PrinterSmartPhoneDevice{* ;}

-dontwarn com.google.**
# APOS A8
-dontwarn com.landicorp.**
#-keep class com.landicorp{*;}
#-keep interface com.landicorp{*;}

-dontwarn com.usdk.apiservice.aidl.**
#-keep class com.usdk.apiservice.aidl.signpanel{*;}
#-keep interface com.usdk.apiservice.aidl.signpanel{*;}



-keep class br.com.appi.android.porting.posweb.components.java2c.PinPadCoordinator{
    void setLibSharedCoordinatorInstance();
    void startICCSMARTDeviceEx();
    void stopICCSMARTDevice();
}

-keep class javax.inject.* { *; }

-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-keep public class android.support.v4.app.** { *; }

-keep class com.ingenico.lar.bc.**{ *; }
-dontwarn java.awt.**
-dontwarn java.beans.Beans
-dontwarn javax.security.**

-dontnote br.com.appi.android.porting.posweb.components.**
-dontnote android.arch.lifecycle.**
-dontnote androidx.**
-dontnote android.pwhal.common.util.**
-dontnote com.usdk.**
-dontnote com.ingenico.**
-dontnote br.com.appi.android.porting.posweb.**
-dontnote com.muxi.pwhal.common.coordinator.**
-dontnote com.posweblib.di.**
-dontnote com.sun.**
-dontnote org.simpleframework.**
-dontnote javax.xml.**
-dontnote javax.security.**

-dontwarn android.pwhal.common.**
-dontnote android.pwhal.common.**

-dontwarn pplib.**

-keep class com.pax.**{ *; }
-keep interface com.pax.**{ *; }
-dontwarn com.pax.**

-keep class br.com.setis.**{ *; }
-keep interface br.com.setis.**{ *; }
-dontwarn br.com.setis.**