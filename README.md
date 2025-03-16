# SchoolBus App

A school bus tracking application for Android that allows parents to track their children's school buses in real-time.

## Features

- Real-time bus tracking on Google Maps
- Bus route visualization
- Notifications for bus delays and arrivals
- User authentication (parents, drivers, administrators)
- Route details with stops and schedules

## Prerequisites

- Android Studio (latest version recommended)
- JDK 8 or higher
- Android SDK with API level 33 or higher
- Google Maps API key
- Firebase project
- Node.js (for Firebase data import)

## Setup Instructions

### 1. Clone the repository

```bash
git clone https://github.com/yourusername/SchoolBus.git
cd SchoolBus
```

### 2. Set up Firebase

1. Go to the [Firebase Console](https://console.firebase.google.com/)
2. Create a new project
3. Add an Android app to your Firebase project
   - Use package name: `com.schoolbus.app`
   - Register the app
4. Download the `google-services.json` file and place it in the `app/` directory
5. Set up Firebase Authentication
   - Enable Email/Password authentication
6. Set up Firebase Realtime Database
   - Create a database in test mode
   - Set up security rules (see below)
   - Import the sample data (see below)

#### Setting Up Database Security Rules

The project includes a `database.rules.json` file with predefined security rules for the app:

1. In the Firebase Console, go to Realtime Database > Rules
2. Copy the contents of `database.rules.json` and paste them into the rules editor
3. Click "Publish" to apply the rules

These rules ensure that:
- Users can only access their own data
- Drivers can only update their assigned buses
- Admins have full access to manage all data
- Unauthenticated users have no access

#### Importing Sample Data

The project includes sample data and an import script to help you set up your Firebase database quickly:

1. Install Node.js dependencies:
   ```bash
   npm install
   ```

2. Update the Firebase configuration in `firebase-import.js` with your project credentials

3. Run the import script:
   ```bash
   npm run import
   ```

This will populate your Firebase database with sample schools, buses, routes, and users for testing.

### 3. Set up Google Maps

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or use your Firebase project
3. Enable the Google Maps Android API
4. Create an API key
5. Restrict the API key to your Android app
6. Add the API key to `AndroidManifest.xml`:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="YOUR_API_KEY" />
   ```

### 4. Build and Run

1. Open the project in Android Studio
2. Sync the project with Gradle files
3. Build the project
4. Run on an emulator or physical device

## Testing the App

### Sample Accounts

- Parent: parent@example.com / password123
- Driver: driver@example.com / password123
- Admin: admin@example.com / password123

### Test Scenarios

1. **Login as a Parent**
   - View active buses
   - Track a specific bus
   - View route details

2. **Login as a Driver**
   - Update bus status
   - View assigned route

3. **Login as an Admin**
   - Manage buses and routes
   - Send notifications

## Database Structure

The Firebase Realtime Database has the following structure:

```
schoolbus-app/
├── users/
│   ├── [user_id]/
│   │   ├── email
│   │   ├── name
│   │   ├── phone
│   │   ├── type (parent, driver, admin)
│   │   └── children/ (for parent users)
│   │       └── [child_id]/
│   │           ├── name
│   │           ├── grade
│   │           ├── schoolId
│   │           └── busId
├── schools/
│   └── [school_id]/
│       ├── name
│       ├── address
│       ├── phone
│       ├── email
│       └── buses/
│           └── [bus_id]: true
├── buses/
│   └── [bus_id]/
│       ├── busNumber
│       ├── capacity
│       ├── driverId
│       ├── driverName
│       ├── routeId
│       ├── routeName
│       ├── status
│       ├── currentLocation/
│       │   ├── latitude
│       │   ├── longitude
│       │   └── lastUpdated
│       └── stops/
│           └── [stop_id]/
│               ├── name
│               ├── order
│               ├── scheduledTime
│               └── location/
│                   ├── latitude
│                   └── longitude
├── routes/
│   └── [route_id]/
│       ├── name
│       ├── description
│       ├── schoolId
│       ├── busId
│       ├── startTime
│       ├── endTime
│       ├── days
│       └── stops/
│           └── [stop_id]: true
└── notifications/
    └── [notification_id]/
        ├── title
        ├── message
        ├── type
        ├── busId
        ├── routeId
        ├── timestamp
        └── recipients/
            └── [user_id]: true
```

## Troubleshooting

- If you encounter build errors, try:
  - Sync project with Gradle files
  - Clean and rebuild the project
  - Update Android Studio and dependencies

- If Firebase connection fails:
  - Verify that `google-services.json` is correctly placed
  - Check internet connectivity
  - Verify Firebase project settings

## License

This project is licensed under the MIT License - see the LICENSE file for details. 