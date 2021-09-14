package kovacs.chatlicenseapp.status;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import kovacs.chatlicenseapp.MainActivity;
import kovacs.chatlicenseapp.R;
import kovacs.chatlicenseapp.databinding.ActivityDisplayStatusBinding;

public class DisplayStatusActivity extends AppCompatActivity {
    private ActivityDisplayStatusBinding binding;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_display_status);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (firebaseUser != null) {
            getUserInfo();
        }

        getStatusInfo();
    }

    private void getUserInfo() {
        firebaseFirestore.collection("users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String tvProfileImage = documentSnapshot.getString("imageProfile");

                if (!tvProfileImage.isEmpty()) {
                    Glide.with(DisplayStatusActivity.this).load(tvProfileImage).into(binding.imageProfile);
                } else {
                    binding.imageProfile.setImageResource(R.drawable.ic_placeholder_person);
                }
            }
        });
    }

    private void getStatusInfo() {
        firebaseFirestore.collection("Status Daily").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    String userID = snapshot.getString("userID");
                    if (userID != null && userID.equals(firebaseUser.getUid())) {

                        String tvCreatedDate = snapshot.getString("createdDate");
                        String tvDescription = snapshot.getString("textStatus");
                        String tvImageStatus = snapshot.getString("imageStatus");

                        binding.tvCreatedDate.setText(tvCreatedDate);
                        binding.statusDescription.setText(tvDescription);
                        Glide.with(DisplayStatusActivity.this).load(tvImageStatus).into(binding.statusImageView);
                    }
                }
            }
        });
    }

    public void callLastActivity(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}