package com.example.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import android.Manifest;

import models.Parts;

public class HomeActivity extends AppCompatActivity {

    private ListView myListView;
    private List<Parts> partsList = new ArrayList<>();
    private List<Parts> filteredList = new ArrayList<>();
    private PartsAdapter customAdapter;
    private DatabaseReference databaseReference;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private int quantity;
    ArrayList<String> myArrayListFiltered = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FirebaseApp.initializeApp(this);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("items");

        myListView = findViewById(R.id.listview);
        customAdapter = new PartsAdapter(this, filteredList);
        myListView.setAdapter(customAdapter);

        // Učitavanje podataka iz Firebase
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Parts item = snapshot.getValue(Parts.class);
                if (item != null) {
                    partsList.add(item);
                    updateFilteredList("");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Parts updatedItem = snapshot.getValue(Parts.class);
                if (updatedItem != null) {
                    for (int i = 0; i < partsList.size(); i++) {
                        if (partsList.get(i).getName().equals(updatedItem.getName())) {
                            partsList.set(i, updatedItem);
                            updateFilteredList("");
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Parts removedItem = snapshot.getValue(Parts.class);
                if (removedItem != null) {
                    partsList.removeIf(part -> part.getName().equals(removedItem.getName()));
                    updateFilteredList("");
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Search funkcionalnost
        EditText searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateFilteredList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Add item click listener for ListView
        myListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = myArrayListFiltered.get(position);
            showQuantityDialog(selectedItem);
        });

        // Check for camera permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // Open menu when clicking the menu icon
        ImageView menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Set listener for navigation menu items
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.profile) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            } else if (item.getItemId() == R.id.add_new_item) {
                startActivity(new Intent(HomeActivity.this, AddNewItemActivity.class));
            } else if (item.getItemId() == R.id.logout) {
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

    }

    private void updateFilteredList(String query) {
        filteredList.clear();
        for (Parts part : partsList) {
            if (part.getName().toLowerCase().contains(query.toLowerCase()) ||
                    part.getModel().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(part);
            }
        }
        customAdapter.notifyDataSetChanged();
    }
    private void showQuantityDialog(String selectedItem) {
        // Create AlertDialog for quantity update
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_quantity, null);
        builder.setView(dialogView);

        TextView itemTextView = dialogView.findViewById(R.id.itemTextView);
        TextView quantityTextView = dialogView.findViewById(R.id.quantityTextView);
        Button increaseButton = dialogView.findViewById(R.id.increaseButton);
        Button decreaseButton = dialogView.findViewById(R.id.decreaseButton);
        Button deleteButton = dialogView.findViewById(R.id.deleteButton);
        ImageView itemImageView = dialogView.findViewById(R.id.itemImageView); // ImageView for the item image

        // Parse item name and quantity from the selectedItem string
        String[] itemDetails = selectedItem.split("\\n");
        String itemName = itemDetails[0].split(":")[1].trim();
        String itemQuantityString = itemDetails[2].split(":")[1].trim();
        quantity = Integer.parseInt(itemQuantityString);

        // Set the initial values for the selected item
        itemTextView.setText("Item: " + itemName);
        quantityTextView.setText(String.valueOf(quantity));

        // Fetch item details from Firebase, including the image URL
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("items");
        databaseReference.orderByChild("name").equalTo(itemName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Get the image URL from the Firebase data
                        String imageUrl = snapshot.child("imageUrl").getValue(String.class); // Make sure "imageUrl" is the correct key

                        // Load the image using Picasso
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Picasso.get().load(imageUrl).into(itemImageView);
                        }
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Item not found in the database.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Logic for increasing and decreasing quantity
        increaseButton.setOnClickListener(v -> {
            quantity++;
            quantityTextView.setText(String.valueOf(quantity));
        });

        decreaseButton.setOnClickListener(v -> {
            if (quantity > 0) {
                quantity--;
                quantityTextView.setText(String.valueOf(quantity));
            }
        });

        // Delete button logic
        deleteButton.setOnClickListener(v -> {
            // Delete item logic
            databaseReference.orderByChild("name").equalTo(itemName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(HomeActivity.this, "Item deleted successfully.", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(HomeActivity.this, "Failed to delete item: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        }
                    } else {
                        Toast.makeText(HomeActivity.this, "Item not found in the database.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(HomeActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        // Save button to update quantity in the database
        builder.setPositiveButton("Save", (dialog, which) -> {
            databaseReference.orderByChild("name").equalTo(itemName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().child("quantity").setValue(quantity)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(HomeActivity.this, "Quantity updated successfully.", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(HomeActivity.this, "Failed to update quantity: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        }
                    } else {
                        Toast.makeText(HomeActivity.this, "Item not found in the database.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(HomeActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        // Cancel button to close the dialog
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
