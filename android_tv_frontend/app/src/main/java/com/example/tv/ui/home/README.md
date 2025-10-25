DPAD_UP from the first content row is handled in HomeActivity.handleDpadUpWithinRails.
- First row (index 0): requests focus on menuHome.
- Other rows: moves to same index in the row above with clamping.
IDs used: topMenu, menuHome, railsContainer, railRow, railScroll, railTitle.
