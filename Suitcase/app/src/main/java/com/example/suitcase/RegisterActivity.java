package com.example.suitcase;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    EditText regfullname, regusername, regemail, regpassword;
    Button registerbtn;
    TextView reglogin;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        mAuth=FirebaseAuth.getInstance();
        regfullname=findViewById(R.id.signup_name);
        regusername=findViewById(R.id.signup_username);
        regemail=findViewById(R.id.signup_email);
        regpassword=findViewById(R.id.signup_password);
        registerbtn=findViewById(R.id.signup_button);
        reglogin=findViewById(R.id.loginRedirectText);



        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password, fullname, username;
                email = String.valueOf(regemail.getText());
                password = String.valueOf(regpassword.getText());
                fullname = String.valueOf(regfullname.getText());
                username = String.valueOf(regusername.getText());

                if (TextUtils.isEmpty(fullname)) {
                    Toast.makeText(RegisterActivity.this, "Enter your fullname", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(RegisterActivity.this, "Enter your username", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this, "Enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length()<8){
                    regpassword.setError("password must be more than 8 characters");
                    return;
                }


                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Account Created",
                                            Toast.LENGTH_SHORT).show();
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        reglogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });


    }
}