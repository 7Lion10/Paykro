package com.example.paykaro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ServiceProviderRecharge extends AppCompatActivity {

    private Spinner serviceProviderSpinner;
    private TextView nameInput;
    private TextView phoneNumberText;
    private EditText amountInput; // Changed to EditText for input
    private Button rechargeButton;
    private String selectedProvider;
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_recharge);

        back = findViewById(R.id.backButton);
        back.setOnClickListener(v->finish());

        serviceProviderSpinner = findViewById(R.id.serviceProviderSpinner);
        nameInput = findViewById(R.id.nameInput);
        phoneNumberText = findViewById(R.id.phoneNumberText);
        rechargeButton = findViewById(R.id.rechargeButton);
        amountInput = findViewById(R.id.amountInput);

        // Retrieve data from the intent
        String contactName = getIntent().getStringExtra("contact_name");
        String phoneNumber = getIntent().getStringExtra("phone_number");

        // Set the retrieved data in the TextViews
        nameInput.setText(contactName);
        phoneNumberText.setText(phoneNumber);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.service_providers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceProviderSpinner.setAdapter(adapter);

        // Set a listener for selection events
        serviceProviderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedProvider = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Set click listener for the recharge button
        rechargeButton.setOnClickListener(v -> {
            String amountStr = amountInput.getText().toString().trim();
            if (amountStr.isEmpty() || Double.parseDouble(amountStr) <= 0) {
                amountInput.setError("Please enter a valid amount greater than 0.");
                Toast.makeText(ServiceProviderRecharge.this, "Invalid amount entered!", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr); // Parse the amount

            Intent intent = new Intent(ServiceProviderRecharge.this, recharge_pin.class);
            intent.putExtra("selected_provider", selectedProvider); // Pass selected provider
            intent.putExtra("contact_name", contactName); // Pass contact name
            intent.putExtra("recharge_amount", amount); // Pass recharge amount

            startActivity(intent);
        });
    }
}
