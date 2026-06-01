# Testing Rules

## 1. Unit Tests
- **Mandatory** for:
    - `Repository`
    - `UseCase`
    - `ViewModel`
- Use **MockK** to mock dependencies. Since we don't use interfaces, MockK's ability to mock concrete classes is essential.

## 2. UI Tests
- **Screenshot Tests**: Mandatory for every `Composable Content`. These should be generated/verified using Compose Previews.
- **GMD (Gradle Managed Device) Tests**: Mandatory for every `Composable Screen` to verify orchestration and navigation flows.

## 3. Coverage
- Ensure high coverage for domain logic and state transitions in ViewModels.
