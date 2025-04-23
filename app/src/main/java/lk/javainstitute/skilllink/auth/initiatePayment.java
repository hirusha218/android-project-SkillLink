package lk.javainstitute.skilllink.auth;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

import lk.javainstitute.skilllink.R;
import lk.javainstitute.skilllink.model.PaymentDetails;
import lk.javainstitute.skilllink.wactivity.MainActivity;
import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.Item;
import lk.payhere.androidsdk.model.StatusResponse;

public class initiatePayment extends AppCompatActivity {

    private static final String TAG = "PayhereDemo";
    private static final String CHANNEL_ID = "payment_channel";
    private static final int NOTIFICATION_ID = 1001;

    private TextView textView;
    private TextInputEditText workerMobileInput, creditCardInput, bankAccountInput, paymentAmountInput;
    private Button searchButton;
    private DatabaseReference databaseRef, paymentRef;
    private CheckBox creditCardCheckBox, bankTransferCheckBox;
    private double paymentAmount;

    private final ActivityResultLauncher<Intent> payHereLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    if (data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
                        Serializable serializable = data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
                        if (serializable instanceof PHResponse) {
                            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) serializable;
                            String msg = response.isSuccess() ? "Payment Success: " + response.getData() : "Payment Failed: " + response;
                            Log.d(TAG, msg);
                            textView.setText(msg);

                            if (response.isSuccess()) {
                                storePaymentDetails(response);
                                sendNotification("Payment Successful", "Your payment was processed successfully.");
                                navigateToMainActivity();
                            }
                        }
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    textView.setText("User canceled the request");
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiate_payment);

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Payment Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        Button payButton = findViewById(R.id.payButton);
        textView = findViewById(R.id.textView);

        payButton.setOnClickListener(view -> initiatePayment());

        databaseRef = FirebaseDatabase.getInstance().getReference("Workers");
        paymentRef = FirebaseDatabase.getInstance().getReference("Payments");

        workerMobileInput = findViewById(R.id.E_W_id);
        creditCardInput = findViewById(R.id.Credit_Card_Number);
        bankAccountInput = findViewById(R.id.Bank_Account_Number);
        paymentAmountInput = findViewById(R.id.Payment_amount);
        searchButton = findViewById(R.id.Search_btn_btn);

        searchButton.setOnClickListener(v -> searchWorker());

        creditCardCheckBox = findViewById(R.id.checkBox1);
        bankTransferCheckBox = findViewById(R.id.checkBox2);

        creditCardCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bankTransferCheckBox.setChecked(false);
                creditCardInput.setEnabled(true);
                bankAccountInput.setEnabled(false);
                bankAccountInput.setText("");
            }
        });

        bankTransferCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                creditCardCheckBox.setChecked(false);
                bankAccountInput.setEnabled(true);
                creditCardInput.setEnabled(false);
                creditCardInput.setText("");
            }
        });
    }

    private void initiatePayment() {
        String amountStr = paymentAmountInput.getText().toString().trim();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter payment amount", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            paymentAmount = Double.parseDouble(amountStr);
            if (paymentAmount <= 0) {
                Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
            return;
        }

        InitRequest req = new InitRequest();
        req.setMerchantId("1229618");
        req.setCurrency("LKR");
        req.setAmount(paymentAmount);
        req.setOrderId(generateOrderId());
        req.setItemsDescription("Worker Payment");

        String workerMobile = workerMobileInput.getText().toString().trim();

        req.getCustomer().setFirstName("Worker Payment");
        req.getCustomer().setLastName("");
        req.getCustomer().setEmail("support@skilllink.com");
        req.getCustomer().setPhone(workerMobile);
        req.getCustomer().getAddress().setAddress("SkillLink");
        req.getCustomer().getAddress().setCity("Colombo");
        req.getCustomer().getAddress().setCountry("Sri Lanka");

        req.getItems().add(new Item(null, "Worker Payment", 1, paymentAmount));
        req.setNotifyUrl("YOUR_ACTUAL_NOTIFY_URL");

        Intent intent = new Intent(this, PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
        payHereLauncher.launch(intent);
    }

    private String generateOrderId() {
        return "SK" + System.currentTimeMillis();
    }

    private void searchWorker() {
        String workerMobile = workerMobileInput.getText().toString().trim();
        if (workerMobile.isEmpty()) {
            workerMobileInput.setError("Please enter Worker Mobile");
            return;
        }

        databaseRef.orderByChild("mobile").equalTo(workerMobile)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot workerSnapshot : snapshot.getChildren()) {
                                loadWorkerDetails(workerSnapshot);
                                return;
                            }
                        } else {
                            Toast.makeText(initiatePayment.this, "Worker not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(initiatePayment.this, "Error searching worker", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadWorkerDetails(DataSnapshot snapshot) {
        String accountNumber = snapshot.child("bankDetails/accountNumber").getValue(String.class);
        String cardNumber = snapshot.child("cardDetails/cardNumber").getValue(String.class);

        if (accountNumber != null) {
            bankAccountInput.setText(accountNumber);
            bankTransferCheckBox.setEnabled(true);
        } else {
            bankAccountInput.setText("");
            bankTransferCheckBox.setEnabled(false);
        }

        if (cardNumber != null) {
            creditCardInput.setText(cardNumber);
            creditCardCheckBox.setEnabled(true);
        } else {
            creditCardInput.setText("");
            creditCardCheckBox.setEnabled(false);
        }
    }

    private void storePaymentDetails(PHResponse<StatusResponse> response) {
        // Get the payment ID or payment number from the response
        String paymentNo = String.valueOf(response.getData().getPaymentNo()); // This might be the correct method

        // Ensure paymentNo is not null
        if (paymentNo == null) {
            paymentNo = "Unknown Payment No";  // Fallback if paymentNo is not available
        }

        String paymentId = paymentRef.push().getKey();
        String workerMobile = workerMobileInput.getText().toString().trim();
        String paymentMethod = creditCardCheckBox.isChecked() ? "Credit Card" : "Bank Transfer";
        long timestamp = System.currentTimeMillis();
        String orderId = generateOrderId();

        PaymentDetails paymentDetails = new PaymentDetails(paymentId, workerMobile, paymentAmount, paymentMethod, timestamp, orderId, paymentNo);

        paymentRef.child(paymentId).setValue(paymentDetails)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Payment details stored successfully");
                    } else {
                        Log.e(TAG, "Failed to store payment details", task.getException());
                    }
                });
    }


    private void sendNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Payment Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    private void clearInputFields() {
        paymentAmountInput.setText("");
        creditCardCheckBox.setChecked(false);
        bankTransferCheckBox.setChecked(false);
        workerMobileInput.setText("");
        creditCardInput.setText("");
        bankAccountInput.setText("");
    }
}
