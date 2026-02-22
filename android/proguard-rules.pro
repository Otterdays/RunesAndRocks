# Add project-specific ProGuard rules here.
# See https://developer.android.com/build/shrink-code

# LibGDX
-keep class com.badlogic.gdx.** { *; }

# Keep application entry
-keep class com.runesandrocks.client.android.AndroidLauncher { *; }
