package com.example.rider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class home extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Retrieve and display user details
        retrieveAndDisplayUserDetails();

        // Set OnClickListener for the Edit ImageButton
        ImageButton editButton = findViewById(R.id.editButton);
        if (editButton != null) {
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openEditUserActivity();
                }
            });
        }
    }

    private void openEditUserActivity() {
        Intent intent = new Intent(this, editlayout.class);
        startActivity(intent);
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

                    // Display user details in the UI (e.g., TextViews)
                    TextView nameTextView = findViewById(R.id.nameTextView);
                    TextView emailTextView = findViewById(R.id.emailTextView);
                    TextView usernameTextView = findViewById(R.id.usernameTextView);

                    nameTextView.setText("Name: " + name);
                    emailTextView.setText("Email: " + email);
                    usernameTextView.setText("Username: " + username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
}
