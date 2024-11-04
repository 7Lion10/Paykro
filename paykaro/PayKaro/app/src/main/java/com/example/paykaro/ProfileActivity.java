package com.example.paykaro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameTextView, phoneTextView, bankNameTextView, accountNumberTextView, emailTextView;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private Button change_pin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Initialize TextViews
        nameTextView = findViewById(R.id.name_text_view);
        phoneTextView = findViewById(R.id.phone_text_view);
        bankNameTextView = findViewById(R.id.bank_name_text_view);
        accountNumberTextView = findViewById(R.id.account_number_text_view);
        emailTextView = findViewById(R.id.email_text_view);

        if (currentUser != null) {
            // Get the logged-in user's UID
            String uid = currentUser.getUid();

            // Initialize Firebase Database reference (corrected to "Users")
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

            // Fetch user data from Firebase Realtime Database
            fetchUserData();
        }


        change_pin=findViewById(R.id.change_pin_button);
        change_pin.setOnClickListener(v->{
            Intent i=new Intent(ProfileActivity.this,ChangePinActivity.class);
            startActivity(i);
        });
    }


    private void fetchUserData() {
        // Attach a listener to read the data at the user's reference
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get user data as a User object
                    User user = dataSnapshot.getValue(User.class);

                    if (user != null) {
                        // Update the UI with fetched data
                        nameTextView.setText("Name: " + user.getUsername());
                        phoneTextView.setText("Phone: " + user.getPhoneNumber());
                        bankNameTextView.setText("Bank Name: " + user.getBankName());
                        accountNumberTextView.setText("Account No: " + user.getAccountNumber());
                        emailTextView.setText("Email: " + user.getEmail());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("ProfileActivity", "loadUser:onCancelled", databaseError.toException());
            }
        });
    }
}
