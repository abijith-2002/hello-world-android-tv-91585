# Content Info Safe-Area QA Checklist

- All six action buttons visible and focusable within screen bounds.
- D-pad navigation:
  - Up from description -> timeInfoRow
  - Down from description -> actionButton1
  - Left/Right cycles across action buttons; Up returns to metadataRow (from button1) or description (others).
- No clipping at left/right/top/bottom edges under strict overscan.
- Ripple/focus outlines align with visible bounds.
- Text title and description remain readable; no truncation beyond expected maxLines.
