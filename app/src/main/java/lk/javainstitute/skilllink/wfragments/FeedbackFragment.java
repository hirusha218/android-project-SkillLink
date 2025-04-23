package lk.javainstitute.skilllink.wfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lk.javainstitute.skilllink.R;
import lk.javainstitute.skilllink.adapter.ReviewAdapter;
import lk.javainstitute.skilllink.model.feedback;

public class FeedbackFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private List<feedback> feedbackList;
    private TextView averageRatingText;
    private TextView totalReviewsText;
    private RatingBar averageRatingBar;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feedback, container, false);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback");
        initializeViews(rootView);
        setupRecyclerView(rootView);
        loadFeedbackData();

        return rootView;
    }

    private void initializeViews(View view) {
        averageRatingText = view.findViewById(R.id.textView24);
        totalReviewsText = view.findViewById(R.id.textView25);
        averageRatingBar = view.findViewById(R.id.ratingBar);
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView_Review);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        feedbackList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(getContext(), feedbackList);
        recyclerView.setAdapter(reviewAdapter);
    }

    private void loadFeedbackData() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feedbackList.clear();
                float totalRating = 0;
                int count = 0;

                for (DataSnapshot feedbackSnapshot : snapshot.getChildren()) {
                    feedback feedbackItem = feedbackSnapshot.getValue(feedback.class);
                    if (feedbackItem != null) {
                        feedbackList.add(feedbackItem);
                        totalRating += feedbackItem.getRating();
                        count++;
                    }
                }

                // Update UI with feedback statistics
                updateFeedbackStatistics(totalRating, count);

                // Notify adapter of data change
                reviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load feedback: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFeedbackStatistics(float totalRating, int count) {
        if (count > 0) {
            float averageRating = totalRating / count;

            // Update average rating text
            averageRatingText.setText(String.format("%.1f", averageRating));

            // Update total reviews text
            totalReviewsText.setText("Based On " + count + " Reviews");

            // Update rating bar
            averageRatingBar.setRating(averageRating);
        } else {
            // Handle case when there are no reviews
            averageRatingText.setText("0.0");
            totalReviewsText.setText("No Reviews Yet");
            averageRatingBar.setRating(0);
        }
    }
}
