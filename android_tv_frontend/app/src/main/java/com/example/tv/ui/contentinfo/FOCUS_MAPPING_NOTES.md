# Focus Mapping Notes

The Content Info screen uses explicit `android:nextFocus*` attributes to ensure stable DPAD navigation within the compact layout:
- Description:
  - nextFocusDown → actionButton1
  - nextFocusLeft/Right → actionButton1
- Time row:
  - nextFocusDown → description
  - nextFocusLeft/Right → description
- Metadata row:
  - nextFocusDown → timeInfoRow
  - nextFocusLeft/Right → description
- Action buttons:
  - 1→6 have explicit left/right mapping
  - nextFocusUp → description
  - nextFocusDown → itself (stay on row)

System date/time is non-focusable and blocks descendants to avoid traps.
