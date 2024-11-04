package com.example.paykaro;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class enterPin extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String userId;
    private double balance ; // Default balance
    private TextView amountText, alertText, bankName, bankingName;
    private EditText upiPinInput;
    private final String CHANNEL_ID = "PaymentNotificationChannel";
    private Button back;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pin);

        back=findViewById(R.id.back);
        back.setOnClickListener(v->finish());

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        amountText = findViewById(R.id.amount_text);
        alertText = findViewById(R.id.alert_text);
        upiPinInput = findViewById(R.id.upi_pin_input);
        bankName = findViewById(R.id.banking_to_name);
        bankingName = findViewById(R.id.banking_to_name);

        Intent intent = getIntent();
        String amount = intent.getStringExtra("amount");
        String contactName = intent.getStringExtra("contact_name");
        String phoneNumber = intent.getStringExtra("phone_number");
        String bankNameValue = intent.getStringExtra("bank_name");

        amountText.setText("₹ " + amount);
        alertText.setText("You are SENDING ₹ " + amount + " to " + contactName);
        bankName.setText(bankNameValue);
        bankingName.setText(contactName);

        userRef.child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    balance = snapshot.getValue(Double.class); // Get the current balance
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(enterPin.this, "Failed to retrieve balance", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.pay_button).setOnClickListener(v -> {
            String pin = upiPinInput.getText().toString().trim();
            if (pin.length() < 6) {
                Toast.makeText(enterPin.this, "Please enter a valid 6-digit UPI PIN", Toast.LENGTH_SHORT).show();
            } else {
                validatePinAndProcessPayment(pin, amount, contactName, phoneNumber, bankNameValue);
            }
        });
    }

    private void validatePinAndProcessPayment(String pin, String amountString, String contactName, String phoneNumber, String bankNameValue) {
        userRef.child("pin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String storedPin = snapshot.getValue(String.class);

                    Log.d("PIN Validation", "Stored PIN: " + storedPin + ", Entered PIN: " + pin);

                    if (storedPin != null && storedPin.trim().equals(pin.trim())) {
                        processPayment(amountString, contactName, phoneNumber, bankNameValue);
                    } else {
                        Toast.makeText(enterPin.this, "Invalid UPI PIN", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(enterPin.this, "UPI PIN not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(enterPin.this, "Failed to validate UPI PIN", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processPayment(String amountString, String contactName, String phoneNumber, String bankNameValue) {
        double amount = Double.parseDouble(amountString);

        if (balance >= amount) {
            double newBalance = balance - amount;
            userRef.child("balance").setValue(newBalance).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(enterPin.this, "Payment Successful", Toast.LENGTH_SHORT).show();
                    showPaymentNotification(amountString, contactName);
                    goToConfirmationScreen(amountString, contactName, phoneNumber, bankNameValue, newBalance);
                } else {
                    Toast.makeText(enterPin.this, "Payment Failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(enterPin.this, "Insufficient Balance", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPaymentNotification(String amount, String contactName) {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Payment Successful")
                .setContentText("₹ " + amount + " sent to " + contactName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Payment Notifications";
            String description = "Notifications for payment status";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void goToConfirmationScreen(String amount, String contactName, String phoneNumber, String bankNameValue, double newBalance) {
        Intent confirmIntent = new Intent(enterPin.this, confirm_bank_trans.class);
        confirmIntent.putExtra("amount", amount);
        confirmIntent.putExtra("contact_name", contactName);
        confirmIntent.putExtra("phone_number", phoneNumber);
        confirmIntent.putExtra("bank_name", bankNameValue);
        confirmIntent.putExtra("transaction_id", generateRandomTransactionId());
        confirmIntent.putExtra("new_balance", newBalance); // Pass the new balance here
        startActivity(confirmIntent);
    }

    private String generateRandomTransactionId() {
        Random random = new Random();
        int transactionNum = random.nextInt(900000000) + 100000000; // Generates a random 9-digit number
        return String.valueOf(transactionNum);
    }
}
