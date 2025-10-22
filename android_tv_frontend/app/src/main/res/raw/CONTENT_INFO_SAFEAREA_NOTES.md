# Content Info screen: scoped scale-down and safe-area notes

Scope
- Only the Content Info screen uses compact dimens (values/dimens_content_info.xml, prefix ci_*).
- No global theme or other screens are affected.

Why dimens (vs global scale)
- Using per-screen dimens avoids focus/ripple misalignment that can happen with root scale transforms.
- D-pad focus rings and ripple masks remain aligned to actual view bounds.

How to tweak for your device
- If action buttons approach the bottom edge, first reduce ci_action_space or ci_actions_margin_top.
- If the title or metadata clip near the top, reduce ci_margin_top or ci_title_text_size.
- Prefer changing spacing before changing text sizes to preserve readability on TV.

Quick knobs
- Horizontal fit: ci_panel_width, ci_actions_width, ci_action_space
- Vertical fit: ci_panel_height, ci_margin_top, ci_desc_height, ci_actions_margin_top
- Typography: ci_title_text_size, ci_meta_text_size, ci_desc_text_size, ci_system_time_size

Verification
- Ensure all 6 action buttons are fully visible and focusable.
- Verify D-pad navigation cycles across all buttons and back to metadata.
- Check that focus rings and ripple effects align with the visual elements.
