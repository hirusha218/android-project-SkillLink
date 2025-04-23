package lk.javainstitute.skilllink.auth;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import lk.javainstitute.skilllink.R;

public class ListJobPstActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private RecyclerView.Adapter LinearLayoutAdapter;
    private int categoryId;
    private String categoryName;
    private String searchText;
    private boolean isSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_job_pst);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Initialize RecyclerView
//        recyclerView = view.findViewById(R.id.recycler_view1);
//        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2)); // 2 columns in grid
//
//        // Initialize the request list and adapter
//        requestList = new ArrayList<>();
//        adapter = new RequestAdapter(requestList, getContext()); // Pass context to the adapter
//        recyclerView.setAdapter(adapter);
    }
}