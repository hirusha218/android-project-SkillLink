package lk.javainstitute.skilllink.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import lk.javainstitute.skilllink.R;
import lk.javainstitute.skilllink.model.Workers;

public class SaveListAdapter extends RecyclerView.Adapter<SaveListAdapter.ViewHolder> {
    private static final String TAG = "SaveListAdapter";
    private List<Workers> savedWorkers;

    public SaveListAdapter(List<Workers> savedWorkers) {
        this.savedWorkers = savedWorkers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_save, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Workers worker = savedWorkers.get(position);
        
        // Set name
        String fullName = "";
        if (worker.getFname() != null && worker.getLname() != null) {
            fullName = worker.getFname() + " " + worker.getLname();
        } else if (worker.getFname() != null) {
            fullName = worker.getFname();
        } else if (worker.getLname() != null) {
            fullName = worker.getLname();
        } else {
            fullName = "No Name";
        }
        holder.nameTextView.setText(fullName);

        // Set job type
        String jobType = worker.getSkill();
        holder.jobTypeTextView.setText(jobType != null ? jobType : "No Job Type");

        // Set city
        if (holder.cityTextView != null) {
            String city = worker.getCity(); // Assuming you've added getCity() to Workers class
            holder.cityTextView.setText(city != null ? city : "No City");
        }
        
        // Load profile image
        String imageUrl = worker.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Log.d(TAG, "Loading image from URL: " + imageUrl);
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.user_13530226)
                            .error(R.drawable.user_13530226)
                            .circleCrop())
                    .into(holder.profileImageView);
        } else {
            Log.d(TAG, "No image URL found for worker at position: " + position);
            holder.profileImageView.setImageResource(R.drawable.user_13530226);
        }
    }

    @Override
    public int getItemCount() {
        return savedWorkers != null ? savedWorkers.size() : 0;
    }

    public void updateData(List<Workers> newWorkers) {
        this.savedWorkers = newWorkers;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView nameTextView;
        TextView jobTypeTextView;
        TextView cityTextView;

        ViewHolder(View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            jobTypeTextView = itemView.findViewById(R.id.jobTypeTextView);
            cityTextView = itemView.findViewById(R.id.cityTextView);
        }
    }
} 