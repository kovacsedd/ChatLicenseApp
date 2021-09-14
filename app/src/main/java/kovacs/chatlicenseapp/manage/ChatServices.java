package kovacs.chatlicenseapp.manage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kovacs.chatlicenseapp.interfaces.OnReadChatCallBack;
import kovacs.chatlicenseapp.model.Chat;

public class ChatServices {
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private Context context;
    private String receiverID;

    public ChatServices(Context context) {
        this.context = context;
    }

    public ChatServices(Context context, String receiverID) {
        this.context = context;
        this.receiverID = receiverID;
    }

    public void readChatData(OnReadChatCallBack onReadChatCallBack) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chat");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Chat> chatList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat != null && (chat.getSender().equals(firebaseUser.getUid()) && chat.getReceiver().equals(receiverID) ||
                            chat.getSender().equals(receiverID) && chat.getReceiver().equals(firebaseUser.getUid()))) {
                        chatList.add(chat);
                    }
                }

                onReadChatCallBack.onReadSuccess(chatList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onReadChatCallBack.onReadFailed();
            }
        });
    }

    public void sendTextMessage(String text) {
        Chat chat = new Chat(
                getCurrentDate(),
                text,
                "",
                "text",
                firebaseUser.getUid(),
                receiverID);

        databaseReference.child("Chat").push().setValue(chat);

        // Add to ChatList
        DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);
        chatReference.child("chatID").setValue(receiverID);

        DatabaseReference chatReference2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
        chatReference2.child("chatID").setValue(firebaseUser.getUid());
    }

    public void sendImage(String imageUrl) {
        Chat chat = new Chat(
                getCurrentDate(),
                "",
                imageUrl,
                "image",
                firebaseUser.getUid(),
                receiverID);

        databaseReference.child("Chat").push().setValue(chat);

        // Add to ChatList
        DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);
        chatReference.child("chatID").setValue(receiverID);

        DatabaseReference chatReference2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
        chatReference2.child("chatID").setValue(firebaseUser.getUid());
    }

    public void sendVoiceMessage(String audioPath) {
        final Uri uriAudio = Uri.fromFile(new File(audioPath));
        final StorageReference audioReference = FirebaseStorage.getInstance().getReference().child("VocalMessages/" + System.currentTimeMillis());
        audioReference.putFile(uriAudio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot audioSnapshot) {
                Task<Uri> uriTask = audioSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                Uri downloadUrl = uriTask.getResult();
                String vocalUrl = String.valueOf(downloadUrl);

                Chat chat = new Chat(
                        getCurrentDate(),
                        "",
                        vocalUrl,
                        "vocal",
                        firebaseUser.getUid(),
                        receiverID
                );

                databaseReference.child("Chat").push().setValue(chat);

                // Add to ChatList
                DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);
                chatReference.child("chatID").setValue(receiverID);

                DatabaseReference chatReference2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
                chatReference2.child("chatID").setValue(firebaseUser.getUid());
            }
        });
    }

    public String getCurrentDate() {
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String today = formatter.format(date);

        Calendar currentDateTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        String currentTime = dateFormat.format(currentDateTime.getTime());

        return today + ", " + currentTime;
    }
}
