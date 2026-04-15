# First FinTech — Android Subscription App

A native Android application built in Kotlin for **First**, a FinTech company offering subscription services to customers. The app allows customers to register, log in, browse available services, subscribe to services, and view their active subscriptions.

---

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Tech Stack](#tech-stack)
- [Setup & Installation](#setup--installation)
- [API Reference](#api-reference)
- [Security](#security)
- [Error Handling](#error-handling)
- [Screens](#screens)

---

## Features

- **User Registration** — register with name, email, phone number, and password
- **User Login** — authenticate and receive a Bearer token
- **Service Catalogue** — browse all available subscription services
- **Discounted Services** — discounted services are visually distinguished with a badge and strikethrough original price
- **Subscribe to Services** — subscribe to one or more services with a confirmation dialog
- **My Subscriptions** — view all active subscriptions (bonus feature)
- **Side Navigation Drawer** — accessible from all main screens with username, email, and nav links
- **Offline Detection** — detects no internet connection and shows appropriate feedback
- **Loading States** — skeleton loading animation on first data fetch
- **Empty States** — clear messaging when no data is available

---

## Architecture

The app follows the **MVVM (Model-View-ViewModel)** architecture pattern with a clean separation of concerns across three layers:

```
UI Layer (Activities + Adapters)
        ↕
ViewModel Layer (state + business logic)
        ↕
Repository Layer (data access)
        ↕
Network Layer (Retrofit + OkHttp)
```

### Layer Responsibilities

**UI Layer** — Activities observe LiveData from ViewModels and update the UI. They do not contain any business logic.

**ViewModel Layer** — holds UI state via `LiveData`, drives API calls via `viewModelScope`, handles input validation, and exposes results wrapped in `Event<Result<T>>` to prevent re-emission on rotation.

**Repository Layer** — makes API calls via `ApiService` and returns raw `Response<T>` objects to the ViewModel.

**Network Layer** — `RetrofitClient` builds the `OkHttpClient` with a logging interceptor and an auth interceptor that automatically attaches the `Authorization: Bearer {token}` header to every secured request.

---

## Project Structure

```
app/src/main/java/com/first/fintech/
├── data/
│   ├── model/              # Request/Response data classes
│   │   ├── login/
│   │   │   ├── LoginRequest.kt
│   │   │   └── LoginResponse.kt
│   │   │
│   │   ├── register/
│   │   │   ├── RegisterRequest.kt
│   │   │   └── RegisterResponse.kt
│   │   │
│   │   ├── services/
│   │   │   ├── Service.kt
│   │   │   └── ServicesResponse.kt
│   │   │
│   │   └── subscriptions/
│   │       ├── Subscription.kt
│   │       ├── SubscribeRequest.kt
│   │       ├── SubscribeResponse.kt
│   │       └── SubscriptionsResponse.kt
│   │
│   ├── network/
│   │   ├── ApiService.kt      # Retrofit interface
│   │   └── RetrofitClient.kt  # OkHttp + Retrofit setup
│   │
│   └── repository/
│       ├── AuthRepository.kt
│       ├── ServiceRepository.kt
│       └── SubscriptionRepository.kt
│
├── ui/
│   ├── login/
│   │   ├── view/
│   │   │   └── LoginActivity.kt
│   │   └── viewmodel/
│   │       ├── LoginViewModel.kt
│   │       └── LoginViewModelFactory.kt
│   │
│   ├── register/
│   │   ├── view/
│   │   │   └── RegisterActivity.kt
│   │   └── viewmodel/
│   │       ├── RegisterViewModel.kt
│   │       └── RegisterViewModelFactory.kt
│   │
│   ├── services/
│   │   ├── view/
│   │   │   ├── ServicesActivity.kt
│   │   │   └── adapter/
│   │   │       └── ServicesAdapter.kt
│   │   ├── viewmodel/
│   │   │   ├── ServicesViewModel.kt
│   │   │   └── ServicesViewModelFactory.kt
│   │
│   └── subscriptions/
│       ├── view/
│       │   ├── SubscriptionsActivity.kt
│       │   └── adapter/
│       │       └── SubscriptionsAdapter.kt
│       ├── viewmodel/
│       │   ├── SubscriptionsViewModel.kt
│       │   └── SubscriptionsViewModelFactory.kt
│
└── util/
    ├── Event.kt            # One-time LiveData event wrapper
    ├── SessionManager.kt   # SharedPreferences token storage
    ├── NetworkUtils.kt     # Internet connectivity check
    └── DrawerHelper.kt     # Reusable side navigation drawer
```

---

## Tech Stack

| Component | Library | Version |
|---|---|---|
| Language | Kotlin | 1.9+ |
| Architecture | MVVM + LiveData | — |
| Networking | Retrofit | 2.9.0 |
| HTTP Client | OkHttp | 4.12.0 |
| JSON Parsing | Gson | 2.9.0 |
| Coroutines | Kotlin Coroutines | 1.7.3 |
| UI Components | Material Components | 1.11.0 |
| ViewModel | AndroidX Lifecycle | 2.7.0 |
| Secure Storage | SharedPreferences | — |

---

## Setup & Installation

### Prerequisites

- Android Studio Hedgehog or later
- Android SDK 24+
- Kotlin 1.9+
- Internet connection for API calls

### Steps

1. Clone the repository:
```bash
git clone https://github.com/Chelsea-Ngigi/first-fintech.git
cd fintech
```

2. Open the project in Android Studio.

3. Set the base URL in `RetrofitClient.kt`:
```kotlin
private const val BASE_URL = "https://your-api-base-url.com/"
```

4. Sync Gradle dependencies:
```
File → Sync Project with Gradle Files
```

5. Run the app on an emulator or physical device:
```
Run → Run 'app'
```

### Gradle Dependencies

```gradle
// Retrofit
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

// OkHttp
implementation 'com.squareup.okhttp3:okhttp:4.12.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'

// ViewModel + LiveData
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.7.0'

// Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'

// Material Components
implementation 'com.google.android.material:material:1.11.0'
```

### Required Permissions

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

---

## API Reference

All endpoints except Register and Login require a `Bearer` token in the `Authorization` header, which is attached automatically by the OkHttp interceptor after login.

### Register Customer

```
POST /api/v1/user/register
Authorization: None
```

Request body:
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "0712345678",
  "password": "password123"
}
```

### Customer Login

```
POST /api/v1/access/login
Authorization: None
```

Request body:
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

Response includes a `token` field that is stored securely and used for all subsequent requests.

### Get Services

```
GET /api/v1/service/services
Authorization: Bearer {token}
```

### Subscribe to Service

```
POST /api/v1/subscription/subscribe
Authorization: Bearer {token}
```

Request body:
```json
{
  "serviceId": "service_id_here",
  "subscriberEmail": "john@example.com"
}
```

### Get Subscriptions (Bonus)

```
GET /api/v1/subscription/subscriptions/{subscriberEmail}
Authorization: Bearer {token}
```

---

## Security

### Token Storage

The Bearer token received on login is stored in `SharedPreferences` via `SessionManager`:

```kotlin
object SessionManager {
    fun saveSession(context: Context, token: String, email: String, name: String)
    fun getToken(context: Context): String
    fun clearSession(context: Context)
}
```

The token is never hardcoded, logged in production builds, or stored in plaintext files.

### Token Attachment

The token is automatically attached to every API request via an OkHttp interceptor:

```kotlin
.addInterceptor { chain ->
    val token = SessionManager.getToken(context)
    val request = if (token.isNotEmpty()) {
        chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
    } else {
        chain.request()
    }
    chain.proceed(request)
}
```

### Session Management

On logout, `SessionManager.clearSession()` wipes all stored credentials and navigates the user back to the login screen with the back stack cleared:

```kotlin
intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
```

### Logging

HTTP logging is enabled only in debug builds:

```kotlin
level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.NONE
```

---

## Error Handling

The app handles errors at multiple layers:

### Input Validation (ViewModel)

Before any API call is made, inputs are validated:

| Field | Rule |
|---|---|
| Name | Must not be blank |
| Email | Must match email pattern |
| Phone | Must be at least 10 digits |
| Password | Must be at least 6 characters |

Validation errors are posted to a `validationError` LiveData and shown via `Snackbar`.

### Network Errors

Before any API call, connectivity is checked:

```kotlin
if (!NetworkUtils.isConnected(this)) {
    showError("No internet connection")
    return
}
```

### API Errors

Three layers of error handling:

```
HTTP error (401, 500...)  → "HTTP {code}" message shown
API business logic error  → API error message shown
Network exception         → exception message shown
```

### Edge Cases

| Scenario | Handling |
|---|---|
| Duplicate registration | API error message shown via Snackbar |
| Invalid credentials | API error message shown via Snackbar |
| Empty services list | Empty state view shown |
| Empty subscriptions list | "You have no active subscriptions" shown |
| No internet on launch | Snackbar warning shown |
| Already logged in | Auto-redirected to Services on app launch |

---

## Screens

### Register Screen
- Fields: Full Name, Email, Phone, Password
- Client-side validation before API call
- Link to Login screen

### Login Screen
- Fields: Email, Password
- Auto-redirects to Services if already logged in
- Link to Register screen

### Services Screen
- Lists all available services in cards
- Discounted services show a badge and strikethrough original price
- Subscribe button triggers confirmation dialog
- Side navigation drawer accessible via hamburger icon

### My Subscriptions Screen
- Lists all active subscriptions for the logged-in user
- Shows service name, and price
- Accessible from side navigation drawer

### Side Navigation Drawer (all main screens)
- Shows user avatar, name, and email in header
- Navigation links: Services, My Subscriptions
- Logout option with confirmation dialog
- Active screen is highlighted

---

## Event Wrapper

The app uses a one-time `Event` wrapper around `LiveData` to prevent observers from re-firing on screen rotation:

```kotlin
open class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) null
        else { hasBeenHandled = true; content }
    }
}
```

This ensures navigation and toast messages only fire once even if the Activity is recreated.
