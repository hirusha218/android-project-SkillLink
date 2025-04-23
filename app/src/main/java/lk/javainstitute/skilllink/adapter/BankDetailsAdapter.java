package lk.javainstitute.skilllink.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lk.javainstitute.skilllink.R;
import lk.javainstitute.skilllink.model.BankDetailsModel;

public class BankDetailsAdapter extends RecyclerView.Adapter<BankDetailsAdapter.ViewHolder> {

    private final List<BankDetailsModel> bankList;

    public BankDetailsAdapter(List<BankDetailsModel> bankList) {
        this.bankList = bankList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bank_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BankDetailsModel bank = bankList.get(position);
        holder.bankName.setText(bank.getBankName());
        holder.accountNumber.setText(bank.getAccountNumber());

    }

    @Override
    public int getItemCount() {
        return bankList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView bankName, accountNumber;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }
}
