# Dependency Injection Rules

## 1. Framework
- Use **Koin** for dependency injection.
- Use **Jetpack App Startup** to initialize the DI graph in a decentralized manner.

## 2. Decentralized Initialization
- Every module (e.g., `:feature:map:ui`, `:feature:map:data`) must have its own `Initializer` class and its own Koin `module`.
- **NO INTERFACES** for repositories/usecases; rely on **MockK** for mocking classes directly in tests.

## 3. Initialization Graph
Initializers must explicitly define their dependencies in the `dependencies()` method to ensure the graph is built in the correct order:
- `FeatureUiInitializer` depends on `FeatureDomainInitializer`.
- `FeatureDomainInitializer` depends on `FeatureDataInitializer`.
- All feature initializers must eventually depend on `CoreKoinInitializer` (in `:core:common`) to ensure Koin is started before modules are loaded.

## 4. Usage
- Use `loadKoinModules()` within the `Initializer` to register the module's definitions.
- ViewModels should be injected using `koinViewModel()` in Composables.
