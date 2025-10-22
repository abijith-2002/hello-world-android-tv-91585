# Safe-Area Resources (Content Info Screen)

Files added/updated for scoped scale-down:
- res/values*/dimens_content_info.xml — ci_* compact tokens (widths, paddings, font sizes)
- res/layout/activity_content_info.xml — uses ci_* tokens; bottom padding via ci_bottom_inset
- res/layout/activity_content_info_preview.xml — full canvas preview
- res/layout/activity_content_info_scaled_preview.xml — 0.9 scale preview
- res/layout/preview_metadata_panel.xml — quick metadata panel preview
- app/src/main/java/.../ContentInfoActivity.kt — ensures initial focus on actionButton1
- System date/time block non-focusable with descendants focus blocking

Adjustments:
- Widen actions: increase ci_actions_width and/or reduce ci_action_space
- Typography: ci_title_text_size and ci_meta_text_size
- Bottom safety: ci_bottom_inset
- Description breathing room: ci_desc_width, ci_desc_end_padding

Scope:
- Changes only affect Content Info screen. No global theme or other screens changed.
