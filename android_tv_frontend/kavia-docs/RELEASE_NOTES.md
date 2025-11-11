# Hello World Android TV — Release Notes

## Version
Unreleased

## Release Date
2025-11-11

## Overview
This update summarizes all features currently implemented in the Android TV app. The application is built with Kotlin, optimized for TV with Leanback, and showcases a centered “Hello world” card experience. It adopts the Nord dark theme and uses Google’s Reddit Sans font for clear, comfortable viewing.

## New Features
- Hello world card displayed on launch with TV-friendly layout.
- Navigation scaffolding and TV activities including Splash, Home, Login, Content Info, and Player.
- Leanback integration for D-pad navigation and focus behavior.
- ExoPlayer-based PlayerActivity with fullscreen Nord dark theme.
- Coil-based image loading utilities and basic repository/network scaffolding (Retrofit + Moshi).
- Configurable API base URL via BuildConfig field.

## UI/UX Updates
- Nord dark theme applied app-wide, including fullscreen variants for Content Info and Player.
- Reddit Sans font integrated for consistent, legible TV typography.
- Minimalist card design with subtle shadows and rounded corners to fit modern TV aesthetics.
- Clear focus states and D-pad-first interactions.

## Performance & Compatibility
- Target/Compile SDK 35; Min SDK 21.
- AndroidX Leanback ensures broad TV compatibility and smooth focus navigation.
- Efficient image loading via Coil; video playback via ExoPlayer 2.19.x.

## Fixes/Improvements
- Theming consistency: ContentInfoActivity and PlayerActivity now use Nord dark fullscreen theme.
- Resource cleanup and strings updates for clarity and consistency.

## Known Issues
None known.

## Getting Started
- Install the APK on an Android TV device or emulator.
- Launch from the TV home to view the Hello world card and navigate using the D-pad.
- For development builds, API base URL can be set via the API_BASE_URL property or environment variable.

## Credits
- Built with Kotlin and AndroidX.
- TV support via AndroidX Leanback.
- Video playback powered by ExoPlayer.
- Image loading by Coil.
- Typography by Google’s Reddit Sans.
