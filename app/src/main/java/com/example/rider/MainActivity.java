package com.example.rider;

import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Set up click listener for the new user button
        findViewById(R.id.createUserButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateUserActivity();
            }
        });
    }

    // Function to open the CreateUserActivity
    private void openCreateUserActivity() {
        Intent intent = new Intent(this, createUser.class);
        startActivity(intent);
    }
}