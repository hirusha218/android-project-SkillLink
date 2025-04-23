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

import java.util.ArrayList;
import java.util.List;

import lk.javainstitute.skilllink.R;
import lk.javainstitute.skilllink.model.Chat;

public class ChatLoadAdapter extends RecyclerView.Adapter<ChatLoadAdapter.ChatViewHolder> {
    private final Context context;
    private final OnChatClickListener chatClickListener;
    private List<Chat> chatList;

    public ChatLoadAdapter(Context context, OnChatClickListener listener) {
        this.context = context;
        this.chatList = new ArrayList<>();
        this.chatClickListener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);

        holder.tvUserName.setText(chat.getUserName());
        holder.tvLastMessage.setText(chat.getLastMessage());
        holder.tvStatus.setText(chat.getStatus());

        // Load avatar image
        if (chat.getAvatarUrl() != null && !chat.getAvatarUrl().isEmpty()) {
            Glide.with(context)
                    .load(chat.getAvatarUrl())
                    .placeholder(R.drawable.profile_avatar_icon)
                    .error(R.drawable.profile_avatar_icon)
                    .circleCrop()
                    .into(holder.ivUserAvatar);
        } else {
            holder.ivUserAvatar.setImageResource(R.drawable.profile_avatar_icon);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (chatClickListener != null) {
                chatClickListener.onChatClick(chat);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void setChats(List<Chat> chats) {
        this.chatList = chats;
        notifyDataSetChanged();
    }

    public void addChat(Chat chat) {
        chatList.add(0, chat); // Add to the beginning of the list
        notifyItemInserted(0);
    }

    public void updateChat(Chat chat) {
        int position = -1;
        for (int i = 0; i < chatList.size(); i++) {
            if (chatList.get(i).getUserId().equals(chat.getUserId())) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            chatList.set(position, chat);
            notifyItemChanged(position);
        }
    }

    public interface OnChatClickListener {
        void onChatClick(Chat chat);
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserAvatar;
        TextView tvUserName;
        TextView tvLastMessage;
        TextView tvStatus;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
