# Complete Firebase Setup Guide for Google Sign-In

## Step 1: Create/Configure Firebase Project

### 1.1 Go to Firebase Console
- Visit: https://console.firebase.google.com/
- Click **"Add project"** or select your existing project
- If creating new:
  - Enter project name (e.g., "Persona")
  - Accept terms and click **Continue**
  - Enable/disable Google Analytics (optional)
  - Click **Create project**

### 1.2 Add Android App to Firebase
- In Firebase Console, click the **Android icon** (⚙️ icon)
- Register your app:
  - **Android package name**: `com.krishnajeena.persona`
  - **App nickname** (optional): Persona
  - Click **Register app**

## Step 2: Enable Google Authentication

### 2.1 Enable Google Sign-In
- In Firebase Console, go to **Authentication** (left sidebar)
- Click **Get started** (if first time)
- Go to **Sign-in method** tab
- Find **Google** in the list
- Click **Google** → Click **Enable** toggle
- Enter a project support email
- Click **Save**

### 2.2 Get Your Web Client ID
- Still in **Authentication** → **Sign-in method** → **Google**
- You'll see "Web SDK configuration" section
- **Copy the Web client ID** (looks like: `123456789-xxxxxxxxxxxxx.apps.googleusercontent.com`)
- Keep this - you'll need it in Step 4

## Step 3: Add SHA-1 Certificate to Firebase

### 3.1 Generate Debug SHA-1 (for testing)

Open Command Prompt/Terminal in your project directory and run:

**For Windows:**
```bash
cd C:\Users\krish\AndroidStudioProjects\Persona
keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
```

**For Mac/Linux:**
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

### 3.2 Copy SHA-1 and SHA-256
From the output, copy both:
- **SHA1**: (e.g., `A1:B2:C3:D4:...`)
- **SHA-256**: (e.g., `AB:CD:EF:12:...`)

### 3.3 Add to Firebase
- Go to Firebase Console → **Project Settings** (⚙️ icon)
- Scroll to **"Your apps"** section
- Find your Android app
- Click **"Add fingerprint"**
- Paste **SHA-1** → Click **Save**
- Click **"Add fingerprint"** again
- Paste **SHA-256** → Click **Save**

### 3.4 Generate Release SHA-1 (for production)

If you have a release keystore:
```bash
keytool -list -v -keystore path/to/your-release-key.jks -alias your-key-alias
```
Add these fingerprints to Firebase as well.

## Step 4: Download google-services.json

### 4.1 Download Configuration File
- In Firebase Console → **Project Settings**
- Scroll to **"Your apps"** → Find your Android app
- Click **"google-services.json"** download button
- Save the file

### 4.2 Add to Your Project
- Copy `google-services.json` to:
  ```
  C:\Users\krish\AndroidStudioProjects\Persona\app\google-services.json
  ```
- **Replace** the existing file if there is one

## Step 5: Update Your Code with Web Client ID

### 5.1 Open GoogleAuthUiClient.kt
Navigate to:
```
app/src/main/java/com/krishnajeena/persona/auth/GoogleAuthUiClient.kt
```

### 5.2 Replace the Web Client ID
Find this line (around line 23):
```kotlin
.setServerClientId("YOUR_WEB_CLIENT_ID")
```

Replace with your actual Web Client ID from Step 2.2:
```kotlin
.setServerClientId("123456789-xxxxxxxxxxxxx.apps.googleusercontent.com")
```

**⚠️ Important:** Use the **Web client ID**, NOT the Android client ID!

## Step 6: Verify build.gradle.kts

Ensure your `app/build.gradle.kts` has:

```kotlin
plugins {
    id("com.google.gms.google-services") version "4.4.0" apply false
}
```

And at the project level `build.gradle.kts`:
```kotlin
plugins {
    id("com.google.gms.google-services") version "4.4.0" apply true
}
```

## Step 7: Build and Test

### 7.1 Sync Gradle
- In Android Studio: **File** → **Sync Project with Gradle Files**

### 7.2 Build the App
```bash
./gradlew clean assembleDebug
```

### 7.3 Test Google Sign-In
1. Install the app on your device/emulator
2. Click the **Google icon** in the top-right corner
3. You should see the sign-in dialog
4. Click **"Sign In with Google"**
5. Select your Google account
6. After successful login:
   - The Google icon changes to your **profile picture**
   - Click it again to see **user details** and **Sign Out** option

## Troubleshooting

### ❌ "Developer Error" or "Sign-in failed"
**Solution:**
- Double-check your SHA-1 fingerprint is correct and added to Firebase
- Make sure you downloaded the latest `google-services.json` **after** adding SHA-1
- Verify Web Client ID is correct in `GoogleAuthUiClient.kt`

### ❌ "Sign-in not available"
**Solution:**
- Ensure Google Sign-in is **enabled** in Firebase Console
- Check that `google-services.json` is in the correct location
- Run `./gradlew clean` and rebuild

### ❌ "API not enabled"
**Solution:**
- Go to Google Cloud Console: https://console.cloud.google.com/
- Select your project
- Go to **APIs & Services** → **Library**
- Search for "Google+ API" and enable it

### ❌ App shows "Loading..." forever
**Solution:**
- Check your internet connection
- Verify Firebase configuration is correct
- Check Logcat for error messages

## Testing Checklist

- [ ] Firebase project created
- [ ] Google Sign-in enabled in Firebase Console
- [ ] Web Client ID copied
- [ ] SHA-1 and SHA-256 added to Firebase
- [ ] `google-services.json` downloaded and placed in `app/` folder
- [ ] Web Client ID updated in `GoogleAuthUiClient.kt`
- [ ] Gradle synced successfully
- [ ] App builds without errors
- [ ] Google icon shows when not logged in
- [ ] Sign-in dialog appears when clicking icon
- [ ] Can successfully sign in with Google account
- [ ] Profile picture shows after login
- [ ] Can see user details in dropdown
- [ ] Can sign out successfully
- [ ] Summarization feature requires login

## Quick Reference

**Firebase Console:** https://console.firebase.google.com/

**File Locations:**
- `google-services.json` → `app/google-services.json`
- Web Client ID → `app/src/main/java/com/krishnajeena/persona/auth/GoogleAuthUiClient.kt`

**Key Commands:**
```bash
# Generate SHA-1
keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android

# Clean and build
./gradlew clean assembleDebug
```

---

Need help? Check the error in Logcat and refer to the troubleshooting section above.
