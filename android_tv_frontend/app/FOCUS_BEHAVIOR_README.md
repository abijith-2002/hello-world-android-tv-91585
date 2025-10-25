# Focus Behavior Overview

- First-row DPAD_UP -> Top menu (menuHome).
- Implemented in HomeActivity.handleDpadUpWithinRails.
- Layout IDs used: topMenu, menuHome, railsContainer, railRow, railScroll, railTitle, thumbImage, overlayGrad, thumbTitle.
- XML hints:
  - view_rail.xml sets nextFocusUp to @id/topMenu.
  - include_top_menu.xml sets nextFocusDown to @id/railsContainer.
