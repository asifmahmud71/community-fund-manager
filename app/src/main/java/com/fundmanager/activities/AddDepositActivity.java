package com.fundmanager.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.fundmanager.R;
import com.fundmanager.models.Deposit;
import com.fundmanager.models.Member;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddDepositActivity extends AppCompatActivity {

    private Spinner memberSpinner, monthSpinner;
    private TextInputEditText amountEditText;
    private MaterialButton addDepositButton;
    private ProgressBar progressBar;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    
    private Map<String, String> memberMap; // Name -> ID
    private List<String> memberNames;
    private List<String> months;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deposit);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.add_deposit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        memberSpinner = findViewById(R.id.memberSpinner);
        monthSpinner = findViewById(R.id.monthSpinner);
        amountEditText = findViewById(R.id.amountEditText);
        addDepositButton = findViewById(R.id.addDepositButton);
        progressBar = findViewById(R.id.progressBar);

        // Initialize data structures
        memberMap = new HashMap<>();
        memberNames = new ArrayList<>();
        months = new ArrayList<>();

        // Load members
        loadMembers();

        // Generate months
        generateMonths();

        // Set click listener
        addDepositButton.setOnClickListener(v -> addDeposit());
    }

    private void loadMembers() {
        databaseReference.child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                memberNames.clear();
                memberMap.clear();
                
                memberNames.add(getString(R.string.select_member));
                
                for (DataSnapshot memberSnapshot : snapshot.getChildren()) {
                    Member member = memberSnapshot.getValue(Member.class);
                    if (member != null) {
                        memberNames.add(member.getName());
                        memberMap.put(member.getName(), member.getMemberId());
                    }
                }

                // Setup spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        AddDepositActivity.this,
                        android.R.layout.simple_spinner_item,
                        memberNames
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                memberSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddDepositActivity.this, 
                        getString(R.string.error_occurred), 
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateMonths() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM-yy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        
        months.add(getString(R.string.select_month));
        
        // Generate last 12 months
        for (int i = 0; i < 12; i++) {
            months.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.MONTH, -1);
        }

        // Setup spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                months
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(adapter);
    }

    private void addDeposit() {
        int memberPosition = memberSpinner.getSelectedItemPosition();
        int monthPosition = monthSpinner.getSelectedItemPosition();
        String amountStr = amountEditText.getText().toString().trim();

        // Validate inputs
        if (memberPosition == 0) {
            Toast.makeText(this, getString(R.string.member_required), Toast.LENGTH_SHORT).show();
            return;
        }

        if (monthPosition == 0) {
            Toast.makeText(this, getString(R.string.month_required), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(amountStr)) {
            amountEditText.setError(getString(R.string.amount_required));
            amountEditText.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            amountEditText.setError(getString(R.string.invalid_amount));
            amountEditText.requestFocus();
            return;
        }

        String memberName = memberNames.get(memberPosition);
        String memberId = memberMap.get(memberName);
        String month = months.get(monthPosition);

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);
        addDepositButton.setEnabled(false);

        // Create deposit object
        String depositId = databaseReference.child("deposits").push().getKey();
        Deposit deposit = new Deposit(depositId, memberId, memberName, amount, month,
                System.currentTimeMillis(), mAuth.getCurrentUser().getUid());

        // Save to Firebase
        databaseReference.child("deposits").child(depositId).setValue(deposit)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Update member's total deposit
                        updateMemberTotalDeposit(memberId, memberName, amount, month);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        addDepositButton.setEnabled(true);
                        Toast.makeText(AddDepositActivity.this, 
                                getString(R.string.deposit_add_failed), 
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateMemberTotalDeposit(String memberId, String memberName, 
                                         double depositAmount, String month) {
        databaseReference.child("members").child(memberId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Member member = snapshot.getValue(Member.class);
                        if (member != null) {
                            double newTotal = member.getTotalDeposit() + depositAmount;
                            double newDue = member.getBaseAmount() - newTotal;
                            
                            // Use batched update for atomic operation
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("totalDeposit", newTotal);
                            updates.put("amountDue", newDue);
                            
                            databaseReference.child("members").child(memberId)
                                    .updateChildren(updates)
                                    .addOnCompleteListener(updateTask -> {
                                        progressBar.setVisibility(View.GONE);
                                        addDepositButton.setEnabled(true);
                                        
                                        if (updateTask.isSuccessful()) {
                                            Toast.makeText(AddDepositActivity.this, 
                                                    getString(R.string.deposit_added), 
                                                    Toast.LENGTH_SHORT).show();

                                            // Send notification
                                            String message = String.format(getString(R.string.deposit_notification),
                                                    memberName, String.format("৳ %.2f", depositAmount), month);
                                            sendNotification(getString(R.string.new_deposit), message);

                                            // Clear form
                                            memberSpinner.setSelection(0);
                                            monthSpinner.setSelection(0);
                                            amountEditText.setText("");
                                        } else {
                                            Toast.makeText(AddDepositActivity.this, 
                                                    getString(R.string.error_occurred), 
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                        addDepositButton.setEnabled(true);
                        Toast.makeText(AddDepositActivity.this, 
                                getString(R.string.error_occurred), 
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendNotification(String title, String message) {
        String notificationId = databaseReference.child("notifications").push().getKey();
        databaseReference.child("notifications").child(notificationId).child("title").setValue(title);
        databaseReference.child("notifications").child(notificationId).child("message").setValue(message);
        databaseReference.child("notifications").child(notificationId).child("timestamp")
                .setValue(System.currentTimeMillis());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
