package kovacs.chatlicenseapp.status;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

import kovacs.chatlicenseapp.MainActivity;
import kovacs.chatlicenseapp.R;
import kovacs.chatlicenseapp.databinding.ActivityAddStatusBinding;
import kovacs.chatlicenseapp.manage.ChatServices;
import kovacs.chatlicenseapp.model.Status;
import kovacs.chatlicenseapp.services.FirebaseServices;

public class AddStatusActivity extends AppCompatActivity {
    private Uri imageUri;
    private ActivityAddStatusBinding binding;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_status);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        imageUri = MainActivity.imageUri;

        initClick();

        getInfo();
    }

    private void initClick() {
        binding.sendStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FirebaseServices(AddStatusActivity.this).uploadImageToFirebase(imageUri, new FirebaseServices.OnCallBack() {
                    @Override
                    public void onUploadSuccess(String imageUrl) {
                        Status status = new Status();
                        status.setId(UUID.randomUUID().toString());
                        status.setCreatedDate(new ChatServices(AddStatusActivity.this).getCurrentDate());
                        status.setImageStatus(imageUrl);
                        status.setUserID(FirebaseAuth.getInstance().getUid());
                        status.setViewCount("0");
                        status.setTextStatus(binding.addStatusComment.getText().toString());

                        new FirebaseServices(AddStatusActivity.this).addStatus(status, new FirebaseServices.OnAddStatusCallBack() {
                            @Override
                            public void onUploadStatusSuccess() {
                                Toast.makeText(getApplicationContext(), "Fotografia de status a fost încărcată!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }

                            @Override
                            public void onUploadStatusFailure() {
                                Toast.makeText(AddStatusActivity.this, "Eroare în timpul încărcări statusului!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onUploadFailure(Exception e) {
                        Log.e("AddStatusActivity", "onUploadFailure: ", e);
                    }
                });
            }
        });
    }

    private void getInfo() {
        Glide.with(this).load(imageUri).into(binding.imageView);

        firebaseFirestore.collection("users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String tvProfileImage = documentSnapshot.getString("imageProfile");

                if (!tvProfileImage.isEmpty()) {
                    Glide.with(AddStatusActivity.this).load(tvProfileImage).into(binding.imageProfile);
                } else {
                    binding.imageProfile.setImageResource(R.drawable.ic_placeholder_person);
                }
            }
        });
    }
}