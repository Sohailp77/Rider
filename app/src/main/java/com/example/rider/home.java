package com.example.rider;

import android.os.Bundle;
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
                    String username= dataSnapshot.child("username").getValue(String.class);

                    // Display user details in the UI (e.g., TextViews)
                    TextView nameTextView = findViewById(R.id.nameTextView);
                    TextView emailTextView = findViewById(R.id.emailTextView);
                    TextView usernameTextView = findViewById(R.id.usernameTextView);

                    nameTextView.setText("Name: " + name);
                    emailTextView.setText("Email: " + email);
                    usernameTextView.setText("username: " + username);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
}
}