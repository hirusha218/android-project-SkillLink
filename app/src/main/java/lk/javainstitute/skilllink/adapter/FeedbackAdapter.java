package lk.javainstitute.skilllink.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import lk.javainstitute.skilllink.R;
import lk.javainstitute.skilllink.model.feedback;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private final Context context;
    private final List<feedback> feedbackList;

    public FeedbackAdapter(Context context, List<feedback> feedbackList) {
        this.context = context;
        this.feedbackList = feedbackList;
    }

    @Override
    public FeedbackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false); // Make sure the XML layout file name is correct
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FeedbackViewHolder holder, int position) {
        feedback currentFeedback = feedbackList.get(position);

        // Set the email, rating, and job description
        holder.C_email.setText(currentFeedback.getC_email());
        holder.rating.setText(String.valueOf(currentFeedback.getRating()));
        holder.ratingBar.setRating(currentFeedback.getRating());
        holder.jobDescription.setText(currentFeedback.getJobDescription());
        holder.Up_dateOrUp_time.setText(currentFeedback.getUp_date() + " " + currentFeedback.getUp_time());

        // Load the profile image
        Glide.with(context)
                .load(currentFeedback.getC_ProfileImgUrl()) // Assuming this URL is valid for image loading
                .into(holder.profileImg);
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public static class FeedbackViewHolder extends RecyclerView.ViewHolder {

        TextView C_email, rating, jobDescription, Up_dateOrUp_time;
        RatingBar ratingBar;
        ImageView profileImg;

        public FeedbackViewHolder(View itemView) {
            super(itemView);

            C_email = itemView.findViewById(R.id.C_email);
            rating = itemView.findViewById(R.id.rating);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            jobDescription = itemView.findViewById(R.id.jobDescription);
            Up_dateOrUp_time = itemView.findViewById(R.id.Up_dateOrUp_time);
            profileImg = itemView.findViewById(R.id.C_profileImgUrl);
        }
    }
}