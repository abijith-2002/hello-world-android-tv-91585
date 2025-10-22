# Content Info Screen – Scoped Safe-Area Adjustments

Summary
- Introduced values/dimens_content_info.xml (ci_*) and applied them only to activity_content_info.xml.
- Ensured all elements (including six action buttons) fit within TV safe area on strict overscan devices.
- Preserved Nord dark palette and Reddit Sans styling.
- Maintained predictable D‑pad navigation with explicit nextFocus attributes.

Key knobs added
- Horizontal: ci_panel_width, ci_actions_width, ci_action_space, ci_actions_side_inset
- Vertical: ci_margin_top, ci_desc_height, ci_actions_margin_top, ci_bottom_inset
- Typography: ci_title_text_size, ci_meta_text_size, ci_desc_text_size
- System time/date: ci_sys_margin_end, ci_sys_time_width
- Misc: ci_divider_side_margin, ci_channel_name_margin_start, ci_time_slot_width, ci_age_width

Navigation
- metadataRow -> description -> actionButton1 .. actionButton6
- actionButton1 nextFocusUp -> metadataRow

Notes
- Do not globalize ci_*; keep adjustments in this file for Content Info only.
