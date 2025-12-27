## Flight Search App
A modern Android app for searching flight routes, saving favorite destinations, and managing airport information using Jetpack Compose and modern Android architecture components.

## Project Overview
This project demonstrates how to build a fully-featured flight search application with:
* Real-time airport search with **autocomplete suggestions**
* **Room database** integration for efficient data persistence
* **DataStore** for persisting lightweight user preferences
* Clean architecture following Android best practices

## Screenshots

### Home Screen
<img width="300" alt="Main screen showing search input" src="https://github.com/user-attachments/assets/563d55b8-f0ce-4f8d-8cc2-9439d1e3efe4" />

### Suggestions Screen
Suggestions appear as the user types

<img width="300" alt="Autocomplete suggestions list" src="https://github.com/user-attachments/assets/5527c25b-94a3-4407-9923-7bb81dba739b" />

### Departure Selected
Select a departure airport to view destinations and like or unlike favorite routes.

<img width="300" alt="Favorite routes highlighted" src="https://github.com/user-attachments/assets/05df8f58-bed9-4156-8b37-8778ab412edf" />

### Favorite Routes
List of favorites shown on app load or when search is cleared

<img width="300" alt="Favorite routes list" src="https://github.com/user-attachments/assets/b15f16e8-59c4-4758-b94c-0a4da689a584" />

### Remove Favorite Route
Unlike a favorite; list updates in real-time

<img width="300" alt="Remove favorite route" src="https://github.com/user-attachments/assets/9a6f7344-7d97-4867-a6a0-9188b6b1b24e" />

### Restore Last Search
App restart restores last search query

<img width="300" alt="Restore last search on app restart" src="https://github.com/user-attachments/assets/07ff292c-25ea-48d5-b52b-1597b9f69785" />


## Architecture
The app follows a unidirectional data flow:
UI → ViewModel → Repository → Room/DataStore → ViewModel → UI

## Architecture Components
* ViewModel
* StateFlow
* Coroutines
* Room
* DataStore
* Manual Dependency Injection



## Getting Started

### Prerequisites
- Android Studio Otter or later
- Kotlin 2.0.21+
- Android SDK API 33+

### Installation
Clone the repository:
```bash
git clone https://github.com/serventino-servenn/Flight-Search-App.git


