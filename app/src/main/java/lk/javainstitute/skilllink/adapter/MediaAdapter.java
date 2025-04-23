package lk.javainstitute.skilllink.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import lk.javainstitute.skilllink.R;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {
    private final Context context;
    private final ArrayList<String> mediaUrls;
    private final AlertDialog parentDialog;

    public MediaAdapter(Context context, ArrayList<String> mediaUrls, AlertDialog parentDialog) {
        this.context = context;
        this.mediaUrls = mediaUrls;
        this.parentDialog = parentDialog;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_media, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        String mediaUrl = mediaUrls.get(position);

        if (isVideoUrl(mediaUrl)) {
            holder.imageView.setVisibility(View.GONE);
            holder.videoView.setVisibility(View.VISIBLE);

            MediaController mediaController = new MediaController(context);
            holder.videoView.setMediaController(mediaController);
            holder.videoView.setVideoURI(Uri.parse(mediaUrl));
        } else {
            holder.videoView.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(mediaUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mediaUrls != null ? mediaUrls.size() : 0;
    }

    private boolean isVideoUrl(String url) {
        String lowercaseUrl = url.toLowerCase();
        return lowercaseUrl.endsWith(".mp4") || lowercaseUrl.endsWith(".3gp")
                || lowercaseUrl.endsWith(".mkv") || lowercaseUrl.contains("video");
    }

    class MediaViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        VideoView videoView;

        MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.media_image_view);
            videoView = itemView.findViewById(R.id.media_video_view);
        }
    }
} 