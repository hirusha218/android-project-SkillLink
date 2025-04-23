package lk.javainstitute.skilllink.wactivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.BounceInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lk.javainstitute.skilllink.R;
import lk.javainstitute.skilllink.auth.ChatActivity;
import lk.javainstitute.skilllink.wfragments.FeedbackFragment;
import lk.javainstitute.skilllink.wfragments.HomeFragment;
import lk.javainstitute.skilllink.wfragments.Job_PostFragment;
import lk.javainstitute.skilllink.wfragments.ProfileFragment;
import lk.javainstitute.skilllink.wfragments.SettingFragment;
import lk.javainstitute.skilllink.wfragments.WalletFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DatabaseReference chatsRef, workersRef, mDatabase;
    private FirebaseAuth mAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));

        // Handle window insets (e.g., status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(); // Initialize mDatabase
        chatsRef = mDatabase.child("chats");
        workersRef = mDatabase.child("Workers");

        // Check authentication
        userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (userId == null) {
            Toast.makeText(this, "User not authenticated. Please log in.", Toast.LENGTH_LONG).show();
            // Redirect to login activity (uncomment if you have one)
            // startActivity(new Intent(MainActivity.this, LoginActivity.class));
            // finish();
            return; // Exit onCreate to prevent further execution
        }

        // Load initial fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Initialize navigation buttons
        ImageButton w_home = findViewById(R.id.w_home);
        ImageButton w_profile = findViewById(R.id.w_profile);
        ImageButton w_bank = findViewById(R.id.w_bank);
        ImageButton w_setting = findViewById(R.id.w_setting);
        ImageButton w_feedback = findViewById(R.id.w_feedback);
        ImageButton w_job_post = findViewById(R.id.w_job_post);
        ImageButton chatButton = findViewById(R.id.ChatView);

        // Set click listeners for navigation buttons
        setButtonClickListener(w_home, new HomeFragment());
        setButtonClickListener(w_profile, new ProfileFragment());
        setButtonClickListener(w_bank, new WalletFragment());
        setButtonClickListener(w_setting, new SettingFragment());
        setButtonClickListener(w_feedback, new FeedbackFragment());
        setButtonClickListener(w_job_post, new Job_PostFragment(), true);

        // Handle chat button click
        if (chatButton != null) {
            chatButton.setOnClickListener(view -> {
                animateButton(view); // Animate button on click
                if (mAuth.getCurrentUser() != null) {
                    fetchAndShowChats(); // Fetch and display chats
                } else {
                    Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null && getSupportFragmentManager() != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            Log.e(TAG, "Fragment or FragmentManager is null");
        }
    }

    private void loadFragmentWithDialog(Fragment fragment, boolean showDialog) {
        loadFragment(fragment);
        if (showDialog) {
            showNotificationDialog();
        }
    }

    private void showNotificationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Important Notice")
                .setMessage("Please do not post more job posts, or your account may be blocked. In addition, when posting, you must provide your real information, and you must upload a photo or video that can prove your employment.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private void setButtonClickListener(ImageButton button, Fragment fragment) {
        setButtonClickListener(button, fragment, false);
    }

    private void setButtonClickListener(ImageButton button, Fragment fragment, boolean showDialog) {
        if (button != null) {
            button.setOnClickListener(view -> {
                animateButton(view); // Animate button on click
                if (showDialog) {
                    loadFragmentWithDialog(fragment, true);
                } else {
                    loadFragment(fragment);
                }
            });
        } else {
            Log.w(TAG, "Button is null for fragment: " + fragment.getClass().getSimpleName());
        }
    }

    private void animateButton(android.view.View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.2f, 1f);
        scaleX.setInterpolator(new BounceInterpolator());
        scaleY.setInterpolator(new BounceInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(300);
        animatorSet.start();
    }

    private void fetchAndShowChats() {
        if (userId == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        chatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> chatList = new HashSet<>(); // Use Set to avoid duplicates
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    String chatId = chatSnapshot.getKey(); // Get the chat ID
                    for (DataSnapshot messageSnapshot : chatSnapshot.getChildren()) {
                        String senderId = messageSnapshot.child("senderId").getValue(String.class);
                        String receiverId = messageSnapshot.child("receiverId").getValue(String.class);

                        if (senderId != null && receiverId != null &&
                                (senderId.equals(userId) || receiverId.equals(userId))) {
                            // Add unique chat identifier
                            String participantName = getChatParticipantName(senderId, receiverId);
                            chatList.add("Chat with " + participantName + " (Chat ID: " + chatId + ")");
                            break; // Avoid duplicate entries for the same chat
                        }
                    }
                }
                showChatDialog(new ArrayList<>(chatList)); // Convert Set to List for dialog
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to read chats: " + databaseError.getMessage());
                Toast.makeText(MainActivity.this, "Failed to load chats", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getChatParticipantName(String senderId, String receiverId) {
        if (!senderId.equals(userId)) {
            return senderId; // Placeholder: Replace with actual name fetch if possible
        } else if (!receiverId.equals(userId)) {
            return receiverId; // Placeholder: Replace with actual name fetch if possible
        }
        return "Unknown User"; // Default case
    }

    private void showChatDialog(List<String> chatList) {
        if (chatList.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Chats")
                    .setMessage("No chats available.")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .setCancelable(true)
                    .show();
            return;
        }

        // Create an ArrayAdapter to populate the list
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chatList);

        new AlertDialog.Builder(this)
                .setTitle("Your Chats")
                .setAdapter(adapter, (dialog, which) -> {
                    // Handle click on a chat item
                    String selectedChat = chatList.get(which);
                    Toast.makeText(MainActivity.this, "Opening chat: " + selectedChat, Toast.LENGTH_SHORT).show();

                    // Extract chat ID and other participant's user ID
                    String chatId = selectedChat.substring(selectedChat.indexOf("(Chat ID: ") + 9, selectedChat.length() - 1);
                    String otherUserId = getOtherUserIdFromChat(chatId); // Implement this method

                    // Verify otherUserId is not null
                    if (otherUserId == null) {
                        Toast.makeText(MainActivity.this, "Error: Receiver ID not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Launch ChatActivity with chatId and otherUserId
                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    intent.putExtra("chatId", chatId);
                    intent.putExtra("userId", otherUserId);
                    startActivity(intent);

                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private String getOtherUserIdFromChat(String chatId) {
        // Assuming the chatId is in the format "userId1_userId2"
        String[] userIds = chatId.split("_");
        if (userIds.length == 2) {
            if (userIds[0].equals(userId)) {
                return userIds[1]; // Return the other user's ID
            } else {
                return userIds[0]; // Return the other user's ID
            }
        }
        return null; // Default case
    }
}