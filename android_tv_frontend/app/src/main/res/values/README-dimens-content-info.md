# Content Info dimens (ci_*) notes

- ci_channel_letter_spacing: tuned to avoid right-edge clipping in compact mode.
- ci_time_label_letter_spacing: ensures tag text fits inside the rounded container without jitter.
- Always tweak ci_* values in small steps (1–2dp/sp) and validate with previews:
  - activity_content_info_preview.xml
  - activity_content_info_scaled_preview.xml
  - preview_action_buttons_row.xml
