# Content Info Screen — Manual Verification

Run the app and navigate:
- Splash → Home → Select any card (DPAD_CENTER) → Content Info

Verify:
- All six action buttons are fully visible within the TV safe area.
- Focus path: metadataRow → timeInfoRow → description → actionButton1..6.
- System time/date (top-right) does not receive focus.
- Visuals keep Nord dark palette and Reddit Sans typography.

Where to tweak:
- res/values*/dimens_content_info.xml (ci_* tokens)
- res/layout/activity_content_info.xml

Previews:
- res/layout/activity_content_info_preview.xml
- res/layout/activity_content_info_scaled_preview.xml
- res/layout/preview_metadata_panel.xml
