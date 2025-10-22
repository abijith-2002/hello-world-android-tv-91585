# Content Info Focus & Scale Notes

- Scale-down approach: Screen-scoped via reduced ci_* dimens only (no global theme changes). Ensures all elements fit within TV safe area.
- Primary focus path:
  - Initial: first action button (wired in Activity onCreate), or metadataRow if needed.
  - Up: action buttons -> description -> timeInfoRow -> metadataRow.
  - Left/Right: action buttons cycle horizontally (1..6).
- Non-focusable: systemDateTime (top-right) is non-interactive to avoid DPAD traps.
- Ripple/Focus outline: maintained via view_action_button.xml dimensions; iconBox + label remain centered.
