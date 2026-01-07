package com.fundmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fundmanager.R;
import com.fundmanager.models.Deposit;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DepositAdapter extends RecyclerView.Adapter<DepositAdapter.DepositViewHolder> {

    private List<Deposit> depositList;
    private SimpleDateFormat dateFormat;

    public DepositAdapter() {
        this.depositList = new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
    }

    @NonNull
    @Override
    public DepositViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_deposit, parent, false);
        return new DepositViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DepositViewHolder holder, int position) {
        Deposit deposit = depositList.get(position);
        holder.bind(deposit, dateFormat);
    }

    @Override
    public int getItemCount() {
        return depositList.size();
    }

    public void setDeposits(List<Deposit> deposits) {
        this.depositList = deposits;
        notifyDataSetChanged();
    }

    static class DepositViewHolder extends RecyclerView.ViewHolder {
        private TextView memberNameTextView;
        private TextView amountTextView;
        private TextView monthTextView;
        private TextView dateTextView;

        public DepositViewHolder(@NonNull View itemView) {
            super(itemView);
            memberNameTextView = itemView.findViewById(R.id.memberNameTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            monthTextView = itemView.findViewById(R.id.monthTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }

        public void bind(Deposit deposit, SimpleDateFormat dateFormat) {
            memberNameTextView.setText(deposit.getMemberName());
            amountTextView.setText(String.format("৳ %.2f", deposit.getAmount()));
            monthTextView.setText(deposit.getMonth());
            dateTextView.setText(dateFormat.format(new Date(deposit.getDepositDate())));
        }
    }
}
