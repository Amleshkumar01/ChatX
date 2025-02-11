package com.example.chatx;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatx.adapter.MessageAdapter;
import com.example.chatx.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView messagesRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private MessageAdapter messageAdapter;
    private List<Message> messages;
    
    private FirebaseAuth auth;
    private DatabaseReference chatRef;
    private String currentUserId;
    private String otherUserId;
    private String otherUserName;
    private String chatId;
    private ValueEventListener messagesListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get user IDs
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        otherUserId = getIntent().getStringExtra("userId");
        otherUserName = getIntent().getStringExtra("userName");

        if (otherUserId == null || otherUserName == null) {
            Toast.makeText(this, "Error: User information not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Generate unique chat ID (smaller UID first to ensure consistency)
        chatId = currentUserId.compareTo(otherUserId) < 0 ? 
                currentUserId + "_" + otherUserId : 
                otherUserId + "_" + currentUserId;

        // Setup Firebase
        chatRef = FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(chatId)
                .child("messages");

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(otherUserName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Initialize views
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        // Setup RecyclerView
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages, currentUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        messagesRecyclerView.setLayoutManager(layoutManager);
        messagesRecyclerView.setAdapter(messageAdapter);

        // Set click listener for send button
        sendButton.setOnClickListener(v -> sendMessage());

        // Start listening for messages
        startListeningForMessages();
    }

    private void startListeningForMessages() {
        messagesListener = chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    Message message = messageSnapshot.getValue(Message.class);
                    if (message != null) {
                        messages.add(message);
                    }
                }
                messageAdapter.notifyDataSetChanged();
                if (!messages.isEmpty()) {
                    messagesRecyclerView.scrollToPosition(messages.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, 
                    "Failed to load messages: " + error.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (!messageText.isEmpty()) {
            // Create message
            String messageId = chatRef.push().getKey();
            if (messageId != null) {
                Message message = new Message(
                    messageId,
                    currentUserId,
                    messageText
                );

                // Update both users' chat lists
                Map<String, Object> updates = new HashMap<>();
                updates.put("/chats/" + chatId + "/messages/" + messageId, message);
                updates.put("/user-chats/" + currentUserId + "/" + otherUserId + "/lastMessage", messageText);
                updates.put("/user-chats/" + otherUserId + "/" + currentUserId + "/lastMessage", messageText);

                FirebaseDatabase.getInstance().getReference().updateChildren(updates, (error, ref) -> {
                    if (error != null) {
                        Toast.makeText(ChatActivity.this, 
                            "Failed to send message: " + error.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    } else {
                        messageInput.setText("");
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messagesListener != null) {
            chatRef.removeEventListener(messagesListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
