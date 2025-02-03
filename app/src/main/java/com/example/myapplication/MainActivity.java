package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        Button loginButton = findViewById(R.id.button);
        loginButton.setTextColor(getResources().getColor(R.color.white));  // Postavi boju teksta

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);  // Definiraj Intent za LoginActivity
                startActivity(intent);  // Pokreni LoginActivity
            }
        });

        Button registerButton = findViewById(R.id.button2);  // Poveži dugme REGISTER (id button2)
        registerButton.setTextColor(getResources().getColor(R.color.white));  // Postavi boju teksta

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);  // Definiraj Intent za RegisterActivity
                startActivity(intent);  // Pokreni RegisterActivity
            }
        });
    }
}