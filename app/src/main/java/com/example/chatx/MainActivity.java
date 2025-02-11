package com.example.chatx;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.chatx.adapter.ChatItemAdapter;
import com.example.chatx.models.ChatItem;
import com.example.chatx.models.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity 
        implements NavigationView.OnNavigationItemSelectedListener {
    
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private ChatItemAdapter chatItemAdapter;
    private List<ChatItem> chatItems;
    private List<ChatItem> userItems;
    private SwipeRefreshLayout swipeRefresh;
    private EditText searchInput;
    private TextView emptyView;
    private ProgressBar progressBar;
    private FloatingActionButton newChatFab;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private String currentUserId;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // Initialize Firebase
            mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() == null) {
                // User not logged in, redirect to LoginActivity
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }
            
            currentUserId = mAuth.getCurrentUser().getUid();
            usersRef = FirebaseDatabase.getInstance().getReference().child("users");

            // Initialize views
            drawerLayout = findViewById(R.id.drawerLayout);
            navigationView = findViewById(R.id.navigationView);
            recyclerView = findViewById(R.id.recyclerView);
            swipeRefresh = findViewById(R.id.swipeRefresh);
            searchInput = findViewById(R.id.searchInput);
            emptyView = findViewById(R.id.emptyView);
            progressBar = findViewById(R.id.progressBar);
            newChatFab = findViewById(R.id.newChatFab);

            if (drawerLayout == null || navigationView == null || recyclerView == null || 
                swipeRefresh == null || searchInput == null || emptyView == null || 
                progressBar == null || newChatFab == null) {
                Toast.makeText(this, "Error initializing views", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Setup toolbar and drawer
            MaterialToolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawerLayout.addDrawerListener(toggle);
                toggle.syncState();
            }

            // Setup navigation
            navigationView.setNavigationItemSelectedListener(this);

            // Setup RecyclerView
            chatItems = new ArrayList<>();
            userItems = new ArrayList<>();
            
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            
            chatItemAdapter = new ChatItemAdapter(chatItems);
            recyclerView.setAdapter(chatItemAdapter);

            // Setup swipe refresh
            swipeRefresh.setOnRefreshListener(() -> {
                loadUsers();
            });

            // Setup search
            searchInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterItems(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            // Setup FAB click listeners
            newChatFab.setOnClickListener(v -> {
                // Show user list or start new chat
                Toast.makeText(MainActivity.this, "Start new chat", Toast.LENGTH_SHORT).show();
            });

            // Load data
            loadCurrentUser();
            loadUsers();
            
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing app: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
        }
    }

    private void loadCurrentUser() {
        try {
            usersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    currentUser = snapshot.getValue(User.class);
                    if (currentUser != null) {
                        currentUser.setUid(currentUserId);
                        updateNavigationHeader();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, 
                        "Error loading profile: " + error.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error loading current user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void updateNavigationHeader() {
        try {
            View headerView = navigationView.getHeaderView(0);
            TextView nameText = headerView.findViewById(R.id.headerName);
            TextView emailText = headerView.findViewById(R.id.headerEmail);
            ShapeableImageView profileImage = headerView.findViewById(R.id.headerProfileImage);

            if (nameText != null && emailText != null && profileImage != null) {
                nameText.setText(currentUser.getName());
                emailText.setText(currentUser.getEmail());
                // TODO: Load profile image if exists
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error updating navigation header: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void loadUsers() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userItems.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String userId = dataSnapshot.getKey();
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            user.setUid(userId);
                            // Add all users except current user to the list
                            if (!userId.equals(currentUserId)) {
                                userItems.add(new ChatItem(user));
                            }
                        }
                    }
                    updateChatItems();
                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                    Toast.makeText(MainActivity.this, 
                        "Error loading users: " + error.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error loading users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void updateChatItems() {
        try {
            if (chatItems == null) chatItems = new ArrayList<>();
            chatItems.clear();
            if (userItems != null) chatItems.addAll(userItems);
            String query = searchInput != null && searchInput.getText() != null ? 
                    searchInput.getText().toString() : "";
            filterItems(query);
            updateEmptyView();
        } catch (Exception e) {
            Toast.makeText(this, "Error updating items: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void filterItems(String query) {
        try {
            if (chatItems == null || chatItemAdapter == null) return;
            
            query = query != null ? query.toLowerCase().trim() : "";
            List<ChatItem> filteredItems = new ArrayList<>();
            
            for (ChatItem item : chatItems) {
                if (item != null && item.getName() != null && 
                        item.getName().toLowerCase().contains(query)) {
                    filteredItems.add(item);
                }
            }
            
            chatItemAdapter.updateItems(filteredItems);
            updateEmptyView();
        } catch (Exception e) {
            Toast.makeText(this, "Error filtering items: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void updateEmptyView() {
        try {
            if (chatItemAdapter.getItemCount() == 0) {
                emptyView.setVisibility(View.VISIBLE);
                if (searchInput.getText().length() > 0) {
                    emptyView.setText("No matches found");
                } else {
                    emptyView.setText("No chats available");
                }
            } else {
                emptyView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error updating empty view: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        try {
            int id = item.getItemId();
            
            if (id == R.id.nav_contacts) {
                Toast.makeText(this, "Contacts", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_saved_messages) {
                Toast.makeText(this, "Saved Messages", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_settings)


            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } catch (Exception e) {
            Toast.makeText(this, "Error handling navigation item selection: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error handling back press: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        } catch (Exception e) {
            Toast.makeText(this, "Error creating options menu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if (item.getItemId() == R.id.action_logout) {
                mAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            }
            return super.onOptionsItemSelected(item);
        } catch (Exception e) {
            Toast.makeText(this, "Error handling options item selection: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }
}