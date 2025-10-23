# Android TV Frontend - Home Categories API

This app fetches home page categories from a backend defined by the provided API doc. Each endpoint returns an array of objects with:
- name: String
- poster: Absolute URL String to image

Configured categories:
- /api/trending
- /api/continue_watching
- /api/action
- /api/family
- /api/comedy
- /api/horror
- /api/drama

Configuration:
- BuildConfig field API_BASE_URL controls the Retrofit base URL.
- To change endpoints or add categories, edit HomeCategory in `app/src/main/java/com/example/tv/data/api/ApiModels.kt`.

Image loading:
- Coil is used with placeholders and error images.

Theming:
- Uses Nord dark theme and Reddit Sans fonts already present in the project.

