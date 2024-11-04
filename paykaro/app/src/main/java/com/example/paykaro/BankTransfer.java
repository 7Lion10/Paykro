package com.example.paykaro;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class BankTransfer extends AppCompatActivity {
    private EditText accEditText, iffcEditText, cus_name;
    private ImageButton imageButton;
    private Button continueButton;
    private Spinner bankNameSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_transfer);

        bankNameSpinner = findViewById(R.id.bank_spinner);
        accEditText = findViewById(R.id.editTextText2);
        iffcEditText = findViewById(R.id.editTextText4);
        imageButton = findViewById(R.id.imageButton);
        continueButton = findViewById(R.id.button2);
        cus_name = findViewById(R.id.name);

        String[] bankNames = {
                "Select Bank", "HDFC Bank", "ICICI Bank", "State Bank of India", "Axis Bank",
                "Kotak Mahindra Bank", "Yes Bank", "IndusInd Bank",
                "Punjab National Bank", "Canara Bank", "Union Bank of India"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bankNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bankNameSpinner.setAdapter(adapter);

        imageButton.setOnClickListener(v->finish());

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String holder_name = cus_name.getText().toString();
                String bank = bankNameSpinner.getSelectedItem().toString();
                String account = accEditText.getText().toString();
                String iffcCode = iffcEditText.getText().toString();

                // Validation checks
                if (holder_name.isEmpty()) {
                    cus_name.setError("Please enter holder's name");
                } else if (bank.equals("Select Bank")) {
                    Toast.makeText(BankTransfer.this, "Please select a bank", Toast.LENGTH_LONG).show();
                } else if (account.isEmpty()) {
                    accEditText.setError("Please enter account number");
                } else if (account.length() != 17) {
                    accEditText.setError("Account number must be 17 digits");
                } else if (iffcCode.isEmpty()) {
                    iffcEditText.setError("Please enter IFSC code");
                } else if (iffcCode.length() != 11) {
                    iffcEditText.setError("IFSC code must be 11 characters");
                } else {
                    Intent intent = new Intent(BankTransfer.this, bank_payment.class);
                    intent.putExtra("holder_name", holder_name);
                    intent.putExtra("account", account);  // Pass account number
                    startActivity(intent);
                }
            }
        });
    }
}
