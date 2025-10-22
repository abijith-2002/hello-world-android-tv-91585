# How to Test the Content Info Screen

1) Build and run the app.
2) Navigate: Splash → Home → use DPAD to any card → press DPAD_CENTER.
3) Verify:
   - All six action buttons are fully visible (no clipping).
   - Focus path: metadataRow → timeInfoRow → description → action buttons (1→6).
   - System date/time (top-right) never receives focus.
   - DPAD_LEFT/RIGHT from time row stays in the panel (returns to description).
   - DPAD_DOWN from description goes to actionButton1; DPAD_UP from row returns to description.
4) Visual style:
   - Nord dark palette and Reddit Sans are maintained.
   - Focus rings and ripple align with elements.

If adjustments are needed, update only ci_* tokens in:
- android_tv_frontend/app/src/main/res/values*/dimens_content_info.xml
