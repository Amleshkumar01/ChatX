package com.example.chatx;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;
    private TextView createAccountButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            // Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();

            // Initialize views
            emailInput = findViewById(R.id.emailInput);
            passwordInput = findViewById(R.id.passwordInput);
            loginButton = findViewById(R.id.loginButton);
            createAccountButton = findViewById(R.id.createAccountButton);

            // Set click listeners
            if (loginButton != null) {
                loginButton.setOnClickListener(v -> loginUser());
            } else {
                Toast.makeText(this, "Error: Login button not found", Toast.LENGTH_LONG).show();
            }

            if (createAccountButton != null) {
                createAccountButton.setOnClickListener(v -> {
                    Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                    startActivity(intent);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error initializing app: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void loginUser() {
        try {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                emailInput.setError("Email is required");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                passwordInput.setError("Password is required");
                return;
            }

            // Show progress and disable button
            loginButton.setEnabled(false);
            loginButton.setText("Logging in...");

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        loginButton.setEnabled(true);
                        loginButton.setText("Login");
                        
                        if (task.isSuccessful()) {
                            // Sign in success
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            String errorMessage = task.getException() != null ? 
                                task.getException().getMessage() : 
                                "Authentication failed";
                            Toast.makeText(LoginActivity.this, 
                                "Login failed: " + errorMessage, 
                                Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        loginButton.setEnabled(true);
                        loginButton.setText("Login");
                        Toast.makeText(LoginActivity.this, 
                            "Network error: " + e.getMessage(), 
                            Toast.LENGTH_LONG).show();
                    });
        } catch (Exception e) {
            loginButton.setEnabled(true);
            loginButton.setText("Login");
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
