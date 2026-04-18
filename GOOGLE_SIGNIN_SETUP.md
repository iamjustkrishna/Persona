# Google Sign-In Setup Instructions

To enable Google Sign-In in your Persona app, follow these steps:

## 1. Firebase Console Setup

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project (or create a new one if you haven't)
3. Go to **Authentication** → **Sign-in method**
4. Enable **Google** sign-in provider
5. Click **Save**

## 2. Get Your Web Client ID

1. In Firebase Console, go to **Project Settings** (gear icon)
2. Select the **General** tab
3. Scroll down to **Your apps** section
4. Find your Android app
5. Under **SDK setup and configuration**, you'll see your **Web client ID** (it looks like: `xxxxx.apps.googleusercontent.com`)
6. Copy this Web Client ID

## 3. Update Your Code

Open the file:
```
app/src/main/java/com/krishnajeena/persona/auth/GoogleAuthUiClient.kt
```

Find this line (around line 20):
```kotlin
.setServerClientId("YOUR_WEB_CLIENT_ID") // Replace with your actual Web Client ID from Firebase
```

Replace `"YOUR_WEB_CLIENT_ID"` with your actual Web Client ID.

For example:
```kotlin
.setServerClientId("123456789-abcdefghijklmnop.apps.googleusercontent.com")
```

## 4. Add SHA-1 Certificate Fingerprint (Important!)

1. Generate your SHA-1 fingerprint:
   - Open terminal in your project directory
   - Run (for debug):
     ```bash
     keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
     ```
   - Copy the **SHA-1** fingerprint

2. Add to Firebase:
   - Go to Firebase Console → **Project Settings**
   - Scroll to **Your apps**
   - Click **Add fingerprint**
   - Paste your SHA-1 fingerprint
   - Click **Save**

## 5. Download and Update google-services.json

1. In Firebase Console, go to **Project Settings**
2. Download the latest `google-services.json`
3. Replace the file in your project:
   ```
   app/google-services.json
   ```

## 6. Build and Test

1. Sync your Gradle project
2. Build and run the app
3. Click the profile icon in the top-right corner
4. You should see the sign-in dialog
5. Click "Sign In with Google"
6. Select your Google account

## Features After Sign-In

Once signed in, users can:
- ✨ Use **Persona Intelligence** - AI-powered article summaries (powered by Gemini)
- 👤 View their profile information
- 🔒 Access premium features

## Troubleshooting

### Issue: Sign-in button doesn't work
- Make sure you've added the SHA-1 fingerprint to Firebase
- Verify your Web Client ID is correct
- Check that Google sign-in is enabled in Firebase Console

### Issue: "Developer Error" message
- Your SHA-1 fingerprint is likely missing or incorrect
- Re-download `google-services.json` after adding SHA-1

### Issue: Sign-in works but user info doesn't show
- Check that you've granted necessary permissions in Firebase Console
- Verify the `google-services.json` is up to date

## Current Implementation Status

✅ Google Sign-In UI components
✅ Authentication state management
✅ User profile display in TopAppBar
✅ Sign-out functionality
✅ Protected summarization feature (requires login)
⏳ Actual Google Sign-In flow (requires Web Client ID configuration)

**Note:** The actual Google Sign-In flow will work once you complete steps 1-5 above!
