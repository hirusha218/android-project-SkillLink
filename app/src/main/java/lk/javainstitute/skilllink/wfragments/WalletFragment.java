package lk.javainstitute.skilllink.wfragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import lk.javainstitute.skilllink.R;
import lk.javainstitute.skilllink.model.BankDetailsModel;
import lk.javainstitute.skilllink.model.CardDetailsModel;

public class WalletFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Button mButton;
    private EditText bankNameDisplay, accountNumberDisplay, userNameDisplay, branchDisplay;
    private EditText cardNumberDisplay, validThruDisplay, cvvDisplay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize UI components
        bankNameDisplay = view.findViewById(R.id.bank_name_display);
        accountNumberDisplay = view.findViewById(R.id.account_number_display);
        userNameDisplay = view.findViewById(R.id.user_name_display);
        branchDisplay = view.findViewById(R.id.branch_display);

        cardNumberDisplay = view.findViewById(R.id.card_number_display);
        validThruDisplay = view.findViewById(R.id.valid_thru_display);
        cvvDisplay = view.findViewById(R.id.cvv_display);

//        mButton = view.findViewById(R.id.you_payment1);

        view.findViewById(R.id.add_card).setOnClickListener(v -> showAddCardDialog());
        view.findViewById(R.id.add_bank).setOnClickListener(v -> showAddBankDialog());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Load bank and card details after the view is created
        loadBankDetails();
        loadCardDetails();
    }


    private void loadBankDetails() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Workers not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("Workers").child(userId).child("bankDetails").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String bankName = snapshot.child("bankName").getValue(String.class);
                    String accountNumber = snapshot.child("accountNumber").getValue(String.class);
                    String userName = snapshot.child("userName").getValue(String.class);
                    String branch = snapshot.child("branch").getValue(String.class);

                    if (bankName != null) bankNameDisplay.setText(bankName);
                    if (accountNumber != null) accountNumberDisplay.setText(accountNumber);
                    if (userName != null) userNameDisplay.setText(userName);
                    if (branch != null) branchDisplay.setText(branch);
                } else {
                    Toast.makeText(getContext(), "No bank details found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load bank details", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateBankDetailsUI(BankDetailsModel bankDetails) {
        if (getActivity() != null) { // Ensure we are on the main thread and UI is accessible
            bankNameDisplay.setText(bankDetails.getBankName());
            accountNumberDisplay.setText(bankDetails.getAccountNumber());
            userNameDisplay.setText(bankDetails.getUserName());
            branchDisplay.setText(bankDetails.getBranch());
        }
    }


    private void loadCardDetails() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Workers not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("Workers").child(userId).child("cardDetails").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String cardNumber = snapshot.child("cardNumber").getValue(String.class);
                    String validThru = snapshot.child("validThru").getValue(String.class);
                    String cvv = snapshot.child("cvv").getValue(String.class);

                    if (cardNumber != null) cardNumberDisplay.setText(maskCardNumber(cardNumber));
                    if (validThru != null) validThruDisplay.setText(validThru);
                    if (cvv != null) cvvDisplay.setText(cvv);
                } else {
                    Toast.makeText(getContext(), "No card details found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load card details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPaymentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter Employee ID");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_enter_employee_id, null);
        builder.setView(view);

        EditText employeeIdInput = view.findViewById(R.id.employee_id_input);

        builder.setPositiveButton("Search", (dialog, which) -> {
            String employeeId = employeeIdInput.getText().toString().trim();
            if (!employeeId.isEmpty()) {
                fetchPaymentMethods(employeeId);
            } else {
                Toast.makeText(getContext(), "Enter a valid Employee ID", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void fetchPaymentMethods(String employeeId) {
        DatabaseReference workerRef = mDatabase.child("Workers").child(employeeId);

        workerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    BankDetailsModel bankDetails = snapshot.child("bankDetails").getValue(BankDetailsModel.class);
                    CardDetailsModel cardDetails = snapshot.child("cardDetails").getValue(CardDetailsModel.class);
                    showPaymentSelectionDialog(bankDetails, cardDetails);
                } else {
                    Toast.makeText(getContext(), "Employee not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void showPaymentSelectionDialog(BankDetailsModel bankDetails, CardDetailsModel cardDetails) {
        if (getContext() == null) return;

        String[] paymentOptions = {
                "Bank: " + (bankDetails != null ? bankDetails.getBankName() + " (" + bankDetails.getAccountNumber() + ")" : "N/A"),
                "Card: " + (cardDetails != null ? maskCardNumber(cardDetails.getCardNumber()) : "N/A")
        };

        new AlertDialog.Builder(getContext())
                .setTitle("Select Payment Method")
                .setItems(paymentOptions, (dialog, which) -> {
                    if (which == 0 && bankDetails != null) {
                        processPayment("Bank", bankDetails.getAccountNumber());
                    } else if (which == 1 && cardDetails != null) {
                        processPayment("Card", cardDetails.getCardNumber());
                    }
                })
                .show();
    }

    private void processPayment(String method, String details) {
        Toast.makeText(getContext(), "Processing payment via " + method + ": " + details, Toast.LENGTH_SHORT).show();
    }


    private void updateCardDetailsUI(CardDetailsModel cardDetails) {
        if (getActivity() != null) {
            cardNumberDisplay.setText(maskCardNumber(cardDetails.getCardNumber())); // Mask the card number
            validThruDisplay.setText(cardDetails.getValidThru());
            cvvDisplay.setText(cardDetails.getCvv());
        }
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber.length() > 4) {
            return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
        }
        return cardNumber; // If the card number is too short, return as is
    }

    private void showAddCardDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Add Card Details");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_card, null);
        builder.setView(view);

        EditText cardNumber = view.findViewById(R.id.card_number);
        EditText validThru = view.findViewById(R.id.valid_thru);
        EditText cvv = view.findViewById(R.id.cvv);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String cardNumberInput = cardNumber.getText().toString().trim();
            String validThruInput = validThru.getText().toString().trim();
            String cvvInput = cvv.getText().toString().trim();

            if (validateCardDetails(cardNumberInput, validThruInput, cvvInput)) {
                String userId = mAuth.getCurrentUser().getUid();
                CardDetailsModel cardDetails = new CardDetailsModel(cardNumberInput, validThruInput, cvvInput);

                mDatabase.child("Workers").child(userId).child("cardDetails").setValue(cardDetails)
                        .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Card details saved", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save card details", Toast.LENGTH_SHORT).show());
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private boolean validateCardDetails(String cardNumber, String validThru, String cvv) {
        if (cardNumber.isEmpty() || cardNumber.length() != 16) {
            Toast.makeText(getContext(), "Invalid card number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (validThru.isEmpty() || !validThru.matches("\\d{2}/\\d{2}")) {
            Toast.makeText(getContext(), "Invalid valid thru date (MM/YY)", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (cvv.isEmpty() || cvv.length() != 3) {
            Toast.makeText(getContext(), "Invalid CVV", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showAddBankDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Add Bank Details");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_bank, null);
        builder.setView(view);

        EditText bankName = view.findViewById(R.id.bank_name);
        EditText accountNumber = view.findViewById(R.id.account_number);
        EditText userName = view.findViewById(R.id.j_nic);
        EditText branch = view.findViewById(R.id.branch);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String bankNameInput = bankName.getText().toString().trim();
            String accountNumberInput = accountNumber.getText().toString().trim();
            String userNameInput = userName.getText().toString().trim();
            String branchInput = branch.getText().toString().trim();

            if (validateBankDetails(bankNameInput, accountNumberInput, userNameInput, branchInput)) {
                String userId = mAuth.getCurrentUser().getUid();
                BankDetailsModel bankDetails = new BankDetailsModel(bankNameInput, accountNumberInput, userNameInput, branchInput, 0);

                mDatabase.child("Workers").child(userId).child("bankDetails").setValue(bankDetails)
                        .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Bank details saved", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save bank details", Toast.LENGTH_SHORT).show());
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private boolean validateBankDetails(String bankName, String accountNumber, String userName, String branch) {
        if (bankName.isEmpty()) {
            Toast.makeText(getContext(), "Bank name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (accountNumber.isEmpty() || accountNumber.length() < 8) {
            Toast.makeText(getContext(), "Invalid account number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (userName.isEmpty()) {
            Toast.makeText(getContext(), "Workers name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (branch.isEmpty()) {
            Toast.makeText(getContext(), "Branch cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
