package com.example.Circuit;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.Circuit.databinding.ActivityHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {
    ActivityHomeBinding binding;
    private DatabaseReference databaseReference;
    private DatabaseReference purchasedReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("items");
        purchasedReference = FirebaseDatabase.getInstance().getReference("purchasedItems");

        // Set up RecyclerView
        List<Item> itemList = new ArrayList<>();
        ItemAdapter itemAdapter = new ItemAdapter(this, itemList);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerView.setAdapter(itemAdapter);

        binding.imageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the AddItem screen
                Intent intent = new Intent(Home.this, AddItem.class);
                startActivity(intent);
            }
        });

        binding.imageViewPurchased.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the PurchasedItem screen
                Intent intent = new Intent(Home.this, PurchasedItem.class);
                startActivity(intent);
            }
        });

        // Handle click on Logout button
        binding.imageViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut(); // Sign out the current user

                // Navigate to Login screen
                Intent intent = new Intent(Home.this, Login.class);
                startActivity(intent);
                finish(); // Finish the current activity (Home)

                // Show toast message
                Toast.makeText(Home.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            }
        });

        // Add the spacing decoration
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        binding.recyclerView.addItemDecoration(new com.example.recyclerview.ItemDecoration(spacingInPixels));

        // Set up swipe-to-delete and swipe-to-purchase functionality
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(itemAdapter, databaseReference, purchasedReference, this));
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Item item = dataSnapshot.getValue(Item.class);
                    itemList.add(item);
                }

                if (itemList.isEmpty()) {
                    binding.textViewNoItem.setVisibility(View.VISIBLE);
                    binding.recyclerView.setVisibility(View.GONE);
                } else {
                    binding.textViewNoItem.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    itemAdapter.notifyDataSetChanged(); // Notify the adapter of the dataset changes
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Log.e("HomeActivity", "Database error: " + error.getMessage());
            }
        });
    }
}
