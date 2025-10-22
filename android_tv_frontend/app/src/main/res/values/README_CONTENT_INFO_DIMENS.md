# Content Info Screen Scoped Dimens (ci_*)

This screen uses scoped `ci_*` dimension tokens to slightly reduce sizes so all UI fits within TV safe area without global scaling.

Adjustments:
- Panel size and margins: `ci_panel_width`, `ci_panel_height`, `ci_margin_start`, `ci_margin_top`
- Typography: `ci_title_text_size`, `ci_meta_text_size`, `ci_desc_text_size`, `ci_system_time_size`, `ci_system_date_size`
- Action row and spacing: `ci_actions_width`, `ci_action_item_width`, `ci_action_space`, `ci_icon_box_*`, `ci_icon_size`, `ci_label_*`
- System date/time margins: `ci_sys_margin_top`, `ci_sys_margin_end`

Focus:
- Explicit `nextFocus*` attributes bound focus within the panel.
- System Date/Time is non-focusable to avoid focus traps.

Tuning:
- If content still overflows on specific devices, reduce `ci_*` values by small increments (e.g., 2–4dp/sp).
