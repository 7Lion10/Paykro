package com.example.paykaro;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;

public class ChangePinActivity extends AppCompatActivity {

    private EditText oldPinDigit1, oldPinDigit2, oldPinDigit3, oldPinDigit4, oldPinDigit5, oldPinDigit6;
    private EditText newPinDigit1, newPinDigit2, newPinDigit3, newPinDigit4, newPinDigit5, newPinDigit6;
    private Button btnProceed;
    private ImageView backButton; // Back button

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin);

        // Initialize Firebase Auth and Database reference
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        }

        // Initialize views for old PIN
        oldPinDigit1 = findViewById(R.id.old_pin_digit_1);
        oldPinDigit2 = findViewById(R.id.old_pin_digit_2);
        oldPinDigit3 = findViewById(R.id.old_pin_digit_3);
        oldPinDigit4 = findViewById(R.id.old_pin_digit_4);
        oldPinDigit5 = findViewById(R.id.old_pin_digit_5);
        oldPinDigit6 = findViewById(R.id.old_pin_digit_6);

        // Initialize views for new PIN
        newPinDigit1 = findViewById(R.id.new_pin_digit_1);
        newPinDigit2 = findViewById(R.id.new_pin_digit_2);
        newPinDigit3 = findViewById(R.id.new_pin_digit_3);
        newPinDigit4 = findViewById(R.id.new_pin_digit_4);
        newPinDigit5 = findViewById(R.id.new_pin_digit_5);
        newPinDigit6 = findViewById(R.id.new_pin_digit_6);

        // Initialize back button and set OnClickListener to go back to ProfileActivity
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChangePinActivity.this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Add TextWatchers to automatically move to the next field
        List<EditText> oldPinFields = new ArrayList<>(List.of(oldPinDigit1, oldPinDigit2, oldPinDigit3, oldPinDigit4, oldPinDigit5, oldPinDigit6));
        List<EditText> newPinFields = new ArrayList<>(List.of(newPinDigit1, newPinDigit2, newPinDigit3, newPinDigit4, newPinDigit5, newPinDigit6));

        setupPinAutoMove(oldPinFields);
        setupPinAutoMove(newPinFields);

        // Proceed button click
        btnProceed = findViewById(R.id.btn_proceed);
        btnProceed.setOnClickListener(v -> validateAndChangePin());
    }

    private void setupPinAutoMove(List<EditText> pinFields) {
        for (int i = 0; i < pinFields.size(); i++) {
            EditText currentField = pinFields.get(i);
            final int currentIndex = i;
            currentField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Do nothing here
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Move to next field when a digit is entered
                    if (s.length() == 1 && currentIndex < pinFields.size() - 1) {
                        pinFields.get(currentIndex + 1).requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // If deleting (backspace), move to previous field if the current is empty
                    if (s.length() == 0 && currentIndex > 0) {
                        pinFields.get(currentIndex - 1).requestFocus();
                    }
                }
            });
        }
    }

    private void validateAndChangePin() {
        // Get the old PIN entered by the user
        String oldPin = getPinFromFields(oldPinDigit1, oldPinDigit2, oldPinDigit3, oldPinDigit4, oldPinDigit5, oldPinDigit6);
        if (TextUtils.isEmpty(oldPin) || oldPin.length() != 6) {
            Toast.makeText(this, "Please enter a valid 6-digit old PIN", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the new PIN entered by the user
        String newPin = getPinFromFields(newPinDigit1, newPinDigit2, newPinDigit3, newPinDigit4, newPinDigit5, newPinDigit6);
        if (TextUtils.isEmpty(newPin) || newPin.length() != 6) {
            Toast.makeText(this, "Please enter a valid 6-digit new PIN", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if old PIN matches the one in Firebase
        userRef.child("pin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String savedPin = dataSnapshot.getValue(String.class);
                if (savedPin != null && savedPin.equals(oldPin)) {
                    // Old PIN matches, update with the new PIN
                    userRef.child("pin").setValue(newPin).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChangePinActivity.this, "PIN updated successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ChangePinActivity.this, ProfileActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ChangePinActivity.this, "Failed to update PIN", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Old PIN does not match
                    Toast.makeText(ChangePinActivity.this, "Old PIN is incorrect", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChangePinActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper function to construct a PIN string from the digit fields
    private String getPinFromFields(EditText... pinDigits) {
        StringBuilder pinBuilder = new StringBuilder();
        for (EditText digit : pinDigits) {
            pinBuilder.append(digit.getText().toString().trim());
        }
        return pinBuilder.toString();
    }
}
