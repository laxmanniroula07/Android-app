package com.example.suitcase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {
    DrawerLayout drawerLayout;
    FloatingActionButton fab;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    RecyclerView recyclerView;
    List<DataClass> dataList;
    MyAdapter adapter;
    SearchView searchView;

    // Shake detection variables
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private long lastShakeTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);

        // Drawer and navigation view setup
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(androidx.fragment.R.id.fragment_container_view_tag, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.navhome);
        }

        // Floating action button setup
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Upload.class);
                startActivity(intent);
            }
        });

        // RecyclerView and database setup
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progressbar);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();
        adapter = new MyAdapter(MainActivity.this, dataList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Android Tutorials");
        dialog.show();
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                    dataClass.setKey(itemSnapshot.getKey());
                    dataList.add(dataClass);
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        // Swipe to delete setup
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                // Delete the item from your data source
                DataClass deletedItem = dataList.get(position);
                databaseReference.child(deletedItem.getKey()).removeValue();

                // Show toast message
                Toast.makeText(MainActivity.this, "Item Deleted Successfully", Toast.LENGTH_SHORT).show();

                // Refresh RecyclerView
                dataList.remove(position);
                adapter.notifyItemRemoved(position);
            }
        };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        // Shake detection setup
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        lastShakeTime = System.currentTimeMillis();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navhome) {
            getSupportFragmentManager().beginTransaction().replace(androidx.fragment.R.id.fragment_container_view_tag, new HomeFragment()).commit();
        } else if (id == R.id.navsettings) {
            getSupportFragmentManager().beginTransaction().replace(androidx.fragment.R.id.fragment_container_view_tag, new SettingFragment()).commit();
        } else if (id == R.id.navshare) {
            getSupportFragmentManager().beginTransaction().replace(androidx.fragment.R.id.fragment_container_view_tag, new ShareFragment()).commit();
        } else if (id == R.id.navabout) {
            getSupportFragmentManager().beginTransaction().replace(androidx.fragment.R.id.fragment_container_view_tag, new AboutFragment()).commit();
        } else if (id == R.id.nav_logout) {  // Ensure the correct ID is used here
            logout();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        // Show a confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Clear user session data
                    SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    // Show a logout message
                    Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();

                    // Redirect to login page
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void searchList(String text) {
        ArrayList<DataClass> searchList = new ArrayList<>();
        for (DataClass dataClass : dataList) {
            if (dataClass.getDataTitle().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(dataClass);
            }
        }
        adapter.searchDataList(searchList);
    }

    // Shake detection logic
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            detectShake(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void detectShake(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float acceleration = (float) Math.sqrt(x * x + y * y + z * z);
        float currentAcceleration = acceleration - SensorManager.GRAVITY_EARTH;
        long currentTime = System.currentTimeMillis();

        if (currentAcceleration > SHAKE_THRESHOLD_GRAVITY) {
            if (currentTime - lastShakeTime > 1000) {
                lastShakeTime = currentTime;
                // Show toast message
                Toast.makeText(MainActivity.this, "Shake detected!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);
        super.onPause();
    }
}
