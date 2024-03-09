package com.example.rider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class createUser extends AppCompatActivity {

    private FirebaseAuth auth;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = database.getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Set up click listener for the new user button

        auth = FirebaseAuth.getInstance();
        findViewById(R.id.loginpage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainaAtivity();
            }
        });

        Button createUserButton = findViewById(R.id.buttonCreateUser);
        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }

    // Function to open the mainactivity
    private void mainaAtivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void registerUser() {
        EditText nameEditText = findViewById(R.id.editTextName);
        EditText usernameEditText = findViewById(R.id.editTextUsername);
        EditText emailEditText = findViewById(R.id.editTextEmail);
        EditText passwordEditText = findViewById(R.id.editTextPassword);

        String name = nameEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Check if any field is empty
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Send email verification first
        sendEmailVerification(email, password, name, username);
    }

    private void sendEmailVerification(String email, String password, String name, String username) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        user.sendEmailVerification()
                                .addOnCompleteListener(this, verificationTask -> {
                                    if (verificationTask.isSuccessful()) {
                                        // Email verification sent successfully
                                        Toast.makeText(createUser.this, "Verification email sent. Please verify your email before logging in.", Toast.LENGTH_SHORT).show();
                                        // Registration successful, save user details to Realtime Database
                                        saveUserDetailsToDatabase(name, username, email);
                                        finish();

                                        //starting main activity
                                        Intent intent = new Intent(this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        // Error sending verification email
                                        Toast.makeText(createUser.this, "Error sending verification email: " + verificationTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // Registration failed, handle the error
                        Toast.makeText(createUser.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserDetailsToDatabase(String name, String username, String email) {
        String userId = auth.getCurrentUser().getUid();

        // Create a User object (you can create a User class or use a Map)
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("username", username);
        user.put("email", email);

        // Save user details to Realtime Database
        usersRef.child(userId).setValue(user)
                .addOnCompleteListener(this, databaseTask -> {
                    if (databaseTask.isSuccessful()) {
                        // Data saved successfully
                        //Toast.makeText(createUser.this, "User created successfully. Verification email sent. Data saved to Realtime Database.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Error saving data to the database
                        Toast.makeText(createUser.this, "Error saving data to Realtime Database: " + databaseTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



}