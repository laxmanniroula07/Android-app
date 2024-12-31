package com.example.Circuit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ShareItem extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private EditText etPhoneNumber;
    private String title;
    private double price;
    private String location;
    private String description;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_share_item);

        etPhoneNumber = findViewById(R.id.editTextNumber2);
        Button shareBtn = findViewById(R.id.button6);
        Button cancelBtn = findViewById(R.id.button7);

        // Retrieve item details from intent
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        price = intent.getDoubleExtra("price", 0.0);
        location = intent.getStringExtra("location");
        description = intent.getStringExtra("description");
        imageUrl = intent.getStringExtra("imageUrl");

        // Register EditText for phone number handling (if needed)
        // etPhoneNumber = findViewById(R.id.editTextNumber2);

        shareBtn.setOnClickListener(v -> {
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            if (!phoneNumber.isEmpty()) {
                String message = getItemDetails();
                sendSMS(phoneNumber, message);
            } else {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            }
        });

        cancelBtn.setOnClickListener(v -> {
            finish(); // Finish current activity and go back to the previous one
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private String getItemDetails() {
        return "Title: " + title + "\n" +
                "Price: " + price + "\n" +
                "Location: " + location + "\n" +
                "Description: " + description + "\n" +
                "Image URL: " + imageUrl;
    }

    private void sendSMS(String phoneNumber, String message) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            try {
                // Using implicit intent to send SMS
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
                smsIntent.putExtra("sms_body", message);

                if (smsIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(smsIntent);
                } else {
                    Toast.makeText(this, "No SMS app available.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Failed to send SMS. Please try again.", Toast.LENGTH_SHORT).show();
                Log.e("ShareItem", "SMS send failed", e);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String phoneNumber = etPhoneNumber.getText().toString().trim();
                String message = getItemDetails();
                sendSMS(phoneNumber, message);
            } else {
                Toast.makeText(this, "SMS permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
