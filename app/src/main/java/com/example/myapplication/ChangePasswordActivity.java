package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        EditText currentPassword = findViewById(R.id.currentPassword);
        EditText newPassword = findViewById(R.id.newPassword);
        EditText confirmPassword = findViewById(R.id.confirmPassword);
        Button savePasswordButton = findViewById(R.id.savePasswordButton);

        savePasswordButton.setOnClickListener(view -> {
            String current = currentPassword.getText().toString();
            String newPass = newPassword.getText().toString();
            String confirmPass = confirmPassword.getText().toString();

            if (current.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Sva polja su obavezna!", Toast.LENGTH_SHORT).show();
            } else if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Lozinke se ne podudaraju!", Toast.LENGTH_SHORT).show();
            } else if (newPass.length() < 6) {
                Toast.makeText(this, "Lozinka mora imati najmanje 6 znakova!", Toast.LENGTH_SHORT).show();
            } else {
                updatePassword(current, newPass);
            }
        });
    }

    private void updatePassword(String currentPassword, String newPassword) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            // After updating password, update the password in the Realtime Database as well
                            updatePasswordInDatabase(newPassword);
                        } else {
                            Toast.makeText(this, "Greška pri promjeni lozinke!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Trenutna lozinka nije ispravna!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updatePasswordInDatabase(String newPassword) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Assuming the Users class has a setter for the password
        userRef.child("password").setValue(newPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Lozinka uspješno promijenjena!", Toast.LENGTH_SHORT).show();
                finish(); // Close this activity
            } else {
                Toast.makeText(this, "Greška pri ažuriranju lozinke u bazi podataka!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
