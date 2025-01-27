package com.example.myapplication;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import models.Parts;

public class AddNewItemActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private ImageView plusImage;
    private EditText itemName, itemModel, itemQuantity;
    private Uri imageUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData(); // Dohvat URI-ja odabrane slike

            // Koristite Glide za učitavanje slike u ImageView
            Glide.with(this)
                    .load(imageUri)
                    .into(plusImage); // Postavite sliku u ImageView
        } else {
            Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        // Firebase inicijalizacija
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("items");

        // Pristupanje EditText elementima
        itemName = findViewById(R.id.itemName);
        itemModel = findViewById(R.id.itemModel);
        itemQuantity = findViewById(R.id.itemQuantity);

        // Inicijalizacija ImageView-a za plus ikonu
        plusImage = findViewById(R.id.plusImage);

        // Postavi OnClickListener na ImageView
        plusImage.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, PICK_IMAGE);
        });

        // Button za spremanje podataka
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(view -> {
            String name = itemName.getText().toString();
            String model = itemModel.getText().toString();
            String quantity = itemQuantity.getText().toString();

            if (!name.isEmpty() && !model.isEmpty() && !quantity.isEmpty()) {
                saveDataToDatabase(); // Uklonjen nepotreban parametar
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        // Dodavanje slike
        plusImage.setOnClickListener(view -> {
            showImageSelectionDialog();
        });
    }

    private void showImageSelectionDialog() {
        DatabaseReference imagesRef = firebaseDatabase.getReference("images");

        imagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> imageUrls = new ArrayList<>();
                List<String> imageNames = new ArrayList<>();

                // Popuniti listu URL-ova slika iz Firebase baze
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String imageName = snapshot.getKey();  // Ime slike (npr. "Baterija")
                    String imageUrl = snapshot.getValue(String.class);  // URL slike
                    imageNames.add(imageName);  // Dodaj ime slike u listu
                    imageUrls.add(imageUrl);  // Dodaj URL slike u listu
                }

                // Kreiranje dijaloga s ListView za odabir slike
                AlertDialog.Builder builder = new AlertDialog.Builder(AddNewItemActivity.this);
                builder.setTitle("Select an Image");

                ListView listView = new ListView(AddNewItemActivity.this);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddNewItemActivity.this, android.R.layout.simple_list_item_1, imageNames);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener((parent, view, position, id) -> {
                    // Kada korisnik odabere sliku, pohrani URL
                    String selectedImageUrl = imageUrls.get(position);
                    imageUri = Uri.parse(selectedImageUrl);

                    // Učitavanje slike u ImageView koristeći Glide
                    Glide.with(AddNewItemActivity.this)
                            .load(imageUri)
                            .into(plusImage);

                    // Zatvoriti dijalog nakon odabira
                    builder.create().dismiss();
                });

                builder.setView(listView);
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                builder.show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddNewItemActivity.this, "Failed to load images: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    private String getFileExtension(Uri fileUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
}
