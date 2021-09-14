package kovacs.chatlicenseapp.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kovacs.chatlicenseapp.R;
import kovacs.chatlicenseapp.adapter.ChatListAdapter;
import kovacs.chatlicenseapp.databinding.FragmentChatBinding;
import kovacs.chatlicenseapp.model.ChatList;

public class ChatFragment extends Fragment {
    private static final String TAG = "chatFragment";
    private FragmentChatBinding binding;
    private RecyclerView recyclerView;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseReference databaseReference;

    private ChatListAdapter chatListAdapter;
    private final List<ChatList> list = new ArrayList<>();
    private final ArrayList<String> allUserID = new ArrayList<>();

    private final Handler handler = new Handler();

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, true);
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);

        chatListAdapter = new ChatListAdapter(list, getContext());
        binding.recyclerView.setAdapter(chatListAdapter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        if (firebaseUser != null) {
            getChatList();
        }

        return binding.getRoot();
    }

    private void getChatList() {
        databaseReference.child("ChatList").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                allUserID.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userID = Objects.requireNonNull(dataSnapshot.child("chatID").getValue()).toString();
                    Log.d(TAG, "onDataChange: userId " + userID);

                    allUserID.add(userID);
                }

                getUserData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserData() {

        handler.post(new Runnable() {
            @Override
            public void run() {
                for (String userID : allUserID) {
                    firebaseFirestore.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Log.d(TAG, "onSuccess: " + documentSnapshot.getString("username"));
                            try {
                                ChatList chatList = new ChatList(
                                        documentSnapshot.getString("userID"),
                                        documentSnapshot.getString("username"),
                                        documentSnapshot.getString("imageProfile")
                                );

                                list.add(chatList);
                            } catch (Exception e) {
                                Log.d(TAG, "onSuccess: " + e.getMessage());
                            }

                            if (chatListAdapter != null) {
                                chatListAdapter.notifyItemInserted(0);
                                chatListAdapter.notifyDataSetChanged();
                                Log.d(TAG, "onSuccess: " + chatListAdapter.getItemCount());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.getMessage());
                        }
                    });
                }
            }
        });

    }
}