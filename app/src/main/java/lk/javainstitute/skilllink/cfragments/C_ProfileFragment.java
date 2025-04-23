package lk.javainstitute.skilllink.cfragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import lk.javainstitute.skilllink.R;

public class C_ProfileFragment extends Fragment implements OnMapReadyCallback {

    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 100;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private Uri imageUri;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private StorageReference storageRef;
    private GoogleMap myMap;
    private FusedLocationProviderClient fusedLocationClient;

    private EditText firstNameTxt, lastNameTxt, emailTxt, mobileTxt, address1Txt, address2Txt, genderTxt, nicTxt;
    private ImageView profileImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_c_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("Customers");
        storageRef = FirebaseStorage.getInstance().getReference();

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
        uploadImg.setOnClickListener(v -> checkPermissionsAndPickImage());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        
        // Set default location to Sri Lanka's center coordinates
        LatLng sriLanka = new LatLng(7.8731, 80.7718);
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sriLanka, 7));
        
        enableMyLocation();
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), 
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            myMap.setMyLocationEnabled(true);
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), 
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            myMap.addMarker(new MarkerOptions()
                                    .position(currentLocation)
                                    .title("Current Location"));
                            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            }
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
                        firstNameTxt.setText(snapshot.child("firstName").getValue(String.class));
                        lastNameTxt.setText(snapshot.child("lastName").getValue(String.class));
                        mobileTxt.setText(snapshot.child("mobile").getValue(String.class));
                        address1Txt.setText(snapshot.child("address1").getValue(String.class));
                        address2Txt.setText(snapshot.child("address2").getValue(String.class));
                        genderTxt.setText(snapshot.child("gender").getValue(String.class));
                        nicTxt.setText(snapshot.child("nic").getValue(String.class));
                        String profileUrl = snapshot.child("profileImage").getValue(String.class);
                        if (profileUrl != null) {
                            Glide.with(getContext()).load(profileUrl).apply(new RequestOptions().circleCrop()).into(profileImage);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void checkPermissionsAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
            } else {
                openImagePicker();
            }
        } else {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
            } else {
                openImagePicker();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).apply(new RequestOptions().circleCrop()).into(profileImage);
            uploadImageToFirebase();
        }
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            StorageReference fileReference = storageRef.child("profile_images/" + mAuth.getCurrentUser().getUid() + ".jpg");
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        dbRef.child(mAuth.getCurrentUser().getUid()).child("profileImage").setValue(downloadUrl)
                                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show());
                    }))
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show());
        }
    }

    private void saveProfileData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            Map<String, Object> profileData = new HashMap<>();
            profileData.put("firstName", firstNameTxt.getText().toString().trim());
            profileData.put("lastName", lastNameTxt.getText().toString().trim());
            profileData.put("mobile", mobileTxt.getText().toString().trim());
            profileData.put("address1", address1Txt.getText().toString().trim());
            profileData.put("address2", address2Txt.getText().toString().trim());
            profileData.put("gender", genderTxt.getText().toString().trim());
            profileData.put("nic", nicTxt.getText().toString().trim());

            dbRef.child(uid).updateChildren(profileData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show())
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
//        profileImage.setImageResource(R.drawable.default_profile_image); // Set a default image
    }

}