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
                openCreateUserActivity();
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

    // Function to open the CreateUserActivity
    private void openCreateUserActivity() {
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

        // Perform Firebase registration
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registration successful, save user details to Realtime Database
                        String userId = auth.getCurrentUser().getUid();

                        // Create a User object (you can create a User class or use a Map)
                        Map<String, Object> user = new HashMap<>();
                        user.put("name", name);
                        user.put("username", username);
                        user.put("email", email);

                        // Save user details to Realtime Database
                        usersRef.child(userId).setValue(user);
                        // Registration successful, update UI or navigate to the home page
                        Toast.makeText(createUser.this, "User created successfully", Toast.LENGTH_SHORT).show();
                        // You can add additional logic here, such as navigating to the home page
                    } else {
                        // Registration failed, handle the error
                        Toast.makeText(createUser.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



}