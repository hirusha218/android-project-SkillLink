package lk.javainstitute.skilllink.wfragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.javainstitute.skilllink.R;

public class ProfileFragment extends Fragment implements OnMapReadyCallback {

    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 100;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 101;
    private static final int REQUEST_CODE_CAMERA = 102;
    private static final int PICK_IMAGE_REQUEST = 1;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Uri imageUri;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private StorageReference storageRef;
    private GoogleMap myMap;
    private Geocoder geocoder;
    private Marker currentMarker;
    private Runnable searchRunnable;

    private EditText firstNameTxt, lastNameTxt, emailTxt, mobileTxt, address1Txt, address2Txt, genderTxt, nicTxt;
    private ImageView profileImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("Workers");
        storageRef = FirebaseStorage.getInstance().getReference("profile_images");
        geocoder = new Geocoder(getContext());

        firstNameTxt = view.findViewById(R.id.first_name);
        lastNameTxt = view.findViewById(R.id.last_name);
        emailTxt = view.findViewById(R.id.user_email);
        mobileTxt = view.findViewById(R.id.user_mobile);
        address1Txt = view.findViewById(R.id.address_line_1);
        address2Txt = view.findViewById(R.id.address_line_2);
        genderTxt = view.findViewById(R.id.gender);
        nicTxt = view.findViewById(R.id.nic);
        profileImage = view.findViewById(R.id.U_profile_image);

        Button btnSave = view.findViewById(R.id.btn_save);
        Button btnDelete = view.findViewById(R.id.btn_delete);
        ImageView uploadImg = view.findViewById(R.id.uploadImg);

        loadProfileData();

