# Architecture Rules

## 1. Module Structure
The project follows a **Feature-based Clean Architecture**. To prevent any single module from becoming a bottleneck, we modularize by **Feature Domains**.

- `:app`: Application entry point, global navigation.
- `:core:network`: Retrofit setup, common interceptors.
- `:core:ui`: Shared design system components, themes, and base composables.
- `:core:common`: Shared utilities and base classes.
- `:core:domain`: **Global** Domain Models (e.g., `User`, `Location`).
- `:feature:[name]:ui`: UI layer (ViewModels, Screens, Content).
- `:feature:[name]:domain`: Feature-specific business logic (**UseCases**, **Domain Models**). (Pure Kotlin/Java).
- `:feature:[name]:data`: Data source implementations for that feature (**Repositories**, API interfaces).

## 2. Layering Guidelines
Every feature follows a strict dependency flow: **Feature -> Domain <- Data**.

1.  **API (Data Layer)**: Retrofit interface definitions in `:data:[name]`.
2.  **Repository (Data Layer)**: Concrete classes in `:data:[name]`. No interfaces required. Functions must be `suspend` and return `DataState<T>`.
3.  **UseCases (Domain Layer)**: Single-responsibility domain logic in `:domain:[name]`.
4.  **ViewModel (Feature Layer)**: Manages UI state using `StateFlow`. Handles user intents. Receives dependencies via constructor.
5.  **Composable Screen (Feature Layer)**: Orchestrates the UI and navigation.
6.  **Composable Content (Feature Layer)**: Pure exhibition (Stateless).
