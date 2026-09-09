# Coding Standards

## 1. Language
- **Kotlin** first.
- Use modern Kotlin features (sealed classes, data objects, extension functions).

## 2. Asynchrony
- Use `Coroutines` for background tasks.
- Use `Flow` and `StateFlow` for data streams and UI state.

## 3. Networking
- Use `Retrofit` for all API communication.

## 4. Documentation
- Document complex domain logic or non-obvious architecture decisions within the code.

## 5. Security
- **Never** check API keys or sensitive strings into Git.
- Use the **Secrets Gradle Plugin** to read keys from properties files.
- All environment-specific properties files MUST reside in the **`/.secrets/`** directory.
- The **`/.secrets/`** directory MUST be ignored in `.gitignore`.
- Use **GitHub Secrets** for all CI/CD operations, injecting them into the `.secrets/` path before building.
