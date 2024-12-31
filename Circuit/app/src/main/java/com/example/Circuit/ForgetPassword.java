package com.example.Circuit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button cancelbtn = findViewById(R.id.buttonCancel);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPassword.this, Login.class);
                startActivity(intent);
            }
        });

        EditText username = findViewById(R.id.editTextTextUsername);
        Button next = findViewById(R.id.buttonNext);

        auth = FirebaseAuth.getInstance();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = username.getText().toString().trim();

                if (email.isEmpty()) {
                    username.setError("Email cannot be empty");
                    return;
                }
                // Send password reset email
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgetPassword.this,
                                            "Password reset email sent. Please check your email.",
                                            Toast.LENGTH_SHORT).show();
                                    // Optionally, navigate the user back to the login activity
                                    finish();
                                } else {
                                    Toast.makeText(ForgetPassword.this,
                                            "Failed to send password reset email. Please try again.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}