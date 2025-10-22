# Content Info Safe-Area Scale-Down (TV)

Scope: Only the Content Info screen is compacted to guarantee all elements, including the action buttons, fit within the TV safe area across devices.

How it works:
- Screen-scoped dimens in `app/src/main/res/values/dimens_content_info.xml` (all keys prefixed `ci_`).
- `activity_content_info.xml` references the `ci_*` dimens for widths, heights, text sizes, and spacings.
- Focus rings and ripple targets remain aligned because child views are not scaled via transforms.

What to tune (incremental changes recommended: 2–4dp/sp):
- Horizontal fit: `ci_panel_width`, `ci_side_inset`, `ci_action_item_width`, `ci_action_space`
- Vertical fit: `ci_title_margin_top`, `ci_meta_row_margin_top`, `ci_desc_margin_top`, `ci_actions_margin_top`, row heights
- Typography: `ci_title_text_size`, `ci_meta_text_size`, `ci_desc_text_size`, `ci_system_time_size`, `ci_system_date_size`

Do NOT:
- Change global app dimens or themes for this adjustment.
- Apply `scaleX/scaleY` to focusable children (keeps focus/ripple geometry correct).

Preview:
- Use `activity_content_info_preview.xml` to view the compact layout in Android Studio Layout Preview.

Notes:
- If extreme overscan is encountered, modestly reduce `ci_action_item_width` and `ci_action_space` first.
