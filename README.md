![homescreen](https://github.com/user-attachments/assets/7cda2148-8cf6-429b-a7f0-7dade56a6f99)# 🍲 Recipe Delight

> A production-style **Kotlin Multiplatform (KMP)** application demonstrating scalable cross-platform architecture using a shared codebase for Android, Desktop, and Web.

---

## 🚀 Overview

**Recipe Delight** is a modern multiplatform application built to explore real-world architectural challenges using Kotlin Multiplatform.

It demonstrates how to:

- Share business logic across platforms
- Implement Clean Architecture in KMP
- Apply the Repository pattern properly
- Use offline-first caching strategies
- Maintain platform-specific implementations cleanly

The project emphasizes **architecture quality over UI complexity**.

---

## 🖥 Supported Platforms

| Platform | Status |
|----------|--------|
| Android | ✅ Stable |
| Desktop (JVM) | 🚧 In Progress |
| Web (JS / WASM) | 🚧 Experimental |

---

## ✨ Core Features

- 🔎 Real-time recipe discovery via REST API  
- 🤖 AI-powered ingredient queries  
- ❤️ Persistent cross-platform favorites  
- 📡 Offline-first caching strategy  
- ⚡ Reactive UI built with Compose Multiplatform  

---

## 📸 Screenshots

### 🏠 Home Screen
![homescreen](https://github.com/user-attachments/assets/a4f22133-98da-4f18-82a1-2471536dcdd6)

### 📂 Categories Screen
![categoriesscreen](https://github.com/user-attachments/assets/0fb9e0ad-262d-462a-8a24-520a1b0387a0)

### 🍲 Details Screen
![detailsscreen](https://github.com/user-attachments/assets/6c7523a1-cbb0-4f0a-ba3d-1c38e480057d)

### 🤖 AI Screen
![aiscreen](https://github.com/user-attachments/assets/6cdb6701-e21e-4388-b2bb-f33bbd2ec04b)

### ❤️ Bookmarks Screen
![bookmarkscreen](https://github.com/user-attachments/assets/6046c472-1b09-4e32-8245-f7a68c88cba3)


## 🛠 Tech Stack

| Concern | Implementation |
|----------|----------------|
| Language | Kotlin Multiplatform |
| UI | Jetpack Compose Multiplatform 
| DI | Koin |
| Networking | Ktor Client |
| Persistence | SQLDelight |
| Async | Coroutines + Flow |
| AI | Gemini API |

---

# 🏗 Architecture

The project follows **Clean Architecture + MVVM** adapted for Kotlin Multiplatform.

## High-Level Flow

```
                ┌──────────────────────────┐
                │     Presentation Layer    │
                │  Compose UI + ViewModels  │
                └──────────────┬───────────┘
                               │
                               ▼
                ┌──────────────────────────┐
                │       Domain Layer        │
                │  UseCases + Core Models   │
                └──────────────┬───────────┘
                               │
                               ▼
                ┌──────────────────────────┐
                │        Data Layer         │
                │  Repository Implementation│
                └───────┬──────────┬───────┘
                        │          │
                        ▼          ▼
               ┌────────────┐   ┌──────────────┐
               │   Ktor API │   │ SQLDelight DB│
               └────────────┘   └──────────────┘
```

---

## 📂 Multiplatform Structure

```
shared/
 ├── commonMain
 │    ├── domain/
 │    ├── data/
 │    ├── presentation/
 │    └── di/
 │
 ├── androidMain
 ├── desktopMain
 └── jsMain
```

### Key Design Decision

- **Business logic lives entirely in `commonMain`**
- Platform-specific code is limited to:
  - Database drivers
  - Entry points
  - Platform UI wiring

This maximizes code reuse and minimizes duplication.

---

## 📡 Offline-First Strategy

The application prioritizes local data access.

### Workflow

1. Fetch from API (Ktor)
2. Cache locally (SQLDelight)
3. Serve UI from local source
4. Sync when network is available

### Benefits

- Faster perceived performance  
- Reduced API dependency  
- Stable UX offline  

---

## 🔧 Engineering Challenges & Solutions

| Problem | Solution |
|----------|----------|
| HTTP 401 errors | Implemented secure API key injection |
| HTTP 400 malformed body | Reworked JSON serialization |
| SQLDelight schema issues | Fixed migration and table definition inconsistencies |
| State crashes in Compose | Introduced safe UI state modeling |
| Multiplatform driver setup | Structured platform-specific database drivers |

---

## 🧪 Testability & Scalability

- Repository abstraction allows easy mocking
- Use cases are platform-independent
- DI ensures replaceable implementations
- Architecture supports adding iOS with minimal friction

---

## 🎯 Why This Project Matters

This project demonstrates:

- Real KMP architecture (not just shared models)
- Proper separation of concerns
- Understanding of cross-platform constraints
- Clean DI setup across targets
- Scalable structure for production apps

---

## 📌 Project Status

Android Actively developed.

### Current Focus

- Desktop stabilization
- Web rendering improvements
- Improved synchronization strategy

---

## 📈 What This Project Demonstrates

- Architectural thinking before implementation  
- Clean separation of layers  
- Production-style project organization  
- Multiplatform scalability  
- Maintainable and extensible codebase  

---

