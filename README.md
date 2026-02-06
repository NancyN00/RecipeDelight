# 🍲 Recipe Delight

**Recipe Delight** is a modern **Kotlin Multiplatform (KMP)** application designed to bring delicious meals closer to you. It bridges the gap between raw data and culinary inspiration, allowing users to explore, learn, and save dishes across **Android, iOS, Desktop, and Web**—all powered by a single, shared codebase.

---

## 🚀 Key Features

* **Real-time Recipe Discovery:** Fetches culinary data from a remote REST API.
* **AI-Powered Queries:** Leveraging AI to answer "What can I cook with these ingredients?" and generating custom cooking instructions.
* **Cross-Platform Favorites:** Save your favorite meals locally and access them across all your devices.
* **Reactive UI:** Built entirely with **Jetpack Compose** (Multiplatform) for a smooth, modern user experience.

---

## 🛠 Tech Stack

| Layer | Technology |
| :--- | :--- |
| **Language** | Kotlin Multiplatform (KMP) |
| **UI Framework** | Jetpack Compose Multiplatform |
| **Dependency Injection** | **Koin** (Shared and Platform-specific) |
| **Networking** | **Ktor** (Type-safe HTTP Client) |
| **Local Storage** | **SQLDelight** (Persistent storage with SQLite) |
| **AI Integration** | **Gemini AI** (Processing recipe queries and ingredient logic) |
| **Concurrency** | Kotlin Coroutines & Flow |

---

## 🏗 Architecture

The project follows **Clean Architecture** principles to ensure the code is maintainable, testable, and scalable:

1.  **Domain Layer:** Contains the business logic and entity definitions (Models).
2.  **Data Layer:** Implements the **Repository Pattern**, managing data flow between the Ktor API and the SQLDelight database.
3.  **Presentation Layer:** Uses the **MVVM Pattern** with ViewModels shared across platforms.
