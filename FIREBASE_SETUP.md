# Firebase Setup Instructions

## Steps to Configure Firebase:

1. **Create a Firebase Project**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Click "Add project"
   - Enter project name and follow the setup wizard

2. **Add Android App to Firebase**
   - In Firebase Console, click "Add app" and select Android
   - Enter package name: `com.fundmanager`
   - Download the `google-services.json` file

3. **Replace the google-services.json file**
   - Replace `/app/google-services.json` with your downloaded file
   - The current file is a placeholder with instructions

4. **Enable Authentication**
   - In Firebase Console, go to Authentication
   - Click "Get started"
   - Enable "Email/Password" sign-in method

5. **Set up Realtime Database**
   - In Firebase Console, go to Realtime Database
   - Click "Create Database"
   - Start in test mode (we'll add security rules later)

6. **Add Security Rules**
   - In Realtime Database, go to "Rules" tab
   - Copy and paste the rules from `FIREBASE_SECURITY_RULES.md`
   - Publish the rules

7. **Enable Cloud Messaging**
   - In Firebase Console, go to Cloud Messaging
   - Note your Server Key (for sending notifications)

8. **Database Structure**
   The app will automatically create the following structure:
   ```
   {
     "users": { ... },
     "members": { ... },
     "deposits": { ... },
     "expenses": { ... },
     "notifications": { ... }
   }
   ```

## Important Notes:
- Keep your `google-services.json` file secure
- Never commit real Firebase credentials to public repositories
- Update security rules before going to production
- Test the app after Firebase configuration
