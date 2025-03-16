/**
 * Firebase Sample Data Import Script
 * 
 * This script imports the sample data from firebase-sample-data.json into your Firebase Realtime Database.
 * 
 * Prerequisites:
 * 1. Node.js installed
 * 2. Firebase CLI installed (npm install -g firebase-tools)
 * 3. Firebase project created
 * 4. Firebase CLI logged in (firebase login)
 * 
 * Usage:
 * 1. Update the firebaseConfig object with your Firebase project credentials
 * 2. Run: node firebase-import.js
 */

const fs = require('fs');
const { initializeApp } = require('firebase/app');
const { getDatabase, ref, set } = require('firebase/database');

// Update with your Firebase project configuration
const firebaseConfig = {
  apiKey: "YOUR_API_KEY",
  authDomain: "YOUR_PROJECT_ID.firebaseapp.com",
  databaseURL: "https://YOUR_PROJECT_ID.firebaseio.com",
  projectId: "YOUR_PROJECT_ID",
  storageBucket: "YOUR_PROJECT_ID.appspot.com",
  messagingSenderId: "YOUR_MESSAGING_SENDER_ID",
  appId: "YOUR_APP_ID"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const database = getDatabase(app);

// Read sample data
try {
  const sampleData = JSON.parse(fs.readFileSync('./firebase-sample-data.json', 'utf8'));
  console.log('Sample data loaded successfully');
  
  // Import data to Firebase
  importData(sampleData);
} catch (error) {
  console.error('Error reading sample data:', error);
  process.exit(1);
}

// Function to import data to Firebase
async function importData(data) {
  try {
    console.log('Starting data import...');
    
    // Import users
    for (const [userId, userData] of Object.entries(data.users)) {
      await set(ref(database, `users/${userId}`), userData);
      console.log(`Imported user: ${userId}`);
    }
    
    // Import schools
    for (const [schoolId, schoolData] of Object.entries(data.schools)) {
      await set(ref(database, `schools/${schoolId}`), schoolData);
      console.log(`Imported school: ${schoolId}`);
    }
    
    // Import buses
    for (const [busId, busData] of Object.entries(data.buses)) {
      await set(ref(database, `buses/${busId}`), busData);
      console.log(`Imported bus: ${busId}`);
    }
    
    // Import routes
    for (const [routeId, routeData] of Object.entries(data.routes)) {
      await set(ref(database, `routes/${routeId}`), routeData);
      console.log(`Imported route: ${routeId}`);
    }
    
    // Import notifications
    for (const [notificationId, notificationData] of Object.entries(data.notifications)) {
      await set(ref(database, `notifications/${notificationId}`), notificationData);
      console.log(`Imported notification: ${notificationId}`);
    }
    
    console.log('Data import completed successfully');
    process.exit(0);
  } catch (error) {
    console.error('Error importing data:', error);
    process.exit(1);
  }
} 