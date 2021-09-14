package kovacs.chatlicenseapp.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import kovacs.chatlicenseapp.MainActivity;
import kovacs.chatlicenseapp.R;
import kovacs.chatlicenseapp.databinding.ActivitySetProfileInfoBinding;
import kovacs.chatlicenseapp.model.User;

public class SetProfileInfoActivity extends AppCompatActivity {
    private ActivitySetProfileInfoBinding binding;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_profile_info);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);

        initCreateAccountButtonClick();
    }

    private void initCreateAccountButtonClick() {
        binding.createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(binding.inputUsername.getText().toString().trim())) {
                    binding.inputUsername.setError("Te rugăm să-ți introduci numele!");
                    binding.inputUsername.requestFocus();
                } else {
                    doUpdate();
                    Toast.makeText(SetProfileInfoActivity.this, "Contul a fost creat cu succes", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void doUpdate() {
        progressDialog.setMessage("Se salvează numele . . .");
        progressDialog.show();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            String userID = firebaseUser.getUid();
            User user = new User(
                    userID,
                    binding.inputUsername.getText().toString(),
                    firebaseUser.getEmail(),
                    "",
                    "",
                    "");

            firebaseFirestore.collection("users").document(firebaseUser.getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    Toast.makeText(SetProfileInfoActivity.this, "Datele dvs. au fost salvate", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SetProfileInfoActivity.this, MainActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(SetProfileInfoActivity.this, "A aparut o eoare în timpul salvării datelor", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}