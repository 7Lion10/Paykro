package com.example.paykaro;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User_Login extends AppCompatActivity {
    private EditText emailField, pinField;
    private TextView registerLink, forgetPin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        ImageButton togglePinVisibility = findViewById(R.id.togglePinVisibility);
        emailField = findViewById(R.id.email);
        pinField = findViewById(R.id.pin);
        registerLink = findViewById(R.id.registerLink);
        forgetPin = findViewById(R.id.forgotPin);

        // Handle register link click
        registerLink.setOnClickListener(v -> {
            Intent i = new Intent(this, UserSignUp.class);
            startActivity(i);
        });

        // Toggle PIN visibility
        togglePinVisibility.setOnClickListener(v -> {
            if (pinField.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // Show password
                pinField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePinVisibility.setImageResource(R.drawable.baseline_visibility_24);

            } else {
                // Hide password
                pinField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePinVisibility.setImageResource(R.drawable.baseline_visibility_off_24);

            }

            // Set cursor to the end of the text
            pinField.setSelection(pinField.getText().length());
        });

        // Handle login button click
        findViewById(R.id.loginButton).setOnClickListener(v -> loginUser());

        // Handle forget PIN click
        forgetPin.setOnClickListener(v -> showResetPinDialog());
    }

    // Show dialog to reset PIN
    private void showResetPinDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset PIN");

        final EditText emailInput = new EditText(this);
        emailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(emailInput);

        builder.setPositiveButton("Send Reset Link", (dialog, which) -> {
            String email = emailInput.getText().toString().trim();
            resetPin(email);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Reset PIN logic
    private void resetPin(String email) {
        if (TextUtils.isEmpty(email)) {
            // Display error message (not in toast)
            emailField.setError("Email is required");
            return;
        }

        // Send password reset email
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Display message indicating link sent
                        emailField.setError(null); // Clear error
                        Toast.makeText(User_Login.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                    } else {
                        // Display error message (not in toast)
                        emailField.setError("Not a registered email");
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
                        // Sign in success, navigate to MainActivity
                        FirebaseUser user = mAuth.getCurrentUser();
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
}
