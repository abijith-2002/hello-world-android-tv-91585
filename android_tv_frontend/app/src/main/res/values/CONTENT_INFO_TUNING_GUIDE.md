# Content Info Safe-Area Tuning Guide

This screen uses scoped dimens (ci_*) to keep all UI within TV safe areas:
- Prefer spacing changes before typography changes for readability.
- Verify D-pad focus order and ripple alignment after tweaks.

Primary knobs:
- Horizontal: ci_panel_width, ci_actions_width, ci_action_space, ci_channel_name_margin_start
- Vertical: ci_margin_top, ci_desc_height, ci_actions_margin_top, ci_bottom_inset
- Typography: ci_title_text_size, ci_meta_text_size, ci_desc_text_size, ci_system_time_size
- Time/date: ci_sys_time_width, ci_sys_margin_end
- Misc: ci_divider_side_margin, ci_time_slot_width, ci_age_width

Test flow:
- Ensure all six action buttons fully visible.
- Focus flows metadata -> actions -> back to metadata.
- No clipping on edges; focus/ripple outlines align with elements.
