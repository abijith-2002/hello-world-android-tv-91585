# Content Info Screen — Scoped Scale-Down

- Uses ci_* dimens to reduce overall scale for safe-area fit on TV without affecting other screens.
- Focus rings and ripple targets align with elements; action buttons have D-pad scaling on focus.
- System date/time block is non-focusable and blocks descendants to avoid focus traps.
- Initial focus is forced to the first action button in ContentInfoActivity.onCreate.

Where to tweak (screen-only):
- res/values*/dimens_content_info.xml
- res/layout/activity_content_info.xml

Previews:
- res/layout/activity_content_info_preview.xml
- res/layout/activity_content_info_scaled_preview.xml
- res/layout/preview_metadata_panel.xml
