package lk.javainstitute.skilllink.wfragments;

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

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private RequestAdapter adapter;
    private List<RequestModel> requestList;
    private List<RequestModel> originalList;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private EditText searchEdt;
    private ImageButton searchBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("JobPosts");

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view1);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));


        requestList = new ArrayList<>();
        originalList = new ArrayList<>();
        adapter = new RequestAdapter(requestList, getContext());
        recyclerView.setAdapter(adapter);

        searchEdt = view.findViewById(R.id.searchEdt);
        searchBtn = view.findViewById(R.id.searchBtn);

        searchBtn.setOnClickListener(v -> performSearch());

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
        String searchText = searchEdt.getText().toString().toLowerCase().toUpperCase().trim();

        if (searchText.isEmpty()) {
            requestList.clear();
            requestList.addAll(originalList);
        } else {
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
        return (request.getJobType() != null && request.getJobType().toLowerCase().toUpperCase().contains(searchText)) ||
                (request.getCity() != null && request.getCity().toLowerCase().toUpperCase().contains(searchText)) ||
                (request.getCategory() != null && request.getCategory().toLowerCase().toUpperCase().contains(searchText)) ||
                (request.getJobDescription() != null && request.getJobDescription().toLowerCase().toUpperCase().contains(searchText));
    }

    private void openSettingsFragment() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new SettingFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

