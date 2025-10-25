# Developer Notes: DPAD_UP Focus Mapping

- First-row DPAD_UP -> Top menu (menuHome) is implemented in:
  - app/src/main/java/com/example/tv/ui/home/HomeActivity.kt (handleDpadUpWithinRails)
- Default focus hints:
  - view_rail.xml sets nextFocusUp on railScroll and railRow to @id/topMenu.
  - include_top_menu.xml defines menu items with nextFocusDown to @id/railsContainer.
- Stable IDs:
  - Top menu: topMenu, first button: menuHome
  - Rails container: railsContainer
  - Rail: railTitle, railScroll, railRow
  - Card: thumbImage, overlayGrad, thumbTitle

Maintenance tips:
- If you add a new first row type, ensure its item view sets an OnKeyListener for KEYCODE_DPAD_UP to delegate to handleDpadUpWithinRails(v, FIRST_CATEGORY).
- Keep menuHome focusable and visible to ensure requestFocus() succeeds.
