package lk.javainstitute.skilllink.cactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import lk.javainstitute.skilllink.R;

public class C_Sign_InActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private EditText email, password;
    private Button buttonSignIn;
    private TextView forgotPassword, signUpLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csign_in);
        getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        // Initialize UI Components
        email = findViewById(R.id.customeremail2);
        password = findViewById(R.id.customerpassword2);
        buttonSignIn = findViewById(R.id.customer_SignIn);
        signUpLink = findViewById(R.id.signUp2);
        forgotPassword = findViewById(R.id.forgotPassword2);

        buttonSignIn.setOnClickListener(view -> loginUser());
        forgotPassword.setOnClickListener(view -> resetPassword());
        signUpLink.setOnClickListener(view -> startActivity(new Intent(C_Sign_InActivity.this, C_Sign_UpActivity.class)));
    }

    private void loginUser() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (!validateInput(userEmail, userPassword)) {
            return;
        }
        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            Toast.makeText(this, "Login Successful! 🎉", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(C_Sign_InActivity.this, C_MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Please verify your email before logging in. 📧", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                        }
                    } else {
                        Toast.makeText(this, "Login failed: Incorrect email or password. 😕", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInput(String userEmail, String userPassword) {
        if (userEmail.isEmpty()) {
            email.setError("Email is needed to log in. 📧");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email.setError("That doesn't look like a valid email. 🤔");
            return false;
        }
        if (userPassword.isEmpty()) {
            password.setError("Password is needed to log in. 🔑");
            return false;
        }
        if (userPassword.length() < 6) {
            password.setError("Password must be at least 6 characters. 🔒");
            return false;
        }
        return true;
    }

    private void resetPassword() {
        String userEmail = email.getText().toString().trim();

        if (userEmail.isEmpty()) {
            email.setError("Enter your email to reset password. 📧");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email.setError("Enter a valid email. 🤔");
            return;
        }

        mAuth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Password reset link sent to your email. 📧", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to send reset email. 😞", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}