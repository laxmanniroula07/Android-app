package com.example.Circuit;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class Login extends AppCompatActivity {
    private FirebaseAuth auth;
    ProgressBar progressBar;
    private ImageView eyeImageView;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //code to navigate to forget password page after clicking on forget password
        TextView forgetpass = findViewById(R.id.textViewForgetPassword);
        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgetPassword.class);
                startActivity(intent);
            }
        });

        //code to navigate to register page after clicking on textView
        TextView SignupView = findViewById(R.id.textViewNewAccount);
        SignupView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        EditText username = findViewById(R.id.editTextUsername);
        EditText password = findViewById(R.id.editTextPassword);
        Button login = findViewById(R.id.buttonLogin);
        eyeImageView = findViewById(R.id.imageViewEye);

        eyeImageView.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Hide password
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                eyeImageView.setImageResource(R.drawable.eye);
            } else {
                // Show password
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                eyeImageView.setImageResource(R.drawable.eye);
            }
            // Toggle the boolean flag
            isPasswordVisible = !isPasswordVisible;

            // Move the cursor to the end of the text
            password.setSelection(password.length());
        });

        auth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if (user.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(user).matches()){
                    username.setError("Enter a valid email address");
                    return;
                }

                if (pass.isEmpty()) {
                    password.setError("Password cannot be empty");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.signInWithEmailAndPassword(user, pass)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Login.this, Home.class));
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        Toast.makeText(Login.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });
    }
}
