package com.example.paykaro;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

public class recharge_pin extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String userId;
    private double balance;
    private TextView rechargeAmountText, alertText, serviceProviderText;
    private EditText upiPinInput;
    private String selectedProvider, contact_name;
    private double rechargeAmount;
    private Button back;
    private final String CHANNEL_ID = "RechargeNotificationChannel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_pin);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        back = findViewById(R.id.back);
        upiPinInput = findViewById(R.id.upi_pin_input);
        serviceProviderText = findViewById(R.id.service_provider);

        Intent intent = getIntent();
        selectedProvider = intent.getStringExtra("selected_provider");
        rechargeAmount = intent.getDoubleExtra("recharge_amount", 0);
        contact_name = intent.getStringExtra("contact_name");

        serviceProviderText.setText(selectedProvider);
        back.setOnClickListener(v -> finish());

        userRef.child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    balance = snapshot.getValue(Double.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(recharge_pin.this, "Failed to retrieve balance", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.check_button).setOnClickListener(v -> {
            String pin = upiPinInput.getText().toString().trim();
            if (pin.length() < 6) {
                Toast.makeText(recharge_pin.this, "Please enter a valid 6-digit UPI PIN", Toast.LENGTH_SHORT).show();
            } else {
                validatePinAndProcessRecharge(pin);
            }
        });
    }

    private void validatePinAndProcessRecharge(String pin) {
        userRef.child("pin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String storedPin = snapshot.getValue(String.class);

                    if (storedPin != null && storedPin.trim().equals(pin.trim())) {
                        checkBalanceAndRecharge();
                    } else {
                        Toast.makeText(recharge_pin.this, "Invalid UPI PIN", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(recharge_pin.this, "UPI PIN not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(recharge_pin.this, "Failed to validate UPI PIN", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkBalanceAndRecharge() {
        if (balance >= rechargeAmount) {
            double newBalance = balance - rechargeAmount;

            userRef.child("balance").setValue(newBalance).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(recharge_pin.this, "Recharge Successful", Toast.LENGTH_SHORT).show();
                    showRechargeNotification(String.valueOf(rechargeAmount), selectedProvider);
                    goToConfirmationScreen(newBalance);
                } else {
                    Toast.makeText(recharge_pin.this, "Recharge Failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(recharge_pin.this, "Insufficient balance for this recharge", Toast.LENGTH_SHORT).show();
        }
    }

    private void showRechargeNotification(String amount, String providerName) {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Recharge Successful")
                .setContentText("₹ " + amount + " recharged to " + providerName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Recharge Notifications";
            String description = "Notifications for recharge status";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void goToConfirmationScreen(double newBalance) {
        Intent confirmIntent = new Intent(recharge_pin.this, confirm_recharge.class);
        confirmIntent.putExtra("recharge_amount", String.valueOf(rechargeAmount));
        confirmIntent.putExtra("selected_provider", selectedProvider);
        confirmIntent.putExtra("contact_name", contact_name);
        confirmIntent.putExtra("new_balance", newBalance);
        startActivity(confirmIntent);
    }
}
