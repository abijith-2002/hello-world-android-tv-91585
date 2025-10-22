# Dev quick-check (Content Info TV-safe layout)

- Build: ./gradlew :app:assembleDebug (from android_tv_frontend/)
- Navigate: Splash -> Home -> Select any card (DPAD_CENTER) to open Content Info.
- Verify:
  - All elements including six action buttons are fully visible (no clipping).
  - D-pad navigation: Up/Down between time row and actions; Left/Right across buttons.
  - Focus rings align with elements; ripple masks fit rounded corners.
  - Nord dark and Reddit Sans styling maintained.
