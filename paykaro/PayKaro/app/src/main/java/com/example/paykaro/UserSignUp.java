package com.example.paykaro;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ImageViewCompat;


public class UserSignUp extends AppCompatActivity {

    private EditText usernameField, emailField, phoneNumberField, pinField;
    private Button nextButton;
    private ImageButton togglePinVisibility;
    private boolean isPinVisible = false;
    private TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);

        // Initialize views
        usernameField = findViewById(R.id.username);
        emailField = findViewById(R.id.email);
        phoneNumberField = findViewById(R.id.phoneNumber);
        pinField = findViewById(R.id.pin);
        nextButton = findViewById(R.id.nextButton);
        togglePinVisibility = findViewById(R.id.togglePinVisibility);
        login=findViewById(R.id.LoginLink);
        // Toggle PIN visibility
        togglePinVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPinVisible) {
                    // Hide PIN
                    pinField.setInputType(129); // 'numberPassword' InputType
                    togglePinVisibility.setImageResource(R.drawable.baseline_visibility_off_24);
                } else {
                    // Show PIN
                    pinField.setInputType(144); // 'textVisiblePassword' InputType
                    togglePinVisibility.setImageResource(R.drawable.baseline_visibility_24);
                }
                isPinVisible = !isPinVisible;
            }
        });


        login.setOnClickListener(v->{
            Intent i=new Intent(UserSignUp.this, User_Login.class);
            startActivity(i);
        });
        // Handle Next Button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameField.getText().toString().trim();
                String email = emailField.getText().toString().trim();
                String phoneNumber = phoneNumberField.getText().toString().trim();
                String pin = pinField.getText().toString().trim();

                // Validate inputs
                if (validateInput(username, email, phoneNumber, pin)) {
                    // Pass data to BankDetails activity
                    Intent intent = new Intent(UserSignUp.this, BankDetails.class);
                    intent.putExtra("username", username);
                    intent.putExtra("email", email);
                    intent.putExtra("phoneNumber", phoneNumber);
                    intent.putExtra("pin", pin);
                    startActivity(intent);
                }
            }
        });
    }

    // Validation method for input fields
    private boolean validateInput(String username, String email, String phoneNumber, String pin) {

        if (TextUtils.isEmpty(username)) {
            usernameField.setError("Username is required");
            return false;
        }
        if (username.length() < 2) {
            usernameField.setError("Username must be at least 2 characters");
            return false;
        }
        if (!username.matches("[a-zA-Z]+")) {  // Regular expression to allow only alphabets
            usernameField.setError("Username must contain only alphabets");
            return false;
        }
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Valid email is required");
            return false;
        }
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() != 10) {
            phoneNumberField.setError("Valid phone number is required");
            return false;
        }
        if (TextUtils.isEmpty(pin)) {
            pinField.setError("PIN is required");
            return false;
        }
        if (pin.length() != 6 || !pin.matches("\\d{6}")) {
            pinField.setError("PIN must be exactly 6 digits");
            return false;
        }

        return true;
    }
}
