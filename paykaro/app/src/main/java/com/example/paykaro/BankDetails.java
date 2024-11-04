package com.example.paykaro;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BankDetails extends AppCompatActivity {

    private EditText accountHolderName, accountNumber, panNumber, aadharNumber;
    private MaterialButton signUpButton;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private AutoCompleteTextView bankNameDropdown;  // Use AutoCompleteTextView instead of Spinner
    private TextInputLayout bankNameInputLayout;

    private static final String TAG = "BankDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize views
        accountHolderName = findViewById(R.id.name);
        bankNameDropdown = findViewById(R.id.bankname_dropdown);  // Initialize AutoCompleteTextView
        bankNameInputLayout = findViewById(R.id.bankname_input_layout);  // TextInputLayout
        accountNumber = findViewById(R.id.Anumber);
        panNumber = findViewById(R.id.panno);
        aadharNumber = findViewById(R.id.Adharno);
        signUpButton = findViewById(R.id.signup);

        // Set up the bank name dropdown with a list of bank names
        String[] bankNames = {
                "HDFC Bank", "ICICI Bank", "State Bank of India", "Axis Bank",
                "Kotak Mahindra Bank", "Yes Bank", "IndusInd Bank",
                "Punjab National Bank", "Canara Bank", "Union Bank of India"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, bankNames);
        bankNameDropdown.setAdapter(adapter);

        // Handle Sign-Up Button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user details from previous activity
                String username = getIntent().getStringExtra("username");
                String email = getIntent().getStringExtra("email");
                String phoneNumber = getIntent().getStringExtra("phoneNumber");
                String pin = getIntent().getStringExtra("pin");

                // Get bank details
                String accountName = accountHolderName.getText().toString().trim();
                String bank = bankNameDropdown.getText().toString().trim();  // Get selected bank from AutoCompleteTextView
                String accountNum = accountNumber.getText().toString().trim();
                String pan = panNumber.getText().toString().trim();
                String aadhar = aadharNumber.getText().toString().trim();

                // Validate inputs
                if (validateBankDetails(accountName, bank, accountNum, pan, aadhar)) {
                    // Set loading state
                    setLoadingState(true);

                    // Create Firebase user with email and password
                    createFirebaseUser(email, pin, username, phoneNumber, accountName, bank, accountNum, pan, aadhar);
                }
            }
        });
    }

    // Method to create a Firebase user
    private void createFirebaseUser(String email, String pin, String username, String phoneNumber, String accountName, String bank, String accountNum, String pan, String aadhar) {
        mAuth.createUserWithEmailAndPassword(email, pin)
                .addOnCompleteListener(this, task -> {
                    setLoadingState(false);  // Reset loading state

                    if (task.isSuccessful()) {
                        // Sign in success, get FirebaseUser
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "createUserWithEmail:success");

                        // If user is created, save the details to Firebase
                        if (user != null) {
                            String userId = user.getUid();
                            User_Database newUser = new User_Database(username, email, phoneNumber, accountName, bank, accountNum, pan, aadhar, pin);

                            // Save user details to Firebase Database
                            saveUserDetails(userId, newUser);
                        }
                    } else {
                        // If sign in fails, display a message to the user
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(BankDetails.this, "User already exists with this email.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(BankDetails.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Validate bank details
    private boolean validateBankDetails(String accountName, String bank, String accountNum, String pan, String aadhar) {
        if (TextUtils.isEmpty(accountName)) {
            accountHolderName.setError("Account holder name is required");
            return false;
        }
        if (TextUtils.isEmpty(bank)) {  // Check if a valid bank is selected
            bankNameInputLayout.setError("Please select a valid bank");
            return false;
        } else {
            bankNameInputLayout.setError(null); // Clear error if valid
        }
        if (TextUtils.isEmpty(accountNum) || accountNum.length() != 17) {
            accountNumber.setError("Valid 17-digit account number is required");
            return false;
        }
        if (TextUtils.isEmpty(pan) || pan.length() != 10) {
            panNumber.setError("Valid PAN number is required");
            return false;
        }
        if (TextUtils.isEmpty(aadhar) || aadhar.length() != 12) {
            aadharNumber.setError("Valid Aadhaar number is required");
            return false;
        }
        return true;
    }

    // Method to save user details in Firebase
    private void saveUserDetails(String userId, User_Database user) {
        databaseReference.child(userId).setValue(user).addOnCompleteListener(task -> {
            setLoadingState(false);  // Reset loading state

            if (task.isSuccessful()) {
                Log.d(TAG, "User details saved successfully");

                // Show success toast and navigate to User_Login activity
                Toast.makeText(BankDetails.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BankDetails.this, User_Login.class);
                startActivity(intent);
                finish();  // Close current activity
            } else {
                Log.e(TAG, "Failed to save user details: " + task.getException().getMessage());
                Toast.makeText(BankDetails.this, "Failed to save details: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to set the loading state for the sign-up button
    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            signUpButton.setEnabled(false);
            signUpButton.setText("Signing Up...");
        } else {
            signUpButton.setEnabled(true);
            signUpButton.setText("Sign Up");
        }
    }
}