# Content Info Safe Area Notes

- The Content Info screen uses scoped dimens under `res/values/dimens_content_info.xml` (ci_* tokens).
- These reduce layout paddings, heights, and text sizes so all elements fit within TV safe area.
- Focus navigation is explicitly bounded via `nextFocus*` attributes to keep D‑pad flow within the panel.
- If further tuning is needed, prefer adjusting ci_* tokens instead of applying a non‑1.0 scale.
- An optional wrapper view (`tvSafeWrapper`) exists in `activity_content_info.xml` for temporary QA scale (kept at 1.0f by default).
