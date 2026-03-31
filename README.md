# EwigChat

Modern Android chat application built with Clean Architecture, MVVM and Firebase.

---

## 📱 Screenshots

<p align="center">
  <img src="screenshots/auth.jpg" width="200"/>
  <img src="screenshots/registration.jpg" width="200"/>
  <img src="screenshots/chats.jpg" width="200"/>
</p>

<p align="center">
  <img src="screenshots/chat.jpg" width="200"/>
  <img src="screenshots/profile.jpg" width="200"/>
</p>

---

## ✨ Features

### 🔐 Authentication
- Firebase Authentication (email/password)
- Email verification flow
- Reactive auth state handling with Kotlin Flow

### 💬 Chat
- Real-time messaging using Cloud Firestore
- One-to-one chats
- Last message preview in chat list
- Message ownership detection (incoming/outgoing)

### 👤 Profile
- Profile management with inline editing
- Username system with transactional uniqueness check

### 🧠 Architecture & State
- Clean Architecture (data / domain / presentation)
- MVVM pattern
- Reactive UI with Kotlin Flow & StateFlow
- Unidirectional data flow

---

## 💡 Highlights

- Real-time updates via Firestore snapshot listeners + Flow
- Auth state drives navigation
- Username uniqueness implemented via transactional writes
- In-memory caching for frequently accessed profile data
- Edge-to-edge UI with proper IME and insets handling

---

## 🧱 Tech Stack

- Kotlin
- MVVM
- Clean Architecture
- Coroutines + Flow
- Hilt (Dependency Injection)
- Firebase Authentication
- Cloud Firestore
- Navigation Component
- Material 3
- ViewBinding

---

## 🏗 Architecture

The project follows Clean Architecture principles with clear separation of concerns:

- **data layer** — repository implementations, Firebase integration, DTOs, mappers
- **domain layer** — use cases and business logic
- **presentation layer** — ViewModels, UI state, and screens

Reactive data flow is built with Kotlin Flow and StateFlow:

- UI subscribes to StateFlow from ViewModel
- ViewModel combines multiple data sources using Flow operators
- Repository exposes cold flows backed by Firebase listeners

The app follows a unidirectional data flow approach for predictable state management.

---

## 🚀 Getting Started

1. Clone the repository
2. Create your Firebase project
3. Add `google-services.json` to the app module
4. Enable Authentication (Email/Password)
5. Set up Firestore database
6. Run the app

---

## 🚧 In Progress

- App settings (theme: light / dark / system)
- Localization (EN / RU)
- DataStore-based settings persistence
- UI polishing (chat list, message input, consistency)
- Viewing interlocutor profile from chat

---

## 🔮 Planned

- Authentication via username
- User avatar upload & management
- Offline-first support (Room + WorkManager)
- Message sending states (sending / sent / failed)
- Instant UI loading via local cache

---

## 🌌 Future Ideas

- Online status & last seen
- Push notifications (FCM)
- Message and chat deletion

---

## 📌 Status

🚧 Evolving from MVP towards a production-ready architecture  
Currently focused on UX polish, settings, and offline-first improvements