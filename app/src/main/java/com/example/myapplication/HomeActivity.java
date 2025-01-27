package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.ArrayList;

import models.Parts;

public class HomeActivity extends AppCompatActivity {

    ListView myListView;
    ArrayList<String> myArrayList = new ArrayList<>();
    ArrayList<String> myArrayListFiltered = new ArrayList<>(); // Filtered list for search
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private int quantity; // Initial quantity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FirebaseApp.initializeApp(this);
        DatabaseReference mRef = database.getReference().child("items");

        // Initialize ArrayAdapter with filtered list
        ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<>(HomeActivity.this, android.R.layout.simple_list_item_1, myArrayListFiltered);

        myListView = findViewById(R.id.listview);
        myListView.setAdapter(myArrayAdapter);

        // Listen for child events in the database
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Parts item = snapshot.getValue(Parts.class);
                if (item != null) {
                    // Format the data to display in ListView
                    String displayText = "Name: " + item.getName() + " \nModel: " + item.getModel() + " \nQuantity: " + item.getQuantity();
                    myArrayList.add(displayText); // Add to original list
                    myArrayListFiltered.add(displayText); // Add to filtered list
                    myArrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Parts updatedItem = snapshot.getValue(Parts.class);
                if (updatedItem != null) {
                    // Update the list when an item changes
                    for (int i = 0; i < myArrayList.size(); i++) {
                        String currentItemText = myArrayList.get(i);
                        if (currentItemText.contains(updatedItem.getName())) {
                            String updatedText = "Name: " + updatedItem.getName() + " \nModel: " + updatedItem.getModel() + " \nQuantity: " + updatedItem.getQuantity();
                            myArrayList.set(i, updatedText); // Update the original list
                            myArrayListFiltered.set(i, updatedText); // Update the filtered list
                            myArrayAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Parts removedItem = snapshot.getValue(Parts.class);
                if (removedItem != null) {
                    // Remove item from both lists
                    for (int i = 0; i < myArrayList.size(); i++) {
                        String currentItemText = myArrayList.get(i);
                        if (currentItemText.contains(removedItem.getName())) {
                            myArrayList.remove(i); // Remove from original list
                            myArrayListFiltered.remove(i); // Remove from filtered list
                            myArrayAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // No action required here
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Search functionality
        EditText searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = charSequence.toString().toLowerCase();
                myArrayListFiltered.clear(); // Clear the filtered list

                if (query.isEmpty()) {
                    myArrayListFiltered.addAll(myArrayList); // Show all items if the search bar is empty
                } else {
                    for (String item : myArrayList) {
                        if (item.toLowerCase().contains(query)) {
                            myArrayListFiltered.add(item); // Add matching items to filtered list
                        }
                    }
                }
                myArrayAdapter.notifyDataSetChanged(); // Update ListView with filtered results
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {}
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

    private void showQuantityDialog(String selectedItem) {
        // Create AlertDialog for quantity update
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_quantity, null);
        builder.setView(dialogView);

        TextView itemTextView = dialogView.findViewById(R.id.itemTextView);
        TextView quantityTextView = dialogView.findViewById(R.id.quantityTextView);
        Button increaseButton = dialogView.findViewById(R.id.increaseButton);
        Button decreaseButton = dialogView.findViewById(R.id.decreaseButton);

        // Set initial values for the selected item
        itemTextView.setText(selectedItem);
        String[] itemDetails = selectedItem.split("\\n");
        String itemName = itemDetails[0].split(":")[1].trim();
        String itemQuantityString = itemDetails[2].split(":")[1].trim();
        quantity = Integer.parseInt(itemQuantityString);

        quantityTextView.setText(String.valueOf(quantity));

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

        // Save button to update quantity in database
        builder.setPositiveButton("Save", (dialog, which) -> {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("items");

            // Find the item by name and update its quantity
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
