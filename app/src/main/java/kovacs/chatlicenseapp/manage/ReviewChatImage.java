package kovacs.chatlicenseapp.manage;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import kovacs.chatlicenseapp.R;

public class ReviewChatImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_chat_image);
    }

    public void callLastActivity(View view) {
        super.onBackPressed();
    }
}