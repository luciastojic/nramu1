package com.example.myapplication;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import models.Parts;

public class AddNewItemActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private ImageView plusImage;
    private EditText itemName, itemModel, itemQuantity;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        // Firebase inicijalizacija
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("items");

        // Pristupanje EditText elementima
        itemName = findViewById(R.id.itemName);
        itemModel = findViewById(R.id.itemModel);
        itemQuantity = findViewById(R.id.itemQuantity);

        // Inicijalizacija ImageView-a za plus ikonu
        plusImage = findViewById(R.id.plusImage);
        plusImage.setOnClickListener(v -> showImagePickerDialog());

        // Button za spremanje podataka
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(view -> {
            String name = itemName.getText().toString();
            String model = itemModel.getText().toString();
            String quantity = itemQuantity.getText().toString();

            if (!name.isEmpty() && !model.isEmpty() && !quantity.isEmpty()) {
                if (imageUri != null) {
                    uploadImageToFirebase(imageUri);
                } else {
                    saveDataToDatabase(null); // Proslijedi null ako nema slike
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        // Provjera dopuštenja za čitanje iz memorije na Androidu 6.0 i novijim
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setItems(new CharSequence[]{"Camera", "Gallery"}, (dialog, which) -> {
            if (which == 0) {
                openCamera();
            } else if (which == 1) {
                openGallery();
            }
        });
        builder.show();
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*"); // Postavljanje vrste fajla kao slika
        startActivityForResult(galleryIntent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                plusImage.setImageBitmap(photo);
                imageUri = getImageUriFromBitmap(photo);
            } else if (requestCode == REQUEST_GALLERY) {
                imageUri = data.getData();
                plusImage.setImageURI(imageUri);
                // Dodajte log za provjeru URI-ja
                Log.d("AddNewItemActivity", "Image URI: " + imageUri.toString());
            }
        }
    }

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private void uploadImageToFirebase(Uri uri) {
        if (uri != null) {
            String fileName = "images/" + UUID.randomUUID().toString() + "." + getFileExtension(uri);
            StorageReference fileReference = storageReference.child(fileName);

            fileReference.putFile(uri).addOnSuccessListener(taskSnapshot ->
                    fileReference.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        String imageUrl = downloadUri.toString();
                        saveDataToDatabase(imageUrl);
                    })
            ).addOnFailureListener(e ->
                    Toast.makeText(this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void saveDataToDatabase(String imageUrl) {
        String id = databaseReference.push().getKey();
        String name = itemName.getText().toString();
        String model = itemModel.getText().toString();
        String quantity = itemQuantity.getText().toString();

        Parts part = new Parts(id, name, model, quantity);
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
