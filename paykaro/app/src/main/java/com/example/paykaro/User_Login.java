package com.example.paykaro;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class User_Login extends AppCompatActivity {
    private EditText emailField, pinField;
    private TextView registerLink, forgetPin;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize views
        ImageButton togglePinVisibility = findViewById(R.id.togglePinVisibility);
        emailField = findViewById(R.id.email);
        pinField = findViewById(R.id.pin);
        registerLink = findViewById(R.id.registerLink);
        forgetPin = findViewById(R.id.forgotPin);

        // Handle register link click and reset fields
        registerLink.setOnClickListener(v -> {
            resetFields();  // Reset email and pin fields
            Intent i = new Intent(this, UserSignUp.class);
            startActivity(i);
        });

        // Toggle PIN visibility
        togglePinVisibility.setOnClickListener(v -> {
            if (pinField.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                pinField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePinVisibility.setImageResource(R.drawable.baseline_visibility_24);
            } else {
                pinField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePinVisibility.setImageResource(R.drawable.baseline_visibility_off_24);
            }
            pinField.setSelection(pinField.getText().length());
        });

        // Handle login button click
        findViewById(R.id.loginButton).setOnClickListener(v -> loginUser());

        // Handle forget PIN click
        forgetPin.setOnClickListener(v -> {
            resetFields();  // Reset email and pin fields
            showResetPinDialog();
        });
    }

    // Reset fields method
    private void resetFields() {
        emailField.setText("");
        pinField.setText("");
    }

    // Show dialog to enter email for resetting PIN
    private void showResetPinDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset PIN");

        final EditText emailInput = new EditText(this);
        emailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailInput.setHint("Enter your registered email");  // Add hint
        builder.setView(emailInput);

        builder.setPositiveButton("Send Reset Link", null);  // Set to null, will handle manually
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();

        // Set the button's onClickListener manually to handle validation before dismissing dialog
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String email = emailInput.getText().toString().trim();

                // If the email is empty, show error in the EditText field
                if (TextUtils.isEmpty(email)) {
                    emailInput.setError("Email is required");
                    emailInput.requestFocus();
                } else {
                    // Email is not empty, send reset link
                    sendPasswordResetLink(email);
                    dialog.dismiss();  // Close the dialog after the reset link is sent
                }
            });
        });

        dialog.show();
    }

    // Send the password reset email using Firebase Realtime Database
    private void sendPasswordResetLink(String email) {
        if (email.isEmpty()) {
            Toast.makeText(User_Login.this, "Please enter an email.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if email exists in the Realtime Database
        userRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Email found in the database
                    Log.d("DEBUG", "Email found in database. Sending reset link to: " + email);

                    // Send password reset link
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(resetTask -> {
                                if (resetTask.isSuccessful()) {
                                    Log.d("DEBUG", "Reset link sent successfully to " + email);
                                    Toast.makeText(User_Login.this, "Reset link sent to your email.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d("DEBUG", "Error sending reset link: " + resetTask.getException().getMessage());
                                    Toast.makeText(User_Login.this, "Error: " + resetTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // Email not found in the database
                    Log.d("DEBUG", "Email not registered in the database.");
                    Toast.makeText(User_Login.this, "This email is not registered.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DEBUG", "Database error: " + databaseError.getMessage());
                Toast.makeText(User_Login.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Login user
    private void loginUser() {
        String email = emailField.getText().toString().trim();
        String pin = pinField.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailField.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(pin) || pin.length() != 6) {
            pinField.setError("PIN must be 6 digits");
            return;
        }

        // Firebase authentication using email and pin (password)
        mAuth.signInWithEmailAndPassword(email, pin)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UPI PIN in Realtime Database
                        FirebaseUser user = mAuth.getCurrentUser();
                        updatePinInDatabase(user.getUid(), pin);
                        Toast.makeText(User_Login.this, "Login successful", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(User_Login.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Close the login activity
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(User_Login.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Update the UPI PIN in Firebase Realtime Database
    private void updatePinInDatabase(String userId, String newPin) {
        userRef.child(userId).child("pin").setValue(newPin)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // PIN updated successfully
                    } else {
                        Toast.makeText(User_Login.this, "Failed to update UPI PIN: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}