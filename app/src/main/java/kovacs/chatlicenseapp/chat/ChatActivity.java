package kovacs.chatlicenseapp.chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import kovacs.chatlicenseapp.MainActivity;
import kovacs.chatlicenseapp.R;
import kovacs.chatlicenseapp.adapter.ChatAdapter;
import kovacs.chatlicenseapp.databinding.ActivityChatBinding;
import kovacs.chatlicenseapp.interfaces.OnReadChatCallBack;
import kovacs.chatlicenseapp.manage.ChatServices;
import kovacs.chatlicenseapp.manage.dialog.DialogReviewSendImage;
import kovacs.chatlicenseapp.model.Chat;
import kovacs.chatlicenseapp.services.FirebaseServices;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;

    private String receiverID;
    private String username;
    private String imageProfile;

    private ChatAdapter chatAdapter;
    private List<Chat> chatList = new ArrayList<>();
    private boolean showAction = false;

    private final int IMAGE_GALLERY_REQUEST = 111;
    private Uri imageUri;

    private static final int REQUEST_CORD_PERMISSION = 332;
    private MediaRecorder mediaRecorder;
    private String audio_path;
    private String sTime;

    private ChatServices chatServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);

        initButtonClick();
        initialize();
        readChat();
    }

    private void initialize() {
        receiverID = getIntent().getStringExtra("userID");
        username = getIntent().getStringExtra("username");
        imageProfile = getIntent().getStringExtra("imageProfile");
        if (receiverID != null) {
            binding.tvUsername.setText(username);
            if (imageProfile.isEmpty()) {
                binding.imageProfile.setImageResource(R.drawable.ic_placeholder_person);
            } else {
                Glide.with(this).load(imageProfile).into(binding.imageProfile);
            }
        }

        chatServices = new ChatServices(this, receiverID);

        binding.editMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(binding.editMessage.getText().toString())) {
                    binding.sendButton.setVisibility(View.INVISIBLE);
                    binding.recordButton.setVisibility(View.VISIBLE);
                } else {
                    binding.sendButton.setVisibility(View.VISIBLE);
                    binding.recordButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);

        chatAdapter = new ChatAdapter(chatList, this);
        binding.recyclerView.setAdapter(chatAdapter);

        // record button
        binding.recordButton.setRecordView(binding.recordView);
        binding.recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                // Start recording
                if (!checkPermissionFromDevice()) {
                    binding.kbEmoteButton.setVisibility(View.INVISIBLE);
                    binding.kbFileButton.setVisibility(View.INVISIBLE);
                    binding.kbCameraButton.setVisibility(View.INVISIBLE);
                    binding.editMessage.setVisibility(View.INVISIBLE);

                    startRecord();
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator != null) {
                        vibrator.vibrate(100);
                    }
                } else {
                    requestPermission();
                }
            }

            @Override
            public void onCancel() {
                try {
                    mediaRecorder.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish(long recordTime) {
                binding.kbEmoteButton.setVisibility(View.VISIBLE);
                binding.kbFileButton.setVisibility(View.VISIBLE);
                binding.kbCameraButton.setVisibility(View.VISIBLE);
                binding.editMessage.setVisibility(View.VISIBLE);

                // Stop recording
                try {
                    sTime = getHumanTimeText(recordTime);
                    stopRecord();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLessThanSecond() {
                binding.kbEmoteButton.setVisibility(View.VISIBLE);
                binding.kbFileButton.setVisibility(View.VISIBLE);
                binding.kbCameraButton.setVisibility(View.VISIBLE);
                binding.editMessage.setVisibility(View.VISIBLE);
            }
        });

        binding.recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                binding.kbEmoteButton.setVisibility(View.VISIBLE);
                binding.kbFileButton.setVisibility(View.VISIBLE);
                binding.kbCameraButton.setVisibility(View.VISIBLE);
                binding.editMessage.setVisibility(View.VISIBLE);
            }
        });
    }

    private void readChat() {
        chatServices.readChatData(new OnReadChatCallBack() {
            @Override
            public void onReadSuccess(List<Chat> list) {
                chatAdapter.setChatList(list);
                binding.recyclerView.smoothScrollToPosition(binding.recyclerView.getAdapter().getItemCount());
            }

            @Override
            public void onReadFailed() {
                Log.d("chatActivity", "onReadFailed: ");
            }
        });
    }

    private void initButtonClick() {
        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(binding.editMessage.getText().toString())) {
                    chatServices.sendTextMessage(binding.editMessage.getText().toString());
                    binding.editMessage.setText("");
                }
            }
        });

        binding.kbFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showAction) {
                    binding.cardViewLayout.setVisibility(View.GONE);
                    showAction = false;
                } else {
                    binding.cardViewLayout.setVisibility(View.VISIBLE);
                    showAction = true;
                }
            }
        });

        binding.galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Selectează imaginea"), IMAGE_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_GALLERY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                reviewImage(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void reviewImage(Bitmap bitmap) {
        new DialogReviewSendImage(ChatActivity.this, bitmap).show(new DialogReviewSendImage.onCallBack() {
            @Override
            public void onButtonSendClick() {
                // upload image to Firebase
                if (imageUri != null) {
                    final ProgressDialog progressDialog = new ProgressDialog(ChatActivity.this);
                    progressDialog.setMessage("Se trimite poza . . .");
                    new FirebaseServices(ChatActivity.this).uploadImageToFirebase(imageUri, new FirebaseServices.OnCallBack() {
                        @Override
                        public void onUploadSuccess(String imageUrl) {
                            chatServices.sendImage(imageUrl);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onUploadFailure(Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    private void startRecord() {
        setUpMediaRecorder();

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Eroare la înregistrare, resetează aplicația!", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecord() {
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;

                chatServices.sendVoiceMessage(audio_path);
            } else {
                Toast.makeText(getApplicationContext(), "Null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Stop recording error " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpMediaRecorder() {
        String path_save = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "audio_record.m4a";
        audio_path = path_save;

        mediaRecorder = new MediaRecorder();
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(path_save);
        } catch (Exception e) {
            Log.d("chatActivity", "setUpMediaRecorder: " + e.getMessage());
        }
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_DENIED || record_audio_result == PackageManager.PERMISSION_DENIED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_CORD_PERMISSION);
    }

    @SuppressLint("DefaultLocale")
    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    public void callLastActivity(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}