package com.fundmanager.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fundmanager.R;
import com.fundmanager.adapters.MemberAdapter;
import com.fundmanager.models.Member;
import com.fundmanager.utils.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AddMemberActivity extends AppCompatActivity {

    private TextInputEditText nameEditText, baseAmountEditText, phoneEditText, addressEditText;
    private MaterialButton addMemberButton;
    private ProgressBar progressBar;
    private RecyclerView membersRecyclerView;
    private TextView noMembersTextView;

    private DatabaseReference databaseReference;
    private MemberAdapter memberAdapter;
    private int currentMemberCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.manage_members);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        baseAmountEditText = findViewById(R.id.baseAmountEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        addMemberButton = findViewById(R.id.addMemberButton);
        progressBar = findViewById(R.id.progressBar);
        membersRecyclerView = findViewById(R.id.membersRecyclerView);
        noMembersTextView = findViewById(R.id.noMembersTextView);

        // Setup RecyclerView
        memberAdapter = new MemberAdapter();
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        membersRecyclerView.setAdapter(memberAdapter);

        // Load members
        loadMembers();

        // Set click listener
        addMemberButton.setOnClickListener(v -> addMember());
    }

    private void loadMembers() {
        databaseReference.child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Member> members = new ArrayList<>();
                for (DataSnapshot memberSnapshot : snapshot.getChildren()) {
                    Member member = memberSnapshot.getValue(Member.class);
                    if (member != null) {
                        members.add(member);
                    }
                }
                
                currentMemberCount = members.size();
                
                if (members.isEmpty()) {
                    noMembersTextView.setVisibility(View.VISIBLE);
                    membersRecyclerView.setVisibility(View.GONE);
                } else {
                    noMembersTextView.setVisibility(View.GONE);
                    membersRecyclerView.setVisibility(View.VISIBLE);
                    memberAdapter.setMembers(members);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddMemberActivity.this, 
                        getString(R.string.error_occurred), 
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMember() {
        String name = nameEditText.getText().toString().trim();
        String baseAmountStr = baseAmountEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError(getString(R.string.name_required));
            nameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(baseAmountStr)) {
            baseAmountEditText.setError(getString(R.string.amount_required));
            baseAmountEditText.requestFocus();
            return;
        }

        double baseAmount;
        try {
            baseAmount = Double.parseDouble(baseAmountStr);
        } catch (NumberFormatException e) {
            baseAmountEditText.setError(getString(R.string.invalid_amount));
            baseAmountEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            phoneEditText.setError(getString(R.string.phone_required));
            phoneEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(address)) {
            addressEditText.setError(getString(R.string.address) + " is required");
            addressEditText.requestFocus();
            return;
        }

        // Check member limit
        if (currentMemberCount >= Constants.MAX_MEMBERS) {
            Toast.makeText(this, getString(R.string.member_limit_reached), 
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);
        addMemberButton.setEnabled(false);

        // Create member object
        String memberId = databaseReference.child("members").push().getKey();
        Member member = new Member(memberId, name, baseAmount, 0, baseAmount, 
                phone, address, System.currentTimeMillis());

        // Save to Firebase
        databaseReference.child("members").child(memberId).setValue(member)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    addMemberButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(AddMemberActivity.this, 
                                getString(R.string.member_added), 
                                Toast.LENGTH_SHORT).show();
                        
                        // Clear form
                        nameEditText.setText("");
                        baseAmountEditText.setText("");
                        phoneEditText.setText("");
                        addressEditText.setText("");
                        
                        // Send notification
                        sendNotification("New Member Added", name + " has been added to the community");
                    } else {
                        Toast.makeText(AddMemberActivity.this, 
                                getString(R.string.member_add_failed), 
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
