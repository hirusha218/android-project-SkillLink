package lk.javainstitute.skilllink.adapter;

import android.content.Context;
import android.content.Intent;
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
import lk.javainstitute.skilllink.auth.Employee_detailsActivity;
import lk.javainstitute.skilllink.model.RequestModel;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private final List<RequestModel> requestList;
    private final Context context;

    // Constructor
    public RequestAdapter(List<RequestModel> requestList, Context context) {
        this.requestList = requestList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_crad, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (requestList != null && !requestList.isEmpty()) {
            RequestModel request = requestList.get(position);

            // Bind data to TextViews
            holder.tvJobType.setText(request.getJobType() != null ? request.getJobType() : "N/A");
            holder.tvCity.setText(request.getCity() != null ? request.getCity() : "N/A");
            holder.tvWorkExperience.setText(request.getWorkExperience() != null ? request.getWorkExperience() : "N/A");
            holder.tvPayment.setText(request.getPayment() != null ? request.getPayment() : "N/A");
            holder.tvWorkTime.setText(request.getWorkTime() != null ? request.getWorkTime() : "N/A");

            // Load image if available
            if (request.getMediaUrls() != null && !request.getMediaUrls().isEmpty()) {
                String imageUrl = request.getMediaUrls().get(0);
                Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(holder.jobImage);
            } else {
                holder.jobImage.setImageResource(R.drawable.ic_placeholder);
            }

            // Retrieve userId and jobId
            String userId = request.getUserid();
            String jobId = request.getJobId();

            // Click listener to open job details
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, Employee_detailsActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("jobId", jobId);
                intent.putExtra("object", request);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return (requestList != null) ? requestList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJobType, tvCity, tvWorkExperience, tvPayment, tvWorkTime;
        ImageView jobImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobType = itemView.findViewById(R.id.Job_Type);
            tvCity = itemView.findViewById(R.id.city);
            tvWorkExperience = itemView.findViewById(R.id.workExperience);
            tvPayment = itemView.findViewById(R.id.payment);
            tvWorkTime = itemView.findViewById(R.id.workTime);
            jobImage = itemView.findViewById(R.id.Job_image);
        }
    }
}
