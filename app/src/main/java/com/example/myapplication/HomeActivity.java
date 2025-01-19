package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private TextView partQuantity;  // TextView za količinu dijela
    private int quantity = 10;  // Početna količina

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inicijaliziraj DrawerLayout, NavigationView i Toolbar
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // Klik listener za otvaranje izbornika
        ImageView menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Postavljanje listenera za stavke izbornika
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.profile) {
                    // Otvori ProfileActivity
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                } else if (item.getItemId() == R.id.add_new_item) {
                    // Otvori AddNewItemActivity
                    startActivity(new Intent(HomeActivity.this, AddNewItemActivity.class));
                } else if (item.getItemId() == R.id.logout) {
                    // Otvori LoginActivity (logika za logout dolazi kasnije)
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }

                drawerLayout.closeDrawer(GravityCompat.START); // Zatvori izbornik
                return true;
            }
        });

        // Povezivanje s UI elementima za količinu dijela
        partQuantity = findViewById(R.id.itemQuantity);
        ImageView increaseButton = findViewById(R.id.increaseButton);
        ImageView decreaseButton = findViewById(R.id.decreaseButton);

        // Postavljanje listenera za ikonu za povećanje
        increaseButton.setOnClickListener(v -> {
            quantity++;  // Povećaj količinu za 1
            updateQuantity();  // Ažuriraj prikaz količine
        });

        // Postavljanje listenera za ikonu za smanjenje
        decreaseButton.setOnClickListener(v -> {
            if (quantity > 0) {  // Provjeri da količina ne padne ispod 0
                quantity--;  // Smanji količinu za 1
                updateQuantity();  // Ažuriraj prikaz količine
            }
        });
    }

    // Metoda za ažuriranje prikaza količine na ekranu
    private void updateQuantity() {
        partQuantity.setText("Quantity: " + quantity);  // Ažuriraj tekst sa novom količinom
    }
}
