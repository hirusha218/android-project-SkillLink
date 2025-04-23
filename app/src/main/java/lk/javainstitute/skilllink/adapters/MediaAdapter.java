package lk.javainstitute.skilllink.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import lk.javainstitute.skilllink.R;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {
    private final Context context;
    private final ArrayList<Uri> mediaUris;
    private final OnItemClickListener listener;

    public MediaAdapter(Context context, ArrayList<Uri> mediaUris, OnItemClickListener listener) {
        this.context = context;
        this.mediaUris = mediaUris;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_media2, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        Uri mediaUri = mediaUris.get(position);
        String mimeType = context.getContentResolver().getType(mediaUri);

        if (mimeType != null && mimeType.startsWith("video/")) {
            // Handle video
            holder.imageView.setVisibility(View.VISIBLE);
            holder.playIcon.setVisibility(View.VISIBLE);

            // Set video thumbnail
            Glide.with(context)
                    .asBitmap()
                    .load(mediaUri)
                    .into(holder.imageView);

        } else {
            // Handle image
            holder.imageView.setVisibility(View.VISIBLE);
            holder.playIcon.setVisibility(View.GONE);

            Glide.with(context)
                    .load(mediaUri)
                    .into(holder.imageView);
        }

        // Set up delete button
        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(position);
            }
        });

        // Set up item click (for video playback)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position, mediaUri);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mediaUris != null ? mediaUris.size() : 0;
    }

    public interface OnItemClickListener {
        void onDeleteClick(int position);

        void onItemClick(int position, Uri mediaUri);
    }

    static class MediaViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView playIcon;
        ImageView deleteButton;

        MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.media_image_view);
            playIcon = itemView.findViewById(R.id.play_icon);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
} 