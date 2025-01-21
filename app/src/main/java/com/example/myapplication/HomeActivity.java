package com.example.myapplication;

import android.Manifest;
import android.view.View;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private TextView partQuantity;  // TextView za količinu dijela
    private int quantity = 10;  // Početna količina

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Provjeri dozvolu za kameru
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Ako dozvola nije dodijeljena, zatraži je
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

        // Inicijaliziraj DrawerLayout, NavigationView i Toolbar
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

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
        partQuantity = findViewById(R.id.partQuantity);
        ImageView increaseButton = findViewById(R.id.increaseButton);
        ImageView decreaseButton = findViewById(R.id.decreaseButton);
        ImageView deleteButton = findViewById(R.id.deleteButton); // Ikona za brisanje

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

        // Postavljanje listenera za ikonu za brisanje
        deleteButton.setOnClickListener(v -> {
            quantity = 0;  // Postavi količinu na 0
            updateQuantity();  // Ažuriraj prikaz količine

            // Logika za skrivanje dugmadi nakon brisanja
            increaseButton.setVisibility(View.GONE);
            decreaseButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            partQuantity.setText("Item Deleted"); // Postavi tekst na "Item Deleted"
        });
    }

    // Metoda za ažuriranje prikaza količine na ekranu
    private void updateQuantity() {
        if (quantity == 0) {
            partQuantity.setText("Out of Stock");
        } else {
            partQuantity.setText("Quantity: " + quantity);
        }
    }

    // Obrada odgovora na zahtjev za dozvolu
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Dozvola za kameru je odobrena, možete koristiti kameru
            } else {
                // Dozvola za kameru je odbijena, obavijestite korisnika ili obavite neku drugu radnju
            }
        }
    }
}
