package com.example.suitcase;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;

public class Splash extends AppCompatActivity {
    LottieAnimationView lottie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        //splas=findViewById(R.id.splas);
        lottie=findViewById(R.id.lottie);

        //splas.animate().translationY(-1400).setDuration(2700).setStartDelay(0);
        lottie.animate().translationY(2000).setDuration(200).setStartDelay(2900);


        new Handler().postDelayed(() -> {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
        },4000);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}