# Home Screen Per-Section Loader State

PUBLIC_INTERFACE
This document explains the state model used by the Home screen to implement per-section loaders.

- HomeViewModel exposes:
  - StateFlow<Map<HomeCategory, RailState>> called `state`
  - Each RailState contains:
    - `title`: Display title for the section
    - `isLoading`: Whether the section is currently loading (drives inline loader chip)
    - `error`: Optional error message for the section (non-blocking)
    - `items`: List<ContentItem> to render in the rail

- Behavior:
  - No global/full-screen loader is used.
  - Each rail shows its own inline loader chip while loading.
  - If a rail fails, an error marker is appended to the title without blocking the rest.
  - Empty lists after loading show an inline "No items" text rather than blocking UI.

- Data flow:
  - CategoryRepository.fetchCategory(HomeCategory): Result<List<ContentItem>>
  - Repository returns empty or fallback items instead of throwing to keep UI responsive.

- Layout:
  - activity_home.xml has a top menu, banner, and scrollable rails container.
  - view_rail.xml contains a per-section loader chip (railLoadingChip), fallback progress view, and empty label.

This model ensures fast progressive rendering: sections appear as they load independently, improving TV UX.
