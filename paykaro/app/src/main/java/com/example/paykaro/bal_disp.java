package com.example.paykaro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class bal_disp extends AppCompatActivity {
    private Button home;
    private TextView balanceText;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bal_disp);

        home = findViewById(R.id.btnHome);
        balanceText = findViewById(R.id.tvBalanceAmount);

        // Initialize Firebase Auth and Database reference
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
            fetchBalance();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        // Home button click
        home.setOnClickListener(view -> {
            Intent intentHome = new Intent(bal_disp.this, MainActivity.class);
            startActivity(intentHome);
            finish();
        });
    }

    private void fetchBalance() {
        // Assuming "balance" is the key where balance data is stored in Firebase for each user
        userRef.child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Double balance = dataSnapshot.getValue(Double.class);
                    if (balance != null) {
                        balanceText.setText("₹ " + balance);
                    } else {
                        balanceText.setText("Balance not available");
                    }
                } else {
                    balanceText.setText("Balance not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(bal_disp.this, "Failed to fetch balance: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
