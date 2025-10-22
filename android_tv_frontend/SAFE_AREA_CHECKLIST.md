# Content Info Safe-Area Checklist

- All six action buttons are fully visible and within the TV safe area.
- No clipping on left, right, or bottom edges.
- Focus path is stable: metadataRow → timeInfoRow → description → buttons 1–6.
- System date/time (top-right) is non-focusable (cannot receive focus).
- Ripple/focus rings align with visual elements (no offset).
- Typography remains readable with Reddit Sans and Nord dark palette.
- Only Content Info screen uses the compact ci_* dimens, no global changes.

Where to adjust:
- res/values*/dimens_content_info.xml (ci_* tokens)
- res/layout/activity_content_info.xml

Previews:
- res/layout/activity_content_info_preview.xml
- res/layout/activity_content_info_scaled_preview.xml
- res/layout/preview_metadata_panel.xml
