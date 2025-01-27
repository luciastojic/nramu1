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

import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    private TextView userName;
    private TextView userEmail; // Dodajemo TextView za e-mail
    private ImageView profileImage;
    private Button changeProfileImageButton;
    private Button editProfileButton;
    private Button changePasswordButton;

    private final ActivityResultCallback<Uri> profileImageResultCallback = uri -> {
        if (uri != null) {
            profileImage.setImageURI(uri);
        }
    };

    private final ActivityResultContracts.GetContent getContent = new ActivityResultContracts.GetContent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);  // Povezivanje layout-a

        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail); // Inicijaliziramo TextView za e-mail
        profileImage = findViewById(R.id.profileImage);
        changeProfileImageButton = findViewById(R.id.changeProfileImageButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);

        userName.setText(getString(R.string.user_name));

        // Dohvati e-mail korisnika koji je prijavljen
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (email != null) {
            userEmail.setText(email); // Postavi e-mail u TextView
        }

        changeProfileImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        });

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
                profileImage.setImageURI(selectedImageUri);
            }
        }
    }
}
