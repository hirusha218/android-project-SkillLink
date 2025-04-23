package lk.javainstitute.skilllink.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import lk.javainstitute.skilllink.R;
import lk.javainstitute.skilllink.model.ChatMessage;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private final Context context;
    private final List<ChatMessage> messages;
    private final String currentUserId;

    public ChatAdapter(Context context, List<ChatMessage> messages, String currentUserId) {
        this.context = context;
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        if (holder.getItemViewType() == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).bind(message);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        // If the sender ID matches current user ID, it's a sent message
        return message.getSenderId().equals(currentUserId) ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView messageImage;
        View readIndicator;

        SentMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.sentMessageText);
            timeText = itemView.findViewById(R.id.sentMessageTime);
            messageImage = itemView.findViewById(R.id.sentMessageImage);
            readIndicator = itemView.findViewById(R.id.readIndicator);
        }

        void bind(ChatMessage message) {
            if (message.isImage()) {
                messageText.setVisibility(View.GONE);
                messageImage.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(message.getMessage())
                        .placeholder(R.drawable.user_13530226)
                        .error(R.drawable.user_13530226)
                        .into(messageImage);
            } else {
                messageText.setVisibility(View.VISIBLE);
                messageImage.setVisibility(View.GONE);
                messageText.setText(message.getMessage());
            }
            timeText.setText(message.getTimestamp());

            // Show read indicator
            if (readIndicator != null) {
                readIndicator.setVisibility(message.isRead() ? View.VISIBLE : View.GONE);
            }
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage, messageImage;

        ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.receivedMessageText);
            timeText = itemView.findViewById(R.id.receivedMessageTime);
            nameText = itemView.findViewById(R.id.receivedSenderName);
            profileImage = itemView.findViewById(R.id.receivedProfileImage);
            messageImage = itemView.findViewById(R.id.receivedMessageImage);
        }

        void bind(ChatMessage message) {
            if (message.isImage()) {
                messageText.setVisibility(View.GONE);
                messageImage.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(message.getMessage())
                        .placeholder(R.drawable.user_13530226)
                        .error(R.drawable.user_13530226)
                        .into(messageImage);
            } else {
                messageText.setVisibility(View.VISIBLE);
                messageImage.setVisibility(View.GONE);
                messageText.setText(message.getMessage());
            }

            timeText.setText(message.getTimestamp());
            nameText.setText(message.getSenderName());

            // Load profile image
            if (message.getSenderProfileUrl() != null && !message.getSenderProfileUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(message.getSenderProfileUrl())
                        .placeholder(R.drawable.profile_avatar_icon)
                        .error(R.drawable.profile_avatar_icon)
                        .circleCrop()
                        .into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.profile_avatar_icon);
            }
        }
    }
} 