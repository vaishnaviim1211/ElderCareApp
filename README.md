# ElderCare App

ElderCare is an Android application designed to help older adults manage appointments, medicines, health tracking, water intake, emergency contacts, and profile information.

## Features

- Firebase user registration and login
- Appointment management
- Medicine reminders and dosage tracking
- Steps and sleep tracking
- Daily water intake tracking
- Emergency contact management
- User profile management

## Tech Stack

- Java
- Android Studio
- Gradle
- Firebase Authentication
- Firebase Realtime Database

## Requirements

- Android Studio
- JDK 11 or newer
- Android SDK
- Android device or emulator
- Internet connection

## Setup

1. Clone this repository:
   ```bash
   git clone https://github.com/vaishnaviim1211/ElderCareApp.git
   ```
2. Open the project folder in Android Studio.
3. Configure Firebase and retain `app/google-services.json` in the `app` directory.
4. Enable Email/Password authentication in Firebase Console.
5. Sync Gradle and run the app.

## Firebase Database Structure

```text
users/
  <user-id>/
    profile/
      name
      email
```

## Security

Do not commit private Firebase service-account keys or passwords. Firebase API keys in `google-services.json` are app configuration values, but database and authentication security rules must be configured in Firebase Console.

