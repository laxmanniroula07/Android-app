package com.example.Circuit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.Circuit.databinding.ActivityAddItemBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddItem extends AppCompatActivity {
    ActivityAddItemBinding binding;
    private DatabaseReference databaseReference;
    private Uri imageUri;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("items");

        // Initialize Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().getReference();

        // Open gallery when image view is clicked
        binding.imageView.setOnClickListener(view -> openGallery());

        // Add item to Firebase when add button is clicked
        binding.buttonadd.setOnClickListener(view -> addItemToDatabase());

        // Navigate back to Home when cancel button is clicked
        binding.buttoncancel.setOnClickListener(view -> {
            Intent intent = new Intent(AddItem.this, Home.class);
            startActivity(intent);
        });

        // Set initial visibility of progress bar to GONE
        binding.progressBar.setVisibility(View.GONE);
    }

    private void openGallery() {
        Intent imagePickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(imagePickerIntent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            binding.imageView.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Image picking failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void addItemToDatabase() {
        String title = binding.editTextimgtitle.getText().toString();
        String priceStr = binding.editTextprice.getText().toString().trim();
        String location = binding.editTextlocation.getText().toString();
        String description = binding.editTextdesc.getText().toString();

        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(location) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            binding.editTextprice.setError("Please enter a valid price");
            return;
        }

        // Show progress bar while uploading
        binding.progressBar.setVisibility(View.VISIBLE);

        // Upload the image to Firebase Storage
        StorageReference fileReference = storageReference.child("images/" + System.currentTimeMillis() + ".jpg"); // Specify a valid path
        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Get the download URL
                        String imageUrl = uri.toString();

                        // Save the item details to the database
                        DatabaseReference newItemRef = databaseReference.push();
                        String itemId = newItemRef.getKey();
                        newItemRef.setValue(new Item(itemId, imageUrl, title, price, location, description))
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(AddItem.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                                    binding.progressBar.setVisibility(View.GONE);
                                    startActivity(new Intent(AddItem.this, Home.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(AddItem.this, "Failed to add item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    binding.progressBar.setVisibility(View.GONE); // Hide progress bar on failure
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    // Image upload failed
                    Toast.makeText(AddItem.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE); // Hide progress bar on failure
                })
                .addOnCompleteListener(task -> {
                    // Hide progress bar on completion (whether success or failure)
                    binding.progressBar.setVisibility(View.GONE);
                });
    }
}
