package com.example.rider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class editlayout extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editlayout);

        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        EdgeToEdge.enable(this);
        // Retrieve and display user details
        retrieveAndDisplayUserDetails();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button submitbtn = findViewById(R.id.submit);
        if (submitbtn != null) {
            submitbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submitvalue();
                }
            });
        }
    }

    private void retrieveAndDisplayUserDetails() {
        String userId = auth.getCurrentUser().getUid();

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve user details from dataSnapshot
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String username = dataSnapshot.child("username").getValue(String.class);


                    EditText nameEditText = findViewById(R.id.editTextName);
                    EditText emailEditText = findViewById(R.id.editTextEmail);
                    EditText usernameEditText = findViewById(R.id.editTextUsername);

                    nameEditText.setText(name);
                    emailEditText.setText(email);
                    usernameEditText.setText(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
    public void submitvalue(){
        // Retrieve updated values from EditTexts
        String newName = ((EditText) findViewById(R.id.editTextName)).getText().toString();
        String newUsername = ((EditText) findViewById(R.id.editTextUsername)).getText().toString();

        // Update values in the Firebase Realtime Database
        updateUserData(newName,newUsername);

        // Start the home activity
        startHomeActivity();

    }

    private void updateUserData(String newName, String newUsername) {
        String userId = auth.getCurrentUser().getUid();

        // Update user data in the database
        usersRef.child(userId).child("name").setValue(newName);
        usersRef.child(userId).child("username").setValue(newUsername);

    }

    private void startHomeActivity() {
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
        finish(); // Optional: Finish the current activity if you don't want to go back to it
    }
}
