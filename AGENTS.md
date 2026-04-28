# AI Agent Instructions for CineScope

Welcome to the CineScope codebase! You are an AI agent helping to build a unified Android app for tickets and streaming.

## 🏗️ Architecture & Component Layers
This application follows a strict unidirectional data flow and clean architecture principles:
- **Presentation Layer** (`/presentation/`): Contains ViewModels. ViewModels hold state (`StateFlow`) and UI side-effects (`SharedFlow`). **Never** put business logic or ViewModel references directly inside `@Composable` functions. Use state hoisting.
- **UI Layer** (`/ui/`): Jetpack Compose screens, theme, and components. Use full-screen composables that accept state and lambda callbacks for events.
- **Navigation** (`/ui/navigation/`): Compose Navigation Graph.
- **Data Layer** (`/data/`): Repositories, API interfaces (Retrofit), session management (DataStore).
- **DI Layer** (`/di/`): Hilt modules.

## 🚀 Key Patterns & Rules
1. **Dependency Injection**: ALWAYS use Dagger Hilt. Never instantiate repositories manually.
2. **Coroutines**: Use `viewModelScope` or `lifecycleScope`. **NEVER** use `GlobalScope`.
3. **Auth Guards**: Authentication checks (for checking out or playback) MUST happen in the NavGraph layer. Unauthorized users should be redirected via a bottom sheet or modal without breaking their backstack.
4. **Resilience**: Every screen MUST handle loading states and error states (e.g., API failures).
5. **Layouts**: Ensure Adaptive Layouts are respected (phone and tablet).

## 🧰 Typical Developer Workflow
- **Build**: `./gradlew assembleDebug`
- **Unit Tests**: `./gradlew testDebugUnitTest`
- **Lint**: `./gradlew lintDebug`
- **Dependency Updates**: We use `gradle/libs.versions.toml` for dependency management. If you add a library, add it there first, then reference it in `app/build.gradle.kts`. Do NOT add dependencies without asking first.

## ⚠️ Important Restrictions
- **No autonomous deletions**: Never delete files without asking the user.
- **No hardcoded secrets**: Never hardcode API base URLs or tokens.
- **No plain-text storage**: Use DataStore for auth token persistence. Never use raw SharedPreferences.
- **Backend separation**: We only consume APIs. Do not build an admin interface or CMS in this app. Payment handling is out of scope.

Refer to `PRD.md` and `IMPLEMENTATION_PLAN.md` for product and delivery goals.

