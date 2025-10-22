# Content Info Safe-Area Compact Strategy

- All compact sizing is scoped to ci_* dimens in:
  - res/values/dimens_content_info.xml (base)
  - res/values-v21/dimens_content_info.xml
  - res/values-sw600dp/dimens_content_info.xml
  - res/values-sw720dp/dimens_content_info.xml
- Layout: res/layout/activity_content_info.xml
- Do not modify global dimens; adjust only ci_* values.
- Validate with: activity_content_info_preview.xml, activity_content_info_scaled_preview.xml, preview_action_buttons_row.xml.

Focus:
- metadataRow -> timeInfoRow -> description -> actionButton1..6
- systemDateTime is non-focusable to avoid traps.

Goal:
- Ensure entire UI (including six action buttons) remains within TV safe area with intact focus and ripple alignment.
