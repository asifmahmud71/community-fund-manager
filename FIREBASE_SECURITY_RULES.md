# Firebase Security Rules

Copy and paste these rules into your Firebase Realtime Database Rules section:

```json
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    },
    "members": {
      ".read": "auth != null",
      ".write": "auth != null && root.child('users').child(auth.uid).child('role').val() === 'admin'"
    },
    "deposits": {
      ".read": "auth != null",
      ".write": "auth != null"
    },
    "expenses": {
      ".read": "auth != null",
      ".write": "auth != null"
    },
    "notifications": {
      ".read": "auth != null",
      ".write": "auth != null"
    }
  }
}
```

## Explanation:

- **users**: Users can only read and write their own data
- **members**: All authenticated users can read, but only admins can write
- **deposits**: All authenticated users can read and write (members can add deposits)
- **expenses**: All authenticated users can read and write
- **notifications**: All authenticated users can read and write
