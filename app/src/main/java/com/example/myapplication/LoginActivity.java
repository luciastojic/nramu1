package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);  // Učitaj layout za login ekran

        mAuth = FirebaseAuth.getInstance();

        // Referenca na UI elemente
        EditText editEmailText = findViewById(R.id.editEmailText);
        EditText editTextPassword = findViewById(R.id.editTextTextPassword);
        progressBar = findViewById(R.id.progressBar);
        Button buttonLog = findViewById(R.id.button3);

        // Na klik dugmeta za prijavu
        buttonLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);  // Prikazujemo progres bar
                String email, password;
                email = String.valueOf(editEmailText.getText());
                password = String.valueOf(editTextPassword.getText());

                // Provjera je li email prazno polje
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Provjera je li lozinka prazno polje
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Prijava korisnika koristeći Firebase
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);  // Sakrij progres bar
                                if (task.isSuccessful()) {
                                    // Ako je prijava uspješna, preusmjeri na HomeActivity
                                    Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);  // Pokrećemo HomeActivity
                                    startActivity(intent);
                                    finish();  // Završavamo LoginActivity, nećemo ga zadržati u pozadini
                                } else {
                                    // Ako prijava nije uspjela, prikaži poruku
                                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}