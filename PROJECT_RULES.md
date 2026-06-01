# Project Rules

## 1. Module Structure
The project follows a **Feature-based Multi-module** architecture.

- `:app`: Application entry point, global navigation, and dependency injection root.
- `:core:network`: Retrofit setup, common interceptors.
- `:core:ui`: Shared design system components, themes, and base composables.
- `:core:common`: Shared utilities, base classes, and domain models.
- `:feature:[name]`: Individual features (e.g., `:feature:map`, `:feature:search`).
- `:data:[name]`: Data source implementations and repositories (e.g., `:data:places`, `:data:history`).

## 2. Layering Guidelines
Every feature module must strictly follow these layers:

1.  **API (Retrofit)**: Interface definitions for network calls.
2.  **Repository**: Orchestrates data from API/Database. Functions must be `suspend` and return a `Result<T>` or a custom state object containing `Loading`, `Success`, and `Error`.
3.  **UseCases**: Single-responsibility domain logic.
4.  **ViewModel**: Manages UI state using `StateFlow`. Handles user intents.
5.  **Composable Screen**: 
    *   Orchestrates the UI.
    *   Handles navigation.
    *   Collects state from ViewModel.
    *   Passes state to Content Composable.
6.  **Composable Content**: 
    *   Pure exhibition (Stateless).
    *   Only receives data and callbacks.
    *   **MUST** have a `@Preview`.
    *   **MUST** have a preview screenshot test.

## 3. Testing Requirements
- **Unit Tests**: Mandatory for `Repository`, `UseCase`, and `ViewModel`.
- **Screenshot Tests**: Mandatory for every `Composable Content` (using Previews).
- **GMD (Gradle Managed Device) Tests**: Mandatory for `Composable Screen` to verify orchestration and flows.
- **Mocking**: Use `MockK` or `Mockito` for dependencies.

## 4. Coding Standards
- Kotlin-first.
- Use `Coroutines` and `Flow` for asynchrony.
- `Hilt` for Dependency Injection.
- `Retrofit` for API calls.
- `Compose` for all UI.
