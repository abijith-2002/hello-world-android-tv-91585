# App Module Notes

- DPAD_UP from the first content row moves focus to the top menu (menuHome).
- Implemented in HomeActivity with explicit OnKeyListener mapping and stable IDs.
- Layouts: activity_home.xml (includes include_top_menu.xml), view_rail.xml, view_thumb_card.xml.
- This preserves Nord dark theme and Reddit Sans via existing styles.
