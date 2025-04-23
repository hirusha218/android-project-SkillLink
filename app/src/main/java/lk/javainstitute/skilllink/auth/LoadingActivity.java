package lk.javainstitute.skilllink.auth;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import lk.javainstitute.skilllink.R;
import lk.javainstitute.skilllink.cactivity.C_MainActivity;
import lk.javainstitute.skilllink.wactivity.MainActivity;

public class LoadingActivity extends AppCompatActivity {

    private ProgressBar loadingBar;
    private TextView loadingText;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loading);

        View mainLayout = findViewById(R.id.main);
        loadingBar = findViewById(R.id.progressBar);
        loadingText = findViewById(R.id.loadingText);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            checkUserType(currentUser.getUid());
        } else {
            // Redirect to login activity if user is not logged in
            startActivity(new Intent(LoadingActivity.this, SplashActivity.class));
            finish();
        }

        // Set up system bar insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Show the loading bar and animate text
        showLoadingBar();
        animateLoadingText();
        retrieveAndStoreFcmToken();
    }

    private void retrieveAndStoreFcmToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String fcmToken = task.getResult();
                        Log.d(TAG, "FCM Token: " + fcmToken);
                        storeFcmToken(fcmToken);
                    } else {
                        Log.e(TAG, "Failed to get FCM token", task.getException());
                        Toast.makeText(this, "Failed to initialize notifications", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void storeFcmToken(String fcmToken) {
        if (userId != null) {
            databaseRef.child("Workers").child(userId).child("fcmToken")
                    .setValue(fcmToken)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "FCM token stored successfully"))
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to store FCM token", e);
                        Toast.makeText(this, "Error saving notification token", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.w(TAG, "No userId available to store FCM token");
        }
    }

    private void checkUserType(String userId) {
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // Check if the user exists in Customers node
        databaseRef.child("Customers").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot customerSnapshot) {
                boolean isCustomer = customerSnapshot.exists();

                // Now check if the user exists in Workers node
                databaseRef.child("Workers").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot workerSnapshot) {
                        boolean isWorker = workerSnapshot.exists();

                        if (isCustomer && isWorker) {
                            // If the user exists in both Customers and Workers, let them choose
                            showAccountSelectionDialog();
                        } else if (isCustomer) {
                            // Redirect to Customer Dashboard
                            Toast.makeText(LoadingActivity.this, "Logged in as Customer", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoadingActivity.this, C_MainActivity.class));
                            finish();
                        } else if (isWorker) {
                            // Redirect to Worker Dashboard
                            Toast.makeText(LoadingActivity.this, "Logged in as Worker", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                            finish();
                        } else {
                            // User not found in either, log them out
                            Toast.makeText(LoadingActivity.this, "User not found in database", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(LoadingActivity.this, SplashActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Database Error: " + error.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Database Error: " + error.getMessage());
            }
        });
    }

    // Show a dialog to let the user choose between Customer and Worker accounts
    private void showAccountSelectionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Select Account Type")
                .setMessage("You are registered as both a Customer and a Worker. Choose an account to continue.")
                .setPositiveButton("Customer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(LoadingActivity.this, C_MainActivity.class));
                        finish();
                    }
                })
                .setNegativeButton("Worker", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void showLoadingBar() {
        loadingBar.setVisibility(View.VISIBLE);
    }

    private void hideLoadingBar() {
        stopTextAnimation();
        loadingBar.setVisibility(View.GONE);
    }

    private void animateLoadingText() {
        String[] loadingTexts = {"Loading", "Loading.", "Loading..", "Loading..."};
        final int[] currentIndex = {0};

        final Runnable textAnimation = new Runnable() {
            @Override
            public void run() {
                if (loadingText != null) {
                    loadingText.setText(loadingTexts[currentIndex[0]]);
                    currentIndex[0] = (currentIndex[0] + 1) % loadingTexts.length;

                    // Fade in animation
                    loadingText.setAlpha(0f);
                    loadingText.animate()
                            .alpha(1f)
                            .setDuration(500)
                            .start();

                    // Schedule next animation
                    loadingText.postDelayed(this, 500);
                }
            }
        };

        loadingText.post(textAnimation);
    }

    private void stopTextAnimation() {
        if (loadingText != null) {
            loadingText.removeCallbacks(null);
            loadingText.clearAnimation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTextAnimation();
    }
}
