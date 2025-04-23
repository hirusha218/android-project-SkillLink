package lk.javainstitute.skilllink.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import lk.javainstitute.skilllink.R;
import lk.javainstitute.skilllink.cactivity.C_Sign_UpActivity;
import lk.javainstitute.skilllink.wactivity.W_Sign_UpActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Button button2 = findViewById(R.id.workerSignUp);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                boolean Check_accept_permissions = true;
//                if (Check_accept_permissions()) {
                // If permissions are accepted, navigate to Worker_Sign_UpActivity
                Intent intent = new Intent(SplashActivity.this, W_Sign_UpActivity.class);
                startActivity(intent);
//                } else {
//                    // Handle the case where permissions are not accepted
//                    Toast.makeText(SplashActivity.this, "Permissions not granted. Please enable permissions to proceed.", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        Button button1 = findViewById(R.id.customerSignIn);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                boolean Check_accept_permissions = true;
//                if (Check_accept_permissions()) {
                // If permissions are accepted, navigate to Worker_Sign_UpActivity
                Intent intent = new Intent(SplashActivity.this, C_Sign_UpActivity.class);
                startActivity(intent);
//                } else {
//                    // Handle the case where permissions are not accepted
//                    Toast.makeText(SplashActivity.this, "Permissions not granted. Please enable permissions to proceed.", Toast.LENGTH_SHORT).show();
//                }
            }
        });


    }
}