# Content Info Safe-Area Notes

- This screen uses scoped ci_* dimens in values/dimens_content_info.xml.
- Do not replace ci_* with global dimens.
- If UI clips on a specific TV, adjust:
  - Horizontal fit: ci_panel_width, ci_actions_width, ci_action_space
  - Vertical fit: ci_desc_height, ci_actions_margin_top, ci_meta_row_margin_top
  - Typography: ci_title_text_size, ci_meta_text_size, ci_desc_text_size
- D-pad focus: metadataRow -> timeInfoRow -> description -> action buttons (1..6).
