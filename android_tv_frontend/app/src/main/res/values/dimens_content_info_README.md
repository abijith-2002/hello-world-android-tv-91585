# Content Info Scoped Dimens (ci_*)

This module defines screen-scoped dimens used only by activity_content_info.xml to slightly reduce overall UI scale (~0.9–0.92x) so all elements, including action buttons, fit within the TV safe area.

Key points:
- No global theme or other screens are changed.
- Focus rings and ripple targets remain aligned because we reduce sizes/margins, not via a runtime transform.
- Update only activity_content_info.xml and view_action_button.xml to use @dimen/ci_* values.

Files:
- values/dimens_content_info.xml
- layout/activity_content_info.xml
- layout/view_action_button.xml
