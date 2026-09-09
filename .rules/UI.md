# UI and Compose Rules

## 1. Composition
- **Composable Screen**: 
    - Purpose: Orchestration, navigation, and state collection.
    - Connects the `ViewModel` to the `Content`.
- **Composable Content**: 
    - Purpose: Pure exhibition (Stateless).
    - Receives data and callbacks.
    - **MUST** be stateless.
    - **MUST** have a `@Preview`.

## 2. Preview and Screenshot Tests
- Every `Composable Content` must have at least one `@Preview`.
- Screenshot tests will be automatically derived from these Previews.

## 3. Design System
- Use the theme defined in `:core:ui`.
- All shared components (buttons, text styles) should reside in `:core:ui`.
