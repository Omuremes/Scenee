# CineScope Implementation Plan

## Current State

- The Android app is still the default Jetpack Compose starter.
- The frontend HTML prototypes already cover the intended UX for discovery, auth, tickets, profile, and series playback flows.
- The PRD is the product source of truth and should drive implementation decisions.

## Delivery Strategy

### Phase 1: Foundation

- Add the Compose dependencies needed for a real app shell.
- Replace the starter screen with a branded application scaffold.
- Establish bottom navigation for Home, Series, Tickets, and Profile.
- Port the prototype's visual language into the app theme.
- Create initial fake-data driven screens so the app is navigable end-to-end.

### Phase 2: Architecture

- Introduce a proper package structure for `data`, `domain`, `navigation`, and `presentation`.
- Move screen state into ViewModels.
- Add repository interfaces and fake implementations for development.
- Add Hilt, DataStore, and app session management.

### Phase 3: Discovery

- Build the home feed with reusable cards for movies, concerts, stand-up, and series.
- Add detail screens for movies, concerts, stand-up, and series.
- Add loading, empty, and error states.

### Phase 4: Authentication

- Implement sign in and sign up flows.
- Persist auth state with DataStore.
- Gate buying and playback actions through navigation-level auth checks.

### Phase 5: Ticketing

- Implement session/date selection.
- Build a seat-map feature with available, selected, pending, and unavailable states.
- Add booking summary and a mock purchase flow.
- Save purchased tickets to the ticket wallet.

### Phase 6: Series Playback

- Build the series detail flow with seasons and episode lists.
- Add a player experience for episode playback.
- Reuse the same auth guard used for ticket purchases.

### Phase 7: Quality

- Improve adaptive layouts for larger screens.
- Add background sync hooks where auth or ticket state changes.
- Add UI and integration tests for navigation, auth gating, ticket purchase, and playback access.

## Recommended Implementation Order

1. Foundation and app shell
2. Discovery screens
3. Auth flow and session state
4. Ticket flow
5. Series playback
6. Profile polish and testing
