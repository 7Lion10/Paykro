package com.example.paykaro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class enterAmount extends AppCompatActivity {

    private EditText amountInput;
    private Button payButton,back;
    private TextView cusName,banking_name,phn_num;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_amount);

        amountInput = findViewById(R.id.amount_input);
        payButton = findViewById(R.id.pay);
        cusName=findViewById(R.id.user_name);
        banking_name=findViewById(R.id.banking_name);
        phn_num=findViewById(R.id.phone_number);
        back=findViewById(R.id.back);
        Intent intent = getIntent();
        String contactName = intent.getStringExtra("contact_name");
        String phoneNumber = intent.getStringExtra("phone_number");
        String bankName = intent.getStringExtra("bank_name");

        cusName.setText(contactName);
        banking_name.setText(" Banking name: "+ contactName);
        phn_num.setText("+91 "+ phoneNumber);

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = amountInput.getText().toString();
                if (amountStr.isEmpty() || Double.parseDouble(amountStr) <= 0) {
                    Toast.makeText(enterAmount.this, "Please enter an amount greater than 0", Toast.LENGTH_SHORT).show();
                } else {
                    Intent pinIntent = new Intent(enterAmount.this, enterPin.class);
                    pinIntent.putExtra("amount", amountStr);
                    pinIntent.putExtra("contact_name", contactName);
                    pinIntent.putExtra("phone_number", phoneNumber);
                    pinIntent.putExtra("bank_name", bankName);
                    startActivity(pinIntent);
                }
            }
        });

        back.setOnClickListener(v -> finish());
    }
}
