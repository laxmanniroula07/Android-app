package com.example.suitcase;

import android.content.Intent;
import android.os.Bundle;
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
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Details extends AppCompatActivity {


        TextView detailDesc, detailTitle, detailprice;
        ImageView detailImage;
        FloatingActionButton deleteButton, editButton;
        String key = "";
        Button sharebtn;
        String imageUrl = "";

        String itemUrl ="https://your-item-url.com";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_details);

            detailDesc = findViewById(R.id.detailDesc);
            detailImage = findViewById(R.id.detailImage);
            detailTitle = findViewById(R.id.detailTitle);
            deleteButton = findViewById(R.id.deleteButton);
            editButton = findViewById(R.id.editButton);
            detailprice = findViewById(R.id.detailprice);
            sharebtn= findViewById(R.id.share);

            Bundle bundle = getIntent().getExtras();
            if (bundle != null){
                detailDesc.setText(bundle.getString("Description"));
                detailTitle.setText(bundle.getString("Title"));
                detailprice.setText(bundle.getString("Language"));
                key = bundle.getString("Key");
                imageUrl = bundle.getString("Image");
                Glide.with(this).load(bundle.getString("Image")).into(detailImage);
            }
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Android Tutorials");
                    FirebaseStorage storage = FirebaseStorage.getInstance();

                    StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            reference.child(key).removeValue();
                            Toast.makeText(Details.this, "Deleted", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    });
                }
            });

            sharebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {shareItem();}
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Details.this, Update.class)
                            .putExtra("Title", detailTitle.getText().toString())
                            .putExtra("Description", detailDesc.getText().toString())
                            .putExtra("Language", detailprice.getText().toString())
                            .putExtra("Image", imageUrl)
                            .putExtra("Key", key);
                    startActivity(intent);
                }
            });
        }
        private void shareItem(){
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareBody = detailTitle.getText().toString() + "\n" + detailDesc.getText().toString() + "\n" + itemUrl;
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        }
    }

