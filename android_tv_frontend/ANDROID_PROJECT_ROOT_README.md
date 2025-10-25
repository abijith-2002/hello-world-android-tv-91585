# Android TV Frontend - DPAD_UP Focus Behavior

PUBLIC_INTERFACE
When focus is on the first content rail row and the user presses DPAD_UP, focus is programmatically moved to the top menu's first button.

Implementation details:
- Activity: HomeActivity
- Behavior:
  - Rows r > 0: DPAD_UP maps to the corresponding index on row r-1 (with clamping).
  - First row r == 0: DPAD_UP requests focus on the top menu’s default button (menuHome).
- Stable IDs:
  - Top menu container: topMenu
  - Default button: menuHome
  - Rails container: railsContainer
- Layouts:
  - activity_home.xml uses an include for the top menu (include_top_menu.xml)
  - view_rail.xml defines railTitle, railScroll, railRow with nextFocusUp hints to topMenu
  - view_thumb_card.xml contains thumbImage, overlayGrad, thumbTitle

No meta/config files were changed. The Nord dark theme and Reddit Sans font are preserved via existing styles.
