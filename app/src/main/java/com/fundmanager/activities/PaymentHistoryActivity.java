package com.fundmanager.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fundmanager.R;
import com.fundmanager.adapters.DepositAdapter;
import com.fundmanager.models.Deposit;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PaymentHistoryActivity extends AppCompatActivity {

    private RecyclerView paymentHistoryRecyclerView;
    private TextView noPaymentsTextView;
    private DepositAdapter depositAdapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.payment_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        paymentHistoryRecyclerView = findViewById(R.id.paymentHistoryRecyclerView);
        noPaymentsTextView = findViewById(R.id.noPaymentsTextView);

        // Setup RecyclerView
        depositAdapter = new DepositAdapter();
        paymentHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        paymentHistoryRecyclerView.setAdapter(depositAdapter);

        // Load payment history
        loadPaymentHistory();
    }

    private void loadPaymentHistory() {
        databaseReference.child("deposits").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Deposit> deposits = new ArrayList<>();
                for (DataSnapshot depositSnapshot : snapshot.getChildren()) {
                    Deposit deposit = depositSnapshot.getValue(Deposit.class);
                    if (deposit != null) {
                        deposits.add(deposit);
                    }
                }

                if (deposits.isEmpty()) {
                    noPaymentsTextView.setVisibility(View.VISIBLE);
                    paymentHistoryRecyclerView.setVisibility(View.GONE);
                } else {
                    noPaymentsTextView.setVisibility(View.GONE);
                    paymentHistoryRecyclerView.setVisibility(View.VISIBLE);
                    
                    // Sort by most recent first
                    Collections.sort(deposits, (d1, d2) -> 
                            Long.compare(d2.getDepositDate(), d1.getDepositDate()));
                    
                    depositAdapter.setDeposits(deposits);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PaymentHistoryActivity.this, 
                        getString(R.string.error_occurred), 
                        Toast.LENGTH_SHORT).show();
            }
        });
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
