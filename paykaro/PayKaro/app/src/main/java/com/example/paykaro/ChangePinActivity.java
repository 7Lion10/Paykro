package com.example.paykaro;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePinActivity extends AppCompatActivity {

    private EditText[] oldPinFields;
    private EditText[] newPinFields;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Initialize Old PIN fields
        oldPinFields = new EditText[]{
                findViewById(R.id.old_pin_digit_1),
                findViewById(R.id.old_pin_digit_2),
                findViewById(R.id.old_pin_digit_3),
                findViewById(R.id.old_pin_digit_4),
                findViewById(R.id.old_pin_digit_5),
                findViewById(R.id.old_pin_digit_6)
        };

        // Initialize New PIN fields
        newPinFields = new EditText[]{
                findViewById(R.id.new_pin_digit_1),
                findViewById(R.id.new_pin_digit_2),
                findViewById(R.id.new_pin_digit_3),
                findViewById(R.id.new_pin_digit_4),
                findViewById(R.id.new_pin_digit_5),
                findViewById(R.id.new_pin_digit_6)
        };

        // Set TextWatchers for each PIN EditText
        setupPinInputListeners(oldPinFields);
        setupPinInputListeners(newPinFields);

        Button proceedButton = findViewById(R.id.btn_proceed);
        proceedButton.setOnClickListener(v -> changePin());
    }

    private void setupPinInputListeners(final EditText[] pinFields) {
        for (int i = 0; i < pinFields.length; i++) {
            final int currentIndex = i;
            pinFields[currentIndex].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Do nothing
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && currentIndex < pinFields.length - 1) {
                        pinFields[currentIndex + 1].requestFocus();
                    } else if (s.length() == 0 && currentIndex > 0) {
                        pinFields[currentIndex - 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Do nothing
                }
            });
        }
    }

    private void changePin() {
        // Collect old PIN
        StringBuilder oldPinBuilder = new StringBuilder();
        for (EditText pinField : oldPinFields) {
            oldPinBuilder.append(pinField.getText().toString());
        }
        String oldPin = oldPinBuilder.toString();

        // Collect new PIN
        StringBuilder newPinBuilder = new StringBuilder();
        for (EditText pinField : newPinFields) {
            newPinBuilder.append(pinField.getText().toString());
        }
        String newPin = newPinBuilder.toString();

        // Validate input
        if (oldPin.isEmpty() || newPin.isEmpty() || newPin.length() != 6) {
            Toast.makeText(this, "Please enter all fields correctly.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current logged-in user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve the user's stored PIN from Firebase
        String userId = user.getUid();
        mDatabase.child(userId).child("pin").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String storedPin = task.getResult().getValue(String.class);

                // Log the stored and entered PINs for cross-verification
                Log.d("ChangePinActivity", "Stored PIN: " + storedPin); // Log the stored PIN
                Log.d("ChangePinActivity", "Entered Old PIN: " + oldPin); // Log the entered PIN

                if (storedPin != null && storedPin.trim().equals(oldPin.trim())) {
                    // Old PIN matches, send password reset email
                    mAuth.sendPasswordResetEmail(user.getEmail())
                            .addOnCompleteListener(resetTask -> {
                                if (resetTask.isSuccessful()) {
                                    Toast.makeText(ChangePinActivity.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ChangePinActivity.this, "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // Old PIN does not match
                    Toast.makeText(ChangePinActivity.this, "Old PIN does not match.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ChangePinActivity.this, "Failed to retrieve stored PIN.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
