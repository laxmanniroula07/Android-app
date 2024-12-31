package com.example.Circuit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private final ItemAdapter mAdapter;
    private final DatabaseReference mDatabaseReference;
    private final DatabaseReference mPurchasedReference;
    private final Context context;
    private final Drawable deleteIcon;
    private final Drawable purchasedIcon;
    private final ColorDrawable deleteBackground;
    private final ColorDrawable purchasedBackground;

    public SwipeToDeleteCallback(ItemAdapter adapter, DatabaseReference databaseReference, DatabaseReference purchasedReference, Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mAdapter = adapter;
        this.mDatabaseReference = databaseReference;
        this.mPurchasedReference = purchasedReference;
        this.context = context;
        deleteIcon = ContextCompat.getDrawable(mAdapter.getContext(), R.drawable.delete);
        purchasedIcon = ContextCompat.getDrawable(mAdapter.getContext(), R.drawable.purchased);
        deleteBackground = new ColorDrawable(Color.RED);
        purchasedBackground = new ColorDrawable(Color.GREEN);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false; // We don't support drag-and-drop functionality
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        Item item = mAdapter.getItem(position);

        if (direction == ItemTouchHelper.LEFT) {
            // Remove item from Firebase Database
            mDatabaseReference.child(item.getId()).removeValue();
            // Remove item from RecyclerView
            mAdapter.removeItem(position);
            // Show toast for deletion
            Toast.makeText(mAdapter.getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
        } else if (direction == ItemTouchHelper.RIGHT) {
            // Move item to Purchased Items in Firebase Database
            mPurchasedReference.child(item.getId()).setValue(item);
            // Remove item from original list
            mDatabaseReference.child(item.getId()).removeValue();
            mAdapter.removeItem(position);
            // Show toast for purchased item
            Toast.makeText(mAdapter.getContext(), "Item added to purchased list", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20; // Adjust this value for better corner shape

        if (dX < 0) { // Swiping to the left
            deleteBackground.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
            deleteBackground.draw(c);

            int deleteIconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
            int deleteIconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
            int deleteIconLeft = itemView.getRight() - deleteIconMargin - deleteIcon.getIntrinsicWidth();
            int deleteIconRight = itemView.getRight() - deleteIconMargin;
            int deleteIconBottom = deleteIconTop + deleteIcon.getIntrinsicHeight();

            deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
            deleteIcon.draw(c);
        } else if (dX > 0) { // Swiping to the right
            purchasedBackground.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
            purchasedBackground.draw(c);

            int purchasedIconTop = itemView.getTop() + (itemView.getHeight() - purchasedIcon.getIntrinsicHeight()) / 2;
            int purchasedIconMargin = (itemView.getHeight() - purchasedIcon.getIntrinsicHeight()) / 2;
            int purchasedIconLeft = itemView.getLeft() + purchasedIconMargin;
            int purchasedIconRight = itemView.getLeft() + purchasedIconMargin + purchasedIcon.getIntrinsicWidth();
            int purchasedIconBottom = purchasedIconTop + purchasedIcon.getIntrinsicHeight();

            purchasedIcon.setBounds(purchasedIconLeft, purchasedIconTop, purchasedIconRight, purchasedIconBottom);
            purchasedIcon.draw(c);
        }
    }
}
