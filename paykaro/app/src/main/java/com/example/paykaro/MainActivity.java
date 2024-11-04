package com.example.paykaro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference contactsRef;
    private ImageView profileIcon;
    private ImageView mobileTransfer;
    private ImageView bankTransfer;
    private ImageView check_bal;
    private ImageView Mob_recharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Ensure this points to your layout file

        // Firebase reference for storing contacts
        contactsRef = FirebaseDatabase.getInstance().getReference("contacts");

        // Check if contacts exist, if not, add them
        checkAndAddHardCodedContacts();

        profileIcon = findViewById(R.id.profile_icon);

        // Set an OnClickListener on the profile icon
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start ProfileActivity when the profile icon is clicked
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        mobileTransfer = findViewById(R.id.to_mobile_number);

        // Set OnClickListener for the mobile transfer icon
        mobileTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start MobileTransferActivity when mobile transfer icon is clicked
                Intent intent = new Intent(MainActivity.this, MobileTransfer.class);
                startActivity(intent);
            }
        });

        bankTransfer = findViewById(R.id.bank_transfer);

        // Set OnClickListener for the bank transfer icon
        bankTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start BankTransferActivity when bank transfer icon is clicked
                Intent intent = new Intent(MainActivity.this, BankTransfer.class);
                startActivity(intent);
            }
        });
        check_bal = findViewById(R.id.check_balance);
        check_bal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start BankTransferActivity when bank transfer icon is clicked
                Intent intent = new Intent(MainActivity.this, balance_pin.class);
                startActivity(intent);
            }
        });

        Mob_recharge = findViewById(R.id.mobile_recharge);
        Mob_recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start BankTransferActivity when bank transfer icon is clicked
                Intent intent = new Intent(MainActivity.this, MobileRecharge.class);
                startActivity(intent);
            }
        });
    }

    private void checkAndAddHardCodedContacts() {
        contactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // If no contacts exist, insert the hardcoded contacts
                if (!snapshot.exists()) {
                    addHardCodedContacts();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle database error
            }
        });
    }

    private void addHardCodedContacts() {
        // List of hardcoded contacts
        List<Contact> contactList = new ArrayList<>();

        contactList.add(new Contact("Rupesh", "9999999999"));
        contactList.add(new Contact("Dhruti", "7676784271"));
        contactList.add(new Contact("Sowrabh", "9108196127"));
        contactList.add(new Contact("Vardhika","8861252757"));
        contactList.add(new Contact("Rupesh", "9999999999"));
        contactList.add(new Contact("Dhruti", "7676784271"));
        contactList.add(new Contact("Sowrabh", "9108196127"));
        contactList.add(new Contact("Vardhika","8861252757"));
        contactList.add(new Contact("Rupesh", "9999999999"));
        contactList.add(new Contact("Dhruti", "7676784271"));
        contactList.add(new Contact("Sowrabh", "9108196127"));
        contactList.add(new Contact("Vardhika","8861252757"));
        contactList.add(new Contact("Rupesh", "9999999999"));
        contactList.add(new Contact("Dhruti", "7676784271"));
        contactList.add(new Contact("Sowrabh", "9108196127"));
        contactList.add(new Contact("Vardhika","8861252757"));
        contactList.add(new Contact("Rupesh", "9999999999"));
        contactList.add(new Contact("Dhruti", "7676784271"));
        contactList.add(new Contact("Sowrabh", "9108196127"));
        contactList.add(new Contact("Vardhika","8861252757"));
        contactList.add(new Contact("Rupesh", "9999999999"));
        contactList.add(new Contact("Dhruti", "7676784271"));
        contactList.add(new Contact("Sowrabh", "9108196127"));
        contactList.add(new Contact("Vardhika","8861252757"));

        // Add more contacts here (total 50 contacts)

        for (Contact contact : contactList) {
            // Store each contact in Firebase under a unique key
            String contactId = contactsRef.push().getKey();
            contactsRef.child(contactId).setValue(contact);
        }
    }
}
