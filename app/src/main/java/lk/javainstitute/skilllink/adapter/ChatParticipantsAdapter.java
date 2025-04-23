package lk.javainstitute.skilllink.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import lk.javainstitute.skilllink.R;

public class ChatParticipantsAdapter extends RecyclerView.Adapter<ChatParticipantsAdapter.ViewHolder> {

    private final List<String> customerIds;
    private final OnCustomerClickListener listener;
    private final DatabaseReference usersRef;

    public ChatParticipantsAdapter(List<String> customerIds, OnCustomerClickListener listener) {
        this.customerIds = customerIds;
        this.listener = listener;
        this.usersRef = FirebaseDatabase.getInstance().getReference("Customers");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_chat_participants, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String customerId = customerIds.get(position);

        // Load user details from Firebase
        usersRef.child(customerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);

                    String fullName = "";
                    if (firstName != null && lastName != null) {
                        fullName = firstName + " " + lastName;
                    } else if (firstName != null) {
                        fullName = firstName;
                    } else if (lastName != null) {
                        fullName = lastName;
                    } else {
                        fullName = "Customers" + customerId;
                    }

                    holder.customerName.setText(fullName);

                    if (email != null && !email.isEmpty()) {
                        holder.customerEmail.setText(email);
                        holder.customerEmail.setVisibility(View.VISIBLE);
                    } else {
                        holder.customerEmail.setVisibility(View.GONE);
                    }
                } else {
                    holder.customerName.setText("Customers" + customerId);
                    holder.customerEmail.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.customerName.setText("Customers" + customerId);
                holder.customerEmail.setVisibility(View.GONE);
            }
        });

        holder.itemView.setOnClickListener(view -> listener.onCustomerClick(customerId));
    }

    @Override
    public int getItemCount() {
        return customerIds.size();
    }

    public interface OnCustomerClickListener {
        void onCustomerClick(String customerId);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView customerName;
        TextView customerEmail;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.tvUserName);
            customerEmail = itemView.findViewById(R.id.tvLastMessage);
        }
    }
}
