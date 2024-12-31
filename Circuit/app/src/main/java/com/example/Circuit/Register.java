package com.example.Circuit;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;


public class Register extends AppCompatActivity {
    private FirebaseAuth auth;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //code to navigate to login page after clicking on button
        Button loginbtn = findViewById(R.id.button_login);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });

        //register functionality code
        EditText fullname = findViewById(R.id.editText_name);
        EditText username = findViewById(R.id.editText_username);
        EditText password = findViewById(R.id.editText_password);
        ImageView eyeImageView1 = findViewById(R.id.imageviewpasseye);
        EditText confirmpassword = findViewById(R.id.editText_confirmpassword);
        ImageView eyeImageView2 = findViewById(R.id.imageviewconpasseye);
        Button signup = findViewById(R.id.button_signup);
        CheckBox agreebox = findViewById(R.id.checkBox);

        eyeImageView1.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Hide password
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                eyeImageView1.setImageResource(R.drawable.eye);
            } else {
                // Show password
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                eyeImageView1.setImageResource(R.drawable.eye);
            }
            // Toggle the boolean flag
            isPasswordVisible = !isPasswordVisible;

            // Move the cursor to the end of the text
            password.setSelection(password.length());
        });

        eyeImageView2.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Hide password
                confirmpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                eyeImageView1.setImageResource(R.drawable.eye);
            } else {
                // Show password
                confirmpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                eyeImageView1.setImageResource(R.drawable.eye);
            }
            // Toggle the boolean flag
            isPasswordVisible = !isPasswordVisible;

            // Move the cursor to the end of the text
            confirmpassword.setSelection(confirmpassword.length());
        });

        auth = FirebaseAuth.getInstance();
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = fullname.getText().toString();
                String user = username.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String conpass = confirmpassword.getText().toString().trim();

                if (name.isEmpty()) {
                    username.setError("Please enter your name");
                    return;
                }

                if (user.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(user).matches()) {
                    username.setError("Please enter a valid email address");
                    return;
                }

                if (pass.isEmpty()) {
                    password.setError("Please enter your password");
                    return;
                }

                if (conpass.isEmpty()) {
                    confirmpassword.setError("Please confirm your password");
                    return;
                }

                if (!pass.equals(conpass)) {
                    confirmpassword.setError("Passwords do not match");
                    return;
                }
                // Check if password is strong enough
                if (password.length() < 8) {
                    password.setError("Your password is too weak. Please choose a stronger password.");
                    return;
                }

                // Check if the user has agreed to the terms and conditions
                if (!agreebox.isChecked()) {
                    Toast.makeText(Register.this, "Please agree to the terms and conditions", Toast.LENGTH_SHORT).show();
                    return;
                }
                 else {
                    auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Register.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Register.this, Login.class));
                            } else {
                                String errorMessage = "Signup failed. Please try again later.";
                                Exception exception = task.getException();
                                if (exception instanceof FirebaseAuthWeakPasswordException) {
                                    errorMessage = "Your password is too weak. Please choose a stronger password.";
                                } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                    errorMessage = "Invalid email address. Please enter a valid email address.";
                                } else if (exception instanceof FirebaseAuthUserCollisionException) {
                                    errorMessage = "This email address is already registered. Please try another one.";
                                }
                                Toast.makeText(Register.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }
}