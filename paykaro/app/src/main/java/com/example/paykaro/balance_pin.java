package com.example.paykaro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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

public class balance_pin extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String userId;
    private TextView bankNameTextView;
    private EditText upiPinInput;
    private Button validatePinButton,back;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_pin);

        back = findViewById(R.id.back);
        back.setOnClickListener(v->finish());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        bankNameTextView = findViewById(R.id.bank_name);
        upiPinInput = findViewById(R.id.upi_pin_input);
        validatePinButton = findViewById(R.id.check_button);

        // Retrieve the bank name passed from MainActivity
        String bankName = getIntent().getStringExtra("bankName");
        if (bankName != null) {
            bankNameTextView.setText(bankName);
        }

        // Handle PIN validation button click
        validatePinButton.setOnClickListener(v -> {
            String pin = upiPinInput.getText().toString().trim();
            if (pin.length() < 6) {
                Toast.makeText(balance_pin.this, "Please enter a valid 6-digit UPI PIN", Toast.LENGTH_SHORT).show();
            } else {
                validatePin(pin);
            }
        });
    }

    // Validate the entered PIN against the stored PIN in Firebase
    private void validatePin(String pin) {
        userRef.child("pin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String storedPin = snapshot.getValue(String.class);
                    Log.d("PIN Validation", "Stored PIN: " + storedPin + ", Entered PIN: " + pin);

                    if (storedPin != null && storedPin.trim().equals(pin.trim())) {
                        Toast.makeText(balance_pin.this, "PIN validated successfully!", Toast.LENGTH_SHORT).show();

                        // Create an intent to open bal_disp.java
                        Intent intent = new Intent(balance_pin.this, bal_disp.class);
                        startActivity(intent);
                        finish(); // Optionally finish this activity if you don't want to return to it
                    } else {
                        Toast.makeText(balance_pin.this, "Invalid UPI PIN", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(balance_pin.this, "UPI PIN not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(balance_pin.this, "Failed to validate UPI PIN", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
