# Content Info Safe-Area Notes

Scope
- This file documents the safe-area scale-down applied only to the Content Info screen.

Where to tweak
- res/values*/dimens_content_info.xml contain all ci_* tokens.
- Key tokens: ci_panel_width/height, ci_actions_width, ci_action_item_width/height, ci_action_space, ci_title_text_size, ci_meta_text_size.
- Bottom inset: ci_bottom_inset; End padding for description: ci_desc_end_padding.
- Panel side padding: ci_panel_side_padding.

Focus behavior
- System time/date container is non-focusable to avoid focus traps.
- Action buttons 1–6 remain fully visible and focusable. Initial focus is set to button 1 in code.

Previews
- activity_content_info_preview.xml
- activity_content_info_scaled_preview.xml
- preview_metadata_panel.xml

Notes
- Keep changes scoped to Content Info only. Do not alter global themes or other screens.
- Maintain Nord dark and Reddit Sans styling as already configured.
