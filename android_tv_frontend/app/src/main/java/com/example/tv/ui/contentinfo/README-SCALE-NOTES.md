# Content Info Scoped Scale (TV Safe Area)

This screen uses screen-scoped dimens (ci_*) to reduce overall sizing ~0.9–0.92x so the entire layout fits inside TV safe areas on a variety of devices without global theme changes.

Why dimens over view scale transforms:
- Keeps focus ring and ripple hit targets aligned with visual bounds.
- Avoids anti-aliasing artifacts from scaling large text and borders.
- Limits scope to this screen only.

Where to adjust:
- res/values/dimens_content_info.xml controls widths, heights, text sizes, and margins.
- layout/activity_content_info.xml consumes only @dimen/ci_* for sizing.
- layout/view_action_button.xml consumes @dimen/ci_* for icons and labels.

D‑pad navigation:
- Action buttons declare explicit nextFocusLeft/Right to ensure consistent navigation after size changes.

If additional reduction is needed for a specific device family:
- Create a qualified dimens file (e.g., values-sw720dp/dimens_content_info.xml) and reduce ci_* by a small step.
