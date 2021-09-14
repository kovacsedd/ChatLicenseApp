package kovacs.chatlicenseapp.contact;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import kovacs.chatlicenseapp.R;
import kovacs.chatlicenseapp.adapter.ContactAdapter;
import kovacs.chatlicenseapp.databinding.ActivityContactBinding;
import kovacs.chatlicenseapp.model.User;

public class Contact extends AppCompatActivity {
    private ActivityContactBinding binding;
    private List<User> userList = new ArrayList<>();
    private ContactAdapter contactAdapter;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (firebaseUser != null) {
            getContactList();
        }
    }

    private void getContactList() {
        firebaseFirestore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    String userID = snapshot.getString("userID");
                    String username = snapshot.getString("username");
                    String imageUrl = snapshot.getString("imageProfile");
                    String bio = snapshot.getString("bio");

                    User newUser = new User();
                    newUser.setUserID(userID);
                    newUser.setUsername(username);
                    newUser.setBio(bio);
                    newUser.setImageProfile(imageUrl);

                    if (userID != null && !userID.equals(firebaseUser.getUid())) {
                        userList.add(newUser);
                    }
                }
                contactAdapter = new ContactAdapter(userList, Contact.this);
                binding.recyclerView.setAdapter(contactAdapter);
            }
        });
    }

    public void callLastActivity(View view) {
        super.onBackPressed();
        finish();
    }
}