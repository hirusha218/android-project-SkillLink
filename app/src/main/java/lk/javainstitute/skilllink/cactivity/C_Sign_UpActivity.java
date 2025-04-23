package lk.javainstitute.skilllink.cactivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import lk.javainstitute.skilllink.model.Customers;

public class C_Sign_UpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private EditText fname, lname, email, password;
    private ImageView imgProfile;
    private Uri profileImageUri;
    private Button buttonSignIn, buttonSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csign_up);
        getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Customers");
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");

        fname = findViewById(R.id.customereFname);
        lname = findViewById(R.id.customereLname);
        email = findViewById(R.id.customereEmail);
        password = findViewById(R.id.customerePassword);
        imgProfile = findViewById(R.id.profile_image2);
        buttonSignIn = findViewById(R.id.sign_in2);
        buttonSignUp = findViewById(R.id.customer_SignUp);

        imgProfile.setOnClickListener(view -> ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start());

        buttonSignIn.setOnClickListener(view -> startActivity(new Intent(C_Sign_UpActivity.this, C_Sign_InActivity.class)));

        // Sign-Up Button
        buttonSignUp.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String firstName = fname.getText().toString().trim();
        String lastName = lname.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (validateFirstName(firstName) && validateLastName(lastName) && validateEmail(userEmail) && validatePassword(userPassword)) {
            // Check if the email already exists
            reference.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Email already exists in the "Customers" node
                        Toast.makeText(C_Sign_UpActivity.this, "Email is already in use. Please try a different email.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Proceed with registration if email is unique
                        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                        if (firebaseUser != null) {
                                            sendEmailVerification(firebaseUser, firstName, lastName, userEmail);
                                        }
                                    } else {
                                        Toast.makeText(C_Sign_UpActivity.this, "Sign-up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("W_Sign_UpActivity", "Database error: " + databaseError.getMessage());
                    Toast.makeText(C_Sign_UpActivity.this, "Database error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean validateFirstName(String firstName) {
        if (firstName.isEmpty()) {
            Toast.makeText(this, "Oops! Looks like you forgot your first name. 🤔", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateLastName(String lastName) {
        if (lastName.isEmpty()) {
            Toast.makeText(this, "Don't leave your last name behind! 😅", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateEmail(String userEmail) {
        if (userEmail.isEmpty()) {
            Toast.makeText(this, "We need your email to stay in touch. 📧", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email.setError("Hmm, that doesn't look like a valid email. 🤨");
            return false;
        }
        return true;
    }

    private boolean validatePassword(String userPassword) {
        if (userPassword.isEmpty()) {
            Toast.makeText(this, "A password is your key to security. Don't forget it! 🔑", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (userPassword.length() < 6) {
            password.setError("Your password should be at least 6 characters long. 🔒");
            return false;
        }
        if (!userPassword.matches(".*[A-Z].*")) {
            password.setError("Include at least one uppercase letter. 🔠");
            return false;
        }
        if (!userPassword.matches(".*[a-z].*")) {
            password.setError("Include at least one lowercase letter. 🔡");
            return false;
        }
        if (!userPassword.matches(".*\\d.*")) {
            password.setError("Include at least one number. 🔢");
            return false;
        }
        if (!userPassword.matches(".*[@#$%^&+=].*")) {
            password.setError("Include at least one special character. 💥");
            return false;
        }
        return true;
    }

    private void sendEmailVerification(FirebaseUser firebaseUser, String firstName, String lastName, String email) {
        firebaseUser.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(C_Sign_UpActivity.this, "Verification email sent. Please check your inbox.", Toast.LENGTH_LONG).show();
                        uploadProfileImage(firebaseUser, firstName, lastName, email);
                    } else {
                        Toast.makeText(C_Sign_UpActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadProfileImage(FirebaseUser firebaseUser, String firstName, String lastName, String email) {
        if (profileImageUri != null) {
            StorageReference fileRef = storageReference.child(firebaseUser.getUid() + ".jpg");

            fileRef.putFile(profileImageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        saveUserData(firebaseUser, firstName, lastName, email, uri.toString());
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(C_Sign_UpActivity.this, "Profile image upload failed", Toast.LENGTH_SHORT).show();
                        saveUserData(firebaseUser, firstName, lastName, email, null);
                    });
        } else {
            saveUserData(firebaseUser, firstName, lastName, email, null);
        }
    }

    private void saveUserData(FirebaseUser firebaseUser, String firstName, String lastName, String email, String imageUrl) {
        String userId = firebaseUser.getUid(); // Get the Firebase UID

        Customers user = new Customers(firstName, lastName, email, imageUrl, userId);
        reference.child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Customers").child(userId);
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(firstName + " " + lastName)
                                .setPhotoUri(imageUrl != null ? Uri.parse(imageUrl) : null)
                                .build();

                        firebaseUser.updateProfile(profileUpdates)
                                .addOnCompleteListener(profileTask -> {
                                    if (profileTask.isSuccessful()) {
                                        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("USER_ID", userId);
                                        editor.apply();

                                        Toast.makeText(C_Sign_UpActivity.this, "Workers registered successfully!", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut(); // Log the user out after registration
                                        startActivity(new Intent(C_Sign_UpActivity.this, C_Sign_InActivity.class));
                                        finish();
                                    }
                                });
                    } else {
                        Toast.makeText(C_Sign_UpActivity.this, "Failed to store user data", Toast.LENGTH_SHORT).show();
                    }
                });
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
