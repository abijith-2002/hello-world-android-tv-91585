# Contributing — Content Info Safe-Area

Scope
- Only adjust the Content Info screen using `ci_*` dimens.
- Do not change global themes or other screens.

Key files
- android_tv_frontend/app/src/main/res/layout/activity_content_info.xml
- android_tv_frontend/app/src/main/res/values*/dimens_content_info.xml
- android_tv_frontend/app/src/main/java/com/example/tv/ui/contentinfo/ContentInfoActivity.kt

Focus/D-Pad
- Keep System Date/Time non-focusable.
- Initial focus: actionButton1.
- Buttons 1–6 have explicit left/right/up/down mapping.
- Description has nextFocusDown/Right/Left pointing to actionButton1.

Previews
- activity_content_info_preview.xml
- activity_content_info_scaled_preview.xml
- preview_metadata_panel.xml

Build
- cd android_tv_frontend && ./gradlew assembleDebug
