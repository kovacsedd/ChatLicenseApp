package kovacs.chatlicenseapp.settings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import kovacs.chatlicenseapp.R;
import kovacs.chatlicenseapp.databinding.ActivitySettingsTabBinding;
import kovacs.chatlicenseapp.profile.Profile;
import kovacs.chatlicenseapp.startup.SplashScreen;

public class SettingsTab extends AppCompatActivity {
    private ActivitySettingsTabBinding binding;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings_tab);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (firebaseUser != null) {
            getUserInfo();
        }

        initClickButtons();
    }

    private void getUserInfo() {
        firebaseFirestore.collection("users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String tvUsername = documentSnapshot.getString("username");
                String tvProfileImage = documentSnapshot.getString("imageProfile");

                binding.tvName.setText(tvUsername);

                if (!tvProfileImage.equals("")) {
                    Glide.with(SettingsTab.this).load(tvProfileImage).into(binding.tvProfileImage);
                } else {
                    binding.tvProfileImage.setImageResource(R.drawable.ic_placeholder_person);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("getUserData", "on Failure " + e.getMessage());
            }
        });
    }

    private void initClickButtons() {
        binding.lyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callProfileActivity();
            }
        });

        binding.lyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // callProfileSettings();
            }
        });

        binding.lyConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callConversationActivity();
            }
        });

        binding.lyHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callHelpActivity();
            }
        });

        binding.lyInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // callInviteFriend();
            }
        });

        binding.lySignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLeaveAccount();
            }
        });
    }

    private void dialogLeaveAccount() {
        AlertDialog.Builder builderAlertDialog = new AlertDialog.Builder(SettingsTab.this);
        builderAlertDialog.setMessage("Dorești să părăsești contul?");
        builderAlertDialog.setPositiveButton("DA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SettingsTab.this, SplashScreen.class));
                finish();
            }
        }).setNegativeButton("NU", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builderAlertDialog.create();
        alertDialog.show();
    }

    private void callProfileActivity() {
        startActivity(new Intent(SettingsTab.this, Profile.class));
        finish();
    }

    private void callConversationActivity() {
        Toast.makeText(this, "Conversation_Activity", Toast.LENGTH_SHORT).show();
    }

    private void callHelpActivity() {
        Toast.makeText(this, "Help_Activity", Toast.LENGTH_SHORT).show();
    }

    public void backToPrevActivity(View view) {
        super.onBackPressed();
    }
}