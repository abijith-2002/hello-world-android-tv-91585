# Android TV Frontend - Focus Navigation Update

PUBLIC_INTERFACE
When focus is on the first content rail row, pressing DPAD_UP moves focus to the top menu's first button.

Key details:
- Entry point: HomeActivity (com.example.tv.ui.home.HomeActivity)
- Stable IDs used:
  - Top menu container: topMenu (in include_top_menu.xml)
  - Default top menu button: menuHome
  - Rails container host: railsContainer
  - Rail layout: view_rail.xml (railTitle, railScroll, railRow)
  - Card layout: view_thumb_card.xml (thumbImage, overlayGrad, thumbTitle)
- Behavior:
  - For rows r > 0: DPAD_UP moves focus to corresponding index in row r-1 (clamped).
  - For the first row (r == 0): DPAD_UP requests focus on menuHome.

Styling remains consistent with the Nord dark theme and Reddit Sans font as configured in existing resources.
