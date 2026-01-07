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
import com.fundmanager.models.Expense;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class AddExpenseActivity extends AppCompatActivity {

    private Spinner categorySpinner;
    private TextInputEditText descriptionEditText, amountEditText;
    private MaterialButton addExpenseButton;
    private ProgressBar progressBar;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private List<String> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.add_expense);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        categorySpinner = findViewById(R.id.categorySpinner);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        amountEditText = findViewById(R.id.amountEditText);
        addExpenseButton = findViewById(R.id.addExpenseButton);
        progressBar = findViewById(R.id.progressBar);

        // Setup categories
        setupCategories();

        // Set click listener
        addExpenseButton.setOnClickListener(v -> addExpense());
    }

    private void setupCategories() {
        categories = new ArrayList<>();
        categories.add(getString(R.string.select_category));
        categories.add(getString(R.string.conveyance));
        categories.add(getString(R.string.entertainment));
        categories.add(getString(R.string.fruits_flower));
        categories.add(getString(R.string.feeding_expense));
        categories.add(getString(R.string.decoration));
        categories.add(getString(R.string.sound_system));
        categories.add(getString(R.string.miscellaneous));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void addExpense() {
        int categoryPosition = categorySpinner.getSelectedItemPosition();
        String description = descriptionEditText.getText().toString().trim();
        String amountStr = amountEditText.getText().toString().trim();

        // Validate inputs
        if (categoryPosition == 0) {
            Toast.makeText(this, getString(R.string.category_required), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            descriptionEditText.setError(getString(R.string.description_required));
            descriptionEditText.requestFocus();
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

        String category = categories.get(categoryPosition);

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);
        addExpenseButton.setEnabled(false);

        // Create expense object
        String expenseId = databaseReference.child("expenses").push().getKey();
        Expense expense = new Expense(expenseId, category, description, amount,
                System.currentTimeMillis(), mAuth.getCurrentUser().getUid());

        // Save to Firebase
        databaseReference.child("expenses").child(expenseId).setValue(expense)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    addExpenseButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(AddExpenseActivity.this, 
                                getString(R.string.expense_added), 
                                Toast.LENGTH_SHORT).show();

                        // Clear form
                        categorySpinner.setSelection(0);
                        descriptionEditText.setText("");
                        amountEditText.setText("");

                        // Send notification
                        String message = String.format(getString(R.string.expense_notification),
                                String.format("৳ %.2f", amount), category);
                        sendNotification(getString(R.string.new_expense), message);
                    } else {
                        Toast.makeText(AddExpenseActivity.this, 
                                getString(R.string.expense_add_failed), 
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
