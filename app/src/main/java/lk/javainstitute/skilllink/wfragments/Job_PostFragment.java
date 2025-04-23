package lk.javainstitute.skilllink.wfragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.UUID;

import lk.javainstitute.skilllink.R;
import lk.javainstitute.skilllink.adapters.MediaAdapter;
import lk.javainstitute.skilllink.model.JobPost;

public class Job_PostFragment extends Fragment {

    private final ArrayList<Uri> mediaUris = new ArrayList<>();
    private Button btnJobImageUp;
    private Spinner spinnerJobCategory, spinnerJob;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private ArrayAdapter<String> categoryAdapter, jobAdapter;
    private ArrayList<String> categoryList, jobList;

    private EditText etCity, etWorkExperience, etPayment, etJobDescription, etWorkTime;
    private RecyclerView mediaRecyclerView;
    private MediaAdapter mediaAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_job__post, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String userId = mAuth.getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference("JobPosts").child(userId);

        btnJobImageUp = rootView.findViewById(R.id.btn_job_image_up);
        spinnerJobCategory = rootView.findViewById(R.id.spinnerJobCategory);
        spinnerJob = rootView.findViewById(R.id.spinnerJob);
        etCity = rootView.findViewById(R.id.City);
        etWorkExperience = rootView.findViewById(R.id.etWorkExperience);
        etPayment = rootView.findViewById(R.id.etPayment);
        etJobDescription = rootView.findViewById(R.id.etJobDescription);
        etWorkTime = rootView.findViewById(R.id.etWorkTime);

