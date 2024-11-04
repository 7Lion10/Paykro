package com.example.paykaro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class ShowContacts extends AppCompatActivity {

    private EditText searchcontacts;
    private ListView listViewContacts;
    private DatabaseReference contactsRef;
    private ArrayAdapter<String> contactsAdapter;
    private ArrayList<String> contactsList;
    private ArrayList<String> filteredList;
    private ImageView back;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        back = findViewById(R.id.imageButton4);
        back.setOnClickListener(v -> finish());

        // Initialize Firebase Database reference
        contactsRef = FirebaseDatabase.getInstance().getReference("contacts");

        // Initialize UI components
        searchcontacts = findViewById(R.id.search);
        listViewContacts = findViewById(R.id.listview);  // Ensure this ID is correct in your XML

        // Initialize lists
        contactsList = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Set up ListView adapter
        contactsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredList);
        listViewContacts.setAdapter(contactsAdapter);

        // Fetch all contacts from Firebase and display in the ListView
        fetchContacts();

        // Add text change listener for dynamic filtering
        searchcontacts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterContacts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set item click listener for ListView
        listViewContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedContact = filteredList.get(position);
                // Split selected contact to get name and phone
                String[] contactDetails = selectedContact.split(" \\(");
                String customerName = contactDetails[0];
                String phoneNumber = contactDetails[1].replace(")", "").trim();

                // Create an intent to start EnterAmount activity
                Intent intent = new Intent(ShowContacts.this, enterAmount.class);
                intent.putExtra("contact_name", customerName);
                intent.putExtra("phone_number", phoneNumber);
                startActivity(intent);
            }
        });
    }

    private void fetchContacts() {
        contactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contactsList.clear();
                for (DataSnapshot contactSnapshot : dataSnapshot.getChildren()) {
                    Contact contact = contactSnapshot.getValue(Contact.class);
                    if (contact != null) {
                        contactsList.add(contact.getName() + " (" + contact.getPhone() + ")");
                    }
                }
                filteredList.clear();
                filteredList.addAll(contactsList);
                contactsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ShowContacts.this, "Error fetching contacts", Toast.LENGTH_SHORT).show();
                Log.e("ShowContacts", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void filterContacts(String query) {
        filteredList.clear();
        for (String contact : contactsList) {
            if (contact.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(contact);
            }
        }
        contactsAdapter.notifyDataSetChanged();
    }
}
