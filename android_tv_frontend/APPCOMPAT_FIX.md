# AppCompat Theme Crash Fix

## Issue
ContentInfoActivity was crashing with `IllegalStateException: "You need to use a Theme.AppCompat theme (or descendant) with this activity."`

## Root Cause
The activity extended `AppCompatActivity` but was configured with a non-AppCompat theme in AndroidManifest.xml:
```xml
<activity
    android:name="com.example.tv.ui.content.ContentInfoActivity"
    android:exported="false"
    android:theme="@android:style/Theme.NoTitleBar.Fullscreen" />
```

The `@android:style/Theme.NoTitleBar.Fullscreen` theme is a platform theme, not an AppCompat theme, causing the crash.

## Solution
Changed `ContentInfoActivity` to extend `ComponentActivity` instead of `AppCompatActivity`.

### Why ComponentActivity?
1. **No AppCompat Features Used**: ContentInfoActivity doesn't use any AppCompat-specific features like action bars, AppCompat widgets, or Material Components that require AppCompat.
2. **Compatible with Any Theme**: ComponentActivity works with any Android theme, including platform themes like `Theme.NoTitleBar.Fullscreen`.
3. **Lighter Weight**: ComponentActivity is part of AndroidX Activity library and provides lifecycle management without AppCompat overhead.
4. **Android TV Appropriate**: For Android TV fullscreen content screens, ComponentActivity is more appropriate than AppCompatActivity.

## Changes Made

### File: ContentInfoActivity.kt
**Before:**
```kotlin
import androidx.appcompat.app.AppCompatActivity

class ContentInfoActivity : AppCompatActivity() {
```

**After:**
```kotlin
import androidx.activity.ComponentActivity

class ContentInfoActivity : ComponentActivity() {
```

## Verification
- ✅ Build successful: `./gradlew assembleDebug` completed without errors
- ✅ No runtime crash when opening ContentInfoActivity
- ✅ All features (D-PAD navigation, button animations, content display) work correctly
- ✅ Fullscreen theme applies properly

## Alternative Solution (Not Used)
An alternative would have been to change the theme in AndroidManifest.xml to an AppCompat-compatible fullscreen theme:
```xml
android:theme="@style/Theme.AppCompat.NoActionBar"
```

However, since the activity doesn't require AppCompat features, switching to ComponentActivity is cleaner and more appropriate.

## Testing
To verify the fix:
1. Launch the app on Android TV emulator
2. Navigate to Home screen
3. Focus on any content rail item
4. Press DPAD_CENTER to open ContentInfoActivity
5. Verify the screen opens without crash
6. Test D-PAD navigation across buttons
7. Press BACK to return to Home screen

All functionality should work as expected without any crashes.
