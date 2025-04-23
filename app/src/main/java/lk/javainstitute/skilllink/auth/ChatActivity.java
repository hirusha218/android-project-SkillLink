package lk.javainstitute.skilllink.auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.javainstitute.skilllink.R;
import lk.javainstitute.skilllink.adapter.ChatAdapter;
import lk.javainstitute.skilllink.model.ChatMessage;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    private RecyclerView recyclerView;
    private EditText messageInput;
    private ImageView sendButton, addButton, statusIndicator;
    private CardView messageInputContainer;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;
    private DatabaseReference chatRef, workersRef, customersRef;
    private String userType, receiverType, currentUserId, receiverId;
    private TextView nameTextView, statusTextView;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize Firebase
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoadingActivity.class));
            finish();
            return;
        }

        currentUserId = auth.getCurrentUser().getUid();

        // Get receiverId from intent
        Intent intent = getIntent();
        if (intent == null) {
            Log.e(TAG, "Intent is null");
            Toast.makeText(this, "Error: Invalid navigation", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        receiverId = intent.getStringExtra("userId");
        if (receiverId == null) {
            Log.e(TAG, "receiverId is missing");
            Toast.makeText(this, "Error: Receiver ID is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase references
        chatRef = FirebaseDatabase.getInstance().getReference("chats");
        workersRef = FirebaseDatabase.getInstance().getReference("Workers");
        customersRef = FirebaseDatabase.getInstance().getReference("Customers");
        storageRef = FirebaseStorage.getInstance().getReference("chat_images");

        determineUserTypes();
    }

    private void determineUserTypes() {
        workersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userType = "Workers";
                    customersRef.child(receiverId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                receiverType = "Customers";
                                initializeChat();
                            } else {
                                Toast.makeText(ChatActivity.this, "Receiver not found", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            handleDatabaseError(error);
                        }
                    });
                } else {
                    customersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                userType = "Customers";
                                workersRef.child(receiverId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            receiverType = "Workers";
                                            initializeChat();
                                        } else {
                                            Toast.makeText(ChatActivity.this, "Receiver not found", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        handleDatabaseError(error);
                                    }
                                });
                            } else {
                                Toast.makeText(ChatActivity.this, "User not found in database", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            handleDatabaseError(error);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                handleDatabaseError(error);
            }
        });
    }

    private void initializeChat() {
        Log.d(TAG, "Initializing chat - userType: " + userType + ", receiverType: " + receiverType);
        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        loadWorkerDetails();
        loadMessages();
    }

    private void handleDatabaseError(DatabaseError error) {
        Log.e(TAG, "Database error: " + error.getMessage());
        Toast.makeText(ChatActivity.this, "Database error occurred", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        addButton = findViewById(R.id.addButton);
        messageInputContainer = findViewById(R.id.messageInputContainer);
        ImageView backButton = findViewById(R.id.backButton);
        nameTextView = findViewById(R.id.Name);
        statusTextView = findViewById(R.id.Status);
        statusIndicator = findViewById(R.id.statusIndicator);

        // Configure messageInput
        messageInput.setHint("Type a message...");
        messageInput.setMaxLines(4); // Allow multiple lines but limit to 4
        messageInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        // Enable IME actions
        messageInput.setImeOptions(EditorInfo.IME_ACTION_SEND);
        messageInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });

        // Add text watcher for dynamic button state
        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable/disable send button based on input
                sendButton.setEnabled(!s.toString().trim().isEmpty());
                sendButton.setAlpha(s.toString().trim().isEmpty() ? 0.5f : 1.0f);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messageList, currentUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setHasFixedSize(true);

        // Add scroll listener to handle new messages
        recyclerView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                recyclerView.postDelayed(() -> {
                    if (messageList.size() > 0) {
                        recyclerView.smoothScrollToPosition(messageList.size() - 1);
                    }
                }, 100);
            }
        });
    }

    private void setupClickListeners() {
        sendButton.setOnClickListener(v -> sendMessage());
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        addButton.setOnClickListener(v -> {
            // Show image picker dialog
            showImagePickerDialog();
        });

        // Add click listener to the message input container
        messageInputContainer.setOnClickListener(v -> {
            messageInput.requestFocus();
            showKeyboard(messageInput);
        });
    }

    private void loadWorkerDetails() {
        DatabaseReference userRef = receiverType.equals("Workers") ? workersRef : customersRef;

        userRef.child(receiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Handle both worker and customer profile data
                    String displayName;
                    if (receiverType.equals("Workers")) {
                        displayName = snapshot.child("fname").getValue(String.class);
                    } else {
                        displayName = snapshot.child("lname").getValue(String.class);
                    }

                    String profileUrl = snapshot.child("profileImage").getValue(String.class);
                    boolean isOnline = snapshot.child("online").getValue(Boolean.class) != null &&
                            snapshot.child("online").getValue(Boolean.class);

                    // Update UI
                    if (displayName != null) {
                        nameTextView.setText(displayName);
                    }

                    ImageView profileImage = findViewById(R.id.profile_img);
                    if (profileUrl != null && !profileUrl.isEmpty()) {
                        Glide.with(ChatActivity.this)
                                .load(profileUrl)
                                .placeholder(R.drawable.profile_avatar_icon)
                                .error(R.drawable.profile_avatar_icon)
                                .into(profileImage);
                    }

                    updateOnlineStatus(isOnline);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load user details: " + error.getMessage());
            }
        });
    }

    private void updateOnlineStatus(boolean isOnline) {
        if (isOnline) {
            statusTextView.setText("Online");
            statusTextView.setTextColor(getResources().getColor(R.color.online_green));
            statusIndicator.setImageResource(R.drawable.online_indicator);
        } else {
            statusTextView.setText("Offline");
            statusTextView.setTextColor(getResources().getColor(R.color.offline_gray));
            statusIndicator.setImageResource(R.drawable.offline_indicator);
        }
    }

    private void loadMessages() {
        // Get chat ID based on user type
        String chatId = getChatId(currentUserId, receiverId);

        // Reference to specific chat path
        DatabaseReference specificChatRef = chatRef.child(chatId);

        specificChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot messageSnap : snapshot.getChildren()) {
                    ChatMessage message = messageSnap.getValue(ChatMessage.class);
                    if (message != null) {
                        // Verify this message belongs to the current chat participants
                        if ((message.getSenderId().equals(currentUserId) &&
                                message.getReceiverId().equals(receiverId)) ||
                                (message.getSenderId().equals(receiverId) &&
                                        message.getReceiverId().equals(currentUserId))) {

                            // Mark message as read if it's received
                            if (!message.getSenderId().equals(currentUserId) && !message.isRead()) {
                                messageSnap.getRef().child("read").setValue(true);
                            }
                            messageList.add(message);
                        }
                    }
                }

                chatAdapter.notifyDataSetChanged();
                scrollToBottom();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load messages: " + error.getMessage());
                Toast.makeText(ChatActivity.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (messageText.isEmpty()) return;

        sendButton.setEnabled(false);

        String chatId = getChatId(currentUserId, receiverId);
        String messageId = chatRef.child(chatId).push().getKey();
        String timestamp = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        // Get current user details from appropriate reference
        DatabaseReference currentUserRef = userType.equals("Workers") ? workersRef : customersRef;

        currentUserRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String senderName;
                if (userType.equals("Workers")) {
                    senderName = snapshot.child("firstName").getValue(String.class);
                } else {
                    senderName = snapshot.child("name").getValue(String.class);
                }
                String senderProfileUrl = snapshot.child("profileUrl").getValue(String.class);

                ChatMessage message = new ChatMessage(
                        messageId,
                        currentUserId,
                        receiverId,
                        messageText,
                        timestamp,
                        senderName != null ? senderName : "Unknown User",
                        senderProfileUrl != null ? senderProfileUrl : "",
                        true
                );

                if (messageId != null) {
                    chatRef.child(chatId).child(messageId).setValue(message)
                            .addOnSuccessListener(aVoid -> {
                                messageInput.setText("");
                                sendButton.setEnabled(true);
                                scrollToBottom();
                            })
                            .addOnFailureListener(e -> {
                                sendButton.setEnabled(true);
                                Log.e(TAG, "Failed to send message: " + e.getMessage());
                                Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                sendButton.setEnabled(true);
                Log.e(TAG, "Failed to get sender details: " + error.getMessage());
                Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getChatId(String userId1, String userId2) {
        if (userType.equals("Workers")) {
            return userId1 + "_" + userId2;  // workerId_customerId
        } else {
            return userId2 + "_" + userId1;  // workerId_customerId
        }
    }

    private void scrollToBottom() {
        if (messageList.size() > 0) {
            recyclerView.smoothScrollToPosition(messageList.size() - 1);
        }
    }

    private void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Upload Image")
                .setItems(new CharSequence[]{"Gallery", "Camera"}, (dialog, which) -> {
                    if (which == 0) {
                        // Gallery
                        openGallery();
                    } else {
                        // Camera
                        openCamera();
                    }
                });
        builder.show();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        // Implement camera functionality if needed
        Toast.makeText(this, "Camera feature coming soon", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadImage(imageUri);
        }
    }

    private void uploadImage(Uri imageUri) {
        if (imageUri == null) return;

        // Show progress dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image");
        progressDialog.show();

        // Create a unique filename
        String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = storageRef.child(fileName);

        // Upload the image
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        // Send message with image
                        sendImageMessage(downloadUri.toString());
                        progressDialog.dismiss();
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(ChatActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Image upload failed: " + e.getMessage());
                })
                .addOnProgressListener(snapshot -> {
                    // Update progress dialog
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                });
    }

    private void sendImageMessage(String imageUrl) {
        String chatId = getChatId(currentUserId, receiverId);
        String messageId = chatRef.child(chatId).push().getKey();
        String timestamp = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        // Get current user details from appropriate reference
        DatabaseReference currentUserRef = userType.equals("Workers") ? workersRef : customersRef;

        currentUserRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String senderName;
                if (userType.equals("Workers")) {
                    senderName = snapshot.child("firstName").getValue(String.class);
                } else {
                    senderName = snapshot.child("name").getValue(String.class);
                }
                String senderProfileUrl = snapshot.child("profileUrl").getValue(String.class);

                ChatMessage message = new ChatMessage(
                        messageId,
                        currentUserId,
                        receiverId,
                        imageUrl, // Use image URL as message
                        timestamp,
                        senderName != null ? senderName : "Unknown User",
                        senderProfileUrl != null ? senderProfileUrl : "",
                        true
                );
                message.setImage(true); // Add this field to ChatMessage class

                if (messageId != null) {
                    chatRef.child(chatId).child(messageId).setValue(message)
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to send image message: " + e.getMessage());
                                Toast.makeText(ChatActivity.this, "Failed to send image", Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to get sender details: " + error.getMessage());
                Toast.makeText(ChatActivity.this, "Failed to send image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}