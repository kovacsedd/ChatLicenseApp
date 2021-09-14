package kovacs.chatlicenseapp.profile;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import kovacs.chatlicenseapp.R;
import kovacs.chatlicenseapp.common.Common;
import kovacs.chatlicenseapp.databinding.ActivityViewImageBinding;

public class ViewImage extends AppCompatActivity {
    private ActivityViewImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_image);

        binding.viewImageZoomed.setImageBitmap(Common.BITMAP_IMAGE);
    }

    public void callLastActivity(View view) {
        super.onBackPressed();
    }
}