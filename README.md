# Community Fund Manager

A comprehensive Android application for managing community funds with real-time tracking of deposits, expenses, and member contributions.

## Features

### 🔐 Authentication System
- Firebase Authentication with Email/Password
- User registration with role selection (Admin/Member)
- Secure login/logout functionality
- Splash screen with auto-login check

### 👥 User Roles & Permissions
- **Admin**: Full access - add/edit members, view all data, manage expenses
- **Member**: Limited access - add deposits, view reports, see payment history

### 📋 Member Management (Admin only)
- Add up to 20 members with details (Name, Base Amount, Phone, Address)
- Display members list with total deposits
- Track individual member contributions

### 💰 Monthly Deposit Tracking
- Add deposits for any member
- Select month from last 12 months dropdown
- Automatic total deposit calculation per member
- Real-time updates across all users

### 💸 Expense Management
- Multiple expense categories:
  - Conveyance
  - Entertainment
  - Fruits & Flower
  - Feeding Expense
  - Decoration
  - Sound System
  - Miscellaneous
- Add expenses with description and amount
- Track who added each expense

### 📊 Payment History
- Complete transaction log of all deposits
- Show member name, amount, month, and date
- Sorted by most recent first
- Accessible via floating action button

### 📈 Reports & Analytics
- Dashboard with summary cards (Total Deposits, Expenses, Balance)
- Pie chart showing expenses by category
- Total members count
- Color-coded balance (green for positive, red for negative)

### 🔔 Notification System
- Firebase Cloud Messaging integration
- Notifications for new deposits and expenses
- Real-time notification delivery to all users

### 🔄 Real-time Data Sync
- Firebase Realtime Database
- Instant updates across all devices
- Automatic data synchronization

## Technology Stack

- **Language**: Java
- **UI**: XML Layouts
- **Backend**: Firebase (Authentication, Realtime Database, Cloud Messaging)
- **Architecture**: Activity-based with RecyclerView adapters
- **Design**: Material Design 3 components
- **Charts**: MPAndroidChart library

## Project Structure

```
app/
├── src/main/
│   ├── java/com/fundmanager/
│   │   ├── activities/
│   │   │   ├── SplashActivity.java
│   │   │   ├── LoginActivity.java
│   │   │   ├── RegisterActivity.java
│   │   │   ├── MainActivity.java
│   │   │   ├── AddMemberActivity.java
│   │   │   ├── AddDepositActivity.java
│   │   │   ├── AddExpenseActivity.java
│   │   │   ├── PaymentHistoryActivity.java
│   │   │   └── ReportsActivity.java
│   │   ├── models/
│   │   │   ├── User.java
│   │   │   ├── Member.java
│   │   │   ├── Deposit.java
│   │   │   └── Expense.java
│   │   ├── adapters/
│   │   │   ├── MemberAdapter.java
│   │   │   └── DepositAdapter.java
│   │   └── utils/
│   │       └── MyFirebaseMessagingService.java
│   ├── res/
│   │   ├── layout/ (all activity and item layouts)
│   │   ├── values/ (colors, strings, themes)
│   │   ├── mipmap/ (app icons)
│   │   └── menu/
│   └── AndroidManifest.xml
├── build.gradle (app level)
└── google-services.json (placeholder - needs configuration)
```

## Setup Instructions

### Prerequisites
- Android Studio (latest version)
- JDK 8 or higher
- Firebase account

### Firebase Configuration

1. **Create a Firebase Project**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Click "Add project" and follow the setup wizard

2. **Add Android App to Firebase**
   - In Firebase Console, click "Add app" and select Android
   - Enter package name: `com.fundmanager`
   - Download the `google-services.json` file
   - Replace `/app/google-services.json` with your downloaded file

3. **Enable Authentication**
   - In Firebase Console, go to Authentication
   - Enable "Email/Password" sign-in method

4. **Set up Realtime Database**
   - In Firebase Console, go to Realtime Database
   - Click "Create Database"
   - Copy security rules from `FIREBASE_SECURITY_RULES.md`

5. **Enable Cloud Messaging**
   - In Firebase Console, go to Cloud Messaging
   - Note your Server Key for sending notifications

For detailed Firebase setup instructions, see [FIREBASE_SETUP.md](FIREBASE_SETUP.md)

### Build and Run

1. Clone the repository
   ```bash
   git clone https://github.com/asifmahmud71/community-fund-manager.git
   cd community-fund-manager
   ```

2. Open the project in Android Studio

3. Configure Firebase (see above)

4. Sync Gradle files

5. Build and run the app
   ```bash
   ./gradlew build
   ```

## Dependencies

```gradle
// AndroidX
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.google.android.material:material:1.11.0'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
implementation 'androidx.cardview:cardview:1.0.0'
implementation 'androidx.recyclerview:recyclerview:1.3.2'

// Firebase
implementation platform('com.google.firebase:firebase-bom:32.7.0')
implementation 'com.google.firebase:firebase-auth'
implementation 'com.google.firebase:firebase-database'
implementation 'com.google.firebase:firebase-messaging'

// Charts
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
implementation 'de.hdodenhof:circleimageview:3.1.0'
```

## Database Structure

```json
{
  "users": {
    "$userId": {
      "userId": "string",
      "name": "string",
      "email": "string",
      "phone": "string",
      "role": "admin" | "member",
      "createdAt": timestamp
    }
  },
  "members": {
    "$memberId": {
      "memberId": "string",
      "name": "string",
      "baseAmount": number,
      "totalDeposit": number,
      "amountDue": number,
      "phone": "string",
      "address": "string",
      "joinedDate": timestamp
    }
  },
  "deposits": {
    "$depositId": {
      "depositId": "string",
      "memberId": "string",
      "memberName": "string",
      "amount": number,
      "month": "MMM-yy",
      "depositDate": timestamp,
      "addedBy": "userId"
    }
  },
  "expenses": {
    "$expenseId": {
      "expenseId": "string",
      "category": "string",
      "description": "string",
      "amount": number,
      "expenseDate": timestamp,
      "addedBy": "userId"
    }
  },
  "notifications": {
    "$notificationId": {
      "title": "string",
      "message": "string",
      "timestamp": timestamp
    }
  }
}
```

## Color Scheme

- Primary: #1976D2 (Blue)
- Accent: #FF5722 (Orange)
- Success: #4CAF50 (Green)
- Error: #F44336 (Red)
- Background: #F5F5F5 (Light Gray)

## Key Screens

### 1. Splash Screen
- App logo and title
- Loading indicator with 2-second delay
- Auto-login check

### 2. Login Screen
- Email and password fields
- Register link
- Material Design card layout

### 3. Register Screen
- Name, email, phone, password fields
- Role selection (Admin/Member radio buttons)

### 4. Main Dashboard
- Welcome message with user role
- Summary stats (deposits/expenses/balance)
- 4 action cards in 2x2 grid
- FAB for payment history

### 5. Add Member (Admin only)
- Form with name, base amount, phone, address
- RecyclerView showing all members

### 6. Add Deposit
- Member spinner
- Month spinner (last 12 months)
- Amount input

### 7. Add Expense
- Category spinner (7 categories)
- Description multiline input
- Amount input

### 8. Payment History
- RecyclerView with CardView items
- Member name, amount, month, formatted date/time

### 9. Reports
- Summary cards
- Pie chart showing expense breakdown by category

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License.

## Support

For support and questions, please open an issue on GitHub.

## Acknowledgments

- Firebase for backend services
- MPAndroidChart for charting library
- Material Design components
