package com.example.paykaro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class bank_payment extends AppCompatActivity {
    private ImageButton closeButton;
    private ImageView profileImage;
    private TextView holderNameText, accountText, currencySymbolText;
    private EditText amountInput, addNoteInput;
    private Button payButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_payment);

        closeButton = findViewById(R.id.close_button);
        profileImage = findViewById(R.id.profile_image);
        holderNameText = findViewById(R.id.name);
        accountText = findViewById(R.id.account_number);
        currencySymbolText = findViewById(R.id.currency_symbol);
        amountInput = findViewById(R.id.amount_input);
        addNoteInput = findViewById(R.id.add_note_textview);
        payButton = findViewById(R.id.pay);

        String holderName = getIntent().getStringExtra("holder_name");
        String account = getIntent().getStringExtra("account");

        holderNameText.setText("Name: "+holderName);
        accountText.setText("Acc-No: "+account);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = amountInput.getText().toString();
                String notes = addNoteInput.getText().toString();

                if (amount.isEmpty()) {
                    amountInput.setError("Please enter an amount");
                } else {
                    // Redirect to enterPin activity
                    Intent intent = new Intent(bank_payment.this, enterPin.class);
                    intent.putExtra("contact_name", holderName);
                    intent.putExtra("account", account);
                    intent.putExtra("amount", amount);
                    intent.putExtra("notes", notes);
                    startActivity(intent);
                }
            }
        });
    }
}
