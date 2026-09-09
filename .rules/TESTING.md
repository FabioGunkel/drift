# Testing Rules

## 1. Unit Tests
- **Mandatory** for:
  - `Repository`
  - `UseCase`
  - `ViewModel`
- Use **MockK** to mock dependencies.
- **Assertions**:
  - Every test **MUST** assert something.
  - Validate function returns for all returning methods.
  - For non-returning methods (side effects), assert mocked calls using **capturing slots** and verify **request counts** (e.g., `coVerify(exactly = 1)`).

## 2. UI Tests
- **Screenshot Tests**: Mandatory for every `Composable Content`. These should be generated/verified using Compose Previews.
- **GMD (Gradle Managed Device) Tests**: Mandatory for every `Composable Screen` to verify orchestration and navigation flows.
  - **Assertions**:
    - Assert semantic properties like `testTagAsResourceId` for element identification.
    - Assert Composable callbacks are triggered correctly.
    - Use **capturing slots** to verify data passed to the ViewModel.
    - Assert expected ViewModel function calls occur during UI interactions.

## 3. Coverage
- **Rule**: Minimum **80% code coverage** for the entire project.
- **Tool**: Use **Kover** for measuring and enforcing coverage.
- Focus high coverage efforts on domain logic, data mapping, and state transitions in ViewModels.