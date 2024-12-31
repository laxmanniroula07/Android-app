package com.example.Circuit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditItem extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextPrice;
    private EditText editTextLocation;
    private EditText editTextDescription;
    private Button buttonSave;
    private Button buttonCancel;
    private String itemID;
    private ImageView imageView;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_item);

        imageView = findViewById(R.id.imageView4);
        editTextTitle = findViewById(R.id.editTextText2);
        editTextPrice = findViewById(R.id.editTextNumber);
        editTextLocation = findViewById(R.id.editTextText4);
        editTextDescription = findViewById(R.id.editTextText5);
        buttonSave = findViewById(R.id.button4);
        buttonCancel = findViewById(R.id.button5);

        itemID = getIntent().getStringExtra("itemId");
        databaseReference = FirebaseDatabase.getInstance().getReference("items").child(itemID);

        // Fetch item details from Firebase Realtime Database
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Item item = dataSnapshot.getValue(Item.class);
                    if (item != null) {
                        editTextTitle.setText(item.getTitle());
                        editTextPrice.setText(String.valueOf(item.getPrice()));
                        editTextLocation.setText(item.getLocation());
                        editTextDescription.setText(item.getDescription());

                        // Load image using Glide
                        Glide.with(EditItem.this)
                                .load(item.getImageUrl())
                                .into(imageView);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditItem.this, "Failed to load item details", Toast.LENGTH_SHORT).show();
            }
        });

        buttonSave.setOnClickListener(v -> {
            saveChangesToDatabase();
        });

        buttonCancel.setOnClickListener(v -> {
            finish(); // Simply finish the activity
        });

        // Apply insets to the main view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void saveChangesToDatabase() {
        String updatedTitle = editTextTitle.getText().toString();
        double updatedPrice = Double.parseDouble(editTextPrice.getText().toString());
        String updatedLocation = editTextLocation.getText().toString();
        String updatedDescription = editTextDescription.getText().toString();

        // Update item in Firebase Realtime Database
        databaseReference.child("title").setValue(updatedTitle);
        databaseReference.child("price").setValue(updatedPrice);
        databaseReference.child("location").setValue(updatedLocation);
        databaseReference.child("description").setValue(updatedDescription)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditItem.this, "Item updated", Toast.LENGTH_SHORT).show();
                        // Navigate to Home activity
                        Intent intent = new Intent(EditItem.this, Home.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish(); // Finish the EditItem activity
                    } else {
                        Toast.makeText(EditItem.this, "Failed to update item", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
