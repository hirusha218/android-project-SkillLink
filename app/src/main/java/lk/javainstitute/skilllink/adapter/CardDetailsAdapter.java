package lk.javainstitute.skilllink.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lk.javainstitute.skilllink.R;
import lk.javainstitute.skilllink.model.CardDetailsModel;

public class CardDetailsAdapter extends RecyclerView.Adapter<CardDetailsAdapter.CardViewHolder> {

    private final List<CardDetailsModel> cardList;

    public CardDetailsAdapter(List<CardDetailsModel> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.crad_details, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardDetailsModel card = cardList.get(position);
        holder.cardNumber.setText(card.getCardNumber());
        holder.validThru.setText(card.getValidThru());
        holder.cvv.setText(card.getCvv());
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView cardNumber, validThru, cvv;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardNumber = itemView.findViewById(R.id.card_number);
            validThru = itemView.findViewById(R.id.valid_thru);
            cvv = itemView.findViewById(R.id.cvv);
        }
    }
}
