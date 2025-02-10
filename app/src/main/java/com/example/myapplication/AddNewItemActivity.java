package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import models.Parts;

public class AddNewItemActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ImageView plusImage;
    private EditText itemName, itemModel, itemQuantity;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        // Firebase inicijalizacija
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("items");

        // Pristupanje UI elementima
        itemName = findViewById(R.id.itemName);
        itemModel = findViewById(R.id.itemModel);
        itemQuantity = findViewById(R.id.itemQuantity);
        plusImage = findViewById(R.id.plusImage);
        Button saveButton = findViewById(R.id.saveButton);

        // Otvaranje galerije na klik
        plusImage.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, PICK_IMAGE);
        });

        // Spremanje podataka u Firebase
        saveButton.setOnClickListener(view -> {
            String name = itemName.getText().toString();
            String model = itemModel.getText().toString();
            String quantity = itemQuantity.getText().toString();

            if (!name.isEmpty() && !model.isEmpty() && !quantity.isEmpty()) {
                saveDataToDatabase();
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                // Prikazivanje slike u ImageView pomoću Picasso
                Picasso.get().load(imageUri).into(plusImage);
            }
        }
    }

    private void saveDataToDatabase() {
        String id = databaseReference.push().getKey();
        String name = itemName.getText().toString();
        String model = itemModel.getText().toString();
        String quantity = itemQuantity.getText().toString();

        String imageUrl = (imageUri != null) ? imageUri.toString() : null; // Provjeri je li slika odabrana

        Parts part = new Parts(id, name, model, quantity, imageUrl); // Dodaj URL slike u model
        databaseReference.child(id).setValue(part)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save item", Toast.LENGTH_SHORT).show());
    }
}
