package lk.javainstitute.skilllink.wactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import lk.javainstitute.skilllink.R;

public class W_Sign_InActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email, password;
    private Button buttonSignIn;
    private TextView forgotPassword, signUpLink;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wsign_in);
        getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.Workeremail2);
        password = findViewById(R.id.Workerpassword2);
        buttonSignIn = findViewById(R.id.workerSignIn);
        forgotPassword = findViewById(R.id.forgotPassword);
        signUpLink = findViewById(R.id.SignUp1);
        progressBar = findViewById(R.id.progressBar);
        buttonSignIn.setOnClickListener(view -> loginUser());
        forgotPassword.setOnClickListener(view -> resetPassword());
        signUpLink.setOnClickListener(view -> startActivity(new Intent(W_Sign_InActivity.this, W_Sign_UpActivity.class)));
    }

    private void loginUser() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (!validateInput(userEmail, userPassword)) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(W_Sign_InActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Please verify your email before logging in.", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                        }
                    } else {
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInput(String userEmail, String userPassword) {
        if (userEmail.isEmpty()) {
            email.setError("Email is required");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email.setError("Enter a valid email");
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

    private void resetPassword() {
        String userEmail = email.getText().toString().trim();

        if (userEmail.isEmpty()) {
            email.setError("Enter your email");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email.setError("Enter a valid email");
            return;
        }

        mAuth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Password reset link sent to your email.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to send reset email.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
