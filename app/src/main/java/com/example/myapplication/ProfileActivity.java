package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    // Definiraj varijablu koja će referencirati TextView
    private TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);  // Povezivanje layout-a

        // Inicijalizacija userName varijable pomoću findViewById
        userName = findViewById(R.id.userName);

        // Pristupanje TextView elementu i postavljanje korisničkog imena
        userName.setText(getString(R.string.user_name));
    }
}
