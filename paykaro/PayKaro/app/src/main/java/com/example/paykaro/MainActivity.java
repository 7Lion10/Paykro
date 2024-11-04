package com.example.paykaro; // Change this to your app's package name

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

        // Add more contacts here (total 50 contacts)

        for (Contact contact : contactList) {
            // Store each contact in Firebase under a unique key
            String contactId = contactsRef.push().getKey();
            contactsRef.child(contactId).setValue(contact);
        }
    }
}