        categoryList = new ArrayList<>();
        jobList = new ArrayList<>();
        categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, categoryList);
        spinnerJobCategory.setAdapter(categoryAdapter);
        jobAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, jobList);
        spinnerJob.setAdapter(jobAdapter);

        loadJobCategories();

        btnJobImageUp.setOnClickListener(v -> openMediaPicker());
        rootView.findViewById(R.id.btnSubmit).setOnClickListener(v -> uploadJobPost());

        // Initialize RecyclerView with the new adapter
        mediaRecyclerView = rootView.findViewById(R.id.mediaRecyclerView);
        mediaRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mediaAdapter = new MediaAdapter(getContext(), mediaUris, new MediaAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                mediaUris.remove(position);
                mediaAdapter.notifyItemRemoved(position);
                mediaAdapter.notifyItemRangeChanged(position, mediaUris.size());
            }

            @Override
            public void onItemClick(int position, Uri mediaUri) {
                String mimeType = requireContext().getContentResolver().getType(mediaUri);
                if (mimeType != null && mimeType.startsWith("video/")) {
                    showVideoDialog(mediaUri);
                }
            }
        });
        mediaRecyclerView.setAdapter(mediaAdapter);

        return rootView;
    }

    private void openMediaPicker() {
        Intent intent = new Intent();
        intent.setType("*/*");
        String[] mimeTypes = {"image/*", "video/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Images or Videos"), 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            mediaUris.clear();
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    mediaUris.add(data.getClipData().getItemAt(i).getUri());
                }
            } else if (data.getData() != null) {
                mediaUris.add(data.getData());
            }
            mediaAdapter.notifyDataSetChanged();
            Toast.makeText(getContext(), mediaUris.size() + " items selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadJobPost() {
        // Validate inputs
        if (spinnerJobCategory.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Please select a job category.", Toast.LENGTH_LONG).show();
            return;
        }
        if (spinnerJob.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Please select a job type.", Toast.LENGTH_LONG).show();
            return;
        }
        if (mediaUris.isEmpty()) {
            Toast.makeText(getContext(), "Please select at least one image or video.", Toast.LENGTH_LONG).show();
            return;
        }

        String city = etCity.getText().toString().trim();
        String workExperience = etWorkExperience.getText().toString().trim();
        String payment = etPayment.getText().toString().trim();
        String jobDescription = etJobDescription.getText().toString().trim();
        String workTime = etWorkTime.getText().toString().trim();

        if (city.isEmpty() || workExperience.isEmpty() || payment.isEmpty() || jobDescription.isEmpty() || workTime.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields.", Toast.LENGTH_LONG).show();
            return;
        }

        String category = spinnerJobCategory.getSelectedItem().toString();
        String jobType = spinnerJob.getSelectedItem().toString();
        String userId = mAuth.getCurrentUser().getUid();
        String jobId = UUID.randomUUID().toString(); // Unique ID for the job post

        // Show progress dialog
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading Job Post");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ArrayList<String> mediaUrls = new ArrayList<>();
        uploadMediaFiles(jobId, category, jobType, userId, mediaUrls, city, workExperience, payment, jobDescription, workTime, progressDialog);
    }

    private void uploadMediaFiles(String jobId, String category, String jobType, String userId,
                                  ArrayList<String> mediaUrls, String city, String workExperience,
                                  String payment, String jobDescription, String workTime, ProgressDialog progressDialog) {
        int totalFiles = mediaUris.size();
        int[] uploadedFiles = {0};

        for (Uri mediaUri : mediaUris) {
            if (!isValidMediaType(mediaUri)) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Only images and videos are allowed.", Toast.LENGTH_LONG).show();
                return;
            }

            String filename = UUID.randomUUID().toString() + "." + getFileExtension(mediaUri);
            StorageReference mediaRef = mStorageRef.child(jobId).child(filename);

            String mimeType = requireContext().getContentResolver().getType(mediaUri);
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType(mimeType)
                    .build();

            mediaRef.putFile(mediaUri, metadata)
                    .addOnSuccessListener(taskSnapshot -> mediaRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                mediaUrls.add(uri.toString());
                                uploadedFiles[0]++;
                                progressDialog.setMessage("Uploading media: " + uploadedFiles[0] + "/" + totalFiles);

                                if (uploadedFiles[0] == totalFiles) {
                                    saveJobPost(jobId, category, jobType, userId, mediaUrls, city, workExperience, payment, jobDescription, workTime, progressDialog);
                                }
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Failed to get URL: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }))
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Media upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }

    private void saveJobPost(String jobId, String category, String jobType, String userId,
                             ArrayList<String> mediaUrls, String city, String workExperience,
                             String payment, String jobDescription, String workTime, ProgressDialog progressDialog) {
        JobPost jobPost = new JobPost(userId, category, jobType, mediaUrls, System.currentTimeMillis(),
                payment, city, workExperience, jobDescription, workTime, jobId);

        mDatabase.child("JobPosts").child(userId).child(jobId).setValue(jobPost)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Job post uploaded successfully!", Toast.LENGTH_SHORT).show();
                        resetJobPostForm();
                    } else {
                        Toast.makeText(getContext(), "Failed to save job post: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean isValidMediaType(Uri mediaUri) {
        String mimeType = requireContext().getContentResolver().getType(mediaUri);
        return mimeType != null && (mimeType.startsWith("image/") || mimeType.startsWith("video/"));
    }

    private String getFileExtension(Uri uri) {
        String mimeType = requireContext().getContentResolver().getType(uri);
        if (mimeType != null) {
            if (mimeType.startsWith("image/")) return "jpg";
            if (mimeType.startsWith("video/")) return "mp4";
        }
        return "jpg";
    }

    private void resetJobPostForm() {
        mediaUris.clear();
        mediaAdapter.notifyDataSetChanged();
        spinnerJobCategory.setSelection(0);
        spinnerJob.setSelection(0);
        etCity.setText("");
        etWorkExperience.setText("");
        etPayment.setText("");
        etJobDescription.setText("");
        etWorkTime.setText("");
    }

    private void loadJobCategories() {
        mDatabase.child("Job Categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                categoryList.add("Select Category");
                for (DataSnapshot categorySnap : snapshot.getChildren()) {
                    categoryList.add(categorySnap.getKey());
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load job categories: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        spinnerJobCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadJobsForCategory(categoryList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadJobsForCategory(String category) {
        if (category.equals("Select Category")) {
            jobList.clear();
            jobList.add("Select Job Type");
            jobAdapter.notifyDataSetChanged();
            return;
        }

        mDatabase.child("Job Categories").child(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jobList.clear();
                jobList.add("Select Job Type");
                for (DataSnapshot jobSnap : snapshot.getChildren()) {
                    jobList.add(jobSnap.getValue(String.class));
                }
                jobAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load job types: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showVideoDialog(Uri videoUri) {
        Dialog dialog = new Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_video_player);

        VideoView videoView = dialog.findViewById(R.id.fullscreen_video_view);
        ImageView closeButton = dialog.findViewById(R.id.close_button);

        MediaController mediaController = new MediaController(requireContext());
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(videoUri);
        videoView.start();

        closeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}