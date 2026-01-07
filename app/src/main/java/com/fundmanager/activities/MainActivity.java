package com.fundmanager.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.fundmanager.R;
import com.fundmanager.models.User;
import com.fundmanager.utils.Constants;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeTextView, roleTextView;
    private TextView totalMembersTextView, totalDepositsTextView;
    private TextView totalExpensesTextView, currentBalanceTextView;
    private MaterialCardView membersCard, depositsCard, expensesCard, reportsCard;
    private MaterialCardView balanceCard;
    private FloatingActionButton fabPaymentHistory;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String currentUserId;
    private User currentUser;

    private double totalDeposits = 0;
    private double totalExpenses = 0;
    private int totalMembers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        
        if (firebaseUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }
        
        currentUserId = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        welcomeTextView = findViewById(R.id.welcomeTextView);
        roleTextView = findViewById(R.id.roleTextView);
        totalMembersTextView = findViewById(R.id.totalMembersTextView);
        totalDepositsTextView = findViewById(R.id.totalDepositsTextView);
        totalExpensesTextView = findViewById(R.id.totalExpensesTextView);
        currentBalanceTextView = findViewById(R.id.currentBalanceTextView);
        
        membersCard = findViewById(R.id.membersCard);
        depositsCard = findViewById(R.id.depositsCard);
        expensesCard = findViewById(R.id.expensesCard);
        reportsCard = findViewById(R.id.reportsCard);
        balanceCard = findViewById(R.id.balanceCard);
        fabPaymentHistory = findViewById(R.id.fabPaymentHistory);

        // Request notification permission for Android 13+
        requestNotificationPermission();

        // Load user data
        loadUserData();

        // Load statistics
        loadStatistics();

        // Set click listeners
        membersCard.setOnClickListener(v -> {
            if (currentUser != null && currentUser.isAdmin()) {
                startActivity(new Intent(MainActivity.this, AddMemberActivity.class));
            } else {
                Toast.makeText(MainActivity.this, 
                        getString(R.string.permission_denied), 
                        Toast.LENGTH_SHORT).show();
            }
        });

        depositsCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddDepositActivity.class));
        });

        expensesCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddExpenseActivity.class));
        });

        reportsCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ReportsActivity.class));
        });

        fabPaymentHistory.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PaymentHistoryActivity.class));
        });
    }

    private void loadUserData() {
        databaseReference.child("users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        currentUser = snapshot.getValue(User.class);
                        if (currentUser != null) {
                            welcomeTextView.setText(getString(R.string.welcome) + ", " + 
                                    currentUser.getName() + "!");
                            roleTextView.setText("Role: " + 
                                    (currentUser.isAdmin() ? "Admin" : "Member"));
                            
                            // Show/hide admin-only features
                            if (!currentUser.isAdmin()) {
                                // Members cannot access member management
                                membersCard.setAlpha(0.5f);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, 
                                getString(R.string.error_occurred), 
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadStatistics() {
        // Load total members
        databaseReference.child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalMembers = (int) snapshot.getChildrenCount();
                totalMembersTextView.setText(String.valueOf(totalMembers));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        // Load total deposits
        databaseReference.child("deposits").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalDeposits = 0;
                for (DataSnapshot depositSnapshot : snapshot.getChildren()) {
                    Double amount = depositSnapshot.child("amount").getValue(Double.class);
                    if (amount != null) {
                        totalDeposits += amount;
                    }
                }
                totalDepositsTextView.setText(String.format("৳ %.2f", totalDeposits));
                updateBalance();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        // Load total expenses
        databaseReference.child("expenses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalExpenses = 0;
                for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                    Double amount = expenseSnapshot.child("amount").getValue(Double.class);
                    if (amount != null) {
                        totalExpenses += amount;
                    }
                }
                totalExpensesTextView.setText(String.format("৳ %.2f", totalExpenses));
                updateBalance();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void updateBalance() {
        double balance = totalDeposits - totalExpenses;
        currentBalanceTextView.setText(String.format("৳ %.2f", balance));
        
        // Change color based on balance
        if (balanceCard != null) {
            if (balance >= 0) {
                balanceCard.setCardBackgroundColor(getResources().getColor(R.color.success));
            } else {
                balanceCard.setCardBackgroundColor(getResources().getColor(R.color.error));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void requestNotificationPermission() {
        // Request notification permission for Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        Constants.NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Disable back button on main screen
        moveTaskToBack(true);
    }
}
