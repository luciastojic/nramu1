package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editName, editEmail;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        // Inicijalizacija elemenata
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        saveButton = findViewById(R.id.saveButton);

        // Postavljanje listenera za spremanje promjena
        saveButton.setOnClickListener(v -> {
            // Spremanje podataka (ovdje možeš koristiti SharedPreferences ili bazu podataka)
            String name = editName.getText().toString();
            String email = editEmail.getText().toString();

            // Logika za spremanje podataka
        });
    }
}
