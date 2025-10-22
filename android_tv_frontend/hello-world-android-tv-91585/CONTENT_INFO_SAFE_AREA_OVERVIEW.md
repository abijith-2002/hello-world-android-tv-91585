# Content Info Screen — Safe-Area Scale-Down (Scoped)

Scope:
- Only the Content Info screen is compacted using ci_* dimens.

Key files:
- android_tv_frontend/app/src/main/res/layout/activity_content_info.xml
- android_tv_frontend/app/src/main/res/values*/dimens_content_info.xml
- android_tv_frontend/app/src/main/java/com/example/tv/ui/contentinfo/ContentInfoActivity.kt

Focus:
- Initial focus → actionButton1 (set in Activity).
- System date/time is non-focusable; blocks descendants.
- Explicit nextFocusRight/Left/Forward set for actionButton1..6.

Previews:
- activity_content_info_preview.xml
- activity_content_info_scaled_preview.xml
- preview_metadata_panel.xml

Build:
- cd android_tv_frontend && ./gradlew assembleDebug