        btnSave.setOnClickListener(v -> saveProfileData());
        btnDelete.setOnClickListener(v -> deleteProfileData());
        profileImage.setOnClickListener(v -> showImagePickerDialog());
        uploadImg.setOnClickListener(v -> showImagePickerDialog());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        setupAddressTextWatcher();

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        LatLng sriLanka = new LatLng(7.8731, 80.7718); // Default position (Sri Lanka)
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sriLanka, 6));
    }

    private void setupAddressTextWatcher() {
        TextWatcher addressWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> updateMapWithAddress();
                handler.postDelayed(searchRunnable, 500);
            }
        };

        address1Txt.addTextChangedListener(addressWatcher);
        address2Txt.addTextChangedListener(addressWatcher);
    }

    private void updateMapWithAddress() {
        if (myMap == null || getContext() == null) return;

        String address1 = address1Txt.getText().toString().trim();
        String address2 = address2Txt.getText().toString().trim();
        String fullAddress = address1 + (address2.isEmpty() ? "" : ", " + address2);

        if (fullAddress.isEmpty()) {
            LatLng sriLanka = new LatLng(7.8731, 80.7718);
            myMap.clear();
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sriLanka, 6));
            currentMarker = null;
            return;
        }

        try {
            List<Address> addresses = geocoder.getFromLocationName(fullAddress, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (currentMarker != null) {
                    currentMarker.remove();
                }
                currentMarker = myMap.addMarker(new MarkerOptions().position(latLng).title(fullAddress));
                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15)); // Zoom level 15 for street view
            } else {
                Toast.makeText(getContext(), "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(getContext(), "Geocoding error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProfileData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            emailTxt.setText(user.getEmail());
            String uid = user.getUid();
            dbRef.child(uid).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        firstNameTxt.setText(snapshot.child("fname").getValue(String.class));
                        lastNameTxt.setText(snapshot.child("lname").getValue(String.class));
                        mobileTxt.setText(snapshot.child("mobile").getValue(String.class));
                        address1Txt.setText(snapshot.child("address1").getValue(String.class));
                        address2Txt.setText(snapshot.child("address2").getValue(String.class));
                        genderTxt.setText(snapshot.child("gender").getValue(String.class));
                        nicTxt.setText(snapshot.child("nic").getValue(String.class));
                        String profileUrl = snapshot.child("profile_images").getValue(String.class);
                        if (profileUrl != null) {
                            Glide.with(getContext()).load(profileUrl).apply(new RequestOptions().circleCrop()).into(profileImage);
                        }
                        updateMapWithAddress();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showImagePickerDialog() {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Update Profile Picture")
                .setItems(new CharSequence[]{"Choose from Gallery", "Take Photo"}, (dialog, which) -> {
                    if (which == 0) {
                        checkPermissionsAndPickImage();
                    } else {
                        Toast.makeText(getContext(), "Camera feature coming soon", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.show();
    }

    private void checkPermissionsAndPickImage() {
        if (getContext() == null || getActivity() == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.READ_MEDIA_VIDEO
                        },
                        REQUEST_CODE_READ_EXTERNAL_STORAGE
                );
            } else {
                openImagePicker();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For Android 6.0 to Android 12
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        REQUEST_CODE_READ_EXTERNAL_STORAGE
                );
            } else {
                openImagePicker();
            }
        } else {
            openImagePicker();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                showPermissionExplanationDialog();
            }
        }
    }

    private void showPermissionExplanationDialog() {
        if (getContext() == null) return;

        new AlertDialog.Builder(getContext())
                .setTitle("Permission Required")
                .setMessage("This app needs storage permission to update your profile picture. Please grant the permission in Settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    openAppSettings();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Permission denied. Cannot update profile picture.", Toast.LENGTH_LONG).show();
                })
                .setCancelable(false)
                .show();
    }

    private void openAppSettings() {
        if (getContext() == null) return;

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void openImagePicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error opening image picker: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (getContext() != null) {
                // Show progress dialog
                ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Processing Image");
                progressDialog.setMessage("Please wait...");
                progressDialog.show();

                // Load and display the selected image
                Glide.with(getContext())
                        .load(imageUri)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.profile_avatar_icon)
                                .error(R.drawable.profile_avatar_icon)
                                .circleCrop())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e,
                                                        Object model,
                                                        Target<Drawable> target,
                                                        boolean isFirstResource) {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource,
                                                           Object model,
                                                           Target<Drawable> target,
                                                           DataSource dataSource,
                                                           boolean isFirstResource) {
                                progressDialog.dismiss();
                                uploadImageToFirebase();
                                return false;
                            }
                        })
                        .into(profileImage);
            }
        }
    }

    private void uploadImageToFirebase() {
        if (imageUri == null || getContext() == null || mAuth.getCurrentUser() == null) return;

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading Profile Picture");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String userId = mAuth.getCurrentUser().getUid();
        StorageReference fileRef = storageRef.child("profile_images").child(userId + ".jpg");

        fileRef.putFile(imageUri)
                .addOnProgressListener(snapshot -> {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                })
                .addOnSuccessListener(taskSnapshot -> {
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        dbRef.child(userId).child("profileImage").setValue(downloadUrl)
                                .addOnSuccessListener(aVoid -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveProfileData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            Map<String, Object> profileData = new HashMap<>();
            profileData.put("fname", firstNameTxt.getText().toString().trim());
            profileData.put("lname", lastNameTxt.getText().toString().trim());
            profileData.put("mobile", mobileTxt.getText().toString().trim());
            profileData.put("address1", address1Txt.getText().toString().trim());
            profileData.put("address2", address2Txt.getText().toString().trim());
            profileData.put("gender", genderTxt.getText().toString().trim());
            profileData.put("nic", nicTxt.getText().toString().trim());

            dbRef.child(uid).updateChildren(profileData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        updateMapWithAddress(); // Update map after saving
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show());
        }
    }

    private void deleteProfileData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            dbRef.child(uid).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                        clearFields();
                        updateMapWithAddress(); // Reset map to default
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to delete profile", Toast.LENGTH_SHORT).show());
        }
    }

    private void clearFields() {
        firstNameTxt.setText("");
        lastNameTxt.setText("");
        mobileTxt.setText("");
        address1Txt.setText("");
        address2Txt.setText("");
        genderTxt.setText("");
        nicTxt.setText("");
        profileImage.setImageResource(R.drawable.profile_avatar_icon); // Reset to default image
    }
}