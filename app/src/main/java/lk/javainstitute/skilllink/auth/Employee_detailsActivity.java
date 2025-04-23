package lk.javainstitute.skilllink.auth;

import static android.content.ContentValues.TAG;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.javainstitute.skilllink.R;
import lk.javainstitute.skilllink.adapter.FeedbackAdapter;
import lk.javainstitute.skilllink.adapter.MediaAdapter;
import lk.javainstitute.skilllink.cactivity.AddFeedbackActivity;
import lk.javainstitute.skilllink.model.RequestModel;
import lk.javainstitute.skilllink.model.Workers;
import lk.javainstitute.skilllink.model.feedback;

public class Employee_detailsActivity extends AppCompatActivity {

    private ImageView userimage;
    private FeedbackAdapter feedbackAdapter;
    private List<feedback> feedbackList;
    private RecyclerView recyclerView;
    private EditText etFirstName, etLastName, etJobCategory, etJob, etCity, etEmail, etWorkDescription, etWorkExperience, etPayment, etWorkTime, etMobile;
    private TextView etnic, addFeedbackTextView, mButton, jobPostMediaText, btnSave;
    private FirebaseAuth mAuth, auth;
    private DatabaseReference mDatabase, saveListRef;
    private String userId, jobId;
    private ImageButton Button1, Button2, Button3;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_details);
        getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        userimage = findViewById(R.id.profile_image3);
        etFirstName = findViewById(R.id.First_Name);
        etLastName = findViewById(R.id.Last_Name);
        etnic = findViewById(R.id.j_nic);
        etJobCategory = findViewById(R.id.Job_Category1);
        etJob = findViewById(R.id.Job1);
        etCity = findViewById(R.id.City1);
        etEmail = findViewById(R.id.Email1);
        etWorkDescription = findViewById(R.id.Work_Description1);
        etWorkExperience = findViewById(R.id.Work_Experices1);
        etPayment = findViewById(R.id.Payament1);
        etWorkTime = findViewById(R.id.Work_Time1);
        etMobile = findViewById(R.id.Mobilej);

        addFeedbackTextView = findViewById(R.id.add_feedback);
        mButton = findViewById(R.id.btn_talk_to_work);

        userId = getIntent().getStringExtra("userId");

        if (userId == null || userId.isEmpty()) {
            Log.d(TAG, "No userId received, fetching from database...");
            fetchWorkerIdFromFirebase(); // Fetch from Firebase if not passed
        } else {
            Log.d(TAG, "Received userId: " + userId);

        }

        addFeedbackTextView.setOnClickListener(View -> onAddFeedbackClick());
        mButton.setOnClickListener(view -> onShowtalk_to_workDialog());

        jobPostMediaText = findViewById(R.id.Job_post_photo_and_video);
        jobPostMediaText.setOnClickListener(v -> showMediaDialog());

        RequestModel request = (RequestModel) getIntent().getSerializableExtra("object");
        if (request != null) {
            userId = getIntent().getStringExtra("userId");
            jobId = getIntent().getStringExtra("jobId");

            // Populate job request details
            etJob.setText(request.getJobType() != null ? request.getJobType() : "N/A");
            etCity.setText(request.getCity() != null ? request.getCity() : "N/A");
            etWorkExperience.setText(request.getWorkExperience() != null ? request.getWorkExperience() : "N/A");
            etPayment.setText(request.getPayment() != null ? request.getPayment() : "N/A");
            etWorkTime.setText(request.getWorkTime() != null ? request.getWorkTime() : "N/A");
            etJobCategory.setText(request.getCategory() != null ? request.getCategory() : "N/A");
            etWorkDescription.setText(request.getJobDescription() != null ? request.getJobDescription() : "N/A");
        }

        fetchUserDetails();
        setupRecyclerView();
        fetchFeedbackData();

        // Initialize and set click listener for back button
        ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v -> onBackPressed());

        btnSave = findViewById(R.id.btn_Save);
        saveListRef = FirebaseDatabase.getInstance().getReference("SaveList");

        btnSave.setOnClickListener(v -> saveWorkerToList());

        checkIfWorkerSaved();
        initializeViews();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void onShowtalk_to_workDialog() {
        // Create dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Contact Method");

        // Inflate custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.talk_to_work, null);
        builder.setView(dialogView);

        // Initialize buttons from dialog view
        ImageButton btnCall = dialogView.findViewById(R.id.btn_call);
        ImageButton btnWhatsapp = dialogView.findViewById(R.id.btn_whatsapp);
        ImageButton btnSkillLinkChat = dialogView.findViewById(R.id.btn_skillLinkChat);

        // Create dialog
        AlertDialog dialog = builder.create();

        // Set click listeners for buttons
        btnCall.setOnClickListener(v -> {
            if (etMobile.getText() != null && !etMobile.getText().toString().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + etMobile.getText().toString()));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        btnWhatsapp.setOnClickListener(v -> {
            if (etMobile.getText() != null && !etMobile.getText().toString().isEmpty()) {
                String phoneNumber = etMobile.getText().toString();
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "WhatsApp is not installed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        btnSkillLinkChat.setOnClickListener(v -> {
            if (userId != null && !userId.isEmpty()) {
                Intent intent = new Intent(Employee_detailsActivity.this, ChatActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Cannot start chat: User ID not available", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        // Show dialog
        dialog.show();
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView_Review1); // Updated ID to match layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedbackList = new ArrayList<>();
        feedbackAdapter = new FeedbackAdapter(this, feedbackList);
        recyclerView.setAdapter(feedbackAdapter);

        // Set fixed size for better performance
        recyclerView.setHasFixedSize(true);

        // Add item decoration for spacing if needed
        recyclerView.addItemDecoration(new ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                       @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = 8; // Add 8dp spacing between items
            }
        });
    }

    private void onAddFeedbackClick() {
        if (userId != null && !userId.isEmpty()) {
            Log.d(TAG, "Sending userId: " + userId);
            Intent intent = new Intent(Employee_detailsActivity.this, AddFeedbackActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        } else {
            Log.d(TAG, "Error: Worker ID not found.");
            Toast.makeText(this, "Error: Worker ID not found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchWorkerIdFromFirebase() {
        DatabaseReference workerRef = FirebaseDatabase.getInstance().getReference("Workers");

        workerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot worker : snapshot.getChildren()) {
                        userId = worker.getKey();
                        Log.d(TAG, "Fetched Worker ID: " + userId);
                        break;
                    }
                } else {
                    Log.d(TAG, "No worker IDs found in database.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }

    private void fetchUserDetails() {
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(Employee_detailsActivity.this, "Worker ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        mDatabase.child("Workers").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(Employee_detailsActivity.this, "Worker details not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                Workers worker = snapshot.getValue(Workers.class);
                if (worker != null) {
                    String fName = worker.getFname();
                    String lName = worker.getLname();
                    String email = worker.getEmail();
                    String nic = snapshot.child("nic").getValue(String.class);
                    String mobile = snapshot.child("mobile").getValue(String.class);

                    if (etFirstName != null) etFirstName.setText(fName != null ? fName : "N/A");
                    if (etLastName != null) etLastName.setText(lName != null ? lName : "N/A");
                    if (etnic != null) etnic.setText(nic != null ? nic : "N/A");
                    if (etEmail != null) etEmail.setText(email != null ? email : "N/A");
                    if (etMobile != null) etMobile.setText(mobile != null ? mobile : "N/A");

                    // Load profile image from Firebase Storage
                    StorageReference fileRef = FirebaseStorage.getInstance().getReference()
                            .child("profile_images/" + userId + ".jpg");

                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Glide.with(Employee_detailsActivity.this)
                                .load(uri.toString())
                                .placeholder(R.drawable.user_13530226)
                                .error(R.drawable.user_13530226)
                                .circleCrop()
                                .into(userimage);
                    }).addOnFailureListener(e -> {
                        userimage.setImageResource(R.drawable.user_13530226);
                    });
                } else {
                    Toast.makeText(Employee_detailsActivity.this, "Worker data is null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Employee_detailsActivity.this, "Failed to load personal details", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchFeedbackData() {
        if (userId == null || userId.isEmpty()) {
            Log.d(TAG, "Cannot fetch feedback: userId is null or empty");
            return;
        }

        DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("Feedback").child(userId);
        feedbackRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feedbackList.clear();
                float totalRating = 0;
                int count = 0;

                for (DataSnapshot feedbackSnapshot : snapshot.getChildren()) {
                    feedback feedbackItem = feedbackSnapshot.getValue(feedback.class);
                    if (feedbackItem != null) {
                        feedbackList.add(feedbackItem);
                        totalRating += feedbackItem.getRating();
                        count++;
                    }
                }

                // Update the average rating display
                if (count > 0) {
                    float averageRating = totalRating / count;
                    RatingBar ratingBar = findViewById(R.id.rating_bar);
                    TextView ratingValue = findViewById(R.id.rating_value);

                    ratingBar.setRating(averageRating);
                    ratingValue.setText(String.format("%.1f", averageRating));
                }

                // Update the adapter
                feedbackAdapter.notifyDataSetChanged();

                // Update visibility based on data
                updateRecyclerViewVisibility();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch feedback: " + error.getMessage());
                Toast.makeText(Employee_detailsActivity.this,
                        "Failed to load feedback", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRecyclerViewVisibility() {
        if (feedbackList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            // Optionally show a "no feedback" message
            TextView noFeedbackText = findViewById(R.id.no_feedback_text);
            if (noFeedbackText != null) {
                noFeedbackText.setVisibility(View.VISIBLE);
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            TextView noFeedbackText = findViewById(R.id.no_feedback_text);
            if (noFeedbackText != null) {
                noFeedbackText.setVisibility(View.GONE);
            }
        }
    }

    private void showMediaDialog() {
        if (jobId == null || userId == null) {
            Toast.makeText(this, "Job details not available", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_media_viewer, null);
        builder.setView(dialogView);

        RecyclerView mediaRecyclerView = dialogView.findViewById(R.id.media_recycler_view);
        mediaRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Create and set the dialog
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Fetch media from Firebase
        DatabaseReference mediaRef = FirebaseDatabase.getInstance()
                .getReference("JobPosts")
                .child(userId)
                .child(jobId);

        mediaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RequestModel jobPost = snapshot.getValue(RequestModel.class);
                if (jobPost != null && jobPost.getMediaUrls() != null && !jobPost.getMediaUrls().isEmpty()) {
                    MediaAdapter mediaAdapter = new MediaAdapter(Employee_detailsActivity.this,
                            jobPost.getMediaUrls(), dialog);
                    mediaRecyclerView.setAdapter(mediaAdapter);
                } else {
                    Toast.makeText(Employee_detailsActivity.this,
                            "No media available", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Employee_detailsActivity.this,
                        "Failed to load media", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void saveWorkerToList() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Check if already saved to toggle
        saveListRef.child(currentUserId).orderByChild("workerId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Worker is saved, so unsave
                            for (DataSnapshot saveSnapshot : snapshot.getChildren()) {
                                saveSnapshot.getRef().removeValue()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(Employee_detailsActivity.this,
                                                    "Worker removed from saved list", Toast.LENGTH_SHORT).show();
                                            btnSave.setText("Save");
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(Employee_detailsActivity.this,
                                                    "Failed to remove worker: " + e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            // Worker is not saved, so save
                            String saveId = saveListRef.child(currentUserId).push().getKey();
                            saveNewWorker(currentUserId, saveId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Employee_detailsActivity.this,
                                "Error checking save status: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveNewWorker(String currentUserId, String saveId) {
        // First fetch the worker's current data from Workers table
        DatabaseReference workerRef = FirebaseDatabase.getInstance().getReference()
                .child("Workers")
                .child(userId);

        workerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Workers worker = snapshot.getValue(Workers.class);
                if (worker != null) {
                    // Create save data with current worker information
                    Map<String, Object> saveData = new HashMap<>();
                    saveData.put("workerId", userId);
                    saveData.put("firstName", worker.getFname());
                    saveData.put("lastName", worker.getLname());
                    saveData.put("email", worker.getEmail());
                    saveData.put("imageUrl", worker.getImageUrl()); // Use the current image URL from Workers table
                    saveData.put("jobType", etJob.getText().toString());
                    saveData.put("city", etCity.getText().toString());
                    saveData.put("savedAt", ServerValue.TIMESTAMP);

                    // Save to SaveList table
                    saveListRef.child(currentUserId)
                            .child(saveId)
                            .setValue(saveData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(Employee_detailsActivity.this,
                                        "Worker saved successfully", Toast.LENGTH_SHORT).show();
                                btnSave.setText("Saved");
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(Employee_detailsActivity.this,
                                        "Failed to save worker: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(Employee_detailsActivity.this,
                            "Worker data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Employee_detailsActivity.this,
                        "Failed to fetch worker details: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfWorkerSaved() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        saveListRef.child(currentUserId).orderByChild("workerId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            btnSave.setText("Saved");
                        } else {
                            btnSave.setText("Save");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error checking saved status: " + error.getMessage());
                    }
                });
    }

    private void initializeViews() {
        // Disable editing for all TextInputEditText fields
        etFirstName.setEnabled(false);
        etLastName.setEnabled(false);
        etEmail.setEnabled(false);
        etJobCategory.setEnabled(false);
        etJob.setEnabled(false);
        etCity.setEnabled(false);
        etWorkExperience.setEnabled(false);
        etPayment.setEnabled(false);
        etWorkTime.setEnabled(false);
        etMobile.setEnabled(false);
        etnic.setEnabled(false);

        // Optional: Change the text color to indicate disabled state
        int disabledTextColor = getResources().getColor(android.R.color.black);
        etFirstName.setTextColor(disabledTextColor);
        etLastName.setTextColor(disabledTextColor);
        etEmail.setTextColor(disabledTextColor);
        etJobCategory.setTextColor(disabledTextColor);
        etJob.setTextColor(disabledTextColor);
        etCity.setTextColor(disabledTextColor);
        etWorkExperience.setTextColor(disabledTextColor);
        etPayment.setTextColor(disabledTextColor);
        etWorkTime.setTextColor(disabledTextColor);
        etMobile.setTextColor(disabledTextColor);
        etnic.setTextColor(disabledTextColor);

        // Special handling for work description to allow scrolling while disabled
        etWorkDescription.setEnabled(true);
        etWorkDescription.setTextColor(getResources().getColor(android.R.color.black));

        // Enable scrolling for work description
        etWorkDescription.setMovementMethod(new ScrollingMovementMethod());
        etWorkDescription.setVerticalScrollBarEnabled(true);

        // Make it focusable for scrolling but not editable
        etWorkDescription.setFocusable(true);
        etWorkDescription.setFocusableInTouchMode(true);
        etWorkDescription.setLongClickable(false); // Disable text selection
        etWorkDescription.setCursorVisible(false); // Hide the cursor

        // Optional: Set max lines and scrolling behavior
        etWorkDescription.setMaxLines(6);
        etWorkDescription.setScroller(new Scroller(this));
    }
}

