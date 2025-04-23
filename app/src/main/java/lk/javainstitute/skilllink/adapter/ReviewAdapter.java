package lk.javainstitute.skilllink.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import lk.javainstitute.skilllink.R;
import lk.javainstitute.skilllink.model.feedback;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final Context context;
    private final List<feedback> feedbackList;

    public ReviewAdapter(Context context, List<feedback> feedbackList) {
        this.context = context;
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        feedback currentFeedback = feedbackList.get(position);

        // Set user email
        holder.emailTextView.setText(currentFeedback.getC_email());
        
        // Set rating
        holder.ratingBar.setRating(currentFeedback.getRating());
        holder.ratingTextView.setText(String.valueOf(currentFeedback.getRating()));
        
        // Set job description
        holder.jobDescriptionTextView.setText(currentFeedback.getJobDescription());
        
        // Set date and time
        String dateTime = currentFeedback.getUp_date() + " " + currentFeedback.getUp_time();
        holder.dateTimeTextView.setText(dateTime);

        // Load profile image using Glide
        if (currentFeedback.getC_ProfileImgUrl() != null && !currentFeedback.getC_ProfileImgUrl().isEmpty()) {
            Glide.with(context)
                    .load(currentFeedback.getC_ProfileImgUrl())
                    .placeholder(R.drawable.user_13530226)
                    .error(R.drawable.user_13530226)
                    .into(holder.profileImageView);
        } else {
            holder.profileImageView.setImageResource(R.drawable.user_13530226);
        }
    }

    @Override
    public int getItemCount() {
        return feedbackList != null ? feedbackList.size() : 0;
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView emailTextView;
        RatingBar ratingBar;
        TextView ratingTextView;
        TextView jobDescriptionTextView;
        TextView dateTimeTextView;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.C_profileImgUrl);
            emailTextView = itemView.findViewById(R.id.C_email);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            ratingTextView = itemView.findViewById(R.id.rating);
            jobDescriptionTextView = itemView.findViewById(R.id.jobDescription);
            dateTimeTextView = itemView.findViewById(R.id.Up_dateOrUp_time);
        }
    }
} 
