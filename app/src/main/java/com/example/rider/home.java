package com.example.rider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class home extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference usersRef;




    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public boolean onSupportNavigateUp() {
        if (doubleBackToExitPressedOnce) {
            // Close the app (call the method you created)
            closeApp();
            return true;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT ).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);

        // **Important change: return false here**
        return false;
    }

    public void closeApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
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

        ImageButton deleteButton = findViewById(R.id.deleteButton);
        if (deleteButton != null) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteButtonClass();
                }
            });
        }
    }

    private void openEditUserActivity() {
        Intent intent = new Intent(this, editlayout.class);
        startActivity(intent);
    }

    private void deleteButtonClass(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Delete user from Firebase Authentication
            currentUser.delete()
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // User deleted from Firebase Authentication successfully

                            // Now, delete user details from Realtime Database
                            deleteUserDataFromDatabase(currentUser.getUid());
                        } else {
                            // Failed to delete user from Firebase Authentication
                            Toast.makeText(this, "Failed to delete user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void deleteUserDataFromDatabase(String userId) {
        // Get reference to the user data in Realtime Database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Remove the data from Realtime Database
        userRef.removeValue()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User data deleted from Realtime Database successfully
                        Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        // Failed to delete user data from Realtime Database
                        Toast.makeText(this, "Failed to delete user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
