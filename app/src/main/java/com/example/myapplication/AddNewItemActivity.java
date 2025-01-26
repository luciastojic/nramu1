package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AddNewItemActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;

    private ImageView plusImage;  // ImageView za plus ikonu
    private EditText itemName, itemModel, itemQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);  // Povezivanje layout-a

        itemName = findViewById(R.id.itemName);
        itemModel = findViewById(R.id.itemModel);
        itemQuantity = findViewById(R.id.itemQuantity);

        plusImage = findViewById(R.id.plusImage);
        plusImage.setOnClickListener(v -> showImagePickerDialog());  // Dodavanje listener-a za plus ikonu

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(view -> {
            String name = itemName.getText().toString();
            String model = itemModel.getText().toString();
            String quantity = itemQuantity.getText().toString();

            if (!name.isEmpty() && !model.isEmpty() && !quantity.isEmpty()) {
                Toast.makeText(this, "Item saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });
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
        startActivityForResult(galleryIntent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                plusImage.setImageBitmap(photo);
            } else if (requestCode == REQUEST_GALLERY) {
                Uri imageUri = data.getData();
                plusImage.setImageURI(imageUri);
            }
        }
    }
}