package com.example.Circuit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShowItem extends AppCompatActivity {
    private String itemID;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_item);

        ImageView imageView = findViewById(R.id.imageView3);
        TextView textViewName = findViewById(R.id.textView);
        TextView textViewPrice = findViewById(R.id.textView2);
        TextView textViewLocation = findViewById(R.id.textView3);
        TextView textViewDesc = findViewById(R.id.textView4);
        Button buttonEdit = findViewById(R.id.button);
        Button buttonDelete = findViewById(R.id.button2);
        Button buttonShare = findViewById(R.id.button3);
        Button buttonCacel = findViewById(R.id.button8);

        Intent intent = getIntent();
        itemID = intent.getStringExtra("itemId");
        String name = intent.getStringExtra("title");
        double price = intent.getDoubleExtra("price", 0.0);
        String location = intent.getStringExtra("location");
        String desc = intent.getStringExtra("description");
        imageUrl = intent.getStringExtra("imageUrl");

        if (itemID == null || itemID.isEmpty()) {
            Toast.makeText(this, "Invalid item ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        textViewName.setText(name);
        textViewPrice.setText(String.valueOf(price));
        textViewLocation.setText(location);
        textViewDesc.setText(desc);
        Glide.with(this).load(imageUrl).into(imageView);

        buttonEdit.setOnClickListener(v -> {
            Intent editIntent = new Intent(ShowItem.this, EditItem.class);
            editIntent.putExtra("itemId", itemID);
            startActivity(editIntent);
        });

        buttonCacel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to Home screen
                finish(); // Finish the current activity (PurchasedItem) to go back to Home
            }
        });

        buttonShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(ShowItem.this, ShareItem.class);
            shareIntent.putExtra("itemId", itemID);
            shareIntent.putExtra("title", name);
            shareIntent.putExtra("price", price);
            shareIntent.putExtra("location", location);
            shareIntent.putExtra("description", desc);
            shareIntent.putExtra("imageUrl", imageUrl);
            startActivity(shareIntent);
        });

        buttonDelete.setOnClickListener(v -> {
            deleteItemFromDatabase(itemID);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void deleteItemFromDatabase(String itemID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("items").child(itemID);
        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ShowItem.this, "Item deleted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(ShowItem.this, "Failed to delete item", Toast.LENGTH_SHORT).show();
                Log.e("ShowItem", "Failed to delete item: " + task.getException().getMessage());
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(ShowItem.this, "Failed to delete item", Toast.LENGTH_SHORT).show();
            Log.e("ShowItem", "Error deleting item", e);
        });
    }
}
