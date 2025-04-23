package lk.javainstitute.skilllink.cfragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import lk.javainstitute.skilllink.R;
import lk.javainstitute.skilllink.auth.LoadingActivity;
import lk.javainstitute.skilllink.model.Customers;

public class C_settingFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private TextView userNameText, userEmailText;
    private ImageView profileImage;
    private FirebaseAuth mAuth;
    private View editProfileItem, changePasswordItem, manageEmailPhoneItem;
    private View notificationSettingsItem, languageSelectionItem;
    private View managePrivacyItem, blockedUsersItem, twoFactorAuthItem;
    private View faqsItem, contactSupportItem, termsConditionsItem;

    public C_settingFragment() {
    }

    public static C_settingFragment newInstance(String param1, String param2) {
        C_settingFragment fragment = new C_settingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_c_setting, container, false);
        mAuth = FirebaseAuth.getInstance();

        userNameText = view.findViewById(R.id.userName);
        userEmailText = view.findViewById(R.id.userEmail);
        profileImage = view.findViewById(R.id.profileImage);


        initializeViews(view);
        setupSettingItems();
        setupLogoutButton(view);
        loadUserProfile();

        return view;
    }

    private void loadUserProfile() {
        String userId = mAuth.getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Customers")
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Customers customers = snapshot.getValue(Customers.class);
                            if (customers != null) {
                                // Set full name
                                String fullName = customers.getFirstName() + " " + customers.getLastName();
                                userNameText.setText(fullName);

                                // Set email
                                userEmailText.setText(customers.getEmail());

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                        Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initializeViews(View view) {
        // Account Settings
        editProfileItem = view.findViewById(R.id.editProfile);
        setSettingItem(editProfileItem, "Edit Profile", R.drawable.ic_edit_profile);

        changePasswordItem = view.findViewById(R.id.changePassword);
        setSettingItem(changePasswordItem, "Change Password", R.drawable.ic_password);

        manageEmailPhoneItem = view.findViewById(R.id.manageEmailPhone);
        setSettingItem(manageEmailPhoneItem, "Manage Email & Phone", R.drawable.ic_email);

        // App Preferences
        notificationSettingsItem = view.findViewById(R.id.notificationSettings);
        setSettingItem(notificationSettingsItem, "Notification Settings", R.drawable.ic_notification);

        languageSelectionItem = view.findViewById(R.id.languageSelection);
        setSettingItem(languageSelectionItem, "Language", R.drawable.ic_language);

        // Privacy & Security
        managePrivacyItem = view.findViewById(R.id.managePrivacy);
        setSettingItem(managePrivacyItem, "Privacy Settings", R.drawable.ic_privacy);

        blockedUsersItem = view.findViewById(R.id.blockedUsers);
        setSettingItem(blockedUsersItem, "Blocked Users", R.drawable.ic_block);

        twoFactorAuthItem = view.findViewById(R.id.twoFactorAuth);
        setSettingItem(twoFactorAuthItem, "Two-Factor Authentication", R.drawable.ic_security);

        // Support & Help
        faqsItem = view.findViewById(R.id.faqs);
        setSettingItem(faqsItem, "FAQs", R.drawable.ic_faq);

        contactSupportItem = view.findViewById(R.id.contactSupport);
        setSettingItem(contactSupportItem, "Contact Support", R.drawable.ic_support);

        termsConditionsItem = view.findViewById(R.id.termsConditions);
        setSettingItem(termsConditionsItem, "Terms & Conditions", R.drawable.ic_terms);
    }

    private void setSettingItem(View item, String title, int iconResId) {
        TextView titleText = item.findViewById(R.id.settingTitle);
        ImageView iconView = item.findViewById(R.id.settingIcon);

        titleText.setText(title);
        iconView.setImageResource(iconResId);
    }

    private void setupSettingItems() {
        editProfileItem.setOnClickListener(v -> {
            // Handle edit profile click
        });

        changePasswordItem.setOnClickListener(v -> {
            // Handle change password click
        });

        // Add other click listeners as needed
    }

    private void setupLogoutButton(View view) {
        Button btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoadingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}