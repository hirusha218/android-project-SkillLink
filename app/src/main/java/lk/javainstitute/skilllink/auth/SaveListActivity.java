package lk.javainstitute.skilllink.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import lk.javainstitute.skilllink.adapter.SaveListAdapter;
import lk.javainstitute.skilllink.model.Workers;

public class SaveListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView emptyStateTextView;
    private SaveListAdapter adapter;
    private List<Workers> savedWorkerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_save_list);

        // Initialize views
        recyclerView = findViewById(R.id.saveListRecyclerView);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);

        // Setup RecyclerView
        savedWorkerList = new ArrayList<>();
        adapter = new SaveListAdapter(savedWorkerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v -> onBackPressed());

        // Load saved workers
        loadSavedWorkers();
    }



    private void loadSavedWorkers() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference savedWorkersRef = FirebaseDatabase.getInstance().getReference()
                .child("SaveList")
                .child(currentUserId);

        savedWorkersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                savedWorkerList.clear();

                for (DataSnapshot workerSnapshot : snapshot.getChildren()) {
                    Workers worker = workerSnapshot.getValue(Workers.class);
                    if (worker != null) {
                        savedWorkerList.add(worker);
                    }
                }

                // Update UI
                if (savedWorkerList.isEmpty()) {
                    emptyStateTextView.setVisibility(View.VISIBLE);
                    emptyStateTextView.setText("No saved workers found");
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateTextView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                emptyStateTextView.setVisibility(View.VISIBLE);
                emptyStateTextView.setText("Error loading saved workers");
                recyclerView.setVisibility(View.GONE);
            }
        });
    }
}