package com.example.Circuit;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.Circuit.databinding.ActivityPurchasedItemBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PurchasedItem extends AppCompatActivity {
    ActivityPurchasedItemBinding binding;
    private DatabaseReference purchasedReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPurchasedItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Database reference
        purchasedReference = FirebaseDatabase.getInstance().getReference("purchasedItems");

        // Set up RecyclerView
        List<Item> purchasedItemList = new ArrayList<>();
        ItemAdapter itemAdapter = new ItemAdapter(this, purchasedItemList);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerView.setAdapter(itemAdapter);
        // Add the spacing decoration
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        binding.recyclerView.addItemDecoration(new com.example.recyclerview.ItemDecoration(spacingInPixels));

        binding.button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to Home screen
                finish(); // Finish the current activity (PurchasedItem) to go back to Home
            }
        });

        purchasedReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                purchasedItemList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Item item = dataSnapshot.getValue(Item.class);
                    purchasedItemList.add(item);
                }

                if (purchasedItemList.isEmpty()) {
                    binding.textView13.setVisibility(View.VISIBLE);
                    binding.recyclerView.setVisibility(View.GONE);
                } else {
                    binding.textView13.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    itemAdapter.notifyDataSetChanged(); // Notify the adapter of the dataset changes
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Log.e("PurchasedItemActivity", "Database error: " + error.getMessage());
            }
        });
    }
}
