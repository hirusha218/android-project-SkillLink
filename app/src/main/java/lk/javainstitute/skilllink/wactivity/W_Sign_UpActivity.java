package lk.javainstitute.skilllink.wactivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import lk.javainstitute.skilllink.R;
import lk.javainstitute.skilllink.model.Workers;

public class W_Sign_UpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private EditText fname, lname, email, skill, password;
    private ImageView imgProfile;
    private Uri profileImageUri;
    private Button buttonSignIn, buttonSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wsign_up);
        getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Workers");
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");

        fname = findViewById(R.id.workerFname);
        lname = findViewById(R.id.workerLname);
        email = findViewById(R.id.WorkerEmail);
        skill = findViewById(R.id.workerSkill);
        password = findViewById(R.id.WorkerPassword);
        imgProfile = findViewById(R.id.profile_image);
        buttonSignIn = findViewById(R.id.SignIn);
        buttonSignUp = findViewById(R.id.workerSignUp);

        imgProfile.setOnClickListener(view -> ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start());

        buttonSignIn.setOnClickListener(view -> startActivity(new Intent(W_Sign_UpActivity.this, W_Sign_InActivity.class)));

        buttonSignUp.setOnClickListener(view -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                updateUserData(currentUser);
            } else {
                registerUser();
            }
        });

        loadUserData();
    }

    private void registerUser() {
        String firstName = fname.getText().toString().trim();
        String lastName = lname.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userSkill = skill.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (!validateInput(firstName, lastName, userEmail, userSkill, userPassword)) {
            return;
        }

        reference.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(W_Sign_UpActivity.this, "Email is already in use. Please try a different email.", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        sendEmailVerification(firebaseUser, firstName, lastName, userSkill, userEmail);
                                    }
                                } else {
                                    Toast.makeText(W_Sign_UpActivity.this, "Sign-up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(W_Sign_UpActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserData(FirebaseUser firebaseUser) {
        String firstName = fname.getText().toString().trim();
        String lastName = lname.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userSkill = skill.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (!validateInput(firstName, lastName, userEmail, userSkill, userPassword)) {
            return;
        }

        String userId = firebaseUser.getUid();
        Workers workers = new Workers(firstName, lastName, userEmail, userSkill, null, userId);

        reference.child(userId).setValue(workers)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(firstName + " " + lastName)
                                .build();
                        firebaseUser.updateProfile(profileUpdates)
                                .addOnCompleteListener(profileTask -> {
                                    if (profileTask.isSuccessful()) {
                                        uploadProfileImage(firebaseUser, firstName, lastName, userSkill, userEmail);
                                    } else {
                                        Toast.makeText(this, "Failed to update profile: " + profileTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Failed to update data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInput(String firstName, String lastName, String userEmail, String userSkill, String userPassword) {
        if (firstName.isEmpty()) {
            fname.setError("First name is required");
            return false;
        }
        if (lastName.isEmpty()) {
            lname.setError("Last name is required");
            return false;
        }
        if (userEmail.isEmpty()) {
            email.setError("Email is required");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email.setError("Enter a valid email");
            return false;
        }
        if (userSkill.isEmpty()) {
            skill.setError("Skill is required");
            return false;
        }
        if (userPassword.isEmpty()) {
            password.setError("Password is required");
            return false;
        }
        if (userPassword.length() < 6) {
            password.setError("Password must be at least 6 characters");
            return false;
        }
        return true;
    }

    private void sendEmailVerification(FirebaseUser firebaseUser, String firstName, String lastName, String userSkill, String userEmail) {
        firebaseUser.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Verification email sent. Please check your inbox.", Toast.LENGTH_LONG).show();
                        uploadProfileImage(firebaseUser, firstName, lastName, userSkill, userEmail);
                    } else {
                        Toast.makeText(this, "Failed to send verification email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadProfileImage(FirebaseUser firebaseUser, String firstName, String lastName, String userSkill, String userEmail) {
        if (profileImageUri != null) {
            StorageReference fileRef = storageReference.child(firebaseUser.getUid() + ".jpg");
            fileRef.putFile(profileImageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        saveUserData(firebaseUser, firstName, lastName, userSkill, userEmail, imageUrl);
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Profile image upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        saveUserData(firebaseUser, firstName, lastName, userSkill, userEmail, null);
                    });
        } else {
            saveUserData(firebaseUser, firstName, lastName, userSkill, userEmail, null);
        }
    }

    private void saveUserData(FirebaseUser firebaseUser, String firstName, String lastName, String userSkill, String userEmail, String imageUrl) {
        String userId = firebaseUser.getUid();
        Workers workers = new Workers(firstName, lastName, userEmail, userSkill, imageUrl, userId);

        reference.child(userId).setValue(workers)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Verify data was saved using ValueEventListener
                        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Workers savedWorker = snapshot.getValue(Workers.class);
                                    if (savedWorker != null && savedWorker.getEmail().equals(userEmail)) {
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(firstName + " " + lastName)
                                                .setPhotoUri(imageUrl != null ? Uri.parse(imageUrl) : null)
                                                .build();

                                        firebaseUser.updateProfile(profileUpdates)
                                                .addOnCompleteListener(profileTask -> {
                                                    if (profileTask.isSuccessful()) {
                                                        Toast.makeText(W_Sign_UpActivity.this, "Worker registered successfully!", Toast.LENGTH_SHORT).show();
                                                        mAuth.signOut();
                                                        startActivity(new Intent(W_Sign_UpActivity.this, W_Sign_InActivity.class));
                                                        finish();

                                                    } else {
                                                        Toast.makeText(W_Sign_UpActivity.this, "Failed to update profile: " + profileTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(W_Sign_UpActivity.this, "Data saved but verification failed!", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(W_Sign_UpActivity.this, "Data not found in database!", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(W_Sign_UpActivity.this, "Failed to verify data: " + error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, "Failed to store worker data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loadUserData() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Workers worker = snapshot.getValue(Workers.class);
                        if (worker != null) {
                            fname.setText(worker.getFname());
                            lname.setText(worker.getLname());
                            email.setText(worker.getEmail());
                            skill.setText(worker.getSkill());
                            if (worker.getImageUrl() != null) {
                                profileImageUri = Uri.parse(worker.getImageUrl());
                                imgProfile.setImageURI(profileImageUri);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(W_Sign_UpActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            profileImageUri = data.getData();
            imgProfile.setImageURI(profileImageUri);
        }
    }
}