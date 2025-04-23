package lk.javainstitute.skilllink.cactivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import lk.javainstitute.skilllink.R;
import lk.javainstitute.skilllink.auth.SaveListActivity;
import lk.javainstitute.skilllink.cfragments.C_BankFragment;
import lk.javainstitute.skilllink.cfragments.C_HomeFragment;
import lk.javainstitute.skilllink.cfragments.C_ProfileFragment;
import lk.javainstitute.skilllink.cfragments.C_settingFragment;
import lk.javainstitute.skilllink.wactivity.MainActivity;
import lk.javainstitute.skilllink.wfragments.ProfileFragment;
import lk.javainstitute.skilllink.wfragments.SettingFragment;

public class C_MainActivity extends AppCompatActivity {

    private boolean isCustomer = true; // Track current account type, true means Customer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmain);
        getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new C_HomeFragment());
        }

        ImageButton c_home = findViewById(R.id.C_home);
        ImageButton c_profile = findViewById(R.id.C_profile);
        ImageButton c_bank = findViewById(R.id.C_bank);
        ImageButton c_setting = findViewById(R.id.C_setting);

        // Set click listeners with animations
        applyAnimationOnClick(c_home, new C_HomeFragment());
        applyAnimationOnClick(c_profile, new C_ProfileFragment());
        applyAnimationOnClick(c_bank, new C_BankFragment());
        applyAnimationOnClick(c_setting, new C_settingFragment());

        ImageButton moreOptionsButton = findViewById(R.id.more_options_button);
        moreOptionsButton.setOnClickListener(view -> showPopupMenu(view));

        // Check if the account is Customer or Worker when the app starts
        isCustomer = isCustomerAccount();
    }

    // Show popup menu
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu, popupMenu.getMenu());

        // Set click listener for menu items
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.profile:
                    // Handle Profile option
                    loadFragment(new ProfileFragment());
                    return true;
                case R.id.settings:
                    // Handle Settings option
                    loadFragment(new SettingFragment());
                    return true;
                case R.id.save_list:
                    // Handle Send Feedback option
                    Intent intent = new Intent(C_MainActivity.this, SaveListActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.about:
                    // Handle About option
                    return true;
                case R.id.help:
                    // Handle Help option
                    return true;
                case R.id.nav_switch_account:
                    // Handle Help option
                    return true;
                default:
                    return false;
            }
        });

        // Show the menu
        popupMenu.show();
    }

    // Switch between customer and worker accounts
    private void switchAccount(PopupMenu popupMenu) {
        isCustomer = !isCustomer; // Toggle account type

        // Store the updated account type in SharedPreferences
        storeAccountType(isCustomer);

        // Show appropriate toast message
        if (isCustomer) {
            Toast.makeText(this, "Switched to Customer Account", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Switched to Worker Account", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(C_MainActivity.this, MainActivity.class)); // Switch to worker activity
            finish();
        }

        // Update the menu item title after switching
        updateSwitchAccountMenuItem(popupMenu);
    }

    // Store account type in SharedPreferences
    private void storeAccountType(boolean isCustomer) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isCustomer", isCustomer);  // Store the boolean indicating the account type
        editor.apply(); // Commit the changes
    }

    // Retrieve account type from SharedPreferences
    private boolean isCustomerAccount() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isCustomer", true);  // Default to true (Customer) if not set
    }

    // Update the menu item title dynamically
    private void updateSwitchAccountMenuItem(PopupMenu popupMenu) {
        MenuItem switchAccountItem = popupMenu.getMenu().findItem(R.id.nav_switch_account);
        if (isCustomer) {
            switchAccountItem.setTitle("Switch to Worker Account");
        } else {
            switchAccountItem.setTitle("Switch to Customer Account");
        }
    }

    // Load fragment dynamically
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // Apply animation with a bounce effect when buttons are clicked
    private void applyAnimationOnClick(ImageButton button, Fragment fragment) {
        button.setOnClickListener(view -> {
            // Scale animation with bounce effect
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.2f, 1f);
            scaleX.setDuration(300);
            scaleX.setInterpolator(new BounceInterpolator());
            scaleX.start();

            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.2f, 1f);
            scaleY.setDuration(300);
            scaleY.setInterpolator(new BounceInterpolator());
            scaleY.start();

            // Load fragment after animation
            loadFragment(fragment);
        });
    }
}
