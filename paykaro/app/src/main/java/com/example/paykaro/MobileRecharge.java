package com.example.paykaro;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MobileRecharge extends AppCompatActivity {

    private EditText editTextPhone;
    private ListView listViewContacts;
    private DatabaseReference contactsRef;
    private ArrayAdapter<String> contactsAdapter;
    private ArrayList<String> contactsList;
    private ArrayList<String> filteredList;
    private ImageView goToContacts,back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        back = findViewById(R.id.imageButton4);
        back.setOnClickListener(v->finish());

        goToContacts = findViewById(R.id.imageButton3);
        goToContacts.setOnClickListener(v -> {
            Intent intent = new Intent(MobileRecharge.this, recharge_by_contact.class);
            startActivity(intent);
        });

        contactsRef = FirebaseDatabase.getInstance().getReference("contacts");

        editTextPhone = findViewById(R.id.editTextPhone);
        listViewContacts = findViewById(R.id.listView);

        contactsList = new ArrayList<>();
        filteredList = new ArrayList<>();

        contactsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredList);
        listViewContacts.setAdapter(contactsAdapter);

        fetchContacts();

        editTextPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterContacts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Set up item click listener for the ListView
        listViewContacts.setOnItemClickListener((parent, view, position, id) -> {
            String selectedContact = filteredList.get(position);
            String contactName = selectedContact.substring(0, selectedContact.indexOf(" ("));
            String phoneNumber = selectedContact.substring(selectedContact.indexOf("(") + 1, selectedContact.indexOf(")"));
            String bankName = "Your Bank Name";  // Retrieve this from your database as well

            Intent intent = new Intent(MobileRecharge.this, ServiceProviderRecharge.class);
            intent.putExtra("contact_name", contactName);
            intent.putExtra("phone_number", phoneNumber);
            intent.putExtra("bank_name", bankName);
            startActivity(intent);
        });
    }

    private void fetchContacts() {
        contactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MobileRecharge.this, "Error fetching contacts", Toast.LENGTH_SHORT).show();
                Log.e("MobileRecharge", "Database error: " + databaseError.getMessage());
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
