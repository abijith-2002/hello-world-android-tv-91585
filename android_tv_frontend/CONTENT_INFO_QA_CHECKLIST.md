# Content Info QA Checklist

Safe-area fit:
- All text, chips, and all six action buttons are fully visible (no clipping).
- System time/date are visible and do not overlap content.

Focus and navigation:
- DPAD Down from metadata → time row → description → first action button.
- Left/Right cycles across six buttons; Up returns to description.
- System Date/Time never receives focus.

Visual:
- Focus rings and ripple states correctly wrap the focused element.
- No subpixel blurriness on icons or text.
- Background image remains centered, gradient overlay intact.

Scaling:
- Default ci_* dimens fit content on common TV profiles.
- If needed for QA, tvSafeWrapper scale can be temporarily set to 0.92f–0.88f.
