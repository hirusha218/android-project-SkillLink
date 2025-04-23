package lk.javainstitute.skilllink.cfragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lk.javainstitute.skilllink.R;
import lk.javainstitute.skilllink.adapter.RequestAdapter;
import lk.javainstitute.skilllink.model.RequestModel;

public class C_HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private RequestAdapter adapter;
    private List<RequestModel> requestList;
    private List<RequestModel> originalList; // To store the original unfiltered list
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private EditText searchEdt;
    private ImageButton searchBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout first
        View view = inflater.inflate(R.layout.fragment_c_home, container, false);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("JobPosts");  // Firebase node

        // Initialize RecyclerView with GridLayoutManager (2 columns)
        recyclerView = view.findViewById(R.id.recycler_view1); // Use the view object for findViewById
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // Use getContext() to avoid Fragment issues

        requestList = new ArrayList<>();
        originalList = new ArrayList<>();
        adapter = new RequestAdapter(requestList, getContext()); // Pass context to the adapter
        recyclerView.setAdapter(adapter);

        // Initialize search views
        searchEdt = view.findViewById(R.id.searchEdt);
        searchBtn = view.findViewById(R.id.searchBtn);

        // Setup search button click listener
        searchBtn.setOnClickListener(v -> performSearch());

        // Setup text change listener for real-time search
        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Load job posts from Firebase
        loadJobPostDetails();

        return view;
    }

    private void loadJobPostDetails() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                originalList.clear();

                if (!snapshot.exists()) {
                    Log.e("JobRequestView", "No job posts found in Firebase.");
                    return;
                }

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot jobSnapshot : userSnapshot.getChildren()) {
                        RequestModel request = jobSnapshot.getValue(RequestModel.class);

                        if (request != null && "verified".equals(request.getStatus())) {
                            request.setUserid(userSnapshot.getKey());
                            request.setJobId(jobSnapshot.getKey());
                            requestList.add(request);
                            originalList.add(request);
                            Log.d("JobRequestView", "Loaded verified job: " + jobSnapshot.getKey() + " for user: " + userSnapshot.getKey());
                        }
                    }
                }

                // Sort the list by timestamp in descending order
                requestList.sort((r1, r2) -> Long.compare(r2.getTimestamp(), r1.getTimestamp()));
                originalList.sort((r1, r2) -> Long.compare(r2.getTimestamp(), r1.getTimestamp()));

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }

    private void performSearch() {
        String searchText = searchEdt.getText().toString().toLowerCase().trim();

        if (searchText.isEmpty()) {
            // If search is empty, restore original list
            requestList.clear();
            requestList.addAll(originalList);
        } else {
            // Filter the list based on search criteria
            requestList.clear();
            for (RequestModel request : originalList) {
                if (matchesSearchCriteria(request, searchText)) {
                    requestList.add(request);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private boolean matchesSearchCriteria(RequestModel request, String searchText) {
        // Search in multiple fields
        return (request.getJobType() != null && request.getJobType().toLowerCase().contains(searchText)) ||
                (request.getCity() != null && request.getCity().toLowerCase().contains(searchText)) ||
                (request.getCategory() != null && request.getCategory().toLowerCase().contains(searchText)) ||
                (request.getJobDescription() != null && request.getJobDescription().toLowerCase().contains(searchText));
    }

    public void navigateToSettings() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new C_settingFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

