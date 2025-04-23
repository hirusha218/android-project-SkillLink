package lk.javainstitute.skilllink.cactivity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lk.javainstitute.skilllink.R;
import lk.javainstitute.skilllink.model.feedback;

public class AddFeedbackActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText etJobDescription;
    private Button btnPublishFeedback;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_feedback);

        // Initialize UI components
        ratingBar = findViewById(R.id.ratingBar2);
        etJobDescription = findViewById(R.id.etJobDescription);
        btnPublishFeedback = findViewById(R.id.button);

        // Customize RatingBar colors
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP); // Filled stars
        stars.getDrawable(1).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP); // Partial stars
        stars.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);   // Empty stars

        // Add rating change listener
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            // Ensure the colors are maintained after change
            stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(1).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        });

        ImageView backBtn = findViewById(R.id.imageView7);
        backBtn.setOnClickListener(v -> onBackPressed());

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Set up window insets for padding adjustment (edge-to-edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Button click listener for publishing feedback
        btnPublishFeedback.setOnClickListener(v -> {
            publishFeedback();
        });
    }

    // Method to handle feedback publishing
    private void publishFeedback() {
        float rating = ratingBar.getRating();
        String jobDescription = etJobDescription.getText().toString().trim();

        // Validate rating
        if (rating == 0) {
            Toast.makeText(this, "Please provide a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate job description
        if (jobDescription.isEmpty()) {
            Toast.makeText(this, "Please provide a job description", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current date and time
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        // Retrieve the actual customer email and profile image URL
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String customerEmail = currentUser != null ? currentUser.getEmail() : "";
        String profileImageUrl = currentUser != null && currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : "";

        // Retrieve the Worker ID from the Intent
        String userId = getIntent().getStringExtra("userId");

        // Validate Worker ID
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Worker ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Feedback object
        feedback feedback = new feedback(userId, rating, jobDescription, customerEmail, currentDate, currentTime, profileImageUrl);

        // Push the feedback to Firebase Database under the Worker's ID
        mDatabase.child("Feedback").child(userId).push().setValue(feedback)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Feedback Published Successfully!", Toast.LENGTH_LONG).show();
                        ratingBar.setRating(0); // Reset the rating
                        etJobDescription.setText(""); // Clear the job description text
                    } else {
                        Toast.makeText(this, "Failed to publish feedback", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}