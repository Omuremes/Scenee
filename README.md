# CineScope 🎬

CineScope is a modern Android application for discovering movies and local events (afisha), built with a clean architecture approach and the latest Android technologies.

The app allows users to explore trending movies, view detailed information about films and actors, read and leave reviews, and browse upcoming events such as cinema screenings, concerts, and shows.

---

## ✨ Features

### 🎬 Movies

* Browse popular and trending movies
* Search movies by title
* View detailed movie information:

  * description
  * cast
  * ratings
* Add/remove favorites
* Read and write reviews

### 🎟️ Events (Afisha)

* Browse local events (cinema, concerts, shows)
* View event details (date, time, location)
* Save events to favorites
* Mock ticket booking (UI only)

### ⚙️ General

* Offline support with local caching
* Pull-to-refresh
* Loading / error states
* Clean and responsive UI
* Multi-screen navigation

---

## 🏗️ Architecture

The project follows **MVVM + Repository Pattern** with clear separation of concerns.

```
presentation/
  ui/
  viewmodel/

domain/
  model/
  usecase/

data/
  repository/
  remote/
  local/
```

### Data Flow

```
UI → ViewModel → UseCase → Repository → (API / Database)
```

---

## 🛠️ Tech Stack

* **Kotlin**
* **Jetpack Compose** — UI
* **ViewModel** — state management
* **Navigation (Compose)**

### Data & Storage

* **Retrofit** — API integration
* **Room** — local database (caching & favorites)
* **DataStore** — user preferences

### Background Processing

* **WorkManager** — background data synchronization

### Dependency Injection

* **Hilt**

---

## 📱 Key Concepts

* Offline-first approach (local cache as a source of truth)
* Clean architecture principles
* Reactive UI with state management
* Separation of layers (UI / Domain / Data)

---

## 🚀 Future Improvements

* Real ticket booking integration
* Authentication & user profiles
* Cloud sync
* Push notifications

---

## 👥 Team

* Developer 1 — UI / Presentation Layer
* Developer 2 — Data Layer / Backend Integration

---

