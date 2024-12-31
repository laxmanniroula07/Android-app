package com.example.Circuit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Item> itemList;
    private Context context;

    // Constructor to initialize the adapter with the context and list of items
    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_adapter, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.titleTextView.setText(item.getTitle());
        holder.priceTextView.setText(String.valueOf(item.getPrice()));
        holder.locationTextView.setText(item.getLocation());
        holder.descriptionTextView.setText(item.getDescription());
        Glide.with(context).load(item.getImageUrl()).into(holder.itemImageView);

        // Set a content description for accessibility
        holder.itemImageView.setContentDescription("Image of " + item.getTitle());

        // Handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start ShowItemActivity and pass item details
                Intent intent = new Intent(context, ShowItem.class);
                intent.putExtra("itemId", item.getId());
                intent.putExtra("title", item.getTitle());
                intent.putExtra("price", item.getPrice());
                intent.putExtra("location", item.getLocation());
                intent.putExtra("description", item.getDescription());
                intent.putExtra("imageUrl", item.getImageUrl());
                // Add more data as needed
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public Item getItem(int position) {
        return itemList.get(position);
    }

    public void removeItem(int position) {
        itemList.remove(position);
        notifyItemRemoved(position);
//        Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
    }

    public Context getContext() {
        return context;
    }
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImageView;
        TextView titleTextView;
        TextView priceTextView;
        TextView locationTextView;
        TextView descriptionTextView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.imageView2);
            titleTextView = itemView.findViewById(R.id.textViewtitle);
            priceTextView = itemView.findViewById(R.id.textViewprice);
            locationTextView = itemView.findViewById(R.id.textViewlocation);
            descriptionTextView = itemView.findViewById(R.id.textViewdesc);
        }
    }
}
