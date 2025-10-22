# Safe-Area Tuning for Content Info

- Adjust only ci_* dimens in:
  - res/values/dimens_content_info.xml
  - res/values-v21/dimens_content_info.xml
- Start with spacing before typography (action spacing, panel width, margins).
- Validate with preview layouts:
  - activity_content_info_preview.xml
  - activity_content_info_scaled_preview.xml
  - preview_action_buttons_row.xml
- Keep focus path intact:
  - metadataRow -> timeInfoRow -> description -> actionButton1..6
