## Flight Search App
A modern Android app for searching flight routes, saving favorite destinations, and managing airport information using Jetpack Compose and modern Android architecture components.

## Project Overview
This project demonstrates how to build a fully-featured flight search application with:
* Real-time airport search with **autocomplete suggestions**
* **Room database** integration for efficient data persistence
* **DataStore** for persisting lightweight user preferences
* Clean architecture following Android best practices

## Screenshots
_(Screenshots coming soon)_

## Features
* Search airports by **IATA code or name**
* Autocomplete suggestions as you type
* View available routes from a selected departure airport
* Save frequently used routes to favorites
* Quick access to favorite destinations

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


