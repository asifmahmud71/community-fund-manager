package com.fundmanager.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.fundmanager.R;
import com.fundmanager.models.Expense;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsActivity extends AppCompatActivity {

    private TextView totalMembersTextView, totalDepositsTextView, totalExpensesTextView;
    private TextView currentBalanceTextView, noExpensesTextView;
    private PieChart expenseChart;
    private DatabaseReference databaseReference;

    private double totalDeposits = 0;
    private double totalExpenses = 0;
    private int totalMembers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.reports);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        totalMembersTextView = findViewById(R.id.totalMembersTextView);
        totalDepositsTextView = findViewById(R.id.totalDepositsTextView);
        totalExpensesTextView = findViewById(R.id.totalExpensesTextView);
        currentBalanceTextView = findViewById(R.id.currentBalanceTextView);
        noExpensesTextView = findViewById(R.id.noExpensesTextView);
        expenseChart = findViewById(R.id.expenseChart);

        // Load data
        loadStatistics();
        loadExpenseBreakdown();
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
                totalDepositsTextView.setText(String.format("৳ %.0f", totalDeposits));
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
                totalExpensesTextView.setText(String.format("৳ %.0f", totalExpenses));
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
        View balanceCard = findViewById(R.id.currentBalanceTextView).getParent().getParent();
        if (balanceCard instanceof MaterialCardView) {
            MaterialCardView cardView = (MaterialCardView) balanceCard;
            if (balance >= 0) {
                cardView.setCardBackgroundColor(getResources().getColor(R.color.success));
            } else {
                cardView.setCardBackgroundColor(getResources().getColor(R.color.error));
            }
        }
    }

    private void loadExpenseBreakdown() {
        databaseReference.child("expenses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Float> categoryTotals = new HashMap<>();
                
                for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                    Expense expense = expenseSnapshot.getValue(Expense.class);
                    if (expense != null) {
                        String category = expense.getCategory();
                        float amount = (float) expense.getAmount();
                        
                        if (categoryTotals.containsKey(category)) {
                            categoryTotals.put(category, categoryTotals.get(category) + amount);
                        } else {
                            categoryTotals.put(category, amount);
                        }
                    }
                }

                if (categoryTotals.isEmpty()) {
                    noExpensesTextView.setVisibility(View.VISIBLE);
                    expenseChart.setVisibility(View.GONE);
                } else {
                    noExpensesTextView.setVisibility(View.GONE);
                    expenseChart.setVisibility(View.VISIBLE);
                    setupPieChart(categoryTotals);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void setupPieChart(Map<String, Float> categoryTotals) {
        List<PieEntry> entries = new ArrayList<>();
        
        for (Map.Entry<String, Float> entry : categoryTotals.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expense Categories");
        
        // Set colors
        List<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.chart_1));
        colors.add(getResources().getColor(R.color.chart_2));
        colors.add(getResources().getColor(R.color.chart_3));
        colors.add(getResources().getColor(R.color.chart_4));
        colors.add(getResources().getColor(R.color.chart_5));
        colors.add(getResources().getColor(R.color.chart_6));
        colors.add(getResources().getColor(R.color.chart_7));
        dataSet.setColors(colors);
        
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(expenseChart));

        expenseChart.setData(data);
        expenseChart.setUsePercentValues(true);
        expenseChart.getDescription().setEnabled(false);
        expenseChart.setDrawHoleEnabled(true);
        expenseChart.setHoleColor(Color.WHITE);
        expenseChart.setTransparentCircleRadius(61f);
        expenseChart.setDrawEntryLabels(false);
        expenseChart.getLegend().setEnabled(true);
        expenseChart.getLegend().setTextSize(12f);
        expenseChart.animateY(1000);
        expenseChart.invalidate();
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
