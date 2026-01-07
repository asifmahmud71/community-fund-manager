package com.fundmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fundmanager.R;
import com.fundmanager.models.Member;
import java.util.ArrayList;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private List<Member> memberList;

    public MemberAdapter() {
        this.memberList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Member member = memberList.get(position);
        holder.bind(member);
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public void setMembers(List<Member> members) {
        this.memberList = members;
        notifyDataSetChanged();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        private TextView memberNameTextView;
        private TextView baseAmountTextView;
        private TextView totalDepositTextView;
        private TextView phoneTextView;
        private TextView addressTextView;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            memberNameTextView = itemView.findViewById(R.id.memberNameTextView);
            baseAmountTextView = itemView.findViewById(R.id.baseAmountTextView);
            totalDepositTextView = itemView.findViewById(R.id.totalDepositTextView);
            phoneTextView = itemView.findViewById(R.id.phoneTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
        }

        public void bind(Member member) {
            memberNameTextView.setText(member.getName());
            baseAmountTextView.setText(String.format("৳ %.2f", member.getBaseAmount()));
            totalDepositTextView.setText(String.format(": ৳ %.2f", member.getTotalDeposit()));
            phoneTextView.setText(member.getPhone());
            addressTextView.setText(member.getAddress());
        }
    }
}
