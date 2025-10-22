# Content Info Layout Notes

- This layout uses scoped dimens (ci_*) from values/dimens_content_info.xml to fit within TV safe areas.
- Do not globalize these values; adjust only ci_*.
- Quick knobs:
  - Horizontal: ci_panel_width, ci_actions_width, ci_action_space
  - Vertical: ci_margin_top, ci_desc_height, ci_actions_margin_top, ci_bottom_inset
  - Typography: ci_title_text_size, ci_meta_text_size, ci_desc_text_size
- Verify:
  - All six action buttons are visible and focusable.
  - D-pad flows: metadata -> actionButton1 ... -> actionButton6 -> back to description.
  - Ripple/focus outlines align with visible bounds.
