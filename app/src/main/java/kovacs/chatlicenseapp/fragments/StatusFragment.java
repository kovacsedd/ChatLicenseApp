package kovacs.chatlicenseapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import kovacs.chatlicenseapp.R;
import kovacs.chatlicenseapp.databinding.FragmentStatusBinding;
import kovacs.chatlicenseapp.status.AddStatusActivity;
import kovacs.chatlicenseapp.status.DisplayStatusActivity;

public class StatusFragment extends Fragment {

    private FragmentStatusBinding binding;

    public StatusFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_status, container, false);

        getProfileInfo();

        binding.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getContext(), DisplayStatusActivity.class));
            }
        });

        binding.statusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getContext(), AddStatusActivity.class));
            }
        });

        return binding.getRoot();
    }

    private void getProfileInfo() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String tvProfileImage = documentSnapshot.getString("imageProfile");

                if (!tvProfileImage.isEmpty()) {
                    Glide.with(StatusFragment.this).load(tvProfileImage).into(binding.imageProfile);
                } else {
                    binding.imageProfile.setImageResource(R.drawable.ic_placeholder_person);
                }
            }
        });
    }
}