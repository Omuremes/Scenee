# CineScope — Unified Tickets & Streaming App
**Platform:** Android (Kotlin + Jetpack Compose) | **Version:** 1.0 | **Date:** April 2026

---

## 1. Overview

| | |
|---|---|
| **WHAT** | A single mobile app for purchasing tickets to movies, concerts, and standup events, plus in-app streaming of series content. |
| **WHO** | Entertainment-seeking mobile users who want a unified platform for booking live events and watching series on-the-go. |
| **WHY** | Users currently juggle multiple apps for tickets and streaming — this unified experience reduces friction and increases engagement. |

---

## 2. Core Features

| # | Feature | Description                                                                                                                                | Example                                                                                                                                                                           |
|---|---------|--------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 | **Event Discovery & Detail** | Home screen shows movies, concerts, and standup events as image cards. Tapping opens a detail page with description, dates, and pricing.   | User taps a concert card → sees artist bio, venue, and available dates with ticket tiers.                                                                                         |
| 2 | **Seat Selection (Hall View)** | Interactive square hall grid with color-coded seats: 🟢 Green = available, 🟡 Yellow = booked/pending, 🔴 Red = unavailable. Tap to select. | User picks "June 15 show" → sees 200-seat hall, selects 2 green seats → proceeds to checkout.                                                                                     |
| 3 | **Series Streaming** | Dedicated Series tab with show cards. Detail page shows synopsis + Watch button. Episode list grouped by season. Tap episode to play inline. | User goes to Series → picks "Dark" → sees synopsis + watch button, taps watch button → sees S1,S2,S3 filters and corresponding episodes under and taps S1E1 → video player opens. |
| 4 | **My Tickets** | Personal ticket wallet showing all purchased tickets with event name, date, seat numbers                                                   | User opens My Tickets → sees "Concert: Radiohead, June 15 – Row C, Seat 12".                                                                                                      |
| 5 | **Auth-Gated Actions** | Unauthorized users can browse freely but hit an auth wall when attempting to buy a ticket or watch a series. Prompted to log in or register. | Guest taps "Buy Ticket" → bottom sheet appears: "Sign in to continue" with login/register options.                                                                                |

---

## 3. Non-Goals

> These boundaries are critical for keeping the agent focused and preventing scope creep.

| What We Won't Build | Why / Clarification |
|---|---|
| ❌ No SaaS / Subscriptions | No recurring billing, premium tiers, or freemium gates. All content access is one-time or role-based. |
| ❌ No Admin CMS | No in-app dashboard for uploading events, managing seat maps, or publishing series. That's a backend concern. |
| ❌ No Social Features | No reviews, ratings, friend activity, or sharing to social platforms. |
| ❌ No Live Streaming | Series content is pre-recorded VOD only. No live broadcasts or real-time event streaming. |
| ❌ No Payment Processing | Payment handling is out of scope for MVP. The app prepares an order; a backend handles the charge. |
| ❌ No Offline Downloads | Series episodes cannot be downloaded for offline viewing. |

---

## 4. Technical Constraints

| Layer            | Technology / Library                               |
|------------------|----------------------------------------------------|
| **Language**     | Kotlin                                             |
| **UI Framework** | Jetpack Compose + Adaptive Layouts                 |
| **UI State**     | ViewModel (`androidx.lifecycle`)                   |
| **Navigation**   | Navigation Component (Compose Nav Graph)           |
| **Architecture** | Repository Pattern + MVVM + Separation of Concerns |
| **DI**           | Hilt (Dagger-Hilt)                                 |
| **Preferences**  | DataStore (auth token, user prefs)                 |
| **Networking**   | Retrofit 2 + OkHttp + Gson/Moshi                   |
| **Min SDK**      | API 26 (Android 8.0)                               |
| **Target SDK**   | API 35                                             |

---

## 5. Agent Rules

### ✅ ALWAYS
- Edit existing code files in-place — never rewrite from scratch unless explicitly asked
- Follow the established architecture: ViewModel → Repository → DataSource (Retrofit / Room / DataStore)
- Inject dependencies via Hilt — no manual instantiation of repositories or ViewModels
- Use Compose state hoisting — state lives in ViewModel, not in composables
- Add meaningful error states and loading indicators on every screen
- Preserve adaptive layout support — test changes on both phone and tablet configurations
- Keep auth guard logic in NavGraph — not scattered in individual screens
- Sync WorkManager tasks after any ticket purchase or auth change

### 🟡 ASK FIRST
- Before creating a new file (new screen, repository, module, data class)
- Before deleting any file from the project
- Before adding a new third-party dependency to `build.gradle`
- Before changing the navigation graph structure or bottom nav destinations
- Before modifying Room schema (requires migration strategy)
- Before changing the Hilt module bindings or DI graph
- Before refactoring a ViewModel's state model

### 🚫 NEVER
- Never delete files autonomously — always ask first
- Never hardcode API base URLs or auth tokens in source files
- Never put business logic inside `@Composable` functions — only in ViewModel/Repository
- Never use `GlobalScope` for coroutines — use `viewModelScope` or `lifecycleScope`
- Never bypass auth guard — all booking and streaming actions must check auth state
- Never store sensitive data (tokens) in SharedPreferences — use DataStore with encryption
- Never call Retrofit directly from a ViewModel — always go through Repository
