package kovacs.chatlicenseapp.services;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import kovacs.chatlicenseapp.model.Status;

public class FirebaseServices {
    private Context context;

    public FirebaseServices(Context context) {
        this.context = context;
    }

    public void uploadImageToFirebase(Uri uri, OnCallBack onCallBack) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ChatImages/" + System.currentTimeMillis() + "." + getFileExtension(uri));

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                Uri downloadUri = uriTask.getResult();

                final String download_uri = String.valueOf(downloadUri);

                onCallBack.onUploadSuccess(download_uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onCallBack.onUploadFailure(e);
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public interface OnCallBack {
        void onUploadSuccess(String imageUrl);
        void onUploadFailure(Exception e);
    }

    public void addStatus(Status status, final OnAddStatusCallBack addStatusCallBack) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Status Daily").document(status.getId()).set(status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                addStatusCallBack.onUploadStatusSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                addStatusCallBack.onUploadStatusFailure();
            }
        });
    }

    public interface OnAddStatusCallBack {
        void onUploadStatusSuccess();
        void onUploadStatusFailure();
    }
}
