package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView userName;
    private ImageView profileImage;
    private Button changeProfileImageButton;
    private Button editProfileButton;
    private Button changePasswordButton;

    // Definiranje ActivityResultCallback za dohvat slike
    private final ActivityResultCallback<Uri> profileImageResultCallback = uri -> {
        // Ako je korisnik odabrao sliku, postavi ju u ImageView
        if (uri != null) {
            profileImage.setImageURI(uri);
        }
    };

    // Način na koji ćeš dobiti sadržaj (sliku) s uređaja
    private final ActivityResultContracts.GetContent getContent = new ActivityResultContracts.GetContent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);  // Povezivanje layout-a

        // Inicijalizacija elemenata
        userName = findViewById(R.id.userName);
        profileImage = findViewById(R.id.profileImage);
        changeProfileImageButton = findViewById(R.id.changeProfileImageButton);
        editProfileButton = findViewById(R.id.editProfileButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);

        // Postavljanje korisničkog imena
        userName.setText(getString(R.string.user_name));

        // Funkcija za promjenu profilne slike
        changeProfileImageButton.setOnClickListener(v -> {
            // Otvoriti galeriju za odabir slike
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        });

        // Funkcija za uređivanje profila (otvara novu aktivnost)
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Navigacija na promjenu lozinke
        changePasswordButton.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                profileImage.setImageURI(selectedImageUri); // postavljanje slike u profilnu sliku
            }
        }
    }
}
